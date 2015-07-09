package trad.syntax.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;

public class ListNode<T extends Node<?>> extends Node<T> implements Iterable<T> {
    public ListNode() {
        children = new ArrayList<T>();
    }
    
    public void add(T element) {
        ((List<T>) children).add(element);
    }
    
    public List<T> getElements() {
        return children;
    }

    @Override
    public Iterator<T> iterator() {
        return children.iterator();
    }
    
    @Override
    public Token getStartToken() {
        if (children.isEmpty())
            return null;
        
        return children.get(0).getStartToken();
    }

    @Override
    public Token getEndToken() {
        if (children.isEmpty())
            return null;
        
        return children.get(children.size() - 1).getEndToken();
    }
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visitListNode(this);
	}
	
	@Override
	public void generateCode(CodeGen gen) {
	    for (Node<?> node : children) {
	        node.generateCode(gen);
	    }
	}

}
