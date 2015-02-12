package se.pp.forsberg.scheme.values.errors;

import se.pp.forsberg.scheme.values.Value;

public class RuntimeError extends Error {
  public RuntimeError(Throwable x) {
    super(x);
  }

  public RuntimeError(java.lang.String msg, Value irritants) {
    super(msg, irritants);
  }
  
}
