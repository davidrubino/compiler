package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Type.TypeEnum;

public class UnaryExpr extends Expression {

    private UnaryOperator op;
    private Expression expr;

    /*
     * ('-' | 'not') expr
     */

    public UnaryExpr(Token startToken, Token endToken, UnaryOperator op, Expression expr) {
        super(startToken, endToken, expr);

        this.op = op;
        this.expr = expr;
    }

    public UnaryOperator.Type getOperator() {
        return op.getType();
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public Object getPayload() {
        return op;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitUnaryExpr(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter(op.getType().toString());
        gen.emit("// " + op + " " + expr);
        String opcode;

        switch (op.getType()) {
        case UNARY_MINUS:
            opcode = "neg";
            break;
        case NOT:
            opcode = "not";
            break;
        default:
            opcode = "nop // Opérateur unaire non géré : " + op;
            break;
        }
        expr.generateCode(gen);
        gen.emit(opcode + " " + gen.getRegister(0) + ", " + gen.getRegister(0));
        gen.leave(op.getType().toString());
    }

    @Override
    public TypeEnum getEvalType() {
        return expr.getEvalType();
    }
}
