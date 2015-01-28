package se.pp.forsberg.scheme.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

public class TestTestParser {

  protected TestParser createParser(java.lang.String s) {
    ANTLRInputStream stream = new ANTLRInputStream(s);
    TestLexer lexer = new TestLexer(stream);
    return new TestParser(new CommonTokenStream(lexer));
  }
  @Test
  public void testComment1() {
    TestParser parser = createParser("12/34");
    System.out.println(parser.thing().toStringTree(parser));
    parser = createParser("(12/34))");
    System.out.println(parser.thing().toStringTree(parser));
  }

}
