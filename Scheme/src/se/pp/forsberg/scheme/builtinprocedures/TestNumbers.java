package se.pp.forsberg.scheme.builtinprocedures;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.Scheme;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class TestNumbers {
  
  public TestNumbers() throws SchemeException {
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
  public void testIsNumber() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(number? #t)"));
    assertEquals(Boolean.TRUE, eval("(number? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(number? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(number? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(number? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(number? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(number? 1)"));
  }
  @Test
  public void testIsComplex() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(complex? #t)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(complex? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(complex? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(complex? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(complex? 1)"));
  }
  @Test
  public void testIsReal() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(real? #t)"));
    assertEquals(Boolean.FALSE, eval("(real? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(real? +nan.0)"));
    assertEquals(Boolean.TRUE, eval("(real? +inf.0)"));
    assertEquals(Boolean.TRUE, eval("(real? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(real? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(real? 1)"));
  }
  @Test
  public void testIsRational() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(rational? #t)"));
    assertEquals(Boolean.FALSE, eval("(rational? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(rational? +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(rational? +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(rational? 123e45)"));
    assertEquals(Boolean.TRUE, eval("(rational? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(rational? 1)"));
  }
  @Test
  public void testIsInteger() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(integer? #t)"));
    assertEquals(Boolean.FALSE, eval("(integer? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(integer? +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(integer? +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(integer? 123e45)"));
    assertEquals(Boolean.FALSE, eval("(integer? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(integer? 1)"));
  }
  @Test
  public void testIsExact() throws SchemeException {
    assertEquals(Boolean.TRUE, eval("(exact? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1+i)"));
    assertEquals(Boolean.TRUE, eval("(exact? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1/2)"));
    assertEquals(Boolean.TRUE, eval("(exact? 1)"));
    assertEquals(Boolean.FALSE, eval("(exact? #i1)"));
  }
  @Test
  public void testIsInexact() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(inexact? 1+i)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1+i)"));
    assertEquals(Boolean.FALSE, eval("(inexact? 1/2)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1/2)"));
    assertEquals(Boolean.FALSE, eval("(inexact? 1)"));
    assertEquals(Boolean.TRUE, eval("(inexact? #i1)"));
  }
  @Test
  public void testIsExactInteger() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(exact-integer? 1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1+i)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? 1/2)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1/2)"));
    assertEquals(Boolean.TRUE, eval("(exact-integer? 1)"));
    assertEquals(Boolean.FALSE, eval("(exact-integer? #i1)"));
  }
  @Test
  public void testIsFinite() throws SchemeException {
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
  public void testIsInfinite() throws SchemeException {
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
  public void testEquals() throws SchemeException {
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
  public void testLessThan() throws SchemeException {
    assertEquals(Boolean.TRUE, eval("(< 1/2 2/2 3/2)"));
    assertEquals(Boolean.FALSE, eval("(< 1/2 1/2 2/2)"));
    assertEquals(Boolean.FALSE, eval("(< 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(< -inf.0 0 +inf.0)"));
    assertEquals(Boolean.FALSE, eval("(< 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(< +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(< +nan.0 +nan.0)"));
  }
  @Test
  public void testLessThanOrEqual() throws SchemeException {
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
  public void testGreaterThan() throws SchemeException {
    assertEquals(Boolean.TRUE, eval("(> 3/2 2/2 1/2)"));
    assertEquals(Boolean.FALSE, eval("(> 2/2 1/2 1/2)"));
    assertEquals(Boolean.FALSE, eval("(> 1/2 2/2 1/2)"));
    
    assertEquals(Boolean.TRUE, eval("(> +inf.0 0 -inf.0)"));
    assertEquals(Boolean.FALSE, eval("(> 0 +nan.0)"));
    assertEquals(Boolean.FALSE, eval("(> +nan.0 0)"));
    assertEquals(Boolean.FALSE, eval("(> +nan.0 +nan.0)"));
  }
  @Test
  public void testGreaterThanOrEqual() throws SchemeException {
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
  public void testIsZero() throws SchemeException {
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
  public void testIsPositive() throws SchemeException {
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
  public void testIsNegative() throws SchemeException {
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
  public void testIsOdd() throws SchemeException {
    assertEquals(Boolean.TRUE, eval("(odd? 5)"));
    assertEquals(Boolean.TRUE, eval("(odd? #i5)"));
    assertEquals(Boolean.FALSE, eval("(odd? 6)"));
    assertEquals(Boolean.FALSE, eval("(odd? #i6)"));
  }
  @Test
  public void testIsEven() throws SchemeException {
    assertEquals(Boolean.FALSE, eval("(even? 5)"));
    assertEquals(Boolean.FALSE, eval("(even? #i5)"));
    assertEquals(Boolean.TRUE, eval("(even? 6)"));
    assertEquals(Boolean.TRUE, eval("(even? #i6)"));
  }
  @Test
  public void testMax() throws SchemeException {
    assertEquals(new LongInteger(4711, true), eval("(max -4711 4711 -17 17)"));
  }
  @Test
  public void testMin() throws SchemeException {
    assertEquals(new LongInteger(-4711, true), eval("(min -4711 4711 -17 17)"));
  }
  @Test
  public void testPlus() throws SchemeException {
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
  public void testMinus() throws SchemeException {
    assertEquals(eval("-1"), eval("(- 1)"));
    assertEquals(eval("-1"), eval("(- 1 2)"));
    assertEquals(eval("-4"), eval("(- 1 2 3)"));
    assertEquals(eval("2"), eval("(- 1 -1)"));
    assertEquals(eval("2-i"), eval("(- 2 +i)"));
    assertEquals(eval("2+i"), eval("(- 2 -i)"));
    assertEquals(eval("3/12"), eval("(- 5/12 1/6)"));
  }
  @Test
  public void testTimes() throws SchemeException {
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
  public void testDivide() throws SchemeException {
    assertEquals(eval("1/2"), eval("(/ 2)"));
    assertEquals(eval("1/2"), eval("(/ 1 2)"));
    assertEquals(eval("1/6"), eval("(/ 1 2 3)"));
    assertEquals(eval("-1"), eval("(/ 1 -1)"));
    assertEquals(eval("-2i"), eval("(/ 2 +i)"));
    assertEquals(eval("2i"), eval("(/ 2 -i)"));
    assertEquals(eval("15/6"), eval("(/ 5/12 1/6)"));
  }
  @Test
  public void testAbs() throws SchemeException {
    //assertEquals(eval("1"), eval("(abs 1)"));
    //assertEquals(eval("1"), eval("(abs -1)"));
    assertEquals(eval("#i1"), eval("(abs #i-1)"));
    assertEquals(eval("1/2"), eval("(abs 1/2)"));
    assertEquals(eval("1/2"), eval("(abs -1/2)"));
    assertEquals(eval("123e45"), eval("(abs 123e45)"));
    assertEquals(eval("123e45"), eval("(abs -123e45)"));
  }
  @Test
  public void testModulo() throws SchemeException {
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
  public void testGcd() throws SchemeException {
    assertEquals(eval("4"), eval("(gcd 32 -36)")); 
    assertEquals(eval("0"), eval("(gcd)"));
    assertEquals(eval("288"), eval("(lcm 32 -36)"));
    assertEquals(eval("#i288"), eval("(lcm 32.0 -36)"));
    assertEquals(eval("1"), eval("(lcm)"));
  }
  @Test
  public void testRationals() throws SchemeException {
    assertEquals(eval("3"), eval("(numerator (/ 6 4))"));
    assertEquals(eval("2"), eval("(denominator(/ 6 4))"));
    assertEquals(eval("#i2"), eval("(denominator(/ 6.0 4))"));
    assertEquals(eval("0"), eval("(numerator 0)"));
    assertEquals(eval("-3"), eval("(numerator (/ 6 -4))"));
    assertEquals(eval("2"), eval("(denominator (/ 6 -4))"));
  }
  @Test
  public void testRound() throws SchemeException {
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
  public void testRationalize() throws SchemeException {
    assertEquals(eval("1/3"), eval("(rationalize .3 1/10)"));
    assertEquals(eval("-1/3"), eval("(rationalize -.3 1/10)"));
  }
  // TODO complex exponentials
  @Test
  public void testSquare() throws SchemeException {
    assertEquals(eval("1764"), eval("(square 42)"));
    assertEquals(eval("4.0"), eval("(square 2.0)"));
    assertEquals(eval("-1"), eval("(square -i)"));
  }
  @Test
  public void testSqrt() throws SchemeException {
    assertEquals(eval("42.0"), eval("(sqrt 1764)"));
    //assertEquals(eval("+i"), eval("(square -1)"));
  }
  @Test
  public void testExactSqrt() throws SchemeException {
    assertEquals(eval("'(4 1)"), eval("(exact-integer-sqrt 17)"));
  }
  @Test
  public void testExpt() throws SchemeException {
    assertEquals(eval("8.0"), eval("(expt 2 3)"));  
  }
  @Test
  public void testComplex() throws SchemeException {
    assertEquals(eval("2+3i"), eval("(make-rectangular 2 3)"));
    //assertEquals(eval("3i"), eval("(make-polar 3 (acos 0))")); // FAIL!
  }
}
