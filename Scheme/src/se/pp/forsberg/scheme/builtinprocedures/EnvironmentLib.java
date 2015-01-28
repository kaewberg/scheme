package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;

public class EnvironmentLib extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("environment"), Nil.NIL));
  }

  public class _Environment extends BuiltInProcedure {
    public _Environment(Environment env) { super("environment", env); }
    @Override public Value apply(Value arguments) {
      return Environment.makeEnvironment(arguments);
    }
  }
  public class SchemeReportEnvironment extends BuiltInProcedure {
    public SchemeReportEnvironment(Environment env) { super("scheme-report-environment", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Integer.class);
      int v = ((Integer) ((Pair)arguments).getCar()).asInt();
      return Environment.schemeReportEnvironment(v);
    }
  }
  public class NullEnvironment extends BuiltInProcedure {
    public NullEnvironment(Environment env) { super("null-environment", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Integer.class);
      int v = ((Integer) ((Pair)arguments).getCar()).asInt();
      return Environment.nullEnvironment(v);
    }
  }
  public class InteractiveEnvironment extends BuiltInProcedure {
    public InteractiveEnvironment(Environment env) { super("interaction-environment", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      return Environment.interactionEnvironment();
    }
  }
  public class Eval extends BuiltInProcedure {
    public Eval(Environment env) { super("eval", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      Value v2 = ((Pair)((Pair)arguments).getCdr()).getCar();
      if (!(v2 instanceof Environment)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid environment")));
      return v1.eval((Environment) v2);
    }
  }

}
