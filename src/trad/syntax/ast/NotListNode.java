package trad.syntax.ast;

import java.util.Arrays;

import org.antlr.v4.runtime.Token;

public abstract class NotListNode extends Node<Node<?>> {
    private Token startToken, endToken;
    
    public NotListNode(Token startToken, Token endToken, Node<?>... children) {
        this.startToken = startToken;
        this.endToken = endToken;
        
        addChildren(Arrays.asList(children));
    }
    
    @Override
    public Token getStartToken() {
        return startToken;
    }
    
    @Override
    public Token getEndToken() {
        return endToken;
    }
}
