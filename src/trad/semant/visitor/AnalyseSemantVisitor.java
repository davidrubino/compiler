package trad.semant.visitor;

import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.Token;

import trad.semant.tds.TDSEntry;
import trad.semant.tds.TdsConstruct;
import trad.semant.tds.TDSEntry.Kind;
import trad.syntax.ast.Expression;
import trad.syntax.ast.Node;
import trad.syntax.ast.Type.TypeEnum;
import trad.syntax.ast.impl.*;
import trad.syntax.ast.impl.UnaryOperator.Type;

public class AnalyseSemantVisitor extends ASTBaseVisitor {
    private TdsConstruct tds = null;
    private boolean error = false;
    private TypeEnum returnType;
    private FunctionDecl currentFunction = null;

    public AnalyseSemantVisitor() {

    }

    public TdsConstruct getTds() {
        return tds;
    }

    @Override
    public void visitVariableDecl(VariableDecl ctx) {
        if (ctx.getType().getType() == TypeEnum.VOID) {
            printError(ctx, "void est invalide pour une variable");
        } else if (!tds.addEntry(ctx.getId(), ctx.getType(), Kind.VARIABLE)) {
            printError(ctx.getId(), "variable en conflit avec un identificateur existant :  " + ctx.getId().getName());
        }
        visitChildren(ctx);
    }

    /**
     * Visite l'AST et vérifie si une fonction du même nom a déjà été déclarée
     * et vérifie si les fonctions avec type ont bien un return !
     */
    @Override
    public void visitFunctionDecl(FunctionDecl ctx) {
        if (!tds.addEntry(ctx.getIdentifier(), ctx.getType(), Kind.FUNCTION))
            printError(ctx.getIdentifier(), "nom de fonction en conflit avec un identificateur existant : " + ctx.getIdentifier().getName());
        tds.addBlock(ctx);
        currentFunction = ctx;
        ctx.setTDSBlock(tds.getTds());
        visit(ctx.getArgumentList());
        visit(ctx.getVariableDeclarationList());
        returnType = TypeEnum.VOID;
        visit(ctx.getInstructionList());
        tds.backBlock();
        currentFunction = null;
        if (!testTypes(ctx.getIdentifier().getEvalType(), returnType)) {
            printError(ctx, "fonction " + ctx.getIdentifier() + ": la valeur de retour doit être de type " + ctx.getIdentifier().getEvalType());
        }
    }

    @Override
    public void visitFunctionCall(FunctionCall ctx) {
        visit(ctx.getFunction());
        visitChildren(ctx);
        FunctionDecl decl = tds.getTds().findFunction(ctx.getFunction());
        if (decl == null)
            return;

        Iterator<Argument> argIt = decl.getArgumentList().iterator();
        Iterator<Expression> exprIt = ctx.getExprList().iterator();

        int i = 1;
        while (argIt.hasNext() && exprIt.hasNext()) {
            Argument arg = argIt.next();
            Expression expr = exprIt.next();

            if (!testTypes(arg.getType().getType(), expr.getEvalType())) {
                printError(expr, "l'argument " + i + " de la fonction n'a pas le type attendu");
            } else if (arg.isRef() && !expr.canBeAssigned()) {
                printError(expr, "passage par référence impossible car la partie gauche ne peut pas être assignée");
            }

            i++;
        }

        while (argIt.hasNext() || exprIt.hasNext()) {
            if (argIt.hasNext()) {
                printError(ctx.getEndToken(), "argument " + i + " manquant");
                argIt.next();
            } else {
                printError(exprIt.next(), "argument " + i + " superflu");
            }
            i++;
        }
    }

    @Override
    public void visitReturnInstr(ReturnInstr ctx) {
        visitChildren(ctx);

        TypeEnum expectType = currentFunction == null ? TypeEnum.VOID : currentFunction.getIdentifier().getEvalType();

        if (!testTypes(ctx.getExpr().getEvalType(), expectType)) {
            printError(ctx, "l'expression retournée ne correspond pas au type de retour de la fonction : " + expectType);
        } else {
            returnType = ctx.getExpr().getEvalType();
        }
    }

    @Override
    public void visitArgument(Argument ctx) {
        if (ctx.getType().getType() == TypeEnum.VOID) {
            printError(ctx, "void est invalide pour une argument");
        } else if (!tds.addEntry(ctx.getId(), ctx.getType(), Kind.ARGUMENT, ctx.isRef())) {
            printError(ctx.getId(), "nom d'argument en conflit avec un identificateur existant : " + ctx.getId().getName());
        }

        visitChildren(ctx);
    }

    /**
     * Visite l'AST, et vérifie si les identificateur utilisés sont déclarés
     * avant !
     */
    @Override
    public void visitIdentifier(Identifier ctx) {
        TDSEntry entry = tds.searchTds(ctx);
        if (entry == null) {
            printError(ctx, "identificateur non déclaré : " + ctx.getName());
        } else {
            ctx.setTdsEntry(entry);
        }
    }

    @Override
    public void visitProgram(Program ctx) {
        this.tds = new TdsConstruct(ctx);
        visit(ctx.getVariableDeclList());
        visit(ctx.getFunctionDeclList());
        visit(ctx.getInstructionList());
    }

