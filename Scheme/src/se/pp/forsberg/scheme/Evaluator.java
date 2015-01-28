//package se.pp.forsberg.scheme;
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
