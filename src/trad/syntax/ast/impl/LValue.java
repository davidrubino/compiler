package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;

public abstract class LValue extends Expression {
    public LValue(Token startToken, Token endToken) {
        super(startToken, endToken);
    }

    @Override
    public final void generateCode(CodeGen gen) {
        generateCode(gen, false);
    }

    @Override
    public abstract void generateCode(CodeGen gen, boolean wantAddress);

    @Override
    public boolean canBeAssigned() {
        return true;
    }
}
