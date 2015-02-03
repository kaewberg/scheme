package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public class ErrorHandler {
  private ErrorHandler parent;
  private Value handler;
  private Op op;
  public ErrorHandler(ErrorHandler parent, Value handler, Op op) {
    super();
    this.parent = parent;
    this.handler = handler;
    this.op = op;
  }
  public ErrorHandler getParent() {
    return parent;
  }
  public Value getHandler() {
    return handler;
  }
  public Op apply(Value v, boolean continuable) {
    Op result = op;
    result = new Op.Apply(result, op.getEnvironment());
    if (!continuable) {
      result = new Op.ErrorOp(op.getEvaluator(), v);
    }
    op.getEvaluator().setValue(new Pair(handler, new Pair(v, Nil.NIL)));
    return result;
  }
}
