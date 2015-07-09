package trad.syntax.ast.impl;

import java.util.List;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Instruction;
import trad.syntax.ast.ListNode;
import trad.syntax.ast.Node;
import trad.syntax.ast.Type.TypeEnum;

public class FunctionCall extends Instruction {
    private Identifier function;
    private ListNode<Expression> exprList;

    public FunctionCall(Token startToken, Token endToken,
            Identifier function, ListNode<Expression> exprList) {
        super(startToken, endToken);

        this.function = function;
        this.exprList = exprList;
    }

    public Identifier getFunction() {
        return function;
    }

    public ListNode<Expression> getExprList() {
        return exprList;
    }

    @Override
    public int getChildCount() {
        return exprList.getChildCount();
    }

    @Override
    public Node<?> getChild(int i) {
        return exprList.getChild(i);
    }

    @Override
    public Object getPayload() {
        return function.getName() + "()";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitFunctionCall(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        FunctionDecl decl = gen.getCurrentBlock().findFunction(function);
        String reg = gen.allocateRegister();
        List<Expression> l = exprList.getElements();
        List<Argument> argList = decl.getArgumentList().getElements();

        for (int i = l.size() - 1; i >= 0; i--) {
            Expression expr = l.get(i);
            expr.generateCode(gen, argList.get(i).isRef());
            gen.emit("stw  " + reg + ", -(sp)");
        }
        gen.freeRegisters(1);
        gen.emit("jsr @fun_" + function);

        gen.emit("adq " + decl.getTDSBlock().getArgumentsSize() + ", sp");

        if (decl.getType().getType() != TypeEnum.VOID) {
            gen.emit("ldw " + gen.getRegister(0) + ", r0");
        }
    }

    @Override
    public TypeEnum getEvalType() {
        return function.getEvalType();
    }
}
