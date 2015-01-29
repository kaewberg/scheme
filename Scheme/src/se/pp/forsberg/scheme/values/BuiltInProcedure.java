package se.pp.forsberg.scheme.values;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public abstract class BuiltInProcedure extends Procedure {

  public static class TODO extends BuiltInProcedure {
    public TODO(java.lang.String name) {
      super(name, null);
    }
    @Override
    public Value apply(Value arguments) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("TODO: implement " + getName())));
    }
  }
  
  final private java.lang.String name;
  final private Environment env;
  
  // Subclasses must have constructor with single parameter Environment
  public BuiltInProcedure(java.lang.String name, Environment env) {
    this.name = name;
    this.env = env;
  }
  
  public java.lang.String getName() { return name; }
  public Environment getEnvironment() { return env; }

  
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }

  @Override
  public boolean eqv(Value value) {
    return this == value;
  }

  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  
  @Override
  public java.lang.String toString() {
    return name;
  }
  
  @Override
  // For procedures that do not need apply or eval
  public Op apply(Op op, Environment env, Value arguments) {
    try {
      op.setValue(apply(arguments));
      return op;
    } catch (SchemeException x) {
      return op.error(x.getError());
    }
  }

}
