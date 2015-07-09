package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Instruction;

public class WhileInstr extends Instruction {
	
	private Expression expr;
	private InstructionList instr;
	
	public WhileInstr(Token startToken, Token endToken, Expression e, InstructionList i) {
		super(startToken, endToken, e, i);
		this.expr = e;
        this.instr = i;
	}

	public Expression getExpr() {
		return expr;
	}

    public InstructionList getInstr1() {
        return instr;
    }

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visitWhileInstr(this);
	}
    
    @Override
    public void generateCode(CodeGen gen) {
        String begin_label = gen.getUniqueLabel("while_begin");
        String end_label = gen.getUniqueLabel("while_end");
        
    	gen.enter("while");
        String reg = gen.allocateRegister();
        gen.setLabel(begin_label);
        expr.generateCode(gen);
        
        // permet de mettre à jour SR depuis la valeur du registre
        gen.emit("tst " + reg);

        // saut à la clause 'end' (ou au début de la boucle si on est pas encore sortis de la boucle) 
        // si boucle terminée
        // c'est à dire si la valeur de test dans le registre est égale à 0
        gen.emit("jeq #" + end_label + "-$-2");

        instr.generateCode(gen);
        
        gen.emit("jea @" + begin_label);
        
        gen.setLabel(end_label);
        gen.leave("while");
    }
}