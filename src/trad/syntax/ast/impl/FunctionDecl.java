package trad.syntax.ast.impl;

import org.antlr.v4.runtime.Token;

import trad.semant.tds.TDSBlock;
import trad.semant.visitor.ASTVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Declaration;
import trad.syntax.ast.ListNode;
import trad.syntax.ast.Type;

public class FunctionDecl extends Declaration {

    private Type type;
    private Identifier id;
    private ListNode<VariableDecl> variableDeclarationList;
    private ListNode<Argument> argumentList;
    private InstructionList instructionList;
    private TDSBlock tds;

    public FunctionDecl(Token startToken, Token endToken, Type type,
            Identifier id, ListNode<VariableDecl> varDeclList,
            ListNode<Argument> argList, InstructionList instrList) {
        super(startToken, endToken, type, id, varDeclList, argList, instrList);

        this.type = type;
        this.id = id;
        this.variableDeclarationList = varDeclList;
        this.argumentList = argList;
        this.instructionList = instrList;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    public InstructionList getInstructionList() {
        return instructionList;
    }

    public void setInstructionList(InstructionList instructionList) {
        this.instructionList = instructionList;
    }

    public ListNode<VariableDecl> getVariableDeclarationList() {
        return variableDeclarationList;
    }

    public void setVariableDeclarationList(
            ListNode<VariableDecl> variableDeclarationList) {
        this.variableDeclarationList = variableDeclarationList;
    }

    public ListNode<Argument> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(ListNode<Argument> argumentList) {
        this.argumentList = argumentList;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitFunctionDecl(this);
    }

    @Override
    public TDSBlock getTDSBlock() {
        return tds;
    }

    public void setTDSBlock(TDSBlock tds) {
        this.tds = tds;
    }

    @Override
    public void generateCode(CodeGen gen) {
        TDSBlock previousBlock = gen.getCurrentBlock();
        gen.setCurrentBlock(tds);

        gen.beginScope("Fonction : " + id);

        instructionList.generateCode(gen);

        gen.setLabel("fun_" + id);
        gen.saveRegisters();
        gen.genProlog();
        gen.genVarList();

        gen.setLabel("fun_end_" + id);
        gen.genEpilog();
        gen.restoreRegisters();
        gen.emitFooter("rts");

        gen.endScope("Fonction : " + id);
        gen.setCurrentBlock(previousBlock);
    }
}
