package trad;

import java.io.PrintStream;
import java.util.Arrays;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import projetIUP.Lanceur;
import trad.antlr.LeacLexer;
import trad.antlr.LeacParser;
import trad.antlr.LeacParser.ProgramContext;
import trad.semant.visitor.AnalyseSemantVisitor;
import trad.syntax.ast.ASTConstructVisitor;
import trad.syntax.ast.CodeGen;
import trad.syntax.ast.impl.Program;

public class Main {
    public static void main(String[] args) throws Exception {
        ArgumentParser p = ArgumentParsers.newArgumentParser("leacc")
                .defaultHelp(true)
                .description("Compilateur Leac");

        p.addArgument("-a", "--ast").action(Arguments.storeTrue())
                .help("Affiche l'AST dans une fenêtre.");

        p.addArgument("-s", "--syntax-tree").action(Arguments.storeTrue())
                .help("Affiche l'arbre syntaxique dans une fenêtre.");

        p.addArgument("-t", "--symbols-table").action(Arguments.storeTrue())
                .help("Affiche la table des symboles");

        p.addArgument("-l", "--assemble").action(Arguments.storeTrue())
                .help("Assemble le programme compilé");

        p.addArgument("-x", "--execute").action(Arguments.storeTrue())
                .help("Exécuter le programme compilé (implique -l)");

        p.addArgument("-p", "--print").action(Arguments.storeTrue())
                .help("Affiche les sources du programme compilé");

        p.addArgument("source")
                .help("Fichier .leac source");

        p.addArgument("destination").nargs("?")
                .setDefault("out.src")
                .help("Fichier .src de destination");

        Namespace res = p.parseArgsOrFail(args);

        ANTLRInputStream inp = new ANTLRFileStream(res.getString("source"));
        LeacLexer lexer = new LeacLexer(inp);

        CommonTokenStream ts = new CommonTokenStream(lexer);
        LeacParser parser = new LeacParser(ts);

        ProgramContext result = parser.program();

        if (res.getBoolean("syntax_tree"))
            new MyTreeViewer(Arrays.asList(parser.getRuleNames()), result).setUseCurvedEdges(true);

        if (parser.getNumberOfSyntaxErrors() != 0) {
            return;
        }

        ASTConstructVisitor visitor = new ASTConstructVisitor();
        visitor.visit(result);

        Program program = visitor.getProgram();

        if (res.getBoolean("ast")) {
            MyTreeViewer t = new MyTreeViewer(Arrays.<String> asList(), program);
            t.setUseCurvedEdges(true);
        }

        AnalyseSemantVisitor semant = new AnalyseSemantVisitor();
        semant.visit(program);

        if (res.getBoolean("symbols_table")) {
            System.out.println(semant.getTds().toString());
        }

        if (semant.errorOccured()) {
            return;
        }

        CodeGen gen = new CodeGen();
        gen.setCurrentBlock(semant.getTds().getTds());
        program.generateCode(gen);

        if (res.getBoolean("print")) {
            System.out.println(gen.toString());
        }

        String outfile = res.getString("destination");
        PrintStream os = new PrintStream(outfile);
        os.print(gen.toString());
        os.close();

        if (res.getBoolean("assemble") || res.getBoolean("execute")) {
            Lanceur.main(new String[] { "-ass", outfile });

            if (res.getBoolean("execute")) {
                Lanceur.main(new String[] { "-batch", outfile.replace(".src", ".iup") });
            }
        }
    }
}
