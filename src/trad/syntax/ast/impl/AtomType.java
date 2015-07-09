package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.Type;

public class AtomType extends Type {

    private Token symbol;

    public AtomType(Token symbol) {
        super(symbol, symbol);
        this.symbol = symbol;
    }

    public TypeEnum getType() {
        return TypeEnum.find(symbol.getType());
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public Object getPayload() {
        return symbol.getText();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitAtomType(this);
    }
}
