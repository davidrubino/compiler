package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.Constant;
import trad.syntax.ast.NotListNode;

public class BoundRange extends NotListNode {

    private Constant<Integer> from;
    private Constant<Integer> to;

    public BoundRange(Token startToken, Token endToken, Constant<Integer> from, Constant<Integer> to) {
        super(startToken, endToken);

        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from.getValue();
    }

    public int getTo() {
        return to.getValue();
    }

    public int getLength() {
        return to.getValue() - from.getValue() + 1;
    }

    @Override
    public Object getPayload() {
        return from + ".." + to;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitBoundRange(this);
    }
}
