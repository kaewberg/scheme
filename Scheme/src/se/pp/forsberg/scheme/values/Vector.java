package se.pp.forsberg.scheme.values;

import java.util.List;

public class Vector extends Value {
  private List<Value> vector;
  public Vector(List<Value> vector) {
    this.vector = vector;
  }
  public List<Value> getVector() {
    return vector;
  }
  @Override
  public boolean isVector() {
    return true;
  }
  @Override
  public boolean equal(Value value) {
    if (eqv(value)) return true;
    if (!value.isVector()) return false;
    Vector other = (Vector) value;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.get(i).equal(other.vector.get(i))) return false;
    }
    return true;
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    if (eqv(value)) return true;
    if (!value.isVector()) return false;
    Vector other = (Vector) value;
    return vector.size() == 0 && other.vector.size() == 0;
  }
  @Override
  public java.lang.String toString() {
    StringBuffer result = new StringBuffer();
    result.append("#(");
    boolean first = true;
    for (Value value: vector) {
      if (first) {
        first = false;
      } else {
        result.append(' ');
      }
      result.append(value);
    }
    result.append(')');
    return result.toString();
  }
  @Override
  public int hashCode() {
    int result = 0;
    for (Value v: vector) {
      result ^= v.hashCode();
    }
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Value)) return false;
    return equal((Value) obj); 
  }
}
