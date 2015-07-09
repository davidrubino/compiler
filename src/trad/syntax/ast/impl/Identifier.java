package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.tds.TDSEntry;
import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Type;
import trad.syntax.ast.Type.TypeEnum;

public class Identifier extends LValue {
    private String name;
    private TDSEntry entry;

    public Identifier(Token token) {
        super(token, token);

        this.name = token.getText();
    }

    public String getName() {
        return name;
    }

    @Override
    public Object getPayload() {
        return getName();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    public void generateCode(CodeGen gen, boolean wantAddress) {
        gen.enter("idf: " + this);

        String base = "bp";
        if (entry.getDepth() != gen.getCurrentBlock().getDepth()) {
            gen.emit("ldw wr, (bp)");
            base = "wr";
        }
        gen.emit("adi " + base + ", " + gen.getRegister(0) + ", #var_" + entry.getBlockNumber() + "_" + entry.getIdentifier());

        if (entry.isRef()) {
            gen.emit("ldw " + gen.getRegister(0) + ", " + "(" + gen.getRegister(0) + ")");
        }

        if (!wantAddress) {
            gen.emit("ldw " + gen.getRegister(0) + ", " + "(" + gen.getRegister(0) + ")");
        }

        gen.leave("idf: " + this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Identifier other = (Identifier) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public TDSEntry getTdsEntry() {
        return entry;
    }

    public void setTdsEntry(TDSEntry tdsEntry) {
        this.entry = tdsEntry;
    }

    @Override
    public TypeEnum getEvalType() {
        if (getTypeNode() == null) {
            return TypeEnum.UNKNOWN;
        }

        return getTypeNode().getType();
    }

    public Type getTypeNode() {
        if (entry != null)
            return entry.getType();
        return null;
    }

    @Override
    public boolean canBeAssigned() {
        return getTypeNode().getType() != TypeEnum.ARRAY;
    }
}
