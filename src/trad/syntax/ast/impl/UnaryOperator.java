package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.Operator;
import trad.antlr.LeacLexer;

public class UnaryOperator extends Operator {
    public static enum Type {
        UNARY_MINUS(LeacLexer.MINUS),
        NOT(LeacLexer.NOT);
        
        private int tokenType;

        Type(int type) {
            this.tokenType = type;
        }
        
        private int getTokenType() {
            return tokenType;
        }
        
        public static Type find(int tokenType) {
            for (Type type : Type.values()) {
                if (type.getTokenType() == tokenType)
                    return type;
            }
            return null;
        }
    }

    public UnaryOperator(Token token) {
        super(token);
    }

    public Type getType() {
        return Type.find(token.getType());
    }
}
