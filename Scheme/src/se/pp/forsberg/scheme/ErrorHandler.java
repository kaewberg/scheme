package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Value;

public class ErrorHandler {
  private ErrorHandler parent;
  private Value handler;
  public ErrorHandler(ErrorHandler parent, Value handler) {
    super();
    this.parent = parent;
    this.handler = handler;
  }
  public ErrorHandler getParent() {
    return parent;
  }
  public Value getHandler() {
    return handler;
  }
}
