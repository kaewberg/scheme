package se.pp.forsberg.scheme;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;

public class Scheme {
  
  private void repl() throws InstantiationException, IllegalAccessException, SchemeException  {
    repl(new BufferedReader(new InputStreamReader(System.in)), System.out);
  }
  private void repl(Reader in, PrintStream out) throws InstantiationException, IllegalAccessException, SchemeException {
   // Parser parser = new Parser(in);
    Port port = Port.STDIO;
    
    out.println("Scheme r7rs");
    
    Environment env = Environment.interactionEnvironment();
    while (true) {
      Value value;
      try {
        out.print("> ");
        Value v = port.read();
        if (Environment.isImport(v)) {
           env.importLibrary(v);
           value = Value.UNSPECIFIED;
        } else {
          value = eval(v, env);
        }
      } catch (SchemeException x) {
        value = x.getError();
        if (value.isError()) {
          Throwable t = ((Error)value).getThrowable();
          if (t != null) t.printStackTrace(out);
        }
      }
      out.println(value);
    }
  }
  
  private void runProgram(String filename) throws FileNotFoundException, SchemeException {
    Parser parser = new Parser(new FileReader(filename));
    Environment env = Environment.schemeReportEnvironment(7);
    
    Value v = parser.read();
    while (Environment.isImport(v)) {
      env.importLibrary(v);
      v = parser.read();
    }
    
    while (!v.isEof()) {
      Value value;
      try {
        value = eval(v, env);
      } catch (SchemeException x) {
        value = x.getError();
        if (value.isError()) {
          Throwable t = ((Error)value).getThrowable();
          if (t != null) t.printStackTrace();
        }
        System.out.println(x.getError());
        System.out.println("Aborting");
        return;
      }
      System.out.println(value);
      v = parser.read();
    }
  }
  

//  private void evalLoop() throws IOException  {
//    evalLoop(new UnbufferedCharStream(System.in), System.out);
//  }
//  private void evalLoop(CharStream in, PrintStream out) throws IOException {
//
//    //ANTLRInputStream stream = new ANTLRInputStream(in);
//    Scheme2Lexer lexer = new Scheme2Lexer(in);
//    Scheme2Parser parser = new Scheme2Parser(new UnbufferedTokenStream(lexer));
//    
//    Environment env = new Environment();
//    while (true) {
//      Value value;
//      try {
//        value = parser.datum().value;
//      } catch (SchemeException x) {
//        value = x.getError();
//      }
//      out.println(value.eval(env));
//    }
//  }
  

  public static String[] commandLine;
  public static void main(String[] arguments) {
    try {
      commandLine = arguments;
      Scheme scheme = new Scheme();
      if (arguments.length > 0) {
        scheme.runProgram(arguments[0]);
      } else {
        scheme.repl();
      }
    } catch (Exception x) {
      x.printStackTrace();
    }
  }
  private static Evaluator evaluator = new Evaluator();
  public static Value eval(Value expression, Environment env) throws SchemeException {
    return evaluator.eval(expression, env);
    // return expression.eval(env);
  }
}
