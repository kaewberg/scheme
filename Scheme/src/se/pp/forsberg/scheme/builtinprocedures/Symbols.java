package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Symbols extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("symbols"), Nil.NIL));
  }

  public class IsSymbol extends BuiltInProcedure {
    public IsSymbol(Environment env) { super("symbol?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v = ((Pair)arguments).getCar();
      return v.isIdentifier()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class SymbolEqual extends BuiltInProcedure {
    public SymbolEqual(Environment env) { super("symbol=?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Integer.MAX_VALUE, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      if (!v1.isIdentifier()) return Boolean.FALSE;
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        if (!arguments.isPair()) throw new SchemeException(new RuntimeError(new IllegalAccessError("Invalid argument to " + getName())));
        Value v2 = ((Pair)arguments).getCar();
        if (!v2.isIdentifier()) return Boolean.FALSE;
        if (!v1.equals(v2)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
      }
      return Boolean.TRUE;
    }
  }
  public class SymbolToString extends BuiltInProcedure {
    public SymbolToString(Environment env) { super("symbol->string", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Identifier.class);
      Identifier id = (Identifier) ((Pair)arguments).getCar();
      return new String(id.getIdentifier());
    }
  }
  public class StringToSymbol extends BuiltInProcedure {
    public StringToSymbol(Environment env) { super("string->symbol", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new Identifier(s.getString());
    }
  }

}
