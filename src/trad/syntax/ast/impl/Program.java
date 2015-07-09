package trad.syntax.ast.impl;

import trad.semant.tds.TDSBlock;
import trad.semant.visitor.ASTVisitor;

import org.antlr.v4.runtime.Token;

import trad.syntax.ast.CodeGen;
import trad.syntax.ast.Declaration;
import trad.syntax.ast.ListNode;

public class Program extends Declaration {

    private Identifier name;
    private InstructionList instruction;
    private ListNode<VariableDecl> variableDeclList;
    private ListNode<FunctionDecl> functionDeclList;
    private TDSBlock tds;

    public Program(Token startToken, Token endToken, Identifier name, InstructionList instr,
            ListNode<VariableDecl> varDeclList,
            ListNode<FunctionDecl> funcDeclList) {
        super(startToken, endToken, name, instr, varDeclList, funcDeclList);

        this.name = name;
        this.instruction = instr;
        this.variableDeclList = varDeclList;
        this.functionDeclList = funcDeclList;
    }

    public Identifier getIdentifier() {
        return name;
    }

    public InstructionList getInstructionList() {
        return instruction;
    }

    public ListNode<VariableDecl> getVariableDeclList() {
        return variableDeclList;
    }

    public ListNode<FunctionDecl> getFunctionDeclList() {
        return functionDeclList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitProgram(this);
    }

    @Override
    public void generateCode(CodeGen gen) {
        // prologue
        gen.emitGlobal(
                  "EXIT_EXC equ 64 // n° d'exception de EXIT\n"
                + "READ_EXC equ 65 // n° d'exception de READ (lit 1 ligne)\n"
                + "WRITE_EXC equ 66 // n° d'exception de WRITE (affiche 1 ligne)\n"
                + "STACK_ADRS equ 0x1000 // base de pile en 1000h (par exemple)\n"
                + "// ces alias permettront de changer les réels registres utilisés\n"
                + "sp equ r15 // alias pour r15, pointeur de pile\n"
                + "wr equ r14 // work register (registre de travail)\n"
                + "bp equ r13 // frame base pointer (pointage environnement)\n"
                + "           // r12, r11 réservés\n"
                + "           // r0 pour résultat de fonction\n"
                + "           // r1 ... r10 disponibles\n"
                + "org STACK_ADRS + 2\n"
                + "start main_\n"
                + "stackbase STACK_ADRS\n"
                + "\n"
                + "str_newline byte 13\n"
                + "            byte 10\n"
                + "            byte 0\n"
                + "str_true    string \"true\"\n"
                + "str_false   string \"false\"\n");

        gen.setLabel("main_");
        gen.beginScope("main");
        gen.emitHeader("ldw sp, #STACK_ADRS // charge sp avec stack_adrs");
        gen.emitHeader("ldq 0, bp // charge bp avec 0");
        gen.genProlog();
        gen.genVarList();

        instruction.generateCode(gen);

        gen.genEpilog();
        gen.emitFooter("trp #EXIT_EXC // arrêt du programme");
        gen.emitFooter("jea @main_ // saute à main_ si redemande exécution");
        gen.endScope("main");

        functionDeclList.generateCode(gen);
    }

    @Override
    public TDSBlock getTDSBlock() {
        return tds;
    }
    
    public void setTDSBlock(TDSBlock tds) {
        this.tds = tds;
    }
}
