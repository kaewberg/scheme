package se.pp.forsberg.scheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.numbers.Complex;
import se.pp.forsberg.scheme.values.numbers.DoubleReal;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.Number;
import se.pp.forsberg.scheme.values.numbers.RationalPair;
import se.pp.forsberg.scheme.values.numbers.Real;

public class TestParser {

  protected Parser createParser(java.lang.String s) {
    return new Parser(new StringReader(s));
  }
  @Test
  public void testComment1() throws SchemeException {
    Parser parser = createParser("; a b c\n#t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment2() throws SchemeException {
    Parser parser = createParser("#| a b c |##t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment3() throws SchemeException {
    Parser parser = createParser("#| a #| b |# c |##t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment4() throws SchemeException {
    Parser parser = createParser("#; a #t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment5() throws SchemeException {
    Parser parser = createParser("#; (a b (c)) #t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean1() throws SchemeException {
    Parser parser = createParser("#t");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean2() throws SchemeException {
    Parser parser = createParser("#true");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean3() throws SchemeException {
    Parser parser = createParser("#f");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertFalse(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean4() throws SchemeException {
    Parser parser = createParser("#false");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertFalse(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean5() throws SchemeException {
    Parser parser = createParser("#T");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean6() throws SchemeException {
    Parser parser = createParser("#tRuE");
    Value value = parser.read();
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testNumber1() throws SchemeException {
    Parser parser = createParser("123");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(123, true)));
    assertFalse(number.eq(new LongInteger(123, false)));
    System.out.println(value);
  }
  @Test
  public void testNumber2() throws SchemeException {
    Parser parser = createParser("123.45e67");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertFalse(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    assertTrue(number.eq(new DoubleReal(123.45e67)));
    System.out.println(value);
  }
  @Test
  public void testNumber2_1() throws SchemeException {
    Parser parser = createParser("123.45E67");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertFalse(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    assertTrue(number.eq(new DoubleReal(123.45e67)));
    System.out.println(value);
  }
  @Test
  public void testNumber3() throws SchemeException {
    Parser parser = createParser("10/3");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertFalse(number.isInteger());
    assertTrue(number.eq(new RationalPair(new LongInteger(10, true), new LongInteger(3, true), true)));
    System.out.println(value);
  }
  @Test
  public void testNumber4() throws SchemeException {
    Parser parser = createParser("10/3-3/10i");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertFalse(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    Real r = new RationalPair(new LongInteger(10, true), new LongInteger(3, true), true);
    Real i = new RationalPair(new LongInteger(-3, true), new LongInteger(10, true), true);
    assertTrue(number.eq(new Complex(r, i, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber3_2() throws SchemeException {
    Parser parser = createParser("5/12");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertFalse(number.isInteger());
    assertTrue(number.eq(new RationalPair(new LongInteger(5, true), new LongInteger(12, true), true)));
    System.out.println(value);
  }
  @Test
  public void testNumber4_1() throws SchemeException {
    Parser parser = createParser("10/3-3/10I");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertFalse(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    Real r = new RationalPair(new LongInteger(10, true), new LongInteger(3, true), true);
    Real i = new RationalPair(new LongInteger(-3, true), new LongInteger(10, true), true);
    assertTrue(number.eq(new Complex(r, i, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber5() throws SchemeException {
    Parser parser = createParser("+4i");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertFalse(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    Real r = new LongInteger(0, true);
    Real i = new LongInteger(4, true);
    assertTrue(number.eq(new Complex(r, i, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber6() throws SchemeException {
    Parser parser = createParser("1+4i");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertFalse(number.isReal());
    assertFalse(number.isRational());
    assertFalse(number.isInteger());
    Real r = new LongInteger(1, true);
    Real i = new LongInteger(4, true);
    assertTrue(number.eq(new Complex(r, i, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber7() throws SchemeException {
    Parser parser = createParser("#b10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(2, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber8() throws SchemeException {
    Parser parser = createParser("#o10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(8, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber9() throws SchemeException {
    Parser parser = createParser("#d10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(10, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber10() throws SchemeException {
    Parser parser = createParser("#x10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(16, true)));
    System.out.println(value);
  }
  @Test
  public void testNumber11() throws SchemeException {
    Parser parser = createParser("#i10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertFalse(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(10, false)));
    System.out.println(value);
  }
  @Test
  public void testNumber12() throws SchemeException {
    Parser parser = createParser("#e10");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
    assertTrue(number.eq(new LongInteger(10, true)));
    System.out.println(value);
  }
  @Test
  public void testNumberNormalization1() throws SchemeException {
    Parser parser = createParser("9/3+0i");
    Value value = parser.read();
    assertTrue(value.isNumber());
    Number number = (Number) value;
    assertTrue(number.isExact());
    assertTrue(number.isComplex());
    assertTrue(number.isReal());
    assertTrue(number.isRational());
    assertTrue(number.isInteger());
   
    assertTrue(number.eq(new LongInteger(3, true)));
    System.out.println(value);
  }
  @Test
  public void testCharacter1() throws SchemeException {
    Parser parser = createParser("#\\s");
    Value value = parser.read();
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals('s', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testCharacter2() throws SchemeException {
    Parser parser = createParser("#\\space");
    Value value = parser.read();
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals(' ', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testCharacter3() throws SchemeException {
    Parser parser = createParser("#\\x0e");
    Value value = parser.read();
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals('\016', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testString1() throws SchemeException {
    Parser parser = createParser("\"Hello world!\"");
    Value value = parser.read();
    assertTrue(value.isString());
    String s = (String) value;
    assertEquals("Hello world!", s.getString());
    System.out.println(value);
  }
  @Test
  public void testString2() throws SchemeException {
    java.lang.String src = "\"a\\\"b\\\\c\\x20;d\\\"e\\  \t  \n  \t  f\ng\rh\"";
    Parser parser = createParser(src);
    Value value = parser.read();
    assertTrue(value.isString());
    String s = (String) value;
    assertEquals("a\"b\\c d\"ef\ng\rh", s.getString());
    System.out.println(value);
  }
  @Test
  public void testSymbol1() throws SchemeException {
    Parser parser = createParser("Hello");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("Hello", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol2() throws SchemeException {
    Parser parser = createParser(".+peculiar");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals(".+peculiar", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol3() throws SchemeException {
    Parser parser = createParser("!$%&*/:<=>?^_~0123456789");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("!$%&*/:<=>?^_~0123456789", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol4() throws SchemeException {
    Parser parser = createParser("|Hello World!|");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("Hello World!", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol5() throws SchemeException {
    Parser parser = createParser("|a\\|b\\x5c;c\\x20;d\re|");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("a|b\\c d\re", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol6() throws SchemeException {
    Parser parser = createParser("#!fold-case\nHello");
    Value value = parser.read();
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("hello", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testByteVector1() throws SchemeException {
    Parser parser = createParser("#u8(#b1 #o2 #d3 #x4)");
    Value value = parser.read();
    assertTrue(value.isByteVector());
    ByteVector v = (ByteVector) value;
    assertEquals(4, v.getVector().size());
    assertEquals(1, (int) v.getVector().get(0));
    assertEquals(2, (int) v.getVector().get(1));
    assertEquals(3, (int) v.getVector().get(2));
    assertEquals(4, (int) v.getVector().get(3));
    System.out.println(value);
  }
  @Test
  public void testByteVector2() throws SchemeException {
    Parser parser = createParser("#u8()");
    Value value = parser.read();
    assertTrue(value.isByteVector());
    ByteVector v = (ByteVector) value;
    assertEquals(0, v.getVector().size());
    System.out.println(value);
  }
  @Test
  public void testList1() throws SchemeException {
    Parser parser = createParser("(1 2 3)");
    Value value = parser.read();
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new Pair(new LongInteger(3,true), Nil.NIL)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList2() throws SchemeException {
    Parser parser = createParser("()");
    Value value = parser.read();
    assertFalse(value.isPair());
    assertTrue(value.isNull());
    System.out.println(value);
  }
  @Test
  public void testList3() throws SchemeException {
    Parser parser = createParser("(1 2 . 3)");
    Value value = parser.read();
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new LongInteger(3,true)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList4() throws SchemeException {
    Parser parser = createParser("((1 . 2) . (3 . 4))");
    Value value = parser.read();
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new Pair(new LongInteger(1,true), new LongInteger(2,true)), new Pair(new LongInteger(3,true), new LongInteger(4, true)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList5() throws SchemeException {
    Parser parser = createParser("(1 2 3 . ())");
    Value value = parser.read();
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new Pair(new LongInteger(3,true), Nil.NIL)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testVector1() throws SchemeException {
    Parser parser = createParser("#(#b1 #o2 #d3 #x4)");
    Value value = parser.read();
    assertTrue(value.isVector());
    Vector v = (Vector) value;
    assertEquals(4, v.getVector().size());
    assertEquals(new LongInteger(1, true), v.getVector().get(0));
    assertEquals(new LongInteger(2, true), v.getVector().get(1));
    assertEquals(new LongInteger(3, true), v.getVector().get(2));
    assertEquals(new LongInteger(4, true), v.getVector().get(3));
    System.out.println(value);
  }
  @Test
  public void testVector2() throws SchemeException {
    Parser parser = createParser("#()");
    Value value = parser.read();
    assertTrue(value.isVector());
    Vector v = (Vector) value;
    assertEquals(0, v.getVector().size());
    System.out.println(value);
  }
  @Test
  public void testAbbreviation() throws SchemeException {
    Parser parser = createParser("'`,,@X");
    Value value = parser.read();
    Value expected =
        new Pair(new Identifier("quote"),
        new Pair(new Pair(new Identifier("quasi-quote"),
        new Pair(new Pair(new Identifier("unquote"),
        new Pair(new Pair(new Identifier("unquote-splicing"),
        new Pair(new Identifier("X"),Nil.NIL)),Nil.NIL)),Nil.NIL)),Nil.NIL));
    assertEquals(expected, value);
    System.out.println(value);
  }
  @Test
  public void testLabel1() throws SchemeException {
    Parser parser = createParser("(#1=x #1#)");
    Value value = parser.read();
    Value expected =
        new Pair(new Identifier("x"),
        new Pair(new Identifier("x"), Nil.NIL));
    assertEquals(expected, value);
    Value car = ((Pair) value).getCar();
    Value cadr = ((Pair)((Pair) value).getCdr()).getCar();
    assertTrue(car == cadr);
    System.out.println(value);
  }
  @Test
  public void testLabel2() throws SchemeException {
    Parser parser = createParser("#1=(x . #1#)");
    Value value = parser.read();
    Identifier x = null;
    System.out.println(value);
    for (int i = 0; i < 10; i++) {
      assertTrue(value.isPair());
      Pair p = (Pair) value;
      assertEquals(new Identifier("x"), p.getCar());
      if (i == 0) x = (Identifier) p.getCar();
      assertEquals(x, p.getCar());
      value = p.getCdr();
    }
  }

  @Test
  public void testLabel3() throws SchemeException {
    Parser parser = createParser("#(#1=x #1#)");
    Value value = parser.read();
    Value expected = new Vector(Arrays.asList((Value) new Identifier("x"), new Identifier("x")));
    assertEquals(expected, value);
    List<Value> v = ((Vector)value).getVector();
    assertEquals(v.get(0), v.get(1));
    System.out.println(value);
  }
  @Test
  public void testLabel4() throws SchemeException {
    Parser parser = createParser("#1=#(x #1#)");
    Value value = parser.read();
    Identifier x = null;
    System.out.println(value);
    for (int i = 0; i < 10; i++) {
      assertTrue(value.isVector());
      List<Value> v = ((Vector) value).getVector();
      assertEquals(2, v.size());
      assertEquals(new Identifier("x"), v.get(0));
      if (i == 0) x = (Identifier) v.get(0);
      assertEquals(x, v.get(0));
      value = v.get(1);
    }
  }
}
