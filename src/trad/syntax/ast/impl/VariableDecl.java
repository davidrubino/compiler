package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.NotListNode;
import trad.syntax.ast.Type;

public class VariableDecl extends NotListNode {
	
	private Type type;
	private Identifier id;
	
	public VariableDecl(Token startToken, Token endToken, Type type, Identifier id) {
	    super(startToken, endToken, type, id);
		this.type = type;
		this.id = id;
	}

	public Type getType() {
		return type;
	}
	
	public Identifier getId() {
		return id;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visitVariableDecl(this);
	}
}
