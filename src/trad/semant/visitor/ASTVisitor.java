package trad.semant.visitor;

import trad.syntax.ast.*;
import trad.syntax.ast.impl.AffectInstr;
import trad.syntax.ast.impl.Argument;
import trad.syntax.ast.impl.ArraySubscript;
import trad.syntax.ast.impl.ArrayType;
import trad.syntax.ast.impl.AtomType;
import trad.syntax.ast.impl.BinaryExpr;
import trad.syntax.ast.impl.BoundRange;
import trad.syntax.ast.impl.FunctionCall;
import trad.syntax.ast.impl.FunctionDecl;
import trad.syntax.ast.impl.Identifier;
import trad.syntax.ast.impl.IfInstr;
import trad.syntax.ast.impl.InstructionList;
import trad.syntax.ast.impl.LValue;
import trad.syntax.ast.impl.Program;
import trad.syntax.ast.impl.ReadInstr;
import trad.syntax.ast.impl.ReturnInstr;
import trad.syntax.ast.impl.UnaryExpr;
import trad.syntax.ast.impl.VariableDecl;
import trad.syntax.ast.impl.WhileInstr;
import trad.syntax.ast.impl.WriteInstr;

public interface ASTVisitor {
    void visitListNode(ListNode<?> ctx);

    void visitAffectInstr(AffectInstr ctx);

    void visitArgument(Argument ctx);

    void visitArraySubscript(ArraySubscript ctx);

    void visitArrayType(ArrayType ctx);

    void visitAtomType(AtomType ctx);

    void visitBinaryExpr(BinaryExpr ctx);

    void visitBoundRange(BoundRange ctx);

    void visitConstant(Constant<?> ctx);

    void visitFunctionCall(FunctionCall ctx);

    void visitFunctionDecl(FunctionDecl ctx);

    void visitIdentifier(Identifier ctx);

    void visitIfInstr(IfInstr ctx);

    void visitInstructionList(InstructionList ctx);

    void visitLvalue(LValue ctx);

    void visitProgram(Program ctx);

    void visitReadInstr(ReadInstr ctx);

    void visitReturnInstr(ReturnInstr ctx);

    void visitUnaryExpr(UnaryExpr ctx);

    void visitVariableDecl(VariableDecl ctx);

    void visitWhileInstr(WhileInstr ctx);

    void visitWriteInstr(WriteInstr ctx);
}
