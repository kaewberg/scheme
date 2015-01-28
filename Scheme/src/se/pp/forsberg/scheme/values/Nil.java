package se.pp.forsberg.scheme.values;

public class Nil extends Value { // extends Pair {
  public final static Nil NIL = new Nil();
  
  @Override
  public boolean isNull() {
    return true;
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
    public int hashCode() {
      return 0;
    }
  @Override
  public java.lang.String toString() {
    return "()";
  }
}
