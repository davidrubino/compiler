package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.Operator;
import trad.antlr.LeacLexer;

public class BinaryOperator extends Operator {
    public static enum Type {
        MULT(LeacLexer.TIMES),
        DIV(LeacLexer.DIVIDE),
        PLUS(LeacLexer.PLUS),
        MINUS(LeacLexer.MINUS),
        LESSER_THAN(LeacLexer.LT),
        LESSER_OR_EQUAL_THAN(LeacLexer.LTEQ),
        GREATER_THAN(LeacLexer.GT),
        GREATER_OR_EQUAL_THAN(LeacLexer.GTEQ),
        EQUALS(LeacLexer.EQ),
        DIFFERENT(LeacLexer.NEQ),
        AND(LeacLexer.AND),
        OR(LeacLexer.OR),
        EXPONENT(LeacLexer.EXPONENT);
        
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
    
    public BinaryOperator(Token token) {
        super(token);
    }

    public Type getType() {
        return Type.find(token.getType());
    }
}
