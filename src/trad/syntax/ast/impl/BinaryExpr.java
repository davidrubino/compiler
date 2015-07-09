package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Type.TypeEnum;

public class BinaryExpr extends Expression {

    private BinaryOperator op;
    private Expression expr1, expr2;

    /*
     * | expr ('*' | '/') expr
       | expr ('+' | '-' ) expr
       | expr ('<' | '<=' | '>' | '>=' | '==' | '!=') expr
       | expr 'and' expr
       | expr 'or' expr
     */

    public BinaryExpr(Token startToken, Token endToken, BinaryOperator op, Expression e1, Expression e2) {
        super(startToken, endToken, e1, e2);

        this.op = op;
        this.expr1 = e1;
        this.expr2 = e2;
    }

    public BinaryOperator.Type getOperatorType() {
        return op.getType();
    }

    public BinaryOperator getOperator() {
        return op;
    }

    public Expression getExpr1() {
        return expr1;
    }

    public Expression getExpr2() {
        return expr2;
    }

    @Override
    public Object getPayload() {
        return op;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitBinaryExpr(this);
    }

    private void generateCondition(CodeGen gen, String cond) {
        String dest = gen.getRegister(0);

        String op1 = gen.allocateRegister();
        expr1.generateCode(gen);

        String op2 = gen.allocateRegister();
        expr2.generateCode(gen);

        gen.emit("// " + op1 + " " + cond + " " + op2);

        gen.emit("ldq -1, " + dest);
        gen.emit("cmp " + op1 + ", " + op2);
        gen.emit("b" + cond + " 2");
        gen.emit("ldq 0, " + dest);

        gen.freeRegisters(2);
    }

    private void generateValue(CodeGen gen, String op) {
        gen.emit("// " + op + " (" + expr1 + ", " + expr2 + ")");

        String op1 = gen.getRegister(0);
        expr1.generateCode(gen);

        String op2 = gen.allocateRegister();
        expr2.generateCode(gen);

        gen.emit(op + " " + op1 + ", " + op2 + ", " + op1);

        gen.freeRegisters(1);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter(op.getType().toString());
        BinaryOperator.Type type = op.getType();

        switch (type) {
        case AND:
            generateValue(gen, "and");
            break;

        case OR:
            generateValue(gen, "or");
            break;

        case PLUS:
            generateValue(gen, "add");
            break;

        case MINUS:
            generateValue(gen, "sub");
            break;

        case MULT:
            generateValue(gen, "mul");
            break;

        case DIV:
            generateValue(gen, "div");
            break;

        case EQUALS:
            generateCondition(gen, "eq");
            break;

        case DIFFERENT:
            generateCondition(gen, "ne");
            break;

        case LESSER_THAN:
            generateCondition(gen, "lw");
            break;

        case LESSER_OR_EQUAL_THAN:
            generateCondition(gen, "le");
            break;

        case GREATER_THAN:
            generateCondition(gen, "gt");
            break;

        case GREATER_OR_EQUAL_THAN:
            generateCondition(gen, "ge");
            break;

        case EXPONENT:
            gen.useExtract(CodeExtracts.POW);

            String reg = gen.getRegister(0);

            expr2.generateCode(gen);
            gen.emit("stw " + reg + ", -(sp)");

            expr1.generateCode(gen);
            gen.emit("stw " + reg + ", -(sp)");

            gen.emit("jsr @pow_");
            gen.emit("adq 4, sp");
            gen.emit("ldw " + reg + ", r0");

            break;
        }

        gen.leave(op.getType().toString());
    }

    @Override
    public TypeEnum getEvalType() {
        if (expr1.getEvalType() == TypeEnum.UNKNOWN || expr2.getEvalType() == TypeEnum.UNKNOWN) {
            return TypeEnum.UNKNOWN;
        }

        switch (op.getType()) {
        case OR:
        case AND:
        case GREATER_OR_EQUAL_THAN:
        case GREATER_THAN:
        case LESSER_OR_EQUAL_THAN:
        case LESSER_THAN:
        case DIFFERENT:
        case EQUALS:
            return TypeEnum.BOOLEAN;
        case MINUS:
        case MULT:
        case PLUS:
        case DIV:
        case EXPONENT:
            return TypeEnum.INT;
        }
        return TypeEnum.UNKNOWN;
    }
}
