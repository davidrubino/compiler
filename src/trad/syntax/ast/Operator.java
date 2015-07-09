package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

public abstract class Operator extends NotListNode {

    protected Token token;
    
    public Operator(Token token) {
        super(token, token);
        this.token = token;
    }
    
    public Token getToken() {
        return token;
    }
    
    @Override
    public Object getPayload() {
        return token.getText();
    }
}
