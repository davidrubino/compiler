package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Instruction;

public class ReturnInstr extends Instruction {
    private Expression expr;

    public ReturnInstr(Token startToken, Token endToken, Expression expr) {
        super(startToken, endToken, expr);
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public void generateCode(CodeGen gen) {
        if (expr != null) {

            gen.allocateRegister();
            expr.generateCode(gen);
            gen.emit("ldw r0, " + gen.getRegister(0));
            gen.freeRegisters(1);
        }
        gen.emit("jmp #fun_end_" + ((FunctionDecl) gen.getCurrentBlock().getDeclaration()).getIdentifier() + "- $ - 2");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitReturnInstr(this);
    }
}
