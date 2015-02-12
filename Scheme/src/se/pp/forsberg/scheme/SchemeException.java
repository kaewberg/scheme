package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;

public class SchemeException extends Exception {
  private static final long serialVersionUID = 1L;
  private final Value error;
  private final boolean continuable;
  
  public SchemeException(Value error, boolean continuable) {
    super(error.toString());
    this.error = error;
    this.continuable = continuable;
  }
  public SchemeException(Value error) {
    this(error, false);
  }
  
  public SchemeException(Error error) {
    this(error, false);
  }
  public SchemeException(Error error, boolean continuable) {
    super(error.getMessage().getString() + " " +error.getIrritants(), error.getThrowable());
    this.error = error;
    this.continuable = continuable;
  }
  
  public SchemeException(String error) {
    this(error, false, Nil.NIL);
  }
  public SchemeException(String error, Value... irritants) {
    this(error, false, (Pair) Pair.makeList(irritants));
  }
  public SchemeException(String error, Pair irritants) {
    this(error, false, irritants);
  }
  public SchemeException(String error, boolean continuable) {
    this(error, continuable, Nil.NIL);
  }
  public SchemeException(String error, boolean continuable, Value... irritants) {
    this(error, continuable, (Pair) Pair.makeList(irritants));
  }
  public SchemeException(String error, boolean continuable, Pair irritants) {
    super(error + " " + irritants);
    this.error = new Error(new se.pp.forsberg.scheme.values.String(error), irritants);
    this.continuable = false;
  }
  public SchemeException(String error, boolean continuable, Nil irritants) {
    super(error);
    this.error = new Error(new se.pp.forsberg.scheme.values.String(error), irritants);
    this.continuable = false;
  }
  public SchemeException(se.pp.forsberg.scheme.values.String msg, Value irritants) {
    super(msg.toString());
    this.error = new Error(msg, irritants);
    this.continuable = false;
  }
  public Value getError() { return error; }
  public boolean isContinuable() {
    return continuable;
  }
}
