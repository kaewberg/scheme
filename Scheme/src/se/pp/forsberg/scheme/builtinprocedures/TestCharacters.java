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

public class TestCharacters {
  
  public TestCharacters() throws SchemeException {
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
  public void testCharacterNames() throws SchemeException {
    assertEquals(eval("#\\x7"), eval("#\\alarm"));
    assertEquals(eval("#\\x8"), eval("#\\backspace"));
    assertEquals(eval("#\\x7f"), eval("#\\delete"));
    assertEquals(eval("#\\x1b"), eval("#\\escape"));
    assertEquals(eval("#\\xa"), eval("#\\newline"));
    assertEquals(eval("#\\x0"), eval("#\\null"));
    assertEquals(eval("#\\xd"), eval("#\\return"));
    assertEquals(eval("#\\x20"), eval("#\\space"));
    assertEquals(eval("#\\x9"), eval("#\\tab"));
  }
  @Test
  public void testCharacterEscapes() throws SchemeException {
    // Unicode lambda
    assertEquals(eval("#\\x03BB"), eval("#\\x03bb"));
    // Simple letter x
    assertEquals(eval("#\\x"), eval("#\\x"));
    // Literal whitespace
    assertEquals(eval("#\\space"), eval("#\\ "));
    assertEquals(eval("#\\tab"), eval("#\\\t"));
    assertEquals(eval("#\\newline"), eval("#\\\n"));
    assertEquals(eval("#\\return"), eval("#\\\r"));
  }
  @Test
  public void testIsChar() throws SchemeException {
    assertEquals(eval("#f"), eval("(char? 'foo)"));
    assertEquals(eval("#f"), eval("(char? (car '(a b)))"));
    assertEquals(eval("#f"), eval("(char? \"bar\")"));
    assertEquals(eval("#f"), eval("(char? 'nil)"));
    assertEquals(eval("#f"), eval("(char? '())"));
    assertEquals(eval("#f"), eval("(char? #f)"));
    assertEquals(eval("#t"), eval("(char? #\\null)"));
  }
  @Test
  public void testCharCompare() throws SchemeException {
//    assertEquals(eval("#t"), eval("(char=?)"));
//    assertEquals(eval("#t"), eval("(char=? #\\a)"));
//    assertEquals(eval("#t"), eval("(char=? #\\a #\\a)"));
//    assertEquals(eval("#t"), eval("(char=? #\\a #\\a #\\a)"));
//    assertEquals(eval("#f"), eval("(char=? #\\a #\\a #\\b)"));
//    Exception ex = null;
//    try {
//      eval("(char=? #\\a #\\a 17)");
//    } catch (Exception x) {
//      ex = x;
//    }
//    assertNotNull(ex);
//    assertEquals(eval("#t"), eval("(char<?)"));
//    assertEquals(eval("#t"), eval("(char<? #\\a)"));
//    assertEquals(eval("#t"), eval("(char<? #\\a #\\b)"));
//    assertEquals(eval("#t"), eval("(char<? #\\a #\\b #\\c)"));
//    assertEquals(eval("#f"), eval("(char<? #\\a #\\a)"));
//    assertEquals(eval("#f"), eval("(char<? #\\a #\\b #\\b)"));
//    ex = null;
//    try {
//      eval("(char<? #\\a #\\b 17)");
//    } catch (Exception x) {
//      ex = x;
//    }
//    assertNotNull(ex);
//    assertEquals(eval("#t"), eval("(char<=?)"));
//    assertEquals(eval("#t"), eval("(char<=? #\\a)"));
//    assertEquals(eval("#t"), eval("(char<=? #\\a #\\b)"));
//    assertEquals(eval("#t"), eval("(char<=? #\\a #\\b #\\c)"));
//    assertEquals(eval("#t"), eval("(char<=? #\\a #\\a)"));
//    assertEquals(eval("#t"), eval("(char<=? #\\a #\\b #\\b)"));
//    assertEquals(eval("#f"), eval("(char<=? #\\a #\\b #\\a)"));
//    ex = null;
//    try {
//      eval("(char<=? #\\a #\\b 17)");
//    } catch (Exception x) {
//      ex = x;
//    }
//    assertNotNull(ex);
//    assertEquals(eval("#t"), eval("(char>?)"));
    assertEquals(eval("#t"), eval("(char>? #\\c)"));
    assertEquals(eval("#t"), eval("(char>? #\\c #\\b)"));
    assertEquals(eval("#t"), eval("(char>? #\\c #\\b #\\a)"));
    assertEquals(eval("#f"), eval("(char>? #\\c #\\c)"));
    assertEquals(eval("#f"), eval("(char>? #\\c #\\b #\\b)"));
    Exception ex = null;
    try {
      eval("(char>? #\\c #\\b 17)");
    } catch (Exception x) {
      ex = x;
    }
    assertNotNull(ex);
    assertEquals(eval("#t"), eval("(char>=?)"));
    assertEquals(eval("#t"), eval("(char>=? #\\c)"));
    assertEquals(eval("#t"), eval("(char>=? #\\c #\\b)"));
    assertEquals(eval("#t"), eval("(char>=? #\\c #\\b #\\a)"));
    assertEquals(eval("#t"), eval("(char>=? #\\c #\\c)"));
    assertEquals(eval("#t"), eval("(char>=? #\\c #\\b #\\b)"));
    assertEquals(eval("#f"), eval("(char>=? #\\c #\\b #\\c)"));
    ex = null;
    try {
      eval("(char>=? #\\c #\\b 17)");
    } catch (Exception x) {
      ex = x;
    }
    assertNotNull(ex);
  }
}
