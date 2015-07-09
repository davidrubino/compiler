package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.Type.TypeEnum;

public abstract class Expression extends NotListNode {

    public Expression(Token startToken, Token endToken, Node<?>... children) {
        super(startToken, endToken, children);
    }

    public abstract TypeEnum getEvalType();
}
