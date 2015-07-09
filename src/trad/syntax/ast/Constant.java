package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;

public abstract class Constant<T> extends Expression {

    protected T value;

    public Constant(Token startToken, Token endToken, T value) {
        super(startToken, endToken);

        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Object getPayload() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitConstant(this);
    }
}
