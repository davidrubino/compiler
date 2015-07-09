package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.antlr.LeacLexer;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Type.TypeEnum;
import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.Instruction;

public class WriteInstr extends Instruction {

    private Expression expr;
    private boolean newLine;

    public WriteInstr(Token startToken, Token endToken, Expression expr) {
        super(startToken, endToken, expr);

        this.expr = expr;
        this.newLine = startToken.getType() == LeacLexer.WRITELN;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitWriteInstr(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        gen.enter("write");

        if (expr != null) {
            String reg = gen.allocateRegister();
            expr.generateCode(gen);

            if (expr.getEvalType() == TypeEnum.BOOLEAN) {
                gen.emit("ldw r0, #str_true");
                gen.emit("tst " + reg);
                gen.emit("bne 2");
                gen.emit("ldw r0, #str_false");
            } else if (expr.getEvalType() == TypeEnum.INT) {
                gen.emit("adq -8, sp       // réserve de la place pour stocker la chaine");
                gen.emit("stw sp, (sp)-4   // paramètre p : adresse de la chaine");
                gen.emit("stw " + reg + ", (sp)-6   // paramètre i : nombre à convertir");
                gen.emit("ldq 10, wr");
                gen.emit("stw wr, (sp)-2   // paramètre b : base");
                gen.emit("adq -6, sp");
                gen.useExtract(CodeExtracts.ITOA);
                gen.emit("jsr @itoa_");
                gen.emit("adq 14, sp");
            } else if (expr.getEvalType() == TypeEnum.STRING) {
                gen.emit("ldw r0, " + reg);
            }

            gen.emit("ldw wr, #WRITE_EXC");
            gen.emit("trp wr");

            gen.freeRegisters(1);
        }

        if (newLine) {
            gen.emit("ldw wr, #WRITE_EXC");
            gen.emit("ldw r0, #str_newline");
            gen.emit("trp wr");
        }

        gen.leave("write");
    }
}
