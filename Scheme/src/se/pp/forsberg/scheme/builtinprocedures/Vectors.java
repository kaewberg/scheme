package se.pp.forsberg.scheme.builtinprocedures;

import java.util.ArrayList;
import java.util.List;

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
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Vectors extends Library {
  public Vectors() throws SchemeException {
    super();
  }
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("vectors"), Nil.NIL));
  }

  public class IsVector extends BuiltInProcedure {
    public IsVector(Environment env) { super("vector?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Value.class);
      Value v = ((Pair)arguments).getCar();
      return v.isVector()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class MakeVector extends BuiltInProcedure {
    public MakeVector(Environment env) { super("make-vector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Integer.class, Value.class);
      Integer k = (Integer) ((Pair)arguments).getCar();
      Value fill = Value.UNSPECIFIED;
      if (((Pair)arguments).getCdr().isPair()) {
        fill = ((Pair)((Pair)arguments).getCdr()).getCar();
      }
      List<Value> result = new ArrayList<Value>(k.asInt());
      for (; k.isPositive(); k = k.minus(LongInteger.ONE)) {
        result.add(fill);
      }
      return new Vector(result);
    }
  }
  public class _Vector extends BuiltInProcedure {
    public _Vector(Environment env) { super("vector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Value.class);
      List<Value> result = new ArrayList<Value>();
      while (!arguments.isNull()) {
        result.add(((Pair)arguments).getCar());
        arguments = ((Pair)arguments).getCdr();
      }
      return new Vector(result);
    }
  }
  public class VectorLength extends BuiltInProcedure {
    public VectorLength(Environment env) { super("vector-length", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Vector.class);
      Vector v = (Vector) ((Pair)arguments).getCar();
      return new LongInteger(v.getVector().size(), true);
    }
  }
  public class VectorRef extends BuiltInProcedure {
    public VectorRef(Environment env) { super("vector-ref", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Vector.class, Integer.class);
      Vector v = (Vector) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      return v.getVector().get(k.asInt());
    }
  }
  public class VectorSet extends BuiltInProcedure {
    public VectorSet(Environment env) { super("vector-set!", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Vector.class, Integer.class, Value.class);
      Vector v = (Vector) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      Value val = ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar();
      v.getVector().set(k.asInt(), val);
      return Value.UNSPECIFIED;
    }
  }
  public class VectorToList extends BuiltInProcedure {
    public VectorToList(Environment env) { super("vector->list", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 3, Vector.class, Integer.class, Integer.class);
      List<Value> v = ((Vector) ((Pair)arguments).getCar()).getVector();
      int from = 0;
      int to = v.size();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size() ||
          to < from || to >= v.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + v + " " + from + " " + to)));
      }
      if (from == to) return Nil.NIL;
      Pair current = new Pair(v.get(from), Nil.NIL);
      Value result = current;
      for (int i = from+1; i < to; i++) {
        current.setCdr(new Pair(v.get(i), Nil.NIL));
        current = (Pair) current.getCdr();
      }
      return result;
    }
  }
  public class ListToVector extends BuiltInProcedure {
    public ListToVector(Environment env) { super("list->vector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Value.class);
      List<Value> result = new ArrayList<Value>();
      while (!arguments.isNull()) {
        result.add(((Pair) arguments).getCar());
        arguments = ((Pair) arguments).getCdr();
      }
      return new Vector(result);
    }
  }
  public class VectorToString extends BuiltInProcedure {
    public VectorToString(Environment env) { super("vector->string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 3, Vector.class, Integer.class, Integer.class);
      List<Value> v = ((Vector) ((Pair)arguments).getCar()).getVector();
      int from = 0;
      int to = v.size();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size() ||
          to < from || to >= v.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + v + " " + from + " " + to)));
      }
      if (from == to) return Nil.NIL;
      StringBuilder result = new StringBuilder();
      
      for (int i = from+1; i < to; i++) {
        if (!v.get(i).isChar())throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector element not a character in" + getName() + " " + v.get(i))));
        result.append(((Character)v.get(i)).getCharacter());
      }
      return new String(result);
    }
  }
  public class StringToVector extends BuiltInProcedure {
    public StringToVector(Environment env) { super("string->vector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 3, String.class);
      StringBuilder s = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      int from = 0;
      int to = s.length();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= s.length() ||
          to < from || to >=s.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException("Stringindex out of bounds " + s + " " + from + " " + to)));
      }
      List<Value> result = new ArrayList<Value>(s.length());
      for (int i = from; i < to; i++) {
        result.add(new Character(s.charAt(i)));
      }
      return new Vector(result);
    }
  }
  public class VectorCopyTo extends BuiltInProcedure {
    public VectorCopyTo(Environment env) { super("vector-copy!", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 3, 5, Vector.class, Integer.class, Vector.class, Integer.class, Integer.class);
      List<Value> dst = ((Vector) ((Pair)arguments).getCar()).getVector();
      int at = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
      List<Value> src = ((Vector) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).getVector();
      int from = 0;
      int to = src.size();
      if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= src.size() ||
          to < from || to >= src.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + src + " " + from + " " + to)));
      }
      if (at < 0 || from >= src.size() ||
          at+to-from < from || at+to-from >= dst.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + dst + " " + from + " " + to)));
      }
      if (from == to) return Nil.NIL;
      List<Value> result = new ArrayList<Value>(to-from-1);
      
      for (int i = from+1; i < to; i++) {
        dst.set(at + i - from, src.get(i));
      }
      return new Vector(result);
    }
  }
  public class VectorCopy extends BuiltInProcedure {
    public VectorCopy(Environment env) { super("vector-copy", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 3, Vector.class, Integer.class, Integer.class);
      List<Value> v = ((Vector) ((Pair)arguments).getCar()).getVector();
      int from = 0;
      int to = v.size();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size() ||
          to < from || to >= v.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + v + " " + from + " " + to)));
      }
      if (from == to) return Nil.NIL;
      List<Value> result = new ArrayList<Value>(to-from-1);
      
      for (int i = from+1; i < to; i++) {
        if (!v.get(i).isChar())throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector element not a character in" + getName() + " " + v.get(i))));
        result.add(v.get(i));
      }
      return new Vector(result);
    }
  }
  public class VectorAppend extends BuiltInProcedure {
    public VectorAppend(Environment env) { super("vector-append", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Vector.class);
      List<Value> result = new ArrayList<Value>();
      while (!arguments.isNull()) {
        Pair pair = (Pair) arguments;
        result.addAll(((Vector)pair.getCar()).getVector());
        arguments = pair.getCdr();
      }
      return new Vector(result);
    }
  }
  public class VectorFill extends BuiltInProcedure {
    public VectorFill(Environment env) { super("vector-fill!", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, 4, Vector.class, Value.class, Integer.class, Integer.class);
      Vector vector = (Vector) ((Pair)arguments).getCar();
      List<Value> v = vector.getVector();
      Value fill = ((Pair)((Pair)arguments).getCdr()).getCar();
      int from = 0;
      int to = v.size();
      if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
        from = ((Integer)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).isPair()) {
          to = ((Integer)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size()||
          to < from || to >= v.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException("Vector index out of bounds " + v + " " + from + " " + to)));
      }
      for (int i = from; i < to; i++) {
        v.set(i, fill);
      }
      return vector;
    }
  }
}
