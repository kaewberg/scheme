package se.pp.forsberg.scheme.values;

import se.pp.forsberg.scheme.Op;

public class Continuation extends Procedure {

  private Op op;
  //private int arity;
  public Continuation(Op op, int arity) {
    this.op = op;
    //this.arity = arity;
  }
  
  @Override
  public boolean equal(Value value) {
    return eqv(value);
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
  public Value apply(Value arguments) {
    throw new RuntimeException("Bad programmer cc apply");
  }

  @Override
  public Op apply(Op parent, Environment env, Value value) {
    //if (arity != 1) return parent.getEvaluator().error("Single return value used in a " + arity + "-value context", value);
    return parent.getEvaluator().callCountinuationWithDynamicWind(parent, op, value);
    //return op.apply(new Value[]{value});
    //return op.apply(value);
  }
//  public Op apply(Op parent, Environment env, Value value[]) {
//    if (arity != value.length) return parent.getEvaluator().error(Integer.toString(value.length)  + " return value used in a " + arity + "-value context", Pair.makeList(value));
//    return op.apply(value);
//  }
  @Override
  public java.lang.String toString() {
    return "[continuation\n" + op.toString() + "]";
  }
  @Override
  public int hashCode() {
    return op.hashCode();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Continuation)) return false;
    return eqv((Value) obj);
  }
}
