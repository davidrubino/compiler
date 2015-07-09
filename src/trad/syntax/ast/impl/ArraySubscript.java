package trad.syntax.ast.impl;

import java.util.List;

import trad.semant.visitor.ASTVisitor;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.ListNode;
import trad.syntax.ast.Type.TypeEnum;

public class ArraySubscript extends LValue {
    private Identifier identifier;
    private ListNode<Expression> bounds;

    public ArraySubscript(Token startToken, Token endToken, Identifier identifier, ListNode<Expression> bounds) {
        super(startToken, endToken);
        this.identifier = identifier;
        this.bounds = bounds;

        addChild(identifier);
        addChildren(bounds.getElements());
    }

    @Override
    public Object getPayload() {
        return identifier + "[]";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitArraySubscript(this);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public List<Expression> getBounds() {
        return bounds.getElements();
    }

    @Override
    public void generateCode(CodeGen gen, boolean wantAddress) {
        int offset = 0;
        ArrayType arr = ((ArrayType) identifier.getTypeNode());
        List<BoundRange> defBounds = arr.getBounds().getElements();

        String reg1 = gen.getRegister(0);
        gen.emit("ldq 0, " + reg1);

        int[] ej = new int[defBounds.size()];

        for (int i = 0; i < ej.length; i++) {
            int factor = arr.getElementsType().getSize();
            for (int j = i; j < ej.length - 1; j++) {
                factor *= defBounds.get(j).getLength();
            }
            offset += factor * defBounds.get(i).getFrom();
            ej[i] = factor;

            String reg2 = gen.allocateRegister();
            bounds.getElements().get(i).generateCode(gen);
            gen.emit("ldw wr, #" + ej[i]);
            gen.emit("mul wr, " + reg2 + ", " + reg2);
            gen.emit("add " + reg1 + ", " + reg2 + ", " + reg1);
            gen.freeRegisters(1);
        }

        gen.emit("adi " + reg1 + ", " + reg1 + ", #" + offset);

        if (!wantAddress) {
            gen.emit("ldw " + reg1 + ", (" + reg1 + ")");
        }
    }
    public ArrayType getArrayType() {
        try {
            return ((ArrayType) identifier.getTypeNode());
        } catch (ClassCastException ex) {
            return null;
        }
    }

    @Override
    public TypeEnum getEvalType() {
        if (getArrayType() == null)
            return TypeEnum.UNKNOWN;
        return getArrayType().getElementsType().getType();
    }
}
