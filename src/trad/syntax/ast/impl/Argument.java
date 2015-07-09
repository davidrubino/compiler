package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.NotListNode;
import trad.syntax.ast.Type;

public class Argument extends NotListNode {
	private Identifier id;
	private Type type;
    private boolean isref;
	
	public Argument(Token startToken, Token endToken, Identifier id, Type type, boolean isref) {
	    super(startToken, endToken);
	    
	    this.isref = isref;
		this.id = id;
		this.type = type;
	}

	public Identifier getId() {
		return id;
	}

	public Type getType() {
		return type;
	}
	
	public boolean isRef() {
	    return isref;
	}
	
	@Override
	public Object getPayload() {
	    return (isref ? "ref " : "") + type + " " + id; 
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visitArgument(this);
	}
}