    @Override
    public void visitBinaryExpr(BinaryExpr ctx) {
        visitChildren(ctx);
        if (ctx.getEvalType() == TypeEnum.UNKNOWN)
            return;

        switch (ctx.getOperatorType()) {
        case OR:
        case AND:
            if (!testTypes(ctx.getExpr1().getEvalType(), TypeEnum.BOOLEAN)) {
                printError(ctx, "opérateur \"" + ctx.getOperator() + "\" : l'opérande de gauche n'est pas de type booléen");
            }
            if (!testTypes(ctx.getExpr2().getEvalType(), TypeEnum.BOOLEAN)) {
                printError(ctx, "opérateur \"" + ctx.getOperator() + "\" : l'opérande de droite n'est pas de type booléen");
            }
            break;

        case GREATER_OR_EQUAL_THAN:
        case GREATER_THAN:
        case LESSER_OR_EQUAL_THAN:
        case LESSER_THAN:
        case MINUS:
        case MULT:
        case PLUS:
        case DIV:
        case EXPONENT:
            if (!testTypes(ctx.getExpr1().getEvalType(), TypeEnum.INT)) {
                printError(ctx.getExpr1(), "opérateur \"" + ctx.getOperator() + "\" : l'opérande de gauche n'est pas de type entier");
            }
            if (!testTypes(ctx.getExpr2().getEvalType(), TypeEnum.INT)) {
                printError(ctx.getExpr2(), "opérateur \"" + ctx.getOperator() + "\" : l'opérande de droite n'est pas de type entier");
            }
            break;

        case EQUALS:
        case DIFFERENT:
            if (!testTypes(ctx.getExpr1().getEvalType(), ctx.getExpr2().getEvalType())) {
                printError(ctx, "opérateur \"" + ctx.getOperator() + "\" : types différents à gauche et à droite");
            }
            break;
        }
    }

    public void visitUnaryExpr(UnaryExpr ctx) {
        visitChildren(ctx);

        if (ctx.getOperator() == Type.UNARY_MINUS) {
            if (!testTypes(ctx.getExpr().getEvalType(), TypeEnum.INT)) {
                printError(ctx, "opérateur \"-\" appliqué sur un type autre qu'entier");
            }
        } else if (ctx.getOperator() == Type.NOT) {
            if (!testTypes(ctx.getExpr().getEvalType(), TypeEnum.BOOLEAN) &&
                    !testTypes(ctx.getExpr().getEvalType(), TypeEnum.INT)) {
                printError(ctx, "opérateur \"not\" appliqué sur un type autre que booléen ou entier");
            }
        }
    }

    @Override
    public void visitAffectInstr(AffectInstr ctx) {
        visitChildren(ctx);
        if (!ctx.getLval().canBeAssigned()) {
            printError(ctx, "assignation impossible");
        } else if (!testTypes(ctx.getLval().getEvalType(), ctx.getExpr().getEvalType())) {
            printError(ctx, "type différent à gauche et à droite de l'affectation");
        }
    }

    @Override
    public void visitWhileInstr(WhileInstr ctx) {
        visitChildren(ctx);
        if (!testTypes(ctx.getExpr().getEvalType(), TypeEnum.BOOLEAN)) {
            printError(ctx, "condition de type non booléen");
        }
    }

    @Override
    public void visitIfInstr(IfInstr ctx) {
        visitChildren(ctx);
        if (!testTypes(ctx.getExpr().getEvalType(), TypeEnum.BOOLEAN)) {
            printError(ctx, "condition de type non booléen");
        }
    }

    private boolean testTypes(TypeEnum... types) {
        TypeEnum first = types[0];
        for (TypeEnum type : types) {
            if (type == TypeEnum.UNKNOWN) {
                return true;
            }

            if (type != first) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visitArraySubscript(ArraySubscript ctx) {
        visitChildren(ctx);
        if (ctx.getIdentifier().getTypeNode().getType() != TypeEnum.ARRAY) {
            printError(ctx, "variable non indexable (n'est pas un tableau)");
            return;
        }
        if (ctx.getArrayType() == null)
            return;

        List<Expression> exprs = ctx.getBounds();
        List<BoundRange> bounds = ctx.getArrayType().getBounds().getElements();

        Iterator<BoundRange> boundIt = bounds.iterator();
        Iterator<Expression> exprIt = exprs.iterator();

        int i = 1;
        while (boundIt.hasNext() && exprIt.hasNext()) {
            Expression expr = exprIt.next();
            boundIt.next();

            if (!testTypes(expr.getEvalType(), TypeEnum.INT)) {
                printError(expr, "l'indice de tableau " + i + " n'est pas de type INT");
            }
            i++;
        }

        while (boundIt.hasNext() || exprIt.hasNext()) {
            if (boundIt.hasNext()) {
                printError(ctx.getEndToken(), "indice " + i + " manquant");
                boundIt.next();
            } else {
                printError(exprIt.next(), "indice " + i + " superflu");
            }
            i++;
        }
    }

    @Override
    public void visitBoundRange(BoundRange ctx) {
        visitChildren(ctx);

        if (ctx.getFrom() >= ctx.getTo()) {
            printError(ctx, "la deuxième borne doit être supérieure à la première");
        }
    }

    private void printError(Node<?> node, String text) {
        printError(node.getStartToken(), text);
    }

    private void printError(Token token, String text) {
        error = true;
        System.out.println(token.getLine() + ":" + (token.getCharPositionInLine() + 1) + "\terreur: " + text);
    }

    public boolean errorOccured() {
        return error;
    }
}
