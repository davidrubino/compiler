package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Constant;
import trad.syntax.ast.Type.TypeEnum;

public class StringConstant extends Constant<String> {
    public StringConstant(Token startToken, Token endToken, String value) {
        super(startToken, endToken, value);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("string constant");
        String cstlabel = gen.getUniqueLabel("str");
        gen.addData(cstlabel, "string " + value);
        gen.emit("ldw " + gen.getRegister(0) + ", #" + cstlabel);
        gen.leave("string constant");
    }

    @Override
    public TypeEnum getEvalType() {
        return TypeEnum.STRING;
    }
}
