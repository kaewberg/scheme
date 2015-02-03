package se.pp.forsberg.scheme;

import java.util.ArrayDeque;
import java.util.Deque;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;

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
  private static boolean DEBUG = false;
  
  Value value = null;
  
  public Value eval(Value v, Environment env) {
    Op stack = new Op.Done(this, env);
    stack = new Op.Eval(stack);
    //value = new Value[]{v};
    value = v;
    if (DEBUG) System.out.println("Eval " + v);
    while (!(stack instanceof Op.Done)) {
      if (stack instanceof Op.ErrorOp) {
        throw new SchemeException(((Op.ErrorOp) stack).getError());
      }
      // Debug
      if (DEBUG) {
        System.out.println("-----------------------------------------------------");
        System.out.println(stack);
        System.out.println("value = " + value);
        if (stack.env != null) System.out.println("env = " + stack.env.vals());
      }
      stack = stack.apply(value);
    }
//    if (value.length != 1) {
//      return new Error("Expected single return value to eval", Pair.makeList(value));
//    }
    return value;
  }
//  public void setValue(Value v) {
//    value = new Value[]{v};
//  }
  public void setValue(Value v) {
    value = v;
  }
  public Op error(java.lang.String message, Value irritant) { return new Op.ErrorOp(this,new String(message), irritant); }
  public Op error(String message, Value irritant) { return new Op.ErrorOp(this, message, irritant); }
  public Op error(Value error) { return new Op.ErrorOp(this, error);  }
  public Value getValue() { return value; }
  public Op error(Value error, boolean continuable) { return new Op.ErrorOp(this, error, continuable); }
  
  // Called when using a continuation
  // Check in environment for dynamic-wind statements
  // If new and old env shares a common parent dynamic-wind, then use only
  // children of that
  public Op callCountinuationWithDynamicWind(Op from, Op to, Value value) {
    Deque<DynamicWind> fromStack = new ArrayDeque<DynamicWind>();
    DynamicWind wind = from.getEnvironment().getDynamicWind();
    while (wind != null) {
      fromStack.add(wind);
      wind = wind.getParent();
    }
    Deque<DynamicWind> toStack = new ArrayDeque<DynamicWind>();
    wind = to.getEnvironment().getDynamicWind();
    while (wind != null) {
      toStack.add(wind);
      wind = wind.getParent();
    }
    while (!fromStack.isEmpty() && !toStack.isEmpty() && fromStack.peek() == toStack.peek()) {
      fromStack.pop();
      toStack.pop();
    }
    // We now want to call after() in everything in fromStack
    // and before in everything in toStack
    // Inner afters should be called first
    // Outer befores should be called first
    // In other words:
    //              wind a1
    //         wind b    wind 2
    //         wind c    wind 3
    //         from       to
    // to
    // before 3
    // before 2
    // after b
    // after c
    //
    // or rather
    //
    // to
    // SetValue v
    // Apply
    // SetValue before3 ()
    // Apply
    // SetValue before2 ()
    // Apply
    // SetValue afterb ()
    // Apply
    // SetValue afterc ()
    Op result = to;
    result = new Op.SetValue(result, to.getEnvironment(), value);
    while (!toStack.isEmpty()) {
      wind = toStack.pop();
      result = new Op.Apply(result, to.getEnvironment());
      result = new Op.SetValue(result, to.getEnvironment(), new Pair(wind.getBefore(), Nil.NIL));
    }
    while (!fromStack.isEmpty()) {
      wind = fromStack.removeLast();
      result = new Op.Apply(result, from.getEnvironment());
      result = new Op.SetValue(result, from.getEnvironment(), new Pair(wind.getAfter(), Nil.NIL));
    }
    return result;
  }
  
 
}