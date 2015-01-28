package se.pp.forsberg.scheme.antlr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
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

public class TestSchemeParser {

  protected Scheme2Parser_old createParser(java.lang.String s) {
    ANTLRInputStream stream = new ANTLRInputStream(s);
    Scheme2Lexer_old lexer = new Scheme2Lexer_old(stream);
    return new Scheme2Parser_old(new CommonTokenStream(lexer));
  }
  @Test
  public void testComment1() {
    Scheme2Parser_old parser = createParser("; a b c\n#t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment2() {
    Scheme2Parser_old parser = createParser("#| a b c |##t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment3() {
    Scheme2Parser_old parser = createParser("#| a #| b |# c |##t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment4() {
    Scheme2Parser_old parser = createParser("#; a #t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testComment5() {
    Scheme2Parser_old parser = createParser("#; (a b (c)) #t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean1() {
    Scheme2Parser_old parser = createParser("#t");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean2() {
    Scheme2Parser_old parser = createParser("#true");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean3() {
    Scheme2Parser_old parser = createParser("#f");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertFalse(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean4() {
    Scheme2Parser_old parser = createParser("#false");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertFalse(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean5() {
    Scheme2Parser_old parser = createParser("#T");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testBoolean6() {
    Scheme2Parser_old parser = createParser("#tRuE");
    Value value = parser.datum().value;
    assertTrue(value.isBoolean());
    assertTrue(((Boolean) value).getBoolean());
    System.out.println(value);
  }
  @Test
  public void testNumber1() {
    Scheme2Parser_old parser = createParser("123");
    Value value = parser.datum().value;
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
  public void testNumber2() {
    Scheme2Parser_old parser = createParser("123.45e67");
    Value value = parser.datum().value;
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
  public void testNumber2_1() {
    Scheme2Parser_old parser = createParser("123.45E67");
    Value value = parser.datum().value;
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
  public void testNumber3() {
    Scheme2Parser_old parser = createParser("10/3");
    Value value = parser.datum().value;
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
  public void testNumber4() {
    Scheme2Parser_old parser = createParser("10/3-3/10i");
    Value value = parser.datum().value;
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
  public void testNumber3_2() {
    Scheme2Parser_old parser = createParser("5/12");
    Value value = parser.datum().value;
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
  public void testNumber4_1() {
    Scheme2Parser_old parser = createParser("10/3-3/10I");
    Value value = parser.datum().value;
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
  public void testNumber5() {
    Scheme2Parser_old parser = createParser("+4i");
    Value value = parser.datum().value;
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
  public void testNumber6() {
    Scheme2Parser_old parser = createParser("1+4i");
    Value value = parser.datum().value;
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
  public void testNumber7() {
    Scheme2Parser_old parser = createParser("#b10");
    Value value = parser.datum().value;
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
  public void testNumber8() {
    Scheme2Parser_old parser = createParser("#o10");
    Value value = parser.datum().value;
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
  public void testNumber9() {
    Scheme2Parser_old parser = createParser("#d10");
    Value value = parser.datum().value;
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
  public void testNumber10() {
    Scheme2Parser_old parser = createParser("#x10");
    Value value = parser.datum().value;
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
  public void testNumber11() {
    Scheme2Parser_old parser = createParser("#i10");
    Value value = parser.datum().value;
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
  public void testNumber12() {
    Scheme2Parser_old parser = createParser("#e10");
    Value value = parser.datum().value;
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
  public void testNumberNormalization1() {
    Scheme2Parser_old parser = createParser("9/3+0i");
    Value value = parser.datum().value;
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
  public void testCharacter1() {
    Scheme2Parser_old parser = createParser("#\\s");
    Value value = parser.datum().value;
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals('s', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testCharacter2() {
    Scheme2Parser_old parser = createParser("#\\space");
    Value value = parser.datum().value;
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals(' ', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testCharacter3() {
    Scheme2Parser_old parser = createParser("#\\x0e");
    Value value = parser.datum().value;
    assertTrue(value.isChar());
    Character c = (Character) value;
    assertEquals('\016', c.getCharacter());
    System.out.println(value);
  }
  @Test
  public void testString1() {
    Scheme2Parser_old parser = createParser("\"Hello world!\"");
    Value value = parser.datum().value;
    assertTrue(value.isString());
    String s = (String) value;
    assertEquals("Hello world!", s.getString());
    System.out.println(value);
  }
  @Test
  public void testString2() {
    java.lang.String src = "\"a\\\"b\\\\c\\x20;d\\\"e\\  \t  \n  \t  f\ng\rh\"";
    Scheme2Parser_old parser = createParser(src);
    Value value = parser.datum().value;
    assertTrue(value.isString());
    String s = (String) value;
    assertEquals("a\"b\\c d\"ef\ng\rh", s.getString());
    System.out.println(value);
  }
  @Test
  public void testSymbol1() {
    Scheme2Parser_old parser = createParser("Hello");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("Hello", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol2() {
    Scheme2Parser_old parser = createParser(".+peculiar");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals(".+peculiar", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol3() {
    Scheme2Parser_old parser = createParser("!$%&*/:<=>?^_~0123456789");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("!$%&*/:<=>?^_~0123456789", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol4() {
    Scheme2Parser_old parser = createParser("|Hello World!|");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("Hello World!", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol5() {
    Scheme2Parser_old parser = createParser("|a\\|b\\x5c;c\\x20;d\re|");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("a|b\\c d\re", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testSymbol6() {
    Scheme2Parser_old parser = createParser("#!fold-case\nHello");
    Value value = parser.datum().value;
    assertTrue(value.isIdentifier());
    Identifier s = (Identifier) value;
    assertEquals("hello", s.getIdentifier());
    System.out.println(value);
  }
  @Test
  public void testByteVector1() {
    Scheme2Parser_old parser = createParser("#u8(#b1 #o2 #d3 #x4)");
    Value value = parser.datum().value;
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
  public void testByteVector2() {
    Scheme2Parser_old parser = createParser("#u8()");
    Value value = parser.datum().value;
    assertTrue(value.isByteVector());
    ByteVector v = (ByteVector) value;
    assertEquals(0, v.getVector().size());
    System.out.println(value);
  }
  @Test
  public void testList1() {
    Scheme2Parser_old parser = createParser("(1 2 3)");
    Value value = parser.datum().value;
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new Pair(new LongInteger(3,true), Nil.NIL)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList2() {
    Scheme2Parser_old parser = createParser("()");
    Value value = parser.datum().value;
    assertFalse(value.isPair());
    assertTrue(value.isNull());
    System.out.println(value);
  }
  @Test
  public void testList3() {
    Scheme2Parser_old parser = createParser("(1 2 . 3)");
    Value value = parser.datum().value;
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new LongInteger(3,true)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList4() {
    Scheme2Parser_old parser = createParser("((1 . 2) . (3 . 4))");
    Value value = parser.datum().value;
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new Pair(new LongInteger(1,true), new LongInteger(2,true)), new Pair(new LongInteger(3,true), new LongInteger(4, true)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testList5() {
    Scheme2Parser_old parser = createParser("(1 2 3 . ())");
    Value value = parser.datum().value;
    assertTrue(value.isPair());
    assertFalse(value.isNull());
    Pair p = (Pair) value;
    Pair expected = new Pair(new LongInteger(1,true), new Pair(new LongInteger(2,true), new Pair(new LongInteger(3,true), Nil.NIL)));
    assertEquals(expected, p);
    System.out.println(value);
  }
  @Test
  public void testVector1() {
    Scheme2Parser_old parser = createParser("#(#b1 #o2 #d3 #x4)");
    Value value = parser.datum().value;
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
  public void testVector2() {
    Scheme2Parser_old parser = createParser("#()");
    Value value = parser.datum().value;
    assertTrue(value.isVector());
    Vector v = (Vector) value;
    assertEquals(0, v.getVector().size());
    System.out.println(value);
  }
  @Test
  public void testAbbreviation() {
    Scheme2Parser_old parser = createParser("'`,,@X");
    Value value = parser.datum().value;
    Value expected =
        new Pair(new Identifier("quote"),
        new Pair(new Pair(new Identifier("quasi-quote"),
        new Pair(new Pair(new Identifier("unquote"),
        new Pair(new Pair(new Identifier("unquote-splicing"),
        new Pair(new Identifier("X"),Nil.NIL)),Nil.NIL)),Nil.NIL)),Nil.NIL));
    assertEquals(expected, value);
    System.out.println(value);
  }
  // TODO label
}
