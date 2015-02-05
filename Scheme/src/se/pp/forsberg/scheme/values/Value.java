package se.pp.forsberg.scheme.values;

import java.util.Map;
import java.util.Set;

import se.pp.forsberg.scheme.values.Value.ValueEqv;



public abstract class Value {
  public static final Value UNSPECIFIED = Boolean.FALSE;

  //abstract public Value eval(Environment env);
  abstract public boolean equal(Value value);
  abstract public boolean eqv(Value value);
  abstract public boolean eq(Value value);
  
  public boolean isNull() { return false; }
  public boolean isPair() { return false; }
  public boolean isNumber() { return false; }
  public boolean isComplex() { return false; }
  public boolean isReal() { return false; }
  public boolean isRational() { return false; }
  public boolean isInteger() { return false; }
  public boolean isError() { return false; }
  public boolean isIdentifier() { return false; }
  public boolean isBoolean() { return false; }
  public boolean isByteVector() { return false; }
  public boolean isChar() { return false; }
  public boolean isString() { return false; }
  public boolean isVector() { return false; }
  public boolean isProcedure() { return false; }
  public boolean isEof() { return false; }
  public boolean isPort() { return false; }
  
  public boolean asBoolean() { return true; }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Value)) return false;
    return equal((Value) obj); 
  }
//  public Value evalWithContinuations(Environment env) {
//    try {
//      Continuation continuation = new Continuation();
//      continuation.recordTopLeve(env);
//      return eval(env, continuation);
//    } catch (ContinuationException x) {
//      Continuation continuation = x.getContinuation();
//      Decision decision = continuation.pop();
//      if (!(decision instanceof TopLevelDecision)) throw new IllegalArgumentException("Continuation implementation problem: expected TOP_LEVEL env, got " + decision);
//      return replay(continuation, new Continuation());
//    }
//  }
  public Value eval(Environment env) { return this; }
  //abstract public Value replay(Continuation replay, Continuation continuation);
  
  protected class ValueEqv {
    Value value;
    public ValueEqv(Value value) {
      this.value = value;
    }
    public Value getValue() { return value; }
    @Override
    public int hashCode() {
      return System.identityHashCode(value);
    }
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ValueEqv)) return false;
      return value.eqv(((ValueEqv)obj).value);
    }
  }
  protected void label(Set<ValueEqv> encounteredValues, Map<ValueEqv, Label> labels) {}
  protected java.lang.String toString(Map<ValueEqv, Label> labels, Set<ValueEqv> definedLabels) {
    return toString(); 
  }
}
