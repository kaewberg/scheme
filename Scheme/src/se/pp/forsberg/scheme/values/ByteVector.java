package se.pp.forsberg.scheme.values;

import java.util.ArrayList;
import java.util.List;

public class ByteVector extends Value {
  List<Byte> vector;
  public ByteVector(List<Byte> vector) {
    this.vector = vector;
  }
  public ByteVector(byte[] byteArray) {
    vector = new ArrayList<Byte>();
    for (byte b: byteArray) {
      vector.add(b);
    }
  }
  public List<Byte> getVector() { return vector; }
  @Override
  public java.lang.String toString() {
    StringBuffer result = new StringBuffer();
    result.append("#u8(");
    boolean first = true;
    for (Byte b: vector) {
      if (first) {
        first = false;
      } else {
        result.append(" ");
      }
      result.append(b);
    }
    result.append(")");
    return result.toString();
  }
  @Override
  public boolean isByteVector() {
    return true;
  }
  @Override
  public boolean equal(Value value) {
    if (eqv(value)) return true;
    if (!value.isByteVector()) return false;
    ByteVector bv = (ByteVector) value;
    if (vector.size() != bv.vector.size()) return false;
    for (int i = 0; i < vector.size(); i ++) {
      if (vector.get(i) != bv.vector.get(i)) return false;
    }
    return true;
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    if (this == value) return true;
    if (!value.isByteVector()) return false;
    ByteVector bv = (ByteVector) value;
    return vector.size() == 0 && bv.vector.size() == 0;
  }
  
  @Override
  public int hashCode() {
    int result = 0;
    for (int i = 0; i < vector.size(); i++) {
      result ^= vector.get(i).hashCode();
    }
    return result;
  }
}
