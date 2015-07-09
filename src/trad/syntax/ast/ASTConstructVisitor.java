package trad.syntax.ast;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;

import trad.antlr.LeacBaseVisitor;
import trad.antlr.LeacParser.*;
import trad.syntax.ast.impl.*;
import static trad.antlr.LeacParser.*;

public class ASTConstructVisitor extends LeacBaseVisitor<Tree> {
    private Program program;

    public Program getProgram() {
        return program;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tree visitProgram(ProgramContext ctx) {
        program = new Program(
                ctx.start, ctx.stop,
                (Identifier) visit(ctx.IDF()),
                (InstructionList) visit(ctx.instr()),
                (ListNode<VariableDecl>) visit(ctx.varDeclList()),
                (ListNode<FunctionDecl>) visit(ctx.funDeclList()));

        return program;
    }

    @SuppressWarnings("unchecked")
    private <T extends Node<?>> ListNode<T> makeListNode(List<? extends ParseTree> elems) {
        ListNode<T> result = new ListNode<T>();
        for (ParseTree ctx : elems) {
            Tree node = visit(ctx);

            if (node == null) {
                throw new RuntimeException("Null node from " + ctx.getClass().getSimpleName());
            }
            result.addChild((T) node);
        }
        return result;
    }

    @Override
    public Constant<?> visitCste(CsteContext ctx) {
        Token token = ((TerminalNode) ctx.getChild(0)).getSymbol();

        switch (token.getType()) {
        case INTCST:
            return new IntConstant(ctx.start, ctx.stop, Integer.valueOf(ctx.INTCST().getText()));

        case BOOLCST:
            return new BoolConstant(ctx.start, ctx.stop, Boolean.valueOf(ctx.BOOLCST().getText()));

        case STRCST:
            return new StringConstant(ctx.start, ctx.stop, ctx.STRCST().getText());

        default:
            return null;
        }
    }

    @Override
    public FunctionCall visitFunCallExpr(FunCallExprContext ctx) {
        ListNode<Expression> exprList = new ListNode<Expression>();

        if (ctx.exprList() != null) {
            exprList = visitExprList(ctx.exprList());
        }

        return new FunctionCall(
                ctx.start, ctx.stop,
                (Identifier) visit(ctx.IDF()),
                exprList);
    }

    @Override
    public InstructionList visitBlockInstr(BlockInstrContext ctx) {
        if (ctx.sequence() == null)
            return new InstructionList();
        else
            return visitSequence(ctx.sequence());
    }

    @Override
    public InstructionList visitSequence(SequenceContext ctx) {
        InstructionList instr = null;

        if (ctx.compInstr() != null)
            instr = (InstructionList) visit(ctx.compInstr());
        else if (ctx.singleInstr() != null)
            instr = (InstructionList) visit(ctx.singleInstr());

        if (ctx.sequence() != null)
            instr.addInstr((InstructionList) visit(ctx.sequence()));

        return instr;
    }

    @Override
    public InstructionList visitWhileInstr(WhileInstrContext ctx) {
        return new InstructionList(
                new WhileInstr(ctx.start, ctx.stop,
                        (Expression) visit(ctx.expr()),
                        (InstructionList) visit(ctx.instr())));
    }

    @Override
    public LValue visitLvalue(LvalueContext ctx) {
        if (ctx.exprList() != null)
            return new ArraySubscript(
                    ctx.start, ctx.stop,
                    (Identifier) visit(ctx.IDF()),
                    visitExprList(ctx.exprList()));
        else
            return (Identifier) visit(ctx.IDF());
    }

    @Override
    public Tree visitAffectInstr(AffectInstrContext ctx) {
        return new InstructionList(new AffectInstr(
                ctx.start, ctx.stop,
                visitLvalue(ctx.lvalue()),
                (Expression) visit(ctx.expr())));
    }

    @Override
    public Tree visitReturnInstr(ReturnInstrContext ctx) {
        return new InstructionList(new ReturnInstr(
                ctx.start, ctx.stop,
                (Expression) visit(ctx.expr())));
    }

    @Override
    public Tree visitReadInstr(ReadInstrContext ctx) {
        return new InstructionList(new ReadInstr(
                ctx.start, ctx.stop,
                visitLvalue(ctx.lvalue())));
    }

    @Override
    public Tree visitWriteInstr(WriteInstrContext ctx) {
        return new InstructionList(new WriteInstr(
                ctx.start, ctx.stop, ctx.expr() == null ? null : (Expression) visit(ctx.expr())));
    }

    @Override
    public ListNode<VariableDecl> visitVarDeclList(VarDeclListContext ctx) {
        ListNode<VariableDecl> list = new ListNode<>();

        for (VarDeclContext varDecl : ctx.varDecl()) {
            list.addChildren(visitVarDecl(varDecl).getElements());
        }
        return list;
    }

    @Override
    public ListNode<Expression> visitExprList(ExprListContext ctx) {
        return makeListNode(ctx.expr());
    }

    @Override
    public ListNode<FunctionDecl> visitFunDeclList(FunDeclListContext ctx) {
        return makeListNode(ctx.funDecl());
    }

    @Override
    public Tree visitInstr(InstrContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public InstructionList visitFunCallInstr(FunCallInstrContext ctx) {
        ListNode<Expression> exprList = new ListNode<Expression>();

        if (ctx.exprList() != null) {
            exprList = visitExprList(ctx.exprList());
        }

        return new InstructionList(new FunctionCall(
                ctx.start, ctx.stop,
                (Identifier) visit(ctx.IDF()),
                exprList));
    }

    @Override
    public BinaryExpr visitBinaryExpr(BinaryExprContext ctx) {
        return new BinaryExpr(
                ctx.start, ctx.stop,
                new BinaryOperator(ctx.op),
                (Expression) visit(ctx.expr(0)),
                (Expression) visit(ctx.expr(1)));
    }

    @Override
    public Tree visitUnaryExpr(UnaryExprContext ctx) {
        return new UnaryExpr(
                ctx.start, ctx.stop,
                new UnaryOperator(ctx.op),
                (Expression) visit(ctx.expr()));
    }

    @Override
    public Tree visitTerminal(TerminalNode node) {
        switch (node.getSymbol().getType()) {
        case IDF:
            return new Identifier(node.getSymbol());
        }
        return null;
    }

    @Override
    public InstructionList visitIfInstr(IfInstrContext ctx) {
        if (ctx.instrElse == null)
            return new InstructionList(new IfInstr(
                    ctx.start, ctx.stop,
                    (Expression) visit(ctx.expr()),
                    (InstructionList) visit(ctx.instrIf)));
        else
            return new InstructionList(new IfInstr(
                    ctx.start, ctx.stop,
                    (Expression) visit(ctx.expr()),
                    (InstructionList) visit(ctx.instrIf),
                    (InstructionList) visit(ctx.instrElse)));
    }

    @Override
    public Argument visitArg(ArgContext ctx) {
        return new Argument(
                ctx.start, ctx.stop,
                (Identifier) visit(ctx.IDF()),
                (Type) visit(ctx.typeName()),
                ctx.REF() != null);
    }

    @Override
    public ListNode<Argument> visitArgList(ArgListContext ctx) {
        return makeListNode(ctx.arg());
    }

    @Override
    public Tree visitSubExpr(SubExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Tree visitAtomType(AtomTypeContext ctx) {
        return new AtomType(
                ((TerminalNode) ctx.getChild(0)).getSymbol());
    }

    @Override
    public ArrayType visitArrayType(ArrayTypeContext ctx) {
        return new ArrayType(
                ctx.start, ctx.stop,
                visitRangeList(ctx.rangeList()),
                (AtomType) visit(ctx.atomType()));
    }

    @Override
    public ListNode<BoundRange> visitRangeList(RangeListContext ctx) {
        return makeListNode(ctx.boundRange());
    }

    @Override
    public BoundRange visitBoundRange(BoundRangeContext ctx) {
        return new BoundRange(
                ctx.start, ctx.stop,
                visitBound(ctx.bound(0)),
                visitBound(ctx.bound(1)));
    }

    @Override
    public IntConstant visitBound(BoundContext ctx) {
        return new IntConstant(
                ctx.start, ctx.stop,
                Integer.valueOf(ctx.getText()));
    }

    @Override
    public ListNode<VariableDecl> visitVarDecl(VarDeclContext ctx) {
        ListNode<VariableDecl> decls = new ListNode<>();
        ListNode<Identifier> idfs = visitIdentList(ctx.identList());

        for (Identifier idf : idfs)
            decls.addChild(new VariableDecl(
                    ctx.start, ctx.stop,
                    (Type) visit(ctx.typeName()),
                    idf));

        return decls;
    }

    @Override
    public ListNode<Identifier> visitIdentList(IdentListContext ctx) {
        return makeListNode(ctx.IDF());
    }

    @Override
    public Tree visitFunDecl(FunDeclContext ctx) {
        return new FunctionDecl(
                ctx.start, ctx.stop,
                (Type) visit(ctx.atomType()),
                (Identifier) visit(ctx.IDF()),
                visitVarDeclList(ctx.varDeclList()),
                visitArgList(ctx.argList()),
                (InstructionList) visit(ctx.instr()));
    }

    @Override
    public Tree visitChildren(RuleNode node) {
        Tree result = defaultResult();
        int n = node.getChildCount();
        for (int i = 0; i < n; i++) {
            if (!shouldVisitNextChild(node, result)) {
                break;
            }

            ParseTree c = node.getChild(i);
            Tree childResult = visit(c);
            result = aggregateResult(result, childResult);
        }

        return result;
    }
}
