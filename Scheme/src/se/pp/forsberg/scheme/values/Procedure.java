package se.pp.forsberg.scheme.values;

import se.pp.forsberg.scheme.Op;

public abstract class Procedure extends Value {
  public Procedure() {
  }
  
  abstract public Value apply(Value arguments);
  //abstract public Value replayApply(Value arguments, Continuation replay, Continuation continuation);
//  public Op apply(Op parent, Environment env, Value v[]) {
//    if (v.length != 1) return parent.getEvaluator().error("Multiple return values used in a single value context", Pair.makeList(v));
//    return apply(parent, env, v[0]);
//  }
  abstract public Op apply(Op parent, Environment env, Value v);
  
  @Override
  public boolean isProcedure() {
    return true;
  }

  @Override
  public java.lang.String toString() {
    return "[Procedure]";
  }

  
}
