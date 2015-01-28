package se.pp.forsberg.scheme.values;

import java.util.List;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.macros.Keyword;

public class Pair extends Value {
  private Value car, cdr;
  public Pair(Value car, Value cdr) {
    this.car = car;
    this.cdr = cdr;
  }
  public static Value makeList(List<Value> values) {
    return makeList(values.toArray(new Value[0]));
  }
  public static Value makeList(Value[] array) {
    return makeList(array, 0);
  }
  protected static Value makeList(Value[] array, int i) {
    if (i >= array.length) return Nil.NIL;
    return new Pair(array[i], makeList(array, i+1));
  }
  public Value getCar() { return car; }
  public Value getCdr() { return cdr; }
  public void setCar(Value car) { this.car = car; }
  public void setCdr(Value cdr) { this.cdr = cdr; }
  
  // First attempt at handling continuations
  // I use the java call stack as the scheme call stack (by recursively
  // calling eval/apply in the java code)
  // In order to handle calling a continuation without changing this, I need to
  // 1) unwind the call stack
  // 2) rebuild the call stack until I'm at the place in the evaluation specified
  // Unwinding is simply done with an exception that's only caught at the top
  // level.
  // Rebuilding is worse...
  // Solution: Change eval() to have a continuation as a parameter.
  // Continuation is a Fifo of decisions.
  // Whenever a decision or recursion is taken in the parser, the continuation
  // stacks the information necessary to later replay the action.
  // Add method replay(Continuation )
  // When a continuation is applied, it throws an
  // exception to return to top level, where replay() is called, using stacked
  // decisions to return to the correct state. 
  // What IS a decision then? Well, whatever is needed to replay the
  // evaluation! I started with an empty object, then walked through the evaluation
  // process, adding data as needed... 
  
