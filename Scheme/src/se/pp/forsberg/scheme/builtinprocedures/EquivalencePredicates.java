package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public class EquivalencePredicates extends Library {
  public EquivalencePredicates() throws SchemeException {
    super();
  }
  public static Value getName() {
    return makeName("scheme-impl", "equal");
  }

  public class Equal extends BuiltInProcedure {
    public Equal(Environment env) { super("equal?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      Value v2 = ((Pair) ((Pair)arguments).getCdr()).getCar();
      return v1.equal(v2)? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class Eq extends BuiltInProcedure {
    public Eq(Environment env) { super("eq?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      Value v2 = ((Pair) ((Pair)arguments).getCdr()).getCar();
      return v1.eq(v2)? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class Eqv extends BuiltInProcedure {
    public Eqv(Environment env) { super("eqv?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      Value v2 = ((Pair) ((Pair)arguments).getCdr()).getCar();
      return v1.eqv(v2)? Boolean.TRUE : Boolean.FALSE;
    }
  }

}
