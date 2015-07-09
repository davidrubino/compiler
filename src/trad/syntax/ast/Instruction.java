package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.Type.TypeEnum;

public abstract class Instruction extends Expression {
    public Instruction(Token startToken, Token endToken, Node<?>... children) {
        super(startToken, endToken, children);
    }

    @Override
    public TypeEnum getEvalType() {
        return TypeEnum.VOID;
    }
}
