package se.pp.forsberg.scheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.numbers.Complex;
import se.pp.forsberg.scheme.values.numbers.DoubleReal;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.RationalPair;

public class TestScheme {
  
  public TestScheme() throws SchemeException {
    
  }

  private Environment env = Environment.schemeReportEnvironment(7);
  //protected SchemeParser createParser(java.lang.String s) {
//    ANTLRInputStream stream = new ANTLRInputStream(s);
//    SchemeLexer lexer = new SchemeLexer(stream);
//    return new SchemeParser(new CommonTokenStream(lexer));
//  }
//  protected Value eval(Value value) {
//    return value.eval(env);
//  }
//  protected Value eval(java.lang.String source) {
//    return createParser(source).datumWs().value.eval(env);
//  }
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
  public void testSelfEvaluating1() throws SchemeException {
    Value value = eval("#t");
    assertEquals(Boolean.TRUE, value);
  }
  @Test
  public void testSelfEvaluating2() throws SchemeException {
    Value value = eval("123");
    assertEquals(new LongInteger(123, true), value);
  }
  @Test
  public void testSelfEvaluating3() throws SchemeException {
    Value value = eval("12/34");
    assertEquals(new RationalPair(new LongInteger(12, true), new LongInteger(34, true), true), value);
  }
  @Test
  public void testSelfEvaluating4() throws SchemeException {
    Value value = eval("12e34");
    assertEquals(new DoubleReal(12e34), value);
  }
  @Test
  public void testSelfEvaluating5() throws SchemeException {
    Value value = eval("12+34i");
    assertEquals(new Complex(new LongInteger(12, true), new LongInteger(34, true), true), value);
  }
  @Test
  public void testSelfEvaluating6() throws SchemeException {
    Value value = eval("1/2+3/4i");
    assertEquals(new Complex(new RationalPair(new LongInteger(1,true), new LongInteger(2, true), true),
                             new RationalPair(new LongInteger(3,true), new LongInteger(4, true), true), true),
                 value);
  }
  @Test
  public void testSelfEvaluating7() throws SchemeException {
    Value value = eval("#(1 2 3)");
    List<Value> list = new ArrayList<Value>();
    list.add(new LongInteger(1, true));
    list.add(new LongInteger(2, true));
    list.add(new LongInteger(3, true));
    assertEquals(new Vector(list), value);
  }
  @Test
  public void testSelfEvaluating8() throws SchemeException {
    Value value = eval("#\\x");
    assertEquals(new Character('x'), value);
  }
  @Test
  public void testSelfEvaluating9() throws SchemeException {
    Value value = eval("\"Hello\"");
    assertEquals(new String("Hello"), value);
  }
  @Test
  public void testSelfEvaluating10() throws SchemeException {
    Value value = eval("#u8(1 2 3)");
    List<Byte> list = new ArrayList<Byte>();
    list.add((byte) 1);
    list.add((byte) 2);
    list.add((byte) 3);
    assertEquals(new ByteVector(list), value);
  }
  @Test
  public void testQuote1() throws SchemeException {
      Value value = eval("'(1 2 3)");
      Pair list = new Pair(new LongInteger(1, true), new Pair(new LongInteger(2, true), new Pair(new LongInteger(3, true), Nil.NIL)));
      assertEquals(list, value);
  }
  @Test
  public void testQuote2() throws SchemeException {
      Value value = eval("'(+ 1 2)");
      Pair list = new Pair(new Identifier("+"), new Pair(new LongInteger(1, true), new Pair(new LongInteger(2, true), Nil.NIL)));
      assertEquals(list, value);
  }
  @Test
  public void testDefine1() throws SchemeException {
    eval("(define test 123)");
    assertEquals(eval("123"), eval("test"));
  }
  @Test
  public void testDefine2() throws SchemeException {
    eval("(define (test x) x)");
    assertEquals(eval("123"), eval("(test 123)"));
  }
  @Test
  public void testDefine3() throws SchemeException {
    eval("(define (test . x) x)");
    assertEquals(eval("'(1 2 3)"), eval("(test 1 2 3)"));
  }
  @Test
  public void testLambda1() throws SchemeException {
    //env.define(new Identifier("rev"), eval("(lambda (list) ((define (rev2 src dst) (if (null? src) dst (rev2 (cdr src) (cons (car src) dst)))) (rev2 list '())))"));
    //assertEquals(eval("'(3 2 1)"), eval("(rev '(1 2 3))"));
    assertEquals(eval("'(foo bar)"), eval("((lambda x x) 'foo 'bar)"));
  }
  @Test
  public void testLambda2() throws SchemeException {
    assertEquals(eval("'foo"), eval("((lambda (x) x) 'foo)"));
  }
  @Test
  public void testLambda4() throws SchemeException {
    assertEquals(eval("'foo"), eval("((lambda (x y) y x) 'foo 'bar)"));
  }
  @Test
  public void testLet1() throws SchemeException {
    assertEquals(eval("6"), eval("(let ((a 1) (b 2)) (define c 3) (+ a b c) (* a b c))"));
  }
  @Test
  public void testLet2() throws SchemeException {
    assertSchemeException(new Schemer() { public void run() throws SchemeException { eval("(let ((a 1) (b a)) (define c 3) (+ a b c) (* a b c))"); }});
  }
  @Test
  public void testLet3() throws SchemeException {
    assertSchemeException(new Schemer() { public void run() throws SchemeException { eval("(let ((a b) (b 2)) (define c 3) (+ a b c) (* a b c))"); }});
  }
  @Test
  public void testLetStar1() throws SchemeException {
    assertEquals(eval("6"), eval("(let* ((a 1) (b 2)) (define c 3) (+ a b c) (* a b c))"));
  }
  @Test
  public void testLetStar2() throws SchemeException {
    assertEquals(eval("3"), eval("(let* ((a 1) (b a)) (define c 3) (+ a b c) (* a b c))"));
  }
  @Test
  public void testLetStar3() throws SchemeException {
    assertSchemeException(new Schemer() { public void run() throws SchemeException { eval("(let* ((a b) (b 2)) (define c 3) (+ a b c) (* a b c))"); }});
  }
  @Test @Ignore
  public void testLetRec1() throws SchemeException {
    assertEquals(eval("'(a b a b a b)"),
        eval("(letrec ((a b) (b c) (c 1)) a)"));
//    assertEquals(eval("'(a b a b a b)"),
//        eval("(letrec ((a (lambda (n) (if (zero? n) '() (cons 'a (b (- n  1))))))" +
//                      "(b (lambda (n) (if (zero? n) '() (cons 'b (a (- n  1)))))))" +
//               "(a 6))"));
    // (letrec (
    //   (a 
    //     (lambda (n)
    //       (if (zero? n) '() (cons 'a (b (- n  1))))
    //     ))
    //   (b
    //     (lambda (n)
    //       (if (zero? n) '() (cons 'b (a (- n  1))))
    //     ))
    //   )
    //  (a 6))"));
  }
  @Test
  public void testCond() throws SchemeException {
    assertEquals(eval("'greater"), eval("(cond ((> 3 2) 'greater) ((< 3 2) 'less))"));
    assertEquals(eval("'equal"), eval("(cond ((> 3 3) 'greater) ((< 3 3) 'less) (else 'equal))"));
    assertEquals(eval("2"), eval("(cond ((assv 'b '((a 1) (b 2))) => cadr) (else #f))"));
  }
  @Test
  public void testCase() throws SchemeException {
    assertEquals(eval("'composite"), eval("(case (* 2 3) ((2 3 5 7) 'prime) ((1 4 6 8 9) 'composite))"));
    assertEquals(eval("'c"), eval("(case (car '(c d)) ((a e i o u) 'vowel) ((w y) 'semivowel) (else => (lambda (x) x)))"));
  }
  @Test
  public void testAnd() throws SchemeException {
   // assertEquals(eval("#t"), eval("(and (= 2 2) (> 2 1))"));
    //assertEquals(eval("#f"), eval("(and (= 2 2) (< 2 1))"));
    assertEquals(eval("'(f g)"), eval("(and 1 2 'c '(f g))"));
    assertEquals(eval("#t"), eval("(and)"));
  }
  @Test
  public void testOr() throws SchemeException {
    assertEquals(eval("#t"), eval("(or (= 2 2) (> 2 1))"));
    assertEquals(eval("#t"), eval("(or (= 2 2) (< 2 1))"));
    assertEquals(eval("#f"), eval("(or #f #f #f)"));
    assertEquals(eval("'(b c)"), eval("(or (memq 'b '(a b c)) (/ 3 0))"));
  }
  @Test
  @Ignore
  // TODO not done with continuation based eval
  public void testQuasiQuote() throws SchemeException {
    assertEquals(eval("'x"), eval("`x"));
    assertEquals(eval("17"), eval("(let ((x 17)) `,x)"));
    assertEquals(eval("'(1 2 3 4)"), eval("`(1 ,@'(2 3) 4)"));
    assertEquals(eval("'(list 3 4)"), eval("`(list ,(+ 1 2) 4)"));
    assertEquals(eval("'(list a 'a)"), eval("(let ((name 'a)) `(list ,name ',name))"));
    //assertEquals(eval("'(a 3 4 5 6 b)"), eval("`(a ,(+ 1 2) ,@(map abs '(4 -5 6)) b)"));
    assertEquals(eval("'((foo 7) . cons)"), eval("`(( foo ,(- 10 3)) ,@(cdr '(c)) . ,(car '(cons)))"));
    //assertEquals(eval("#(10 5 2 4 3 8)"), eval("`#(10 5 ,(sqrt 4) ,@(map sqrt '(16 9)) 8)"));
    //assertEquals(eval("'(list foo bar baz)"), eval("(let ((foo '(foo bar)) (@baz 'baz)) `(list ,@foo , @baz))"));
    assertEquals(eval("'(a `(b ,(+ 1 2) ,(foo 4 d) e) f)"), eval("`(a `(b ,(+ 1 2) ,(foo ,(+ 1 3) d) e) f)"));
    assertEquals(eval("'(a `(b ,x ,'y d) e)"), eval("(let ((name1 'x) (name2 'y)) `(a `(b ,,name1 ,',name2 d) e))"));
  }
  interface Schemer {
    void run() throws SchemeException;
  }
  protected static void assertSchemeException(Schemer runnable) {
    try {
      runnable.run();
    } catch (SchemeException x) {
      return;
    }
    assertTrue("Expected SchemeException", false);
  }

}
