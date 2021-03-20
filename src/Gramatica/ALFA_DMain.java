package Gramatica;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.stringtemplate.v4.*;
import java.io.*;

public class ALFA_DMain {
   public static void main(String[] args) throws Exception {
	
      if(args.length!=1){
          System.err.println("Insira o ficheiro a processar!");
	  System.exit(1);
      }

      // create a CharStream that reads from standard input:
      CharStream input = CharStreams.fromFileName(args[0]);
      // create a lexer that feeds off of input CharStream:
      ALFA_DLexer lexer = new ALFA_DLexer(input);
      // create a buffer of tokens pulled from the lexer:
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      // create a parser that feeds off the tokens buffer:
      ALFA_DParser parser = new ALFA_DParser(tokens);
      // replace error listener:
      //parser.removeErrorListeners(); // remove ConsoleErrorListener
      //parser.addErrorListener(new ErrorHandlingListener());
      // begin parsing at main rule:
      ParseTree tree = parser.main();
      if (parser.getNumberOfSyntaxErrors() == 0) {
         // print LISP-style tree:
         // System.out.println(tree.toStringTree(parser));
         SemanticaVisit semantica = new SemanticaVisit();
         STGGenerator compiler = new STGGenerator();
         semantica.visit(tree);

         ST code = compiler.visit(tree);
         String filename = "Output.java";
         try
         {
            code.add("name", "Output");
            PrintWriter pw = new PrintWriter(new File(filename));
            pw.print(code.render());
            pw.close();
         }
         catch(IOException e)
         {
            System.err.println("ERROR: Impossível escrever em "+filename);
            System.exit(3);
         }

      }
   }
}
