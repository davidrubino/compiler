package trad.semant.visitor;

import org.antlr.v4.runtime.misc.NotNull;

import trad.syntax.ast.Constant;
import trad.syntax.ast.ListNode;
import trad.syntax.ast.Node;
import trad.syntax.ast.impl.*;

public class ASTBaseVisitor implements ASTVisitor {

    @Override
    public void visitAffectInstr(AffectInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitArgument(Argument ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitArraySubscript(ArraySubscript ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitArrayType(ArrayType ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitAtomType(AtomType ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitBinaryExpr(BinaryExpr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitBoundRange(BoundRange ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitConstant(Constant<?> ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitFunctionCall(FunctionCall ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitFunctionDecl(FunctionDecl ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitIdentifier(Identifier ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitIfInstr(IfInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitInstructionList(InstructionList ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitLvalue(LValue ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitProgram(Program ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitReadInstr(ReadInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitReturnInstr(ReturnInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitUnaryExpr(UnaryExpr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitVariableDecl(VariableDecl ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitWhileInstr(WhileInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitWriteInstr(WriteInstr ctx) {
        visitChildren(ctx);
    }

    @Override
    public void visitListNode(ListNode<?> ctx) {
        visitChildren(ctx);
    }

    public void visit(@NotNull Node<?> node) {
        node.accept(this);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation initializes the aggregate result to
     * {@link #defaultResult defaultResult()}. Before visiting each child, it
     * calls {@link #shouldVisitNextChild shouldVisitNextChild}; if the result
     * is {@code false} no more children are visited and the current aggregate
     * result is returned. After visiting a child, the aggregate result is
     * updated by calling {@link #aggregateResult aggregateResult} with the
     * previous aggregate result and the result of visiting the child.
     */
    public void visitChildren(@NotNull Node<?> ctx) {
        int n = ctx.getChildCount();
        for (int i = 0; i < n; i++) {

            Node<?> c = ctx.getChild(i);
            c.accept(this);
        }
    }
}
