package se.pp.forsberg.scheme.values;

import se.pp.forsberg.scheme.Op;

public abstract class Procedure extends Value {
  public Procedure() {
  }
  
  abstract public Value apply(Value arguments);
  //abstract public Value replayApply(Value arguments, Continuation replay, Continuation continuation);
  abstract public Op apply(Op parent, Environment env, Value cdr);
  
  @Override
  public boolean isProcedure() {
    return true;
  }

  @Override
  public java.lang.String toString() {
    return "[Procedure]";
  }

  
}
