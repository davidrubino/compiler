package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Instruction;

public class AffectInstr extends Instruction {

    private LValue lval;
    private Expression expr;

    public AffectInstr(Token startToken, Token endToken, LValue lval, Expression expr) {
        super(startToken, endToken, lval, expr);
        this.lval = lval;
        this.expr = expr;
    }

    public LValue getLval() {
        return lval;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitAffectInstr(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("affect");

        String reg_val = gen.allocateRegister();
        expr.generateCode(gen);

        String reg_addr = gen.allocateRegister();
        lval.generateCode(gen, true);

        gen.emit("stw " + reg_val + ", (" + reg_addr + ")");

        gen.freeRegisters(2);
        gen.leave("affect");
    }
}
