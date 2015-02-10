package se.pp.forsberg.scheme.builtinprocedures;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.Scheme;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Value;

public class TestPairsAndList {
  private static Environment env = Environment.schemeReportEnvironment(7);

  protected Parser createParser(java.lang.String s) {
    return new Parser(new StringReader(s));
  }
  protected Value eval(Value value) {
    return Scheme.eval(value, env);
    //return value.eval(env);
  }
  protected Value eval(java.lang.String source) {
    return eval(createParser(source).read());
  }
  

  @Test
  public void testIsPair() {
    assertEquals(Boolean.FALSE, eval("(pair? 'hello)"));
    assertEquals(Boolean.FALSE, eval("(pair? '())"));
    assertEquals(Boolean.FALSE, eval("(pair? #(1 2 3))"));
    assertEquals(Boolean.TRUE, eval("(pair? '(1 2 3))"));
    assertEquals(Boolean.TRUE, eval("(pair? '(1 . 2))"));
  }
  @Test
  public void testCons() {
    assertEquals(eval("'(1 2 3)"), eval("(cons 1 (cons 2 (cons 3 '())))"));
  }
  @Test
  public void testPair() {
    assertEquals(eval("1"), eval("(car '(1))"));
    assertEquals(eval("'()"), eval("(cdr '(1))"));
    assertEquals(eval("'()"), eval("(car '(()))"));
    assertEquals(eval("1"), eval("(car '(1 . 2))"));
    assertEquals(eval("2"), eval("(cdr '(1 . 2))"));
    assertEquals(eval("1"), eval("(caar '((1 . 2) . (3 . 4)))"));
    assertEquals(eval("2"), eval("(cdar '((1 . 2) . (3 . 4)))"));
    assertEquals(eval("3"), eval("(cadr '((1 . 2) . (3 . 4)))"));
    assertEquals(eval("4"), eval("(cddr '((1 . 2) . (3 . 4)))"));
    assertEquals(eval("1"), eval("(caaar '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("2"), eval("(cdaar '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("3"), eval("(cadar '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("4"), eval("(cddar '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("5"), eval("(caadr '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("6"), eval("(cdadr '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("7"), eval("(caddr '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("8"), eval("(cdddr '(((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))))"));
    assertEquals(eval("1"), eval("(caaaar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("2"), eval("(cdaaar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("3"), eval("(cadaar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("4"), eval("(cddaar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("5"), eval("(caadar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("6"), eval("(cdadar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("7"), eval("(caddar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("8"), eval("(cdddar '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("9"), eval("(caaadr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("10"), eval("(cdaadr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("11"), eval("(cadadr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("12"), eval("(cddadr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("13"), eval("(caaddr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("14"), eval("(cdaddr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("15"), eval("(cadddr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
    assertEquals(eval("16"), eval("(cddddr '((((1 . 2) . (3 . 4)) . ((5 . 6) . (7 . 8))) . (((9 . 10) . (11 . 12)) . ((13 . 14) . (15 . 16)))))"));
  }
  @Test
  public void testIsNull() {
    assertEquals(Boolean.FALSE, eval("(null? 'hello)"));
    assertEquals(Boolean.TRUE, eval("(null? '())"));
    assertEquals(Boolean.FALSE, eval("(null? #(1 2 3))"));
    assertEquals(Boolean.FALSE, eval("(null? '(1 2 3))"));
    assertEquals(Boolean.FALSE, eval("(null? '(1 . 2))"));
  }
  @Test
  public void testIsList() {
    assertEquals(Boolean.FALSE, eval("(list? 'hello)"));
    assertEquals(Boolean.TRUE, eval("(list? '())"));
    assertEquals(Boolean.FALSE, eval("(list? #(1 2 3))"));
    assertEquals(Boolean.TRUE, eval("(list? '(1 2 3))"));
    assertEquals(Boolean.FALSE, eval("(list? '(1 2 . 3))"));
  }
  @Test
  public void testMakeList() {
    assertEquals(eval("'()"), eval("(make-list 0)"));
    assertEquals(eval("'(2 2 2)"), eval("(make-list 3 2)"));
  }
  @Test
  public void testList() {
    assertEquals(eval("'()"), eval("(list)"));
    assertEquals(eval("'(1 2 3)"), eval("(list 1 2 3)"));
  }
  @Test
  public void testAppend() {
    assertEquals(eval("'(1 2 3)"), eval("(append '(1 2 3))"));
    assertEquals(eval("'()"), eval("(append '())"));
    assertEquals(eval("'(1 2 3)"), eval("(append '() '(1 2 3))"));
    assertEquals(eval("'(1 2 3 4 5 6)"), eval("(append '(1 2 3) '(4 5 6))"));
    assertEquals(eval("'(1 2 3 4 . 5)"), eval("(append '(1 2 3) '(4 . 5))"));
  }
  @Test
  public void testReverse() {
    assertEquals(eval("'()"), eval("(reverse '())"));
    assertEquals(eval("'(1 2 3)"), eval("(reverse '(3 2 1))"));
  }
  @Test
  public void testlistTail() {
    assertEquals(eval("'(1 2 3)"), eval("(list-tail '(1 2 3) 0)"));
    assertEquals(eval("'(2 3)"), eval("(list-tail '(1 2 3) 1)"));
    assertEquals(eval("'(3)"), eval("(list-tail '(1 2 3) 2)"));
    assertEquals(eval("'()"), eval("(list-tail '(1 2 3) 3)"));
  }
  @Test
  public void testlistRef() {
    assertEquals(eval("1"), eval("(list-ref '(1 2 3) 0)"));
    assertEquals(eval("2"), eval("(list-ref '(1 2 3) 1)"));
    assertEquals(eval("3"), eval("(list-ref '(1 2 3) 2)"));
  }
  @Test
  public void testMember() {
    assertEquals(eval("'(a b c)"), eval("(memq 'a '(a b c))"));
    assertEquals(eval("'(b c)"), eval("(memq 'b '(a b c))"));
    assertEquals(eval("#f"), eval("(memq 'd '(a b c))"));
    assertEquals(eval("#f"), eval("(memq '(a) '(b (a) c))"));
    assertEquals(eval("'((a) c)"), eval("(member '(a) '(b (a) c))"));
    assertEquals(eval("'(3 4 5)"), eval("(member 1 '(1 2 3 4 5) (lambda (y x) (= x (- y 2))))"));
  }
  @Test
  public void testlistSet() {
    assertEquals(eval("'(one two three)"), eval("(let ((ls (list 'one 'two 'five!)))(list-set! ls 2 'three)ls)"));
    Exception ex = null;
    try {
      eval("(list-set! '(0 1 2) 1 \"oops\")");
    } catch (Exception x) {
      ex = x;
    }
    assertNotNull(ex);
  }
  @Test
  public void testAssoc() {
    assertEquals(eval("'(a 1)"), eval("(assq 'a '((a 1) (b 2) (c 3)))"));
    assertEquals(eval("'(b 2)"), eval("(assq 'b '((a 1) (b 2) (c 3)))"));
    assertEquals(eval("#f"), eval("(assq 'd '((a 1) (b 2) (c 3)))"));
    assertEquals(eval("#f"),eval("(assq '(a a) '(((a a) 1) (b 2) (c 3)))"));
    assertEquals(eval("'((a a ) 1)"),eval("(assoc '(a a) '(((a a) 1) (b 2) (c 3)))"));
    assertEquals(eval("'(3 c)"), eval("(assoc 1 '((1 a) (2 b) (3 c) (4 d) (5 e)) (lambda (y x) (= x (- y 2))))"));
  }
  @Test
  public void testListCopy() {
    eval("(define a '(1 8 2 8)) ; a may be immutable");
    eval("(define b (list-copy a))");
    eval("(set-car! b 3) ; b is mutable");
    assertEquals(eval("'(1 8 2 8)"), eval("a"));
    assertEquals(eval("'(3 8 2 8)"), eval("b"));
  }
}