  // State PAIR
  // Pair evaluation can lead to the following:
  // 1) Procedure application
  // 1a) List evaluation (ie. eval of car and cdr recursive
  //     Record: No data accumulated yet. State EVAL_LIST
  //     Replay: evalList()
  // 1b) Application of evaluated car to recursively evaluated cdr
  //     Record: Evaluated list. State APPLY
  //     Replay: list.car.apply(list.cdr)
  // 2) Keyword application
  //     Record: Keyword, this. State KEYWORD
  //     Replay: keyword.apply(this)
  // 3) Quote evaluation
  //     Record: N/A continuations cannot be called from within a quote expression.
  //     Replay: N/A
  // 4) Quasi-quote evaluation
  //     Record: No data accumulated yet. Quasi-quote level 1. State QUASIQUOTE
  //     Replay: evalQuasiQuote()
  // 5) Cond-expand evaluation
  //     Record: TODO
  //     Replay: TODO
  
  
//  @Override
//  public Value replay(Continuation replay, Continuation continuation) {
//    Decision d = replay.pop();
//    if (replay.isEmpty()) return continuation.getResult();
//    if (d instanceof KeywordDecision) {
//      KeywordDecision decision = (KeywordDecision) d;
//      Keyword keyword = decision.getKeyword();
//      Pair pair = decision.getPair();
//      Environment env = decision.getEnvironment();
//      continuation.recordKeyword(keyword, pair, env);
//      try {
//       return decision.getKeyword().replayApply(decision.getPair(), decision.getEnvironment(), replay, continuation);
//      } finally {
//        continuation.pop();
//      }
//    } else if (d instanceof QuasiQuoteDecision) {
//      QuasiQuoteDecision decision = (QuasiQuoteDecision) d;
//      Environment env = decision.getEnvironment();
//      continuation.recordQuasiQuote(env, 1);
//      try {
//        return replayQuasiQuote(env, 1, replay, continuation);
//      } finally {
//        continuation.pop();
//      }
//    } else if (d instanceof EvalListDecision) {
//      EvalListDecision decision = (EvalListDecision) d;
//      Environment env = decision.getEnvironment();
//      continuation.recordEvalList(this, env);
//      try {
//        return replayEvalList(this, env, replay, continuation);
//      } finally {
//        continuation.pop();
//      }
//    } else if (d instanceof ApplyDecision) {
//      ApplyDecision decision = (ApplyDecision) d;
//      Procedure operator = decision.getOperator();
//      Value operands = decision.getOperands();
//      continuation.recordApply(operator, operands);
//      try {
//        return operator.replayApply(operands, replay, continuation);
//      } finally {
//        continuation.pop();
//      }
//    } else {
//      throw new IllegalArgumentException("Continuation implementation problem: Expected one of keyword, quasiquote, evallist, apply. Got " + d);
//    }
//  }
  @Override
  public Value eval(Environment env) {
    if (car.isIdentifier()) {
      Identifier id = (Identifier) car;
      java.lang.String s = id.getIdentifier();
      Value v = env.lookup(id);
      if (v != null && v instanceof Keyword) {
        Keyword keyword = (Keyword) v;
//        continuation.recordKeyword(keyword, this, env);
//        try {
          return keyword.apply(this, env).eval(env);
//        } finally {
//          continuation.pop();
//        }
      }
      //if (keyword.equals("define") && env.isTopLevel()) return evalTopLevelDefine(env);
      //if (keyword.equals("let")) return evalLet(env);
     // if (keyword.equals("let*")) return evalLetStar(env);
      //if (keyword.equals("letrec")) return evalLetrec(env);
      //if (keyword.equals("letrec*")) return evalLetrecStar(env);
      if (s.equals("quote")) return evalQuote();
      //if (keyword.equals("lambda")) return evalLambda(env);
      //if (keyword.equals("if")) return evalIf(env);
      //if (keyword.equals("set!")) return evalSet(env);
//      if (keyword.equals("cond")) return evalCond(env);
//      if (keyword.equals("case")) return evalCase(env);
//      if (keyword.equals("and")) return evalAnd(env);
//      if (keyword.equals("or")) return evalOr(env);
//      if (keyword.equals("when")) return evalWhen(env);
//      if (keyword.equals("unless")) return evalUnless(env);
      if (s.equals("quasi-quote")) {
//        continuation.recordQuasiQuote(env, 1);
//        try {
          return evalQuasiQuote(env, 1);
//        } finally {
//          continuation.pop();
//        }
      }
      // TODO cond-expand
    }
    // Normal procedure application (x y z)
//    continuation.recordEvalList(env);
    Value evalResult;
//    try {
      evalResult = evalList(this, env);
//    } finally {
//      continuation.pop();
//    }
    Procedure operator;
    Value operands;
//    try {
      if (!evalResult.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("What just happened?")));
      Pair list = (Pair) evalResult;
     
