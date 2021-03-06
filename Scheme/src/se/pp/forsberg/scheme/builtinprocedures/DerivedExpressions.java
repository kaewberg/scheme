package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public class DerivedExpressions extends Library {
  
  public DerivedExpressions() throws SchemeException {
    getEnvironment().define(new Identifier("<undefined>"), null);
  }
  
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("derived-expressions"), Nil.NIL));
  }


}
