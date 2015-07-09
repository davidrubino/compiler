package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Constant;
import trad.syntax.ast.Type.TypeEnum;

public class BoolConstant extends Constant<Boolean> {

    public BoolConstant(Token startToken, Token endToken, Boolean value) {
        super(startToken, endToken, value);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("bool constant");
        gen.emit("ldq " + (value ? -1 : 0) + ", " + gen.getRegister(0));
        gen.leave("bool constant");
    }

    @Override
    public TypeEnum getEvalType() {
        return TypeEnum.BOOLEAN;
    }
}
