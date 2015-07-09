package trad.syntax.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.Tree;

import trad.semant.visitor.ASTVisitor;

public abstract class Node<T extends Node<?>> implements Tree {
    protected Node<?> parent;
    protected List<T> children;

    public Node() {
        this.children = new ArrayList<T>();
    }

    protected void addChild(T child) {
        if (child == null)
            return;
        children.add(child);
        child.setParent(this);
    }

    protected void addChildren(Collection<? extends T> children) {
        for (T child : children) {
            addChild(child);
        }
    }

    public void setParent(Node<?> parent) {
        this.parent = parent;
    }

    @Override
    public Node<?> getChild(int i) {
        return children.get(i);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public Tree getParent() {
        return parent;
    }

    @Override
    public Object getPayload() {
        return getClass().getSimpleName();
    }

    @Override
    public String toStringTree() {
        return getPayload().toString();
    }

    @Override
    public String toString() {
        return getPayload().toString();
    }

    public void accept(ASTVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    public abstract Token getStartToken();

    public abstract Token getEndToken();

    public void generateCode(CodeGen gen, boolean wantAddress) {
        if (wantAddress)
            throw new UnsupportedOperationException();
        else
            generateCode(gen);
    }

    public void generateCode(CodeGen gen) {
        gen.emit("nop // Pas généré : " + this);
    }

    public boolean canBeAssigned() {
        return false;
    }
}
