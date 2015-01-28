//package se.pp.forsberg.scheme.values.continuations;
//
//import java.util.ArrayDeque;
//import java.util.Deque;
//
//import se.pp.forsberg.scheme.ContinuationException;
//import se.pp.forsberg.scheme.values.Environment;
//import se.pp.forsberg.scheme.values.Pair;
//import se.pp.forsberg.scheme.values.Procedure;
//import se.pp.forsberg.scheme.values.Value;
//import se.pp.forsberg.scheme.values.macros.Keyword;
//
//public class Continuation extends Value {
//  
//  Deque<Decision> decisions = new ArrayDeque<Decision>();
//  private Value result;
//  
//  public Continuation() {};
//  
//  public void apply(Value result) {
//    setResult(result);
//    throw new ContinuationException(this);
//  }
//
//  public Value getResult() { return result; }
//  private void setResult(Value result) { this.result = result; }
//
//  @Override
//  public Value eval(Environment env, Continuation continuation) {
//    return this;
//  }
//  @Override
//  public Value replay(Continuation replay, Continuation continuation) {
//    throw new IllegalArgumentException("Continuation implementation problem: Reached self-evaluating object but more to replay");
//  }
//
//  @Override
//  public boolean equal(Value value) {
//    return eqv(value);
//  }
//
//  @Override
//  public boolean eqv(Value value) {
//    return this == value;
//  }
//
//  @Override
//  public boolean eq(Value value) {
//    return eqv(value);
//  }
//
//
//  public Decision pop() {
//    return decisions.pop();
//  }
//  public boolean isEmpty() {
//    return decisions.isEmpty();
//  }
//  
//  public void recordTopLeve(Environment env) {
//    decisions.add(new TopLevelDecision(env));
//  }
//  public void recordKeyword(Keyword keyword, Pair pair, Environment env) {
//    decisions.add(new KeywordDecision(keyword, pair, env));
//  }
//  public void recordEvalList(Value list, Environment env) {
//    decisions.add(new EvalListDecision(list, env));
//  }
//  public void recordApply(Procedure operator, Value operands) {
//    decisions.add(new ApplyDecision(operator, operands));
//  }
//  public void recordQuasiQuote(Environment env, int i) {
//    decisions.add(new QuasiQuoteDecision(env, i));
//  }
//  public void recordCar() {
//    decisions.add(new CarDecision());
//  }
//  public void recordCdr(Value car) {
//    decisions.add(new CdrDecision(car));
//  }
//}
