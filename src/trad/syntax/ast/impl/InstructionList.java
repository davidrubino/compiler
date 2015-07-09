package trad.syntax.ast.impl;


import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.Instruction;
import trad.syntax.ast.ListNode;

public class InstructionList extends ListNode<Instruction> {
	public InstructionList(Instruction firstInstr) {
		addChild(firstInstr);
	}

	public InstructionList() {
    }

    public void addInstr(InstructionList other) {
        addChildren(other.getElements());
	}
	
	@Override
	public Object getPayload() {
	    return "InstructionList[" + children.size() + "]";
	}
	
	public void accept(ASTVisitor visitor){
		visitor.visitInstructionList(this);
	}
}
