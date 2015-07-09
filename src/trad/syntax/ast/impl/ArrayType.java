package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.ListNode;
import trad.syntax.ast.Type;

public class ArrayType extends Type {
    private ListNode<BoundRange> bounds;
    private AtomType type;

    public ArrayType(Token startToken, Token endToken, ListNode<BoundRange> bounds, AtomType type) {
        super(startToken, endToken, bounds);
        this.bounds = bounds;
        this.type = type;
    }

    public ListNode<BoundRange> getBounds() {
        return bounds;
    }

    public Type getElementsType() {
        return type;
    }

    public TypeEnum getType() {
        return TypeEnum.ARRAY;
    }

    @Override
    public Object getPayload() {
        return type + bounds.getElements().toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitArrayType(this);
    }

    @Override
    public int getSize() {
        return 2;
    }
}
