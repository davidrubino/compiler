package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Constant;
import trad.syntax.ast.Type.TypeEnum;

public class IntConstant extends Constant<Integer> {

    public IntConstant(Token startToken, Token endToken, Integer value) {
        super(startToken, endToken, value);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("int constant");

        if (value >= -128 && value <= 127)
            gen.emit("ldq " + value + ", " + gen.getRegister(0));
        else
            gen.emit("ldw " + gen.getRegister(0) + ", #" + value);

        gen.leave("int constant");
    }

    @Override
    public TypeEnum getEvalType() {
        return TypeEnum.INT;
    }
}
