package trad.semant.tds;

import trad.semant.tds.TDSEntry.Kind;
import trad.syntax.ast.Declaration;
import trad.syntax.ast.Type;
import trad.syntax.ast.impl.Identifier;

public class TdsConstruct {

    private TDSBlock tds;
    private int currentDepth;
    private int entryCount, blockCount;

    public TdsConstruct(Declaration decl) {
        super();
        this.tds = new TDSBlock(null, 0, 0, decl);
        this.currentDepth = 0;
        this.entryCount = 0;
        this.blockCount = 0;
    }

    public boolean addEntry(Identifier identifier, Type type, Kind kind, boolean isRef) {
        for (TDSEntry entry : tds.getEntries()) {
            if (entry.getIdentifier().getName().equals(identifier.getName())) {
                return false;
            }
        }
        TDSEntry entry = this.tds.addEntry(entryCount, identifier, type, kind, isRef);
        identifier.setTdsEntry(entry);
        entryCount++;
        return true;
    }

    public boolean addEntry(Identifier identifier, Type type, Kind kind) {
        return addEntry(identifier, type, kind, false);
    }

    public void addBlock(Declaration decl) {
        currentDepth++;
        blockCount++;
        this.tds = new TDSBlock(tds, currentDepth, blockCount, decl);
    }

    public void backBlock() {
        currentDepth--;
        tds = tds.getParent();
    }

    public TDSBlock getTds() {
        return tds;
    }

    public String toString() {
        return this.tds.toString();
    }

    public TDSEntry searchTds(Identifier i) {
        for (TDSEntry entry : tds.getEntries()) {
            if (entry.getIdentifier().getName().equals(i.getName())) {
                return entry;
            }
        }
        if (tds.getParent() != null) {
            for (TDSEntry entry : tds.getParent().getEntries()) {
                if (entry.getIdentifier().getName().equals(i.getName())) {
                    return entry;
                }
            }
        }
        return null;

    }
}
