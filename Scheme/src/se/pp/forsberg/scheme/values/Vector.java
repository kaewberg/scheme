package se.pp.forsberg.scheme.values;

import java.util.HashSet;
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
  protected java.lang.String toString(java.util.Map<ValueEqv,Label> labels, java.util.Set<ValueEqv> definedLabels) {
    StringBuffer result = new StringBuffer();
    ValueEqv me = new ValueEqv(this);
    
    Label ref = labels.get(me);
    if (ref != null) {
      if (definedLabels.contains(me)) {
        return ref.toString();
      }
      labels.put(me, ref);
      Label def = new Label(ref.getLabel(), false);
      result.append(def);
      definedLabels.add(me);
    }
    result.append("#(");
    boolean first = true;
    for (Value value: vector) {
      if (first) {
        first = false;
      } else {
        result.append(' ');
      }
      result.append(value.toString(labels, definedLabels));
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

  @Override
  protected void labelShared(java.util.Set<ValueEqv> encounteredValues, java.util.Map<ValueEqv,Label> labels) {
    ValueEqv me = new ValueEqv(this);
    if (labels.containsKey(me)) return;
    if (encounteredValues.contains(me)) {
      labels.put(me, new Label(labels.size(), true));
      return;
    }
    encounteredValues.add(me);
    for (Value v: vector) {
      v.label(encounteredValues, labels);
    }
  }
  @Override
  protected void label(java.util.Set<ValueEqv> encounteredValues, java.util.Map<ValueEqv,Label> labels) {
    ValueEqv me = new ValueEqv(this);
    if (labels.containsKey(me)) return;
    if (encounteredValues.contains(me)) {
      labels.put(me, new Label(labels.size(), true));
      return;
    }
    encounteredValues.add(me);
    for (Value v: vector) {
      v.label(new HashSet<ValueEqv>(encounteredValues), labels);
    }
  }
  @Override
  public java.lang.String toString() {
    return toStringSafe(); 
  }
}
