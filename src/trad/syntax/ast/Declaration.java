package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

import trad.semant.tds.TDSBlock;
import trad.syntax.ast.impl.Identifier;

public abstract class Declaration extends NotListNode {
    public Declaration(Token startToken, Token endToken, Node<?>... children) {
        super(startToken, endToken, children);
    }

    public abstract TDSBlock getTDSBlock();

    public abstract Identifier getIdentifier();
}
