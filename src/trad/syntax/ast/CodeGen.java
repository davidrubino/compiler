package trad.syntax.ast;

import java.util.ArrayList;
import java.util.HashSet;

import trad.semant.tds.TDSBlock;
import trad.semant.tds.TDSEntry;
import trad.semant.tds.TDSEntry.Kind;

public class CodeGen {

    private StringBuffer codeSection = new StringBuffer();
    private StringBuffer dataSection = new StringBuffer();
    private String label;
    private TDSBlock currentBlock;
    private ArrayList<String> labels = new ArrayList<>();
    private FunctionScope scope = new FunctionScope();
    private HashSet<String> usedExtracts = new HashSet<>();

    private int spacing = 0;

    public void genProlog() {
        emitHeader("stw bp, -(sp) // empile le contenu du registre BP");
        emitHeader("ldw bp, sp    // charge SP dans BP");
    }
    
    public void genVarList() {
        for (TDSEntry entry : currentBlock.getEntries()) {
            if (entry.getKind() == Kind.VARIABLE) {
                emitHeader("var_" + currentBlock.getBlockNumber() + "_" + entry.getIdentifier() + " EQU " + entry.getOffset()); 
            } else if (entry.getKind() == Kind.ARGUMENT) {
                emitHeader("var_" + currentBlock.getBlockNumber() + "_" + entry.getIdentifier() + " EQU " + (entry.getOffset() + 2 * scope.getModifiedRegisters()));
            }
        }
        if (getCurrentBlock().getLocalVariablesSize() != 0) {
            emitHeader("adq -" + getCurrentBlock().getLocalVariablesSize() + ", sp");
        }
    }

    public void genEpilog() {
        emitFooter("ldw sp, bp    // abandon infos locales");
        emitFooter("ldw bp, (sp)+ // charge bp avec ancien bp");
    }
    
    public void endScope(String block) {
        spacing--;
        emitFooter("// Exit: " + block);
        
        codeSection.append(scope.getCode());
    }

    public void beginScope(String block) {
        scope = new FunctionScope();
        emitHeader("// Enter: " + block);
        spacing++;
    }
    

    public void enter(String block) {
        emit("// Enter: " + block);
        spacing++;
    }

    public void leave(String block) {
        spacing--;
        emit("// Exit: " + block);
    }

    public void emit(String code, StringBuffer buff) {
        String spaces = "";
        for (int i = 0; i < spacing; i++)
            spaces += "    ";

        if (label != null && !code.startsWith("//")) {
            code = label + "  " + code;
            label = null;
        }

        buff.append(spaces + code + "\n");
    }

    public void emitGlobal(String code) {
        emit(code, codeSection);
    }

    public void emit(String code) {
        emit(code, scope.getBody());
    }

    public void emitHeader(String code) {
        emit(code, scope.getHeader());

    }

    public void emitFooter(String code) {
        emit(code, scope.getFooter());

    }

    public void addData(String label, String code) {
        dataSection.append(label + "  " + code + "\n");
    }

    public void setLabel(String label) {
        if (this.label != null) {
            emit("nop // Alias label");
        }

        this.label = label;
    }

    public String getUniqueLabel(String prefix) {
        int i = 0;
        String currentLabel;

        do {
            currentLabel = prefix + "_" + i;
            i++;
        } while (labels.contains(currentLabel));

        labels.add(currentLabel);
        return currentLabel;
    }

    public TDSBlock getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(TDSBlock currentBlock) {
        this.currentBlock = currentBlock;
    }

    public String allocateRegister() {
        return scope.allocateRegister();
    }

    public String getRegister(int n) {
        return scope.getRegister(n);
    }

    public void freeRegisters(int count) {
        scope.freeRegisters(count);
    }

    @Override
    public String toString() {
        return codeSection.toString() + dataSection.toString();
    }

    public void saveRegisters() {
        scope.saveRegisters(this);
    }
    
    public void restoreRegisters() {
        scope.restoreRegisters(this);
    }

    public void useExtract(String extract) {
        if (!usedExtracts.contains(extract)) {
            usedExtracts.add(extract);
            dataSection.insert(0, extract + "\n");
        }
    }
}