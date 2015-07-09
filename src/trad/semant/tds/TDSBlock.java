package trad.semant.tds;

import java.util.ArrayList;

import trad.semant.tds.TDSEntry.Kind;
import trad.syntax.ast.Declaration;
import trad.syntax.ast.Type;
import trad.syntax.ast.impl.FunctionDecl;
import trad.syntax.ast.impl.Identifier;

import com.bethecoder.ascii_table.ASCIITable;

public class TDSBlock {

    private int depth, blockNumber;
    private TDSBlock parent;
    private Declaration decl;
    private ArrayList<TDSEntry> entries;
    private ArrayList<TDSBlock> children;
    private int argOffset = 4;
    private int varOffset = 0;

    public TDSBlock(TDSBlock parent, int depth, int blockNumber, Declaration decl) {
        this.depth = depth;
        this.entries = new ArrayList<TDSEntry>();
        this.children = new ArrayList<TDSBlock>();
        this.decl = decl;

        if (depth == 0) {
            this.parent = null;
            this.blockNumber = 0;
        } else {
            this.children = new ArrayList<TDSBlock>();
            this.parent = parent;
            this.blockNumber = blockNumber;
            parent.addChild(this);
        }
    }

    public void addChild(TDSBlock son) {
        this.children.add(son);
    }

    public void setChildrenParent(TDSBlock parent) {
        for (TDSBlock child : this.children)
            child.setParent(parent);
    }

    public TDSEntry getEntry(int n) {
        return this.entries.get(n);
    }

    public TDSEntry addEntry(int id, Identifier identifier, Type type, Kind kind, boolean isRef) {
        int offset = 0;
        if (kind == Kind.ARGUMENT) {
            offset = argOffset;
            if (isRef) {
                argOffset += 2;
            } else {
                argOffset += type.getSize();
            }
        } else if (kind == Kind.VARIABLE) {
            varOffset -= type.getSize();
            offset = varOffset;
        }

        TDSEntry entry = new TDSEntry(this, id, identifier, type, offset, kind, isRef);
        this.entries.add(entry);
        return entry;
    }

    public void setParent(TDSBlock parent) {
        this.parent = parent;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public int getDepth() {
        return depth;
    }

    public TDSBlock getParent() {
        return parent;
    }

    public ArrayList<TDSEntry> getEntries() {
        return entries;
    }

    public TDSEntry search(Identifier idf) {
        for (TDSEntry entry : entries) {
            if (entry.getIdentifier().equals(idf))
                return entry;
        }

        if (parent != null)
            return parent.search(idf);
        else
            return null;
    }

    public Declaration getDeclaration() {
        return decl;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int depth) {
        String whitespace = "";
        for (int i = 0; i < depth; i++) {
            whitespace += "\t";
        }

        String[] headers = { "id", "name", "type", "size", "kind", "offset", "depth" };
        String[][] data = new String[entries.size()][];

        for (int i = 0; i < data.length; i++) {
            TDSEntry entry = entries.get(i);
            data[i] = new String[] { "" + entry.getId(),
                    "" + entry.getIdentifier(), "" + entry.getType(),
                    "" + entry.getSize(),
                    "" + entry.getKind(), "" + entry.getOffset(),
                    "" + entry.getDepth() };
        }

        String out = "";
        if (data.length > 0) {
            out += whitespace + "Table " + decl.getIdentifier() + ":\n";
            out += indent(ASCIITable.getInstance().getTable(headers, data),
                    whitespace);
            for (TDSBlock block : children)
                out += block.toString(depth + 1);
        }
        return out;
    }

    static String indent(String str, String prefix) {
        String[] lines = str.split("\n");
        String out = "";

        for (String line : lines)
            out += prefix + line + "\n";

        return out;
    }

    public int getLocalVariablesSize() {
        int size = 0;
        for (TDSEntry entry : entries) {
            if (entry.getKind() == Kind.VARIABLE) {
                size += entry.getType().getSize();
            }
        }
        return size;
    }

    public int getArgumentsSize() {
        int size = 0;
        for (TDSEntry entry : entries) {
            if (entry.getKind() == Kind.ARGUMENT) {
                size += entry.getType().getSize();
            }
        }
        return size;
    }

    public FunctionDecl findFunction(Identifier function) {
        for (TDSBlock block : children) {
            Declaration decl = block.getDeclaration();
            if (!(decl instanceof FunctionDecl)) {
                continue;
            }

            FunctionDecl fundec = (FunctionDecl) decl;

            if (fundec.getIdentifier().equals(function))
                return fundec;
        }

        if (parent != null)
            return parent.findFunction(function);
        else
            return null;
    }
}
