package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.Op.Apply;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;
import se.pp.forsberg.scheme.values.errors.FileError;
import se.pp.forsberg.scheme.values.errors.ReadError;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Exceptions extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("exceptions"), Nil.NIL));
  }

  public class WithExceptionHandler extends BuiltInProcedure {
    public WithExceptionHandler(Environment env) { super("with-exception-handler", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, Procedure.class);
      Procedure handler = (Procedure) ((Pair)arguments).getCar();
      Procedure thunk = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
      try {
        return thunk.apply(Nil.NIL);
      } catch (SchemeException x) {
        Value v = handler.apply(new Pair(x.getError(), Nil.NIL));
        if (!x.isContinuable()) throw new SchemeException(v, false);
        return v;
      }
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, 2, Procedure.class);
      Procedure handler = (Procedure) ((Pair)arguments).getCar();
      Procedure thunk = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
      env.addErrorHandler(handler, op);
      Op result = op;
      result = new Op.Apply(op.getEvaluator(), result, env);
      op.getEvaluator().setValue(new Pair(thunk, Nil.NIL));
      return result;
    }
  }
  public class Raise extends BuiltInProcedure {
    public Raise(Environment env) { super("raise", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      throw new SchemeException(v1);
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return op.getEvaluator().error(v1);
    }
  }
  public class RaiseContinuable extends BuiltInProcedure {
    public RaiseContinuable(Environment env) { super("raise-continuable", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      throw new SchemeException(v1, true);
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return op.getEvaluator().error(v1, true);
    }
  }
  public class _Error extends BuiltInProcedure {
    public _Error(Environment env) { super("error", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Integer.MAX_VALUE);
      if (!((Pair)arguments).getCar().isString()) throw new SchemeException(new RuntimeError("Expected string in " + getName(), arguments));
      String msg = (String) ((Pair)arguments).getCar();
      Value irritants = ((Pair)arguments).getCdr();
      throw new SchemeException(new RuntimeError(msg, irritants));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, 1, Integer.MAX_VALUE);
      if (!((Pair)arguments).getCar().isString()) return op.getEvaluator().error("Expected string in " + getName(), arguments);
      String msg = (String) ((Pair)arguments).getCar();
      Value irritants = ((Pair)arguments).getCdr();
      return op.getEvaluator().error(msg, irritants);
    }
  }
  public class IsErrorObject extends BuiltInProcedure {
    public IsErrorObject(Environment env) { super("error-object?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return v1.isError()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class ErrorObjectMessage extends BuiltInProcedure {
    public ErrorObjectMessage(Environment env) { super("error-object-message", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Error.class);
      Error v1 = (Error) ((Pair)arguments).getCar();
      return v1.getMessage();
    }
  }
  public class ErrorObjectIrritants extends BuiltInProcedure {
    public ErrorObjectIrritants(Environment env) { super("error-object-irritants", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Error.class);
      Error v1 = (Error) ((Pair)arguments).getCar();
      return v1.getIrritants();
    }
  }
  public class IsReadError extends BuiltInProcedure {
    public IsReadError(Environment env) { super("read-error?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Error.class);
      Error v1 = (Error) ((Pair)arguments).getCar();
      return (v1 instanceof ReadError)? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsFileError extends BuiltInProcedure {
    public IsFileError(Environment env) { super("file-error?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Error.class);
      Error v1 = (Error) ((Pair)arguments).getCar();
      return (v1 instanceof FileError)? Boolean.TRUE : Boolean.FALSE;
    }
  }

}