      if (!list.car.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected a procedure instead of " + car)));
      operator = (Procedure) list.car;
      operands = list.cdr;
      if (!list.cdr.isPair() && ! list.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument list instead of " + car)));
//    } finally {
//      continuation.pop();
//    }
//    continuation.recordApply(operator, operands);
//    try {
      return operator.apply(operands);
//    } finally {
//      continuation.pop();
//    }
  }
//  protected Value evalSet(Environment env) {
//    {
//      if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed set!")));
//      Pair pair = (Pair) cdr;
//      if (!pair.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed set!")));
//      Identifier id = (Identifier) pair.car;
//      if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed set!")));
//      pair = (Pair) cdr;
//      if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed set!")));
//      env.set(id, pair.car.eval(env));
//      return Nil.NIL;
//    }
//  }
//  protected Value evalIf(Environment env) {
//    {
//      if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed if")));
//      Pair pair = (Pair) cdr;
//      Value test = pair.car;
//      if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed if")));
//      pair = (Pair) pair.cdr;
//      Value consequent = pair.car;
//      Value alternative = null;
//      if (!pair.cdr.isNull()) {
//        if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed if")));
//        pair = (Pair) pair.cdr;
//        alternative = pair.car;
//      }
//      if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed if")));
//      if (test.eval(env).asBoolean()) {
//        return consequent.eval(env);
//      } else {
//        if (alternative != null) return alternative.eval(env);
//      }
//      return Nil.NIL;
//    }
//  }
//  protected Value evalLambda(Environment env) {
//    {
//      if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed lambda")));
//      Pair pair = (Pair) cdr;
//      Value formals = pair.car;
//      Value body = pair.cdr;
//      return new Lambda(formals, body, env);
//    }
//  }
  protected Value evalQuote() {
    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quote")));
    Pair pair = (Pair) cdr;
    if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quote")));
    return pair.car;
  }
//  protected Value evalLet(Environment env) {
//    // (let ((x 1) (y 2)) body)
//    // Semantics: variable values are evaluated in parent env and defined in new env for body
//    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//    Pair pair = (Pair) cdr;
//    Value bindings = pair.car;
//    Value body = pair.cdr;
//    Environment letEnv = new Environment(env);
//    while (!bindings.isNull()) {
//     if (!bindings.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//     pair = (Pair) bindings;
//     bindings = pair.cdr;
//     if (!pair.car.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//     Pair binding = (Pair) pair.car;
//     if (!binding.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//     Identifier id = (Identifier) binding.car;
//     if (!binding.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//     pair = (Pair) binding.cdr;
//     if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let")));
//     Value value = pair.car;
//     if (letEnv.contains(id)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " defined more than once in let")));
//     letEnv.define(id, value.eval(env));
//    }
//    return evalBody(body, letEnv);
//  }
//  private Value evalLetStar(Environment env) {
//    // (let* ((x 1) (y 2)) body)
//    // Semantics: each variables value is evaluated in an env where previous definitions are visible
//    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//    Pair pair = (Pair) cdr;
//    Value bindings = pair.car;
//    Value body = pair.cdr;
//    while (!bindings.isNull()) {
//     if (!bindings.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//     pair = (Pair) bindings;
//     bindings = pair.cdr;
//     if (!pair.car.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//     Pair binding = (Pair) pair.car;
//     if (!binding.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//     Identifier id = (Identifier) binding.car;
//     if (!binding.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//     pair = (Pair) binding.cdr;
//     if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed let*")));
//     Value value = pair.car;
//     Environment newEnv = new Environment(env);
//     newEnv.define(id, value.eval(env));
//     env = newEnv;
//    }
//    return evalBody(body, env);
//  }
//  private Value evalLetrec(Environment env) {
//    // (letrec ((x 1) (y 2)) body)
//    // Semantics: each variables value is evaluated in the new env for body
//    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//    Pair pair = (Pair) cdr;
//    Value bindings = pair.car;
//    Value body = pair.cdr;
//    env = new Environment(env);
//    while (!bindings.isNull()) {
//     if (!bindings.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//     pair = (Pair) bindings;
//     bindings = pair.cdr;
//     if (!pair.car.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//     Pair binding = (Pair) pair.car;
//     if (!binding.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//     Identifier id = (Identifier) binding.car;
//     if (!binding.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//     pair = (Pair) binding.cdr;
//     if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed letrec")));
//     Value value = pair.car;
//     if (env.contains(id)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " defined more than once in letrec")));
//     env.define(id, value.eval(env));
//    }
//    return evalBody(body, env);
//  }
//  private Value evalLetrecStar(Environment env) {
//    // TODO understand the distinction between letrec and letrec* ...
//    return evalLetrec(env);
//  }
//  protected Value evalBody(Value body, Environment env) {
//    if (body.isNull()) return body;
//    boolean first = true;
//    while (!body.isNull()) {
//      if (!body.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed body")));
//      Pair pair = (Pair) body;
//      Value next = pair.cdr;
//      if (!pair.car.isPair()) break;
//      pair = (Pair) pair.car;
//      if (!pair.car.isIdentifier()) break;
//      java.lang.String keyword = ((Identifier) pair.car).getIdentifier();
//      if (keyword.equals("define")) {
//        if (first) {
//          env = new Environment(env);
//          first = false;
//        }
//        if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//        pair = (Pair) pair.cdr;
//        if (pair.car.isIdentifier()) {
//          Identifier id = (Identifier) pair.car;
//          if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//          pair = (Pair) pair.cdr;
//          if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//          env.define(id, pair.car.eval(env));
//        } else if (pair.car.isPair()) {
//          // (define (x y z)
//          Pair formals = (Pair) pair.car;
//          if (!formals.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//          Identifier id = (Identifier) formals.car;
//          Value lambdaBody = pair.cdr;
//          env.define(id, new Lambda(formals.cdr, lambdaBody, env));
//        } else {
//          throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//        }
//      } else {
//        // TODO other definitions than define
//        break;
//      }
//      body = next;
//    }
//    Value expressions = body;
//    if (expressions.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Empty body")));
//    Value result = null;;
//    while (!expressions.isNull()) {
//      if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed body")));
//      Pair pair = (Pair) expressions;
//      result = pair.car.eval(env);
//      expressions = pair.cdr;
//    }
//    return result;
//  }
//  protected Value evalTopLevelDefine(Environment env) {
//    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//    Pair pair = (Pair) cdr;
//    if (pair.car.isIdentifier()) {
//      Identifier id = (Identifier) pair.car;
//      if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//      pair = (Pair) pair.cdr;
//      if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//      env.define(id, pair.car.eval(env));
//      return Value.UNSPECIFIED;
//    } else if (pair.car.isPair()) {
//      // (define (x y z)
//      Pair formals = (Pair) pair.car;
//      if (!formals.car.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//      Identifier id = (Identifier) formals.car; 
//      Value body = pair.cdr;
//      env.define(id, new Lambda(formals.cdr, body, env));
//      return Value.UNSPECIFIED;
//    } else {
//      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed define")));
//    }
//  }
//  protected Value evalCond(Environment env) {
//    Value result = Value.UNSPECIFIED;
//    Value clauses = cdr;
//    while (!clauses.isNull()) {
//      if (!clauses.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//      Pair pair = (Pair) clauses;
//      clauses = pair.cdr;
//      Value clause = pair.car;
//      if (!clause.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//      pair = (Pair) clause;
//      Value test = pair.car;
//      if (test.isIdentifier() && ((Identifier) test).getIdentifier().equals("else")) {
//        if (pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//        Value expressions = pair.cdr;
//        while (!expressions.isNull()) {
//          if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//          pair = (Pair) expressions;
//          result = pair.car.eval(env);
//          expressions = pair.cdr;
//        }
//        return result;
//      } else {
//        Value value = test.eval(env);
//        if (value.asBoolean()) {
//          Value expressions = pair.cdr;
//          result = value;
//          if (expressions.isPair()) {
//            Pair first = (Pair) expressions;
//            if (first.car.isIdentifier() && ((Identifier) first.car).getIdentifier().equals("=>")) {
//              if (!first.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//              Pair second = (Pair) first.cdr;
//              if (!second.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//              Value proc = second.car.eval(env);
//              if (!proc.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//              return ((Procedure)proc).apply(new Pair(value, Nil.NIL));
//            }
//          }
//          while (!expressions.isNull()) {
//            if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed cond")));
//            pair = (Pair) expressions;
//            result = pair.car.eval(env);
//            expressions = pair.cdr;
//          }
//          return result;
//        }
//      }
//    }
//    return result;
//  }
//  protected Value evalCase(Environment env) {
//    Value key = cdr;
//    Value result = Value.UNSPECIFIED;
//    if (!key.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//    Pair pair = (Pair) key;
//    Value keyVal = pair.car.eval(env);
//    Value clauses = pair.cdr;
//    while (!clauses.isNull()) {
//      if (!clauses.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//      pair = (Pair) clauses;
//      Value clause =  pair.car;
//      clauses = pair.cdr;
//      if (!clause.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//      pair = (Pair) clause;
//      Value test = pair.car;
//      if (test.isIdentifier() && ((Identifier) test).getIdentifier().equals("else")) {
//        if (pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//        Value expressions = pair.cdr;
//        if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//        pair = (Pair) expressions;
//        if (pair.car.isIdentifier() && ((Identifier) pair.car).getIdentifier().equals("=>")) {
//          if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//          pair = (Pair) pair.cdr;
//          if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//          Value proc = pair.car.eval(env);
//          if (!proc.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//          return ((Procedure)proc).apply(new Pair(keyVal, Nil.NIL));
//        }
//        while (!expressions.isNull()) {
//          if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//          pair = (Pair) expressions;
//          expressions = pair.cdr;
//          Value expression = pair.car;
//          result = expression.eval(env);
//        }
//        return result;
//      }
//      Value expressions = pair.cdr;
//      while (!test.isNull()) {
//        if (!test.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//        pair = (Pair) test;
//        test = pair.cdr;
//        if (pair.car.eqv(keyVal)) {
//          if (expressions.isPair()) {
//            pair = (Pair) expressions;
//            if (pair.car.isIdentifier() && ((Identifier)pair.car).getIdentifier().equals("=>")) {
//              if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//              pair = (Pair) pair.cdr;
//              if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//              Value proc = pair.car.eval(env);
//              if (!proc.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//              return ((Procedure)proc).apply(new Pair(keyVal, Nil.NIL));
//            }
//          }
//          while (!expressions.isNull()) {
//            if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed case")));
//            pair = (Pair) expressions;
//            expressions = pair.cdr;
//            Value expression = pair.car;
//            result = expression.eval(env);
//          }
//          return result;
//        }
//      }
//    }
//    return result;
//  }
//  protected Value evalAnd(Environment env) { 
//    Value tests = cdr;
//    Value value = Boolean.TRUE;
//    while (!tests.isNull()) {
//      if (!tests.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed and")));
//      Pair pair = (Pair) tests;
//      value = pair.car.eval(env);
//      if (!value.asBoolean()) return Boolean.FALSE;
//      tests = pair.cdr;
//    }
//    return value;
//  }
//  protected Value evalOr(Environment env) { 
//    Value tests = cdr;
//    while (!tests.isNull()) {
//      if (!tests.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed or")));
//      Pair pair = (Pair) tests;
//      Value value = pair.car.eval(env);
//      if (value.asBoolean()) return value;
//      tests = pair.cdr;
//    }
//    return Boolean.FALSE;
//  }
//  protected Value evalWhen(Environment env) { 
//    if (!car.eval(env).asBoolean()) return Value.UNSPECIFIED;
//    Value expressions = cdr;
//    
//    while (!expressions.isNull()) {
//      if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed when")));
//      Pair pair = (Pair) expressions;
//      pair.car.eval(env);
//      expressions = pair.cdr;
//    }
//    return Value.UNSPECIFIED;
//  }
//  protected Value evalUnless(Environment env) { 
//    if (car.eval(env).asBoolean()) return Value.UNSPECIFIED;
//    Value expressions = cdr;
//    
//    while (!expressions.isNull()) {
//      if (!expressions.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed unless")));
//      Pair pair = (Pair) expressions;
//      pair.car.eval(env);
//      expressions = pair.cdr;
//    }
//    return Value.UNSPECIFIED;
//  }

//  private Value replayQuasiQuote(Environment env, int i, Continuation replay, Continuation continuation) {
//    // TODO Auto-generated method stub
//    return null;
//  }
  protected Value evalQuasiQuote(Environment env, int level) {
    if (!cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
    Pair pair = (Pair) cdr;
    if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
    return evalQuasiQuote(env, pair.car, level);
  }
  protected Value evalQuasiQuote(Environment env, Value value, int level) {
    // TODO record
    if (level == 0) return value.eval(env);
    if (!value.isPair()) return value;
    Pair pair = (Pair) value;
    if (pair.car.isIdentifier()) {
      Identifier id = (Identifier) pair.car;
      if (id.getIdentifier().equals("quasi-quote")) {
        return new Pair(id, new Pair(pair.evalQuasiQuote(env, level+1), Nil.NIL));
      }
    }
    if (!value.isPair()) return value;
    pair = (Pair) value;
    if (pair.car.isIdentifier()) {
      Identifier id = (Identifier) pair.car;
      if (id.getIdentifier().equals("unquote") || id.getIdentifier().equals("unquote-splicing")) {
        if (!pair.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
        pair = (Pair) pair.cdr;
        if (!pair.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
        // TODO record
        if (level == 1) return pair.car.eval(env);
        return new Pair(id, new Pair(evalQuasiQuote(env, pair.car, level-1), Nil.NIL));
      }
    }
    // ((unquote-splicing (x)) y z) -> (x y z)
    if (pair.car.isPair()) {
      Pair unquote = (Pair) pair.car;
      if (unquote.car.isIdentifier() && ((Identifier) unquote.car).getIdentifier().equals("unquote-splicing")) {
        if (!unquote.cdr.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
        unquote = (Pair) unquote.cdr;
        if (!unquote.cdr.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
        return concat(evalQuasiQuote(env, unquote.car, level-1), evalQuasiQuote(env, pair.cdr, level));
      }
    }
    return new Pair(evalQuasiQuote(env, pair.car, level), evalQuasiQuote(env, pair.cdr, level));
  }
  protected Value concat(Value list, Value rest) {
    if (list.isNull()) return rest;
    if (!list.isPair())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
    Pair pair = (Pair) list;
    while (!pair.cdr.isNull()) {
      if (!pair.cdr.isPair())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed quasi-quote")));
      pair = (Pair) pair.cdr;
    }
    pair.cdr = rest;
    return list;
  }

//  protected Value replayEvalList(Value list, Environment env, Continuation replay, Continuation continuation) {
//    Decision d = replay.pop();
//    if (replay.isEmpty()) return continuation.getResult();
//    if (d instanceof CarDecision) {
//      continuation.recordCar();
//      Value car;
//      try {
//        car = ((Pair)list).car.replay(replay, continuation);
//      } finally {
//        continuation.pop();
//      }
//      continuation.recordCdr(car);
//      try {
//        return new Pair(car, evalList(((Pair)list).cdr, env, continuation));
//      } finally {
//        continuation.pop();
//      }
//    } else if (d instanceof CdrDecision) {
//      CdrDecision decision = (CdrDecision) d;
//      Value car = decision.getCar();
//      continuation.recordCdr(car);
//      try {
//        return new Pair(car, replayEvalList(((Pair)list).cdr, env, replay, continuation));
//      } finally {
//        continuation.pop();
//      }
//    } else {
//      throw new IllegalArgumentException("Continuation implementaion problem: Expected car or cdr, got " + d);
//    }
//  }
  protected Value evalList(Value list, Environment env) {
   if (list.isNull()) return Nil.NIL;
   if (!list.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal function call, expected argument list")));
//   continuation.recordCar();
   Value car;
//   try {
     car = ((Pair)list).car.eval(env);
//   } finally {
//     continuation.pop();
//   }
//   continuation.recordCdr(car);
//   try {
     return new Pair(car, evalList(((Pair)list).cdr, env));
//   } finally {
//     continuation.pop();
//   }
  }
  
  @Override
  public boolean isPair() {
    return true;
  }
  @Override
  public java.lang.String toString() {
    if (car.isIdentifier() && cdr.isPair()) {
      Identifier id = (Identifier) car;
      Pair value = (Pair) cdr;
      if (id.getIdentifier().equalsIgnoreCase("quote")) {
        return "'" + value.car.toString();
      }
      if (id.getIdentifier().equalsIgnoreCase("quasi-quote")) {
        return "`" + value.car.toString();
      }
      if (id.getIdentifier().equalsIgnoreCase("unquote")) {
        return "," + value.car.toString();
      }
      if (id.getIdentifier().equalsIgnoreCase("unquote-splicing")) {
        return ",@" + value.car.toString();
      }
    }
    StringBuffer result = new StringBuffer();
    result.append('(');
    boolean first = true;
    for (Pair p = this; true; p = (Pair) p.cdr) {
      if (first) {
        first = false;
      } else {
        result.append(' ');
      }
      result.append(p.car);
      if (!p.cdr.isPair()) {
        if (!p.cdr.isNull()) {
          result.append(" . ");
          result.append(p.cdr);
        }
        break;
      }
    }
    result.append(")");
    return result.toString();
  }
  @Override
  public boolean equal(Value value) {
    if (eqv(value)) return true;
    if (!value.isPair()) return false;
    Pair other = (Pair) value;
    return car.equal(other.car) && cdr.equal(other.cdr);
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Value)) return false;
    return equal((Value)obj);
  }
  @Override
  public int hashCode() {
    return car.hashCode() ^ cdr.hashCode();
  }
}
