package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Strings extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("strings"), Nil.NIL));
  }

  public class IsString extends BuiltInProcedure {
    public IsString(Environment env) { super("string?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return v1.isString()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class MakeString extends BuiltInProcedure {
    public MakeString(Environment env) { super("make-string", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2, Integer.class, Character.class);
      Integer k = (Integer) ((Pair)arguments).getCar();
      Character c = new Character(' ');
      if (!((Pair)arguments).getCdr().isNull()) {
        c = (Character) ((Pair)((Pair)arguments).getCdr()).getCar();
      }
      StringBuilder result = new StringBuilder();
      for (; k.isPositive(); k = k.minus(LongInteger.ONE)) {
        result.append(c.getCharacter());
      }
      return new String(result.toString());
    }
  }
  public class _String extends BuiltInProcedure {
    public _String(Environment env) { super("string", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Character.class);
      StringBuilder result = new StringBuilder();
      while (!arguments.isNull()) {
        if (!arguments.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed argument list in " + getName())));
        if (!((Pair)arguments).getCar().isChar())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected character arguments in" + getName())));
        Character c = (Character) ((Pair)arguments).getCar();
        result.append(c.getCharacter());
      }
      return new String(result.toString());
    }
  }
  public class StringLength extends BuiltInProcedure {
    public StringLength(Environment env) { super("string-length", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new LongInteger(s.getString().length(), true); 
    }
  }
  public class StringRef extends BuiltInProcedure {
    public StringRef(Environment env) { super("string-ref", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class, Integer.class);
      String s = (String) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();;
      return new Character(s.getString().charAt(k.asInt()));
    }
  }
  public class StringSet extends BuiltInProcedure {
    public StringSet(Environment env) { super("string-set!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class, Integer.class, Character.class);
      String s = (String) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      Character c = (Character) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar();
      s.getStringBuilder().setCharAt(k.asInt(), c.getCharacter());
      return Value.UNSPECIFIED;
    }
  }
  public class StringUpcase extends BuiltInProcedure {
    public StringUpcase(Environment env) { super("string-upcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new String(s.getString().toUpperCase());
    }
  }
  public class StringDowncase extends BuiltInProcedure {
    public StringDowncase(Environment env) { super("string-downcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new String(s.getString().toLowerCase());
    }
  }
  // TODO real folding
  public class StringFoldcase extends BuiltInProcedure {
    public StringFoldcase(Environment env) { super("string-foldcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new String(s.getString().toLowerCase());
    }
  }
  public class Substring extends BuiltInProcedure {
    public Substring(Environment env) { super("substring", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class, Integer.class, Integer.class);
      String s = (String) ((Pair)arguments).getCar();
      Integer from = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      Integer to = (Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar();
      return new String(s.getString().substring(from.asInt(), to.asInt()-1));
    }
  }
  public class StringAppend extends BuiltInProcedure {
    public StringAppend(Environment env) { super("string-append", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, String.class);
      StringBuilder result = new StringBuilder();
      while (!arguments.isNull()) {
        String s = (String) ((Pair) arguments).getCar();
        result.append(s.getString());
      }
      return new String(result);
    }
  }
  public class StringToList extends BuiltInProcedure {
    public StringToList(Environment env) { super("string->list", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 3, String.class, Integer.class, Integer.class);
      StringBuilder s = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      int from = 0;
      int to = s.length();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer) ((Pair)arguments).getCdr()).asInt();
        if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCdr()).asInt() + 1;
        }
      }
      if (from < 0 || from >= s.length() ||
          to < from || to >= s.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("String index out of bounds " + s + " " + from + " " + to)));
      }
      if (from ==  to) return Nil.NIL;
      Pair current = new Pair(new Character(s.charAt(from)), Nil.NIL);
      Value result = current;
      for (int i = from+1; i < to; i++) {
        current.setCdr(new Pair(new Character(s.charAt(i)), Nil.NIL));
      }
      return result;
    }
  }
  public class ListToString extends BuiltInProcedure {
    public ListToString(Environment env) { super("list->string", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Character.class);
      StringBuilder result = new StringBuilder();
      while (!arguments.isNull()) {
        Character c = (Character) ((Pair) arguments).getCar();
        result.append(c.getCharacter());
      }
      return new String(result);
    }
  }
  public class StringCopy extends BuiltInProcedure {
    public StringCopy(Environment env) { super("string-copy", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 3, String.class, Integer.class, Integer.class);
      StringBuilder s = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      int from = 0;
      int to = s.length();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt() + 1;
        }
      }
      if (from < 0 || from >= s.length() ||
          to < from || to >= s.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("String index out of bounds " + s + " " + from + " " + to)));
      }
      return new String(s.subSequence(from,  to));
    }
  }
  public class StringCopyTo extends BuiltInProcedure {
    public StringCopyTo(Environment env) { super("string-copy!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 3, 5, String.class, Integer.class, String.class, Integer.class, Integer.class);
      StringBuilder dst = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      int at = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
      StringBuilder src = ((String) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).getStringBuilder();
      int from = 0;
      int to = src.length();
      if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr()).getCar()).asInt() + 1;
        }
      }
      if (from < 0 || from >= src.length() ||
          to < from || to >= src.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("String index out of bounds " + src + " " + from + " " + to)));
      }
      if (at < 0 || at >= dst.length() ||
          (at + to - from < 0 || at + to - from >= dst.length())) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("String index out of bounds " + dst + " " + at + " " + (at+to-from))));
      }
      if (src == dst) {
        src = new StringBuilder(src);
      }
      dst.insert(at, src, from, to);
      return Value.UNSPECIFIED;
    }
  }
  public class StringFill extends BuiltInProcedure {
    public StringFill(Environment env) { super("string-fill!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, 4, String.class, Character.class, Integer.class, Integer.class);
      StringBuilder s = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      char c = ((Character) ((Pair)((Pair)arguments).getCdr()).getCar()).getCharacter();
      int from = 0;
      int to = s.length();
      if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCar()).asInt() + 1;
        }
      }
      if (from < 0 || from >= s.length() ||
          to < from || to >= s.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("String index out of bounds " + s + " " + from + " " + to)));
      }
      for (int i = from; i < to; i++) {
        s.setCharAt(i, c);
      }
      return Value.UNSPECIFIED;
    }
  }

}
