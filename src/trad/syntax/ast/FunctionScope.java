package trad.syntax.ast;

public class FunctionScope {
    // registres utilisables

    private static final int MIN_REGISTER = 1;
    private static final int MAX_REGISTER = 10;
    private static final int AVAILABLE_REGISTERS = MAX_REGISTER - MIN_REGISTER + 1;

    int usedRegisters = 0;
    int modifiedRegisters = 0;
    private StringBuffer body = new StringBuffer();
    private StringBuffer header = new StringBuffer();
    private StringBuffer footer = new StringBuffer();

    public int getModifiedRegisters() {
        return modifiedRegisters;
    }

    public StringBuffer getBody() {
        return body;
    }

    public StringBuffer getHeader() {
        return header;
    }

    public StringBuffer getFooter() {
        return footer;
    }

    public String getCode() {
        return header.toString() + body.toString() + footer.toString();
    }

    public String allocateRegister() {
        if (usedRegisters >= AVAILABLE_REGISTERS)
            throw new RuntimeException("Plus de registres disponibles");

        modifiedRegisters = Math.max(modifiedRegisters, usedRegisters + 1);
        return "r" + (MIN_REGISTER + usedRegisters++);
    }

    public String getRegister(int n) {
        int reg = usedRegisters + MIN_REGISTER - n - 1;

        if (reg < MIN_REGISTER || reg > MAX_REGISTER) {
            throw new RuntimeException("Tentative d'accès à un registre invalide");
        }
        return "r" + reg;
    }

    public void freeRegisters(int count) {
        if (usedRegisters < count)
            throw new RuntimeException("Trop de registres dépilés");

        usedRegisters -= count;
    }

    public void saveRegisters(CodeGen codeGen) {
        for (int i = MIN_REGISTER; i < MIN_REGISTER + modifiedRegisters; i++) {
            String s = "stw r" + i + ", -(sp)";
            codeGen.emitHeader(s);
        }
    }
    
    public void restoreRegisters(CodeGen codeGen) {
        for (int i = MIN_REGISTER + modifiedRegisters - 1; i >= MIN_REGISTER; i--) {
            String s = "ldw r" + i + ", (sp)+";
            codeGen.emitFooter(s);
        }
    }
}
