package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Instruction;

public class IfInstr extends Instruction {

    private Expression expr;
    private InstructionList instr1, instr2;

    public IfInstr(Token startToken, Token endToken, Expression e, InstructionList i) {
        super(startToken, endToken, e, i);
        this.expr = e;
        this.instr1 = i;
    }

    public IfInstr(Token startToken, Token endToken, Expression e, InstructionList i1, InstructionList i2) {
        super(startToken, endToken, e, i1, i2);

        this.expr = e;
        this.instr1 = i1;
        this.instr2 = i2;
    }

    public Expression getExpr() {
        return expr;
    }

    public InstructionList getInstr1() {
        return instr1;
    }

    public InstructionList getInstr2() {
        return instr2;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitIfInstr(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("if");
        String reg = gen.allocateRegister();
        expr.generateCode(gen);

        String end_label = gen.getUniqueLabel("if_end");
        String else_label = gen.getUniqueLabel("if_else");

        // permet de mettre à jour SR depuis la valeur du registre
        gen.emit("tst " + reg);

        // saut à la clause 'else' (ou à la fin du if si pas de else) si condition non vérifiée
        // c'est à dire si la valeur de test dans le registre est égale à 0
        gen.emit("jeq #" + (instr2 != null ? else_label : end_label) + "-$-2");

        instr1.generateCode(gen);

        if (instr2 != null) {
            gen.emit("jea @" + end_label);
            gen.setLabel(else_label);
            instr2.generateCode(gen);
        }
        gen.setLabel(end_label);
        gen.leave("if");
    }
}
