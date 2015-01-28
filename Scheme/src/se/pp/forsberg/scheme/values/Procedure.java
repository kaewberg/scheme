package se.pp.forsberg.scheme.values;



public abstract class Procedure extends Value {
  public Procedure() {
  }
  
  abstract public Value apply(Value arguments);
  //abstract public Value replayApply(Value arguments, Continuation replay, Continuation continuation);
  
  @Override
  public boolean isProcedure() {
    return true;
  }

  @Override
  public java.lang.String toString() {
    return "[Procedure]";
  }
  
}
