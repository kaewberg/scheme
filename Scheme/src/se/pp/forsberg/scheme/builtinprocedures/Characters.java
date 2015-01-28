package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Characters extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("characters"), Nil.NIL));
  }

  public class IsCharacter extends BuiltInProcedure {
    public IsCharacter(Environment env) { super("char?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return v1.isChar()? Boolean.TRUE : Boolean.FALSE;
    }
  }

  public class IsCharAlphabetic extends BuiltInProcedure {
    public IsCharAlphabetic(Environment env) { super("char-alphabetic?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return java.lang.Character.isLetter(c.getCharacter())? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsCharNumeric extends BuiltInProcedure {
    public IsCharNumeric(Environment env) { super("char-numeric?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return java.lang.Character.isDigit(c.getCharacter())? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsCharWhitespace extends BuiltInProcedure {
    public IsCharWhitespace(Environment env) { super("char-whitespace?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return java.lang.Character.isWhitespace(c.getCharacter())? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsCharUppercase extends BuiltInProcedure {
    public IsCharUppercase(Environment env) { super("char-uppercase?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return java.lang.Character.isUpperCase(c.getCharacter())? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsCharLowercase extends BuiltInProcedure {
    public IsCharLowercase(Environment env) { super("char-lowercase?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return java.lang.Character.isLowerCase(c.getCharacter())? Boolean.TRUE : Boolean.FALSE;
    }
  }
  // TODO digit-value unicode

  public class CharToInteger extends BuiltInProcedure {
    public CharToInteger(Environment env) { super("char->integer", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return new LongInteger(c.getCharacter(), true);
    }
  }
  public class IntegerToChar extends BuiltInProcedure {
    public IntegerToChar(Environment env) { super("integer->char", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Integer.class);
      int i = ((Integer) ((Pair)arguments).getCar()).asInt();
      if (i >= 65536) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Integer too large for character value")));
      return new Character((char) i);
    }
  }
  public class CharUpcase extends BuiltInProcedure {
    public CharUpcase(Environment env) { super("char-upcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return new Character(java.lang.Character.toUpperCase(c.getCharacter()));
    }
  }
  public class CharDowncase extends BuiltInProcedure {
    public CharDowncase(Environment env) { super("char-downcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return new Character(java.lang.Character.toLowerCase(c.getCharacter()));
    }
  }
  // TODO real folding
  public class CharFoldcase extends BuiltInProcedure {
    public CharFoldcase(Environment env) { super("char-foldcase", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Character.class);
      Character c = (Character) ((Pair)arguments).getCar();
      return new Character(java.lang.Character.toLowerCase(c.getCharacter()));
    }
  }

}
