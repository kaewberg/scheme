package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Number;
import se.pp.forsberg.scheme.values.numbers.Real;

public class Inexact extends Library {

  public Inexact() throws SchemeException {
    super();
  }
  public static Value getName() {
    return makeName("scheme", "inexact");
  }
  

  class Exp extends BuiltInProcedure {
    public Exp(Environment env) { super("exp", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).exp();
    }
  }
  class Log extends BuiltInProcedure {
    public Log(Environment env) { super("log", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Number.class);
      if (((Pair) ((Pair)arguments).getCdr()).getCdr().isNull()) {
        return ((Number)((Pair)arguments).getCar()).log();
      } else {
        return ((Number)((Pair)arguments).getCar()).log((Number) ((Pair) ((Pair)arguments).getCdr()).getCdr());
      }
    }
  }
  class Sin extends BuiltInProcedure {
    public Sin(Environment env) { super("sin", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).sin();
    }
  }
  class Cos extends BuiltInProcedure {
    public Cos(Environment env) { super("cos", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).cos();
    }
  }
  class Tan extends BuiltInProcedure {
    public Tan(Environment env) { super("tan", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).tan();
    }
  }
  class Asin extends BuiltInProcedure {
    public Asin(Environment env) { super("asin", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).asin();
    }
  }
  class Acos extends BuiltInProcedure {
    public Acos(Environment env) { super("acos", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).acos();
    }
  }
  class Atan extends BuiltInProcedure {
    public Atan(Environment env) { super("atan", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Number.class);
      Number n1 = (Number)((Pair)arguments).getCar();
      Number n2 = ((Pair) ((Pair)arguments).getCdr()).getCdr().isNull()? null : (Number) ((Pair) ((Pair)arguments).getCdr()).getCdr();
      if (n2 == null) {
        return n1.atan();
      } else {
        if (!n2.isReal()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument type in call to " + getName() + ", expected Real got " + ((Pair)arguments).getCar().getClass().getName() + " " + n2)));
        return ((Real)n1).atan((Real) n2);
      }
    }
  }
  class Sqrt extends BuiltInProcedure {
    public Sqrt(Environment env) { super("sqrt", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number z = (Number)((Pair)arguments).getCar();
      return z.sqrt();
    }
  }

  class IsFinite extends BuiltInProcedure {
    public IsFinite(Environment env) { super("finite?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number number = (Number) ((Pair)arguments).getCar();
      if (number.getRealPart().isInfinite()) return Boolean.FALSE;
      if (number.getRealPart().isNaN()) return Boolean.FALSE;
      if (number.getImaginaryPart().isInfinite()) return Boolean.FALSE;
      if (number.getImaginaryPart().isNaN()) return Boolean.FALSE;
      return Boolean.TRUE;
    }
  }
  class IsInfinite extends BuiltInProcedure {
    public IsInfinite(Environment env) { super("infinite?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number number = (Number) ((Pair)arguments).getCar();
      if (number.getRealPart().isInfinite()) return Boolean.TRUE;
      if (number.getImaginaryPart().isInfinite()) return Boolean.TRUE;
      return Boolean.FALSE;
    }
  } 
  class IsNaN extends BuiltInProcedure {
    public IsNaN(Environment env) { super("nan?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number number = (Number) ((Pair)arguments).getCar();
      if (number.getRealPart().isNaN()) return Boolean.TRUE;
      if (number.getImaginaryPart().isNaN()) return Boolean.TRUE;
      return Boolean.FALSE;
    }
  }
}
