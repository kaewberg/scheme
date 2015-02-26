package se.pp.forsberg.scheme.builtinprocedures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import org.junit.Test;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.Scheme;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Value;

public class TestSymbols {
  
  public TestSymbols() throws SchemeException {
  }
  
  private Environment env = Environment.schemeReportEnvironment(7);
  
  protected Parser createParser(java.lang.String s) {
    return new Parser(new StringReader(s));
  }
  protected Value eval(Value value) throws SchemeException {
    return Scheme.eval(value, env);
    //return value.eval(env);
  }
  protected Value eval(java.lang.String source) throws SchemeException {
    return eval(createParser(source).read());
  }
  @Test
  public void testIsSymbol() throws SchemeException {
    assertEquals(eval("#t"), eval("(symbol? 'foo)"));
    assertEquals(eval("#t"), eval("(symbol? (car '(a b)))"));
    assertEquals(eval("#f"), eval("(symbol? \"bar\")"));
    assertEquals(eval("#t"), eval("(symbol? 'nil)"));
    assertEquals(eval("#f"), eval("(symbol? '())"));
    assertEquals(eval("#f"), eval("(symbol? #f)"));
  }
  @Test
  public void testSymbolEq() throws SchemeException {
    assertEquals(eval("#t"), eval("(symbol=? 'foo 'foo 'foo)"));
    assertEquals(eval("#f"), eval("(symbol=? 'foo 'foo 'bar)"));
    assertEquals(eval("#f"), eval("(symbol=? 'foo 'foo \"foo\")"));
  }
  @Test
  public void testSymbolToString() throws SchemeException {
    assertEquals(eval("\"flying-fish\""), eval("(symbol->string 'flying-fish)"));
    assertEquals(eval("\"Martin\""), eval("(symbol->string 'Martin)"));
    assertEquals(eval("\"Malvina\""), eval("(symbol->string (string->symbol \"Malvina\"))"));
    Exception ex = null;
    try {
      eval("(string-set! (symbol->string 'test) 1 #\\y))");
    } catch (Exception x) {
      ex = x;
    }
    assertNotNull(ex);
  }
  @Test
  public void testStringToSymbol() throws SchemeException {
    assertEquals(eval("'mISSISSIppi"), eval("(string->symbol \"mISSISSIppi\")"));
    assertEquals(eval("#t"), eval("(eqv? 'bitBlt (string->symbol \"bitBlt\"))"));
    assertEquals(eval("#t"), eval("(eqv? 'LollyPop (string->symbol (symbol->string 'LollyPop)))"));
    assertEquals(eval("#t"), eval("(string=? \"K. Harper, M.D.\" (symbol->string (string->symbol \"K. Harper, M.D.\")))"));
  }
}
