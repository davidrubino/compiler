package trad.syntax.ast;

import org.antlr.v4.runtime.Token;

import trad.antlr.LeacLexer;

public abstract class Type extends NotListNode {
    public enum TypeEnum {
        INT(LeacLexer.INT),
        STRING(LeacLexer.STRING),
        BOOLEAN(LeacLexer.BOOL),
        VOID(LeacLexer.VOID),
        ARRAY(LeacLexer.ARRAY),
        UNKNOWN(-1);

        private int tokenType;

        TypeEnum(int type) {
            this.tokenType = type;
        }

        private int getTokenType() {
            return tokenType;
        }

        public static TypeEnum find(int tokenType) {
            for (TypeEnum type : TypeEnum.values()) {
                if (type.getTokenType() == tokenType)
                    return type;
            }
            return null;
        }
    }

    public Type(Token startToken, Token endToken, Node<?>... children) {
        super(startToken, endToken, children);
    }

    public abstract int getSize();

    public abstract TypeEnum getType();
}
