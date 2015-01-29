package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.macros.Keyword;

//
//import java.util.HashSet;
//import java.util.Set;
//
//import se.pp.forsberg.scheme.values.Environment;
//import se.pp.forsberg.scheme.values.Identifier;
//import se.pp.forsberg.scheme.values.Value;
//
//public class Evaluator {
//  final Set<EvalStep> todo = new HashSet<EvalStep>();
//  interface ValueHandler {
//    void handleValue(Value value);
//  }
//  class Continuation {
//    Continuation parent;
//    ValueHandler handler;
//    Runnable next;
//    public Continuation(Continuation parent, ValueHandler handler, Runnable next) {
//      this.parent = parent;
//      this.handler = handler;
//      this.next = next;
//    }
//  }
//  abstract class EvalStep {
//    Continuation cc;
//    public EvalStep(Continuation cc) {
//      this.cc = cc;
//    }
//    abstract Value eval();
//  }
//  
//  // Try 2 at continuations
//  // Replace recursive eval/apply with a single iterative eval function maintaining
//  // a call stack of continuations.
//  // A continuation is
//  // * a place where the value being calculated should land (eg car/cdr of a pair)
//  // * how to continue evaluation
//  // * pointer to parent continuation
//  //
//  // Hmm... We could have a set of evaluation steps that are todo, each
//  public Value eval(final Value value, final Environment env) {
//    Continuation cc = null; // Null continuation == top of stack, return value
//    todo.add(new EvalStep(cc) { @Override public Value eval() {
//      return eval(value, env, null);
//      }});
//    while (true) {
//      EvalStep step = todo.iterator().next();
//      todo.remove(step);
//      Value result = step.eval();
//      if (result != null) return result;
//    }
//  }
//
//  // iterative eval step.
//  private Value eval(Value value, Environment env, Object object) {
//    if (value.isIdentifier()) {
//      return env.lookup((Identifier) value); // TODO handle error
//    }
//    if (!value.isPair()) return value;
//    
//  }
//}


/*
 * Try yet again.....
 * 
 * Need to think how continuation stack would look like
 * 
 * OPS:
 * eval id or constant      insert constant in stacked continuation
 * eval pair                stack 1 apply and 1 evallist
 * evallist '()             use '() in stacked cont
 * evallist pair            stack (cons x (evallist (cdr pair)))
 *                          stack (eval (car pair))
 * cons val (evallist _)    stack (cons val x)
 *                          stack (evallist _)
 * cons x y                 use (cons x y) in stacked cont
 * 
 * EVA
 * EVP
 * ELN
 * ELI
 * COL
 * CON
 * APP
 * DEF
 * 
 * OP        PARAM          CONTINUATIONS
 * eval      (+ (* 2 3) 4)  (return x)
 *                          (apply (car x) (cdr x))
 *                          (evallist (+ (* 2 3) 4))
 * evallist  (+ (* 2 3) 4)  (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons x (evallist ((* 2 3) 4)))
 *                          (eval +)
 * eval       +             (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (evallist ((* 2 3) 4))
 * evallist  ((* 2 3) 4)    (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (eval (* 2 3))
 * eval      (* 2 3)        (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (evallist (* 2 3))
 * evallist  (* 2 3)        (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons x (evallist (2 3)))
 *                          (eval *)
 * eval       *             (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (evallist (2 3))
 * evallist   (2 3)         (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons x (evallist (3)))
 *                          (eval 2)
 * eval       2             (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons 2 x)
 *                          (evallist (3))
 * evallist   (3)           (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons 2 x)
 *                          (cons x (evallist '())
 *                          (eval 3)
 * eval        3            (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons 2 x)
 *                          (cons 3 x)
 *                          (evallist '())
 * evallist   '()           (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons 2 x)
 *                          (cons 3 '())
 * cons       3 '()         (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * x)
 *                          (cons 2 (3))
 * cons       2 (3)         (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply (car x) (cdr x))
 *                          (cons * (2 3)
 * cons       * (2 3)       (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons x (evallist (4)))
 *                          (apply * (2 3))
 * apply      * (2 3)       (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons 6 x)
 *                          (evallist (4))
 * evallist   (4)           (return x)
 *                          (apply (car x) (cdr x))
 *                          (cons + x)
 *                          (cons 6 x)
 *                          (cons 4 x)
 *                          (evallist '())
 * evallist   '()           (return x)
 *                          (apply + (6 4))
 * apply      + (6 4)       (return 10)
 * return     10
 */

public class Evaluator {
  
  Value value;
  
  Value eval(Value v, Environment env) {
    Op stack = new Op.Eval(this, null, env);
    value = v;
    while (stack != null) {
      stack = stack.apply(value);
    }
    return value;
  }
  void setValue(Value v) {
    value = v;
  }
  Op error(String message, Value irritant) { return null; }
}