package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;

public class SchemeException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final Value error;
  private final boolean continuable;
  public SchemeException(Error error) {
    super(error.getThrowable().getMessage(), error.getThrowable());
    this.error = error;
    this.continuable = false;
  }
  public SchemeException(Value error) {
    super(error.toString());
    this.error = error;
    this.continuable = false;
  }
  public SchemeException(Value error, boolean continuable) {
    super(error.toString());
    this.error = error;
    this.continuable = continuable;
  }
  public Value getError() { return error; }
  public boolean isContinuable() {
    return continuable;
  }
}
