package se.pp.forsberg.scheme.values.errors;

import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;

public class Error extends Value {
  Throwable x;
  String message;
  Value irritants = Nil.NIL;
  public Error(Throwable x) {
    this.x = x;
    message = new String(x.getMessage());
  }
  public Error(java.lang.String msg) {
    message = new String(msg);
    this.irritants = Nil.NIL;
  }
  public Error(java.lang.String msg, Value... irritants) {
    message = new String(msg);
    this.irritants = Pair.makeList(irritants);
  }
  public Error(String msg, Value irritants) {
    message = msg;
    this.irritants = irritants;
  }
  public String getMessage() {
    return message;
  }
  public Throwable getThrowable() {
    return x;
  }
  public Value getIrritants() {
    return irritants;
  }
  @Override
  public boolean isError() {
    return true;
  }
  @Override
  public java.lang.String toString() {
    if (message != null) {
      return  "[Error " + message + " " + irritants + "]";
    }
    return "[Error " + x.getMessage() + "]";
  }
  @Override
  public boolean equal(Value value) {
    if (eqv(value)) return true;
    if (this.getClass() != value.getClass()) return false;
    Error other = (Error) value;
    return x.equals(other.x);
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
}
