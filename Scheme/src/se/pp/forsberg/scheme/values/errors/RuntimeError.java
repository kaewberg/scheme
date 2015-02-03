package se.pp.forsberg.scheme.values.errors;

import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.String;

public class RuntimeError extends Error {
  public RuntimeError(Throwable x) {
    super(x);
  }

  public RuntimeError(Value msg, Value irritants) {
    super(msg, irritants);
  }

  public RuntimeError(java.lang.String msg, Value irritants) {
    super(new String(msg), irritants);
  }
}
