package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Instruction;
import trad.syntax.ast.Type.TypeEnum;

public class ReadInstr extends Instruction {

    private LValue lval;

    public ReadInstr(Token startToken, Token endToken, LValue lval) {
        super(startToken, endToken, lval);
        this.lval = lval;
    }

    public LValue getLval() {
        return lval;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitReadInstr(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.emit("adq -16, sp // réserve de l'espace pour la chaine de caractère");
        gen.emit("ldw r0, sp");
        gen.emit("ldw wr, #READ_EXC");
        gen.emit("trp wr");

        if (lval.getEvalType() == TypeEnum.INT || lval.getEvalType() == TypeEnum.BOOLEAN) {
            gen.useExtract(CodeExtracts.ATOI);
            gen.emit("stw r0, -(sp)");
            gen.emit("jsr @atoi_");
            gen.emit("adq 2, sp");
        }
        gen.emit("adq 16, sp");

        String reg = gen.allocateRegister();
        lval.generateCode(gen, true);
        gen.emit("stw r0, (" + reg +")");
        gen.freeRegisters(1);
    }
}
