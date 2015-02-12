package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;


public class Booleans extends Library {
  public Booleans() throws SchemeException {
    super();
  }
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("booleans"), Nil.NIL));
  }

  public class Equal extends BuiltInProcedure {
    public Equal(Environment env) { super("not", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Value.class);
      Value v = ((Pair)arguments).getCar();
      return v.eqv(Boolean.FALSE)? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsBoolean extends BuiltInProcedure {
    public IsBoolean(Environment env) { super("boolean?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Value.class);
      return ((Pair)arguments).getCar().isBoolean()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class BooleaEqv extends BuiltInProcedure {
    public BooleaEqv(Environment env) { super("boolean=?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Value.class);
      Value v = ((Pair)arguments).getCar();
      if (!v.isBoolean()) return Boolean.FALSE;
      Boolean b = (Boolean) v;
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        v = ((Pair)arguments).getCar();
        if (!v.isBoolean()) return Boolean.FALSE;
        if (!v.eqv(b)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
      }
      return Boolean.TRUE;
    }
  }

}
