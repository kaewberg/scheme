package se.pp.forsberg.scheme.builtinprocedures;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class TestNumbers {
  
  private static Environment env = Environment.schemeReportEnvironment(7);
  static {
    Library.load(Library.makeName("scheme", "inexact"), env);
    Library.load(Library.makeName("scheme", "complex"), env);
  }
  protected Parser createParser(java.lang.String s) {
    return new Parser(new StringReader(s));
  }
  protected Value eval(Value value) {
    return value.eval(env);
  }
  protected Value eval(java.lang.String source) {
    return createParser(source).read().eval(env);
  }
  @Test
  public void testIsNumber() {
    assertEquals(Boolean.FALSE, eval("(number? #t)"));
    assertEquals(Boolean.TRUE, eval("(number? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(number? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(number? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(number? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(number? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(number? 1)"));
  }
  @Test
  public void testIsComplex() {
    assertEquals(Boolean.FALSE, eval("(complex? #t)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(complex? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(complex? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(complex? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1)"));
  }
  @Test
  public void testIsReal() {
    assertEquals(Boolean.FALSE, eval("(real? #t)"));
    assertEquals(Boolean.FALSE, eval("(real? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(real? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(real? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(real? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(real? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(real? 1)"));
  }
  @Test
  public void testIsRational() {
    assertEquals(Boolean.FALSE, eval("(rational? #t)"));
    assertEquals(Boolean.FALSE, eval("(rational? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(rational? +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(rational? +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(rational? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(rational? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(rational? 1)"));
  }
  @Test
  public void testIsInteger() {
    assertEquals(Boolean.FALSE, eval("(integer? #t)"));
    assertEquals(Boolean.FALSE, eval("(integer? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(integer? +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(integer? +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(integer? 123e45)"));
    assertEquals(Boolean.FALSE, eval("(integer? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(integer? 1)"));
  }
  @Test
  public void testIsExact() {
    assertEquals(Boolean.TRUE, eval("(exact? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1+i)"));
    assertEquals(Boolean.TRUE, eval("(exact? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1/2)"));
    assertEquals(Boolean.TRUE, eval("(exact? 1)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1)"));
  }
  @Test
  public void testIsInexact() {
    assertEquals(Boolean.FALSE, eval("(inexact? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1+i)"));
    assertEquals(Boolean.FALSE, eval("(inexact? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1/2)"));
    assertEquals(Boolean.FALSE, eval("(inexact? 1)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1)"));
  }
  @Test
  public void testIsExactInteger() {
    assertEquals(Boolean.FALSE, eval("(exact-integer? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1/2)"));
    assertEquals(Boolean.TRUE, eval("(exact-integer? 1)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1)"));
  }
  @Test
  public void testIsFinite() {
    assertEquals(Boolean.TRUE, eval("(finite? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(finite? +inf.0+i)"));
    assertEquals(Boolean.FALSE, eval("(finite? 1+inf.0i)"));
    assertEquals(Boolean.FALSE, eval("(finite? +nan.0+i)"));
    assertEquals(Boolean.FALSE, eval("(finite? 1+nan.0i)"));
    assertEquals(Boolean.TRUE, eval("(finite? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(finite? 1)"));
    assertEquals(Boolean.FALSE, eval("(finite? +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(finite? -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(finite? +nan.0)"));
  }
  @Test
  public void testIsInfinite() {
    assertEquals(Boolean.FALSE, eval("(infinite? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(infinite? +inf.0+i)"));
    assertEquals(Boolean.TRUE, eval("(infinite? 1+inf.0i)"));
    assertEquals(Boolean.FALSE, eval("(infinite? +nan.0+i)"));
    assertEquals(Boolean.FALSE, eval("(infinite? 1+nan.0i)"));
    assertEquals(Boolean.FALSE, eval("(infinite? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(infinite? 1)"));
    assertEquals(Boolean.TRUE, eval("(infinite? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(infinite? -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(infinite? +nan.0)"));
  }
  @Test
  public void testEquals() {
    assertEquals(Boolean.TRUE, eval("(= 1+i 1+i 1+i)"));
    assertEquals(Boolean.TRUE, eval("(= #i1+i #i1+i #i1+i)"));
    assertEquals(Boolean.FALSE, eval("(= 1+i #i1+i 1+i)"));
    assertEquals(Boolean.FALSE, eval("(= 1+i 2+i 1+i)"));
    assertEquals(Boolean.TRUE, eval("(= 1/2 1/2 1/2)"));
    assertEquals(Boolean.TRUE, eval("(= #i1/2 #i1/2 #i1/2)"));
    assertEquals(Boolean.FALSE, eval("(= 1/2 #i1/2 1/2)"));
    assertEquals(Boolean.TRUE, eval("(= 1/2 2/4 3/6)"));
    assertEquals(Boolean.FALSE, eval("(= 1/2 1/3 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(= +inf.0 +inf.0 +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(= -inf.0 -inf.0 -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(= +nan.0 +nan.0 +nan.0)"));
  }
  @Test
  public void testLessThan() {
    assertEquals(Boolean.TRUE, eval("(< 1/2 2/2 3/2)"));
    assertEquals(Boolean.FALSE, eval("(< 1/2 1/2 2/2)"));
    assertEquals(Boolean.FALSE, eval("(< 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(< -inf.0 0 +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(< 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(< +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(< +nan.0 +nan.0)"));
  }
  @Test
  public void testLessThanOrEqual() {
    assertEquals(Boolean.TRUE, eval("(<= 1/2 2/2 3/2)"));
    assertEquals(Boolean.TRUE, eval("(<= 1/2 1/2 2/2)"));
    assertEquals(Boolean.FALSE, eval("(< 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(<= -inf.0 0 +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(<= +inf.0 +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(<= -inf.0 -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(<= 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(<= +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(<= +nan.0 +nan.0)"));
  }
  @Test
  public void testGreaterThan() {
    assertEquals(Boolean.TRUE, eval("(> 3/2 2/2 1/2)"));
    assertEquals(Boolean.FALSE, eval("(> 2/2 1/2 1/2)"));
    assertEquals(Boolean.FALSE, eval("(> 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(> +inf.0 0 -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(> 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(> +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(> +nan.0 +nan.0)"));
  }
  @Test
  public void testGreaterThanOrEqual() {
    assertEquals(Boolean.TRUE, eval("(>= 3/2 2/21 1/22)"));
    assertEquals(Boolean.TRUE, eval("(>= 2/2 1/2 1/2)"));
    assertEquals(Boolean.FALSE, eval("(> 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(>= +inf.0 0 -inf.0)"));
    assertEquals(Boolean.TRUE, eval("(>= -inf.0 -inf.0)"));
    assertEquals(Boolean.TRUE, eval("(>= +inf.0 +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(>= 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(>= +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(>= +nan.0 +nan.0)"));
  }
  @Test
  public void testIsZero() {
    assertEquals(Boolean.TRUE, eval("(zero? 0)"));
    assertEquals(Boolean.TRUE, eval("(zero? #i0)"));
    assertEquals(Boolean.TRUE, eval("(zero? 0/2)"));
    assertEquals(Boolean.TRUE, eval("(zero? #i0/2)"));
    assertEquals(Boolean.TRUE, eval("(zero? 0.0)"));
    assertEquals(Boolean.TRUE, eval("(zero? #i0.0)"));
    assertEquals(Boolean.TRUE, eval("(zero? 0+0i)"));
    assertEquals(Boolean.TRUE, eval("(zero? #i0+0i)"));
    assertEquals(Boolean.FALSE, eval("(zero? 1)"));
  }
  @Test
  public void testIsPositive() {
    assertEquals(Boolean.TRUE, eval("(positive? 1)"));
    assertEquals(Boolean.TRUE, eval("(positive? #i1)"));
    assertEquals(Boolean.TRUE, eval("(positive? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(positive? #i1/2)"));
    assertEquals(Boolean.TRUE, eval("(positive? 1.0)"));
    assertEquals(Boolean.FALSE, eval("(positive? 0)"));
    assertEquals(Boolean.FALSE, eval("(positive? #i0)"));
    assertEquals(Boolean.FALSE, eval("(positive? -1)"));
    assertEquals(Boolean.FALSE, eval("(positive? #i-1)"));
    assertEquals(Boolean.FALSE, eval("(positive? -1/2)"));
    assertEquals(Boolean.FALSE, eval("(positive? #i-1/2)"));
    assertEquals(Boolean.FALSE, eval("(positive? -1.0)"));
  }
  @Test
  public void testIsNegative() {
    assertEquals(Boolean.FALSE, eval("(negative? 1)"));
    assertEquals(Boolean.FALSE, eval("(negative? #i1)"));
    assertEquals(Boolean.FALSE, eval("(negative? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(negative? #i1/2)"));
    assertEquals(Boolean.FALSE, eval("(negative? 1.0)"));
    assertEquals(Boolean.FALSE, eval("(negative? 0)"));
    assertEquals(Boolean.FALSE, eval("(negative? #i0)"));
    assertEquals(Boolean.TRUE, eval("(negative? -1)"));
    assertEquals(Boolean.TRUE, eval("(negative? #i-1)"));
    assertEquals(Boolean.TRUE, eval("(negative? -1/2)"));
    assertEquals(Boolean.TRUE, eval("(negative? #i-1/2)"));
    assertEquals(Boolean.TRUE, eval("(negative? -1.0)"));
  }
  @Test
  public void testIsOdd() {
    assertEquals(Boolean.TRUE, eval("(odd? 5)"));
    assertEquals(Boolean.TRUE, eval("(odd? #i5)"));
    assertEquals(Boolean.FALSE, eval("(odd? 6)"));
    assertEquals(Boolean.FALSE, eval("(odd? #i6)"));
  }
  @Test
  public void testIsEven() {
    assertEquals(Boolean.FALSE, eval("(even? 5)"));
    assertEquals(Boolean.FALSE, eval("(even? #i5)"));
    assertEquals(Boolean.TRUE, eval("(even? 6)"));
    assertEquals(Boolean.TRUE, eval("(even? #i6)"));
  }
  @Test
  public void testMax() {
    assertEquals(new LongInteger(4711, true), eval("(max -4711 4711 -17 17)"));
  }
  @Test
  public void testMin() {
    assertEquals(new LongInteger(-4711, true), eval("(min -4711 4711 -17 17)"));
  }
  @Test
  public void testPlus() {
    assertEquals(eval("0"), eval("(+)"));
    assertEquals(eval("1"), eval("(+ 1)"));
    assertEquals(eval("3"), eval("(+ 1 2)"));
    assertEquals(eval("6"), eval("(+ 1 2 3)"));
    assertEquals(eval("0"), eval("(+ 1 -1)"));
    assertEquals(eval("2+i"), eval("(+ 2 +i)"));
    assertEquals(eval("2-i"), eval("(+ 2 -i)"));
    assertEquals(eval("7/12"), eval("(+ 5/12 1/6)"));
  }
  @Test
  public void testMinus() {
    assertEquals(eval("-1"), eval("(- 1)"));
    assertEquals(eval("-1"), eval("(- 1 2)"));
    assertEquals(eval("-4"), eval("(- 1 2 3)"));
    assertEquals(eval("2"), eval("(- 1 -1)"));
    assertEquals(eval("2-i"), eval("(- 2 +i)"));
    assertEquals(eval("2+i"), eval("(- 2 -i)"));
    assertEquals(eval("3/12"), eval("(- 5/12 1/6)"));
  }
  @Test
  public void testTimes() {
    assertEquals(eval("1"), eval("(*)"));
    assertEquals(eval("1"), eval("(* 1)"));
    assertEquals(eval("2"), eval("(* 1 2)"));
    assertEquals(eval("6"), eval("(* 1 2 3)"));
    assertEquals(eval("-1"), eval("(* 1 -1)"));
    assertEquals(eval("2i"), eval("(* 2 +i)"));
    assertEquals(eval("-2i"), eval("(* 2 -i)"));
    assertEquals(eval("5/72"), eval("(* 5/12 1/6)"));
  }
  @Test
  public void testDivide() {
    assertEquals(eval("1/2"), eval("(/ 2)"));
    assertEquals(eval("1/2"), eval("(/ 1 2)"));
    assertEquals(eval("1/6"), eval("(/ 1 2 3)"));
    assertEquals(eval("-1"), eval("(/ 1 -1)"));
    assertEquals(eval("-2i"), eval("(/ 2 +i)"));
    assertEquals(eval("2i"), eval("(/ 2 -i)"));
    assertEquals(eval("15/6"), eval("(/ 5/12 1/6)"));
  }
  @Test
  public void testAbs() {
    //assertEquals(eval("1"), eval("(abs 1)"));
    //assertEquals(eval("1"), eval("(abs -1)"));
    assertEquals(eval("#i1"), eval("(abs #i-1)"));
    assertEquals(eval("1/2"), eval("(abs 1/2)"));
    assertEquals(eval("1/2"), eval("(abs -1/2)"));
    assertEquals(eval("123e45"), eval("(abs 123e45)"));
    assertEquals(eval("123e45"), eval("(abs -123e45)"));
  }
  @Test
  public void testModulo() {
    assertEquals(eval("'(2 1)"), eval("(floor/ 5 2)"));
    assertEquals(eval("'(-3 1)"), eval("(floor/ -5 2)"));
    assertEquals(eval("'(-3 -1)"), eval("(floor/ 5 -2)"));
    assertEquals(eval("'(2 -1)"), eval("(floor/ -5 -2)"));
    assertEquals(eval("'(2 1)"), eval("(truncate/ 5 2)"));
    assertEquals(eval("'(-2 -1)"), eval("(truncate/ -5 2)"));
    assertEquals(eval("'(-2 1)"), eval("(truncate/ 5 -2)"));
    assertEquals(eval("'(2 -1)"), eval("(truncate/ -5 -2)"));
    assertEquals(eval("(truncate-quotient 5 2)"), eval("(quotient 5 2)"));
    assertEquals(eval("(truncate-quotient -5 2)"), eval("(quotient -5 2)"));
    assertEquals(eval("(truncate-quotient 5 -2)"), eval("(quotient 5 -2)"));
    assertEquals(eval("(truncate-quotient -5 -2)"), eval("(quotient -5 -2)"));
    assertEquals(eval("(truncate-remainder 5 2)"), eval("(remainder 5 2)"));
    assertEquals(eval("(truncate-remainder -5 2)"), eval("(remainder -5 2)"));
    assertEquals(eval("(truncate-remainder 5 -2)"), eval("(remainder 5 -2)"));
    assertEquals(eval("(truncate-remainder -5 -2)"), eval("(remainder -5 -2)"));
    assertEquals(eval("(floor-remainder 5 2)"), eval("(modulo 5 2)"));
    assertEquals(eval("(floor-remainder -5 2)"), eval("(modulo -5 2)"));
    assertEquals(eval("(floor-remainder 5 -2)"), eval("(modulo 5 -2)"));
    assertEquals(eval("(floor-remainder -5 -2)"), eval("(modulo -5 -2)"));
  }
  @Test
  public void testGcd() {
    assertEquals(eval("4"), eval("(gcd 32 -36)")); 
    assertEquals(eval("0"), eval("(gcd)"));
    assertEquals(eval("288"), eval("(lcm 32 -36)"));
    assertEquals(eval("#i288"), eval("(lcm 32.0 -36)"));
    assertEquals(eval("1"), eval("(lcm)"));
  }
  @Test
  public void testRationals() {
    assertEquals(eval("3"), eval("(numerator (/ 6 4))"));
    assertEquals(eval("2"), eval("(denominator(/ 6 4))"));
    assertEquals(eval("#i2"), eval("(denominator(/ 6.0 4))"));
    assertEquals(eval("0"), eval("(numerator 0)"));
    assertEquals(eval("-3"), eval("(numerator (/ 6 -4))"));
    assertEquals(eval("2"), eval("(denominator (/ 6 -4))"));
  }
  @Test
  public void testRound() {
    assertEquals(eval("-5.0"), eval("(floor -4.3)"));
    assertEquals(eval("-4.0"), eval("(ceiling -4.3)"));
    assertEquals(eval("-4.0"), eval("(truncate -4.3)"));
    assertEquals(eval("-4.0"), eval("(round -4.3)"));
    assertEquals(eval("3.0"), eval("(floor 3.5)"));
    assertEquals(eval("4.0"), eval("(ceiling 3.5)"));
    assertEquals(eval("3.0"), eval("(truncate 3.5)"));
    assertEquals(eval("4.0"), eval("(round 3.5)"));
    assertEquals(eval("4"), eval("(round 7/2)"));
    assertEquals(eval("7"), eval("(round 7)"));
  }
  @Test
  public void testRationalize() {
    assertEquals(eval("1/3"), eval("(rationalize .3 1/10)"));
    assertEquals(eval("-1/3"), eval("(rationalize -.3 1/10)"));
  }
  // TODO complex exponentials
  @Test
  public void testSquare() {
    assertEquals(eval("1764"), eval("(square 42)"));
    assertEquals(eval("4.0"), eval("(square 2.0)"));
    assertEquals(eval("-1"), eval("(square -i)"));
  }
  @Test
  public void testSqrt() {
    assertEquals(eval("42.0"), eval("(sqrt 1764)"));
    //assertEquals(eval("+i"), eval("(square -1)"));
  }
  @Test
  public void testExactSqrt() {
    assertEquals(eval("'(4 1)"), eval("(exact-integer-sqrt 17)"));
  }
  @Test
  public void testExpt() {
    assertEquals(eval("8.0"), eval("(expt 2 3)"));  
  }
  @Test
  public void testComplex() {
    assertEquals(eval("2+3i"), eval("(make-rectangular 2 3)"));
    //assertEquals(eval("3i"), eval("(make-polar 3 (acos 0))")); // FAIL!
  }
}
