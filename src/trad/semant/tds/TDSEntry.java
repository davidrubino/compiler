package trad.semant.tds;

import trad.syntax.ast.Type;
import trad.syntax.ast.impl.Identifier;

public class TDSEntry {

    public enum Kind {
        FUNCTION,
        VARIABLE,
        ARGUMENT;
    }

    private TDSBlock block;
    private int id;
    private Identifier identifier;
    private Type type;
    private Kind kind;
    private boolean isRef;
    private int offset;

    public TDSEntry(TDSBlock tds, int id, Identifier identifier, Type type, int offset, Kind kind, boolean isRef) {
        this.block = tds;
        this.id = id;
        this.identifier = identifier;
        this.type = type;
        this.kind = kind;
        this.offset = offset;
        this.isRef = isRef;
    }

    public TDSBlock getBlock() {
        return block;
    }

    public int getBlockNumber() {
        return block.getBlockNumber();
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return getType().getSize();
    }

    public int getDepth() {
        return block.getDepth();
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public boolean isRef() {
        return isRef;
    }

    public String toString() {
        return "ID: " + getId() + ", idf: " + identifier + ", type: " + type.getPayload() + ", depth: " + getDepth();
    }
}
