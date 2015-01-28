package se.pp.forsberg.scheme.builtinprocedures;

import java.util.ArrayList;
import java.util.List;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class ByteVectors extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("byte-vectors"), Nil.NIL));
  }
  static Integer MAX = new LongInteger(255, true);

  public class IsByteVector extends BuiltInProcedure {
    public IsByteVector(Environment env) { super("bytevector", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return v1.isByteVector()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class MakeByteVector extends BuiltInProcedure {
    public MakeByteVector(Environment env) { super("make-bytevector", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2, Integer.class, Integer.class);
      Integer k = (Integer) ((Pair)arguments).getCar();
      byte b = 0;
      if (((Pair)arguments).getCdr().isPair()) {
        Integer i = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
        if (i.isNegative() || i.greaterThan(MAX)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Byte value too large " + i)));
        b = i.asByte();
      }
      List<Byte> result = new ArrayList<Byte>(k.asInt());
      for (; k.isPositive(); k = k.minus(LongInteger.ONE)) {
        result.add(b);
      }
      return new ByteVector(result);
    }
  }
  public class _ByteVector extends BuiltInProcedure {
    public _ByteVector(Environment env) { super("bytevector", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Integer.class, Integer.class);
      List<Byte> result = new ArrayList<Byte>();
      while (!arguments.isNull()) {
        Integer i = (Integer) ((Pair) arguments).getCar();
        if (i.isNegative() || i.greaterThan(MAX)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Byte value too large " + i)));
        result.add(i.asByte());
        arguments = ((Pair) arguments).getCdr();
      }
      return new ByteVector(result);
    }
  }
  public class ByteVectorLength extends BuiltInProcedure {
    public ByteVectorLength(Environment env) { super("bytevector-length", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, ByteVector.class);
      ByteVector v1 = (ByteVector) ((Pair)arguments).getCar();
      return new LongInteger(v1.getVector().size(), true);
    }
  }
  public class ByteVectorRef extends BuiltInProcedure {
    public ByteVectorRef(Environment env) { super("bytevector-u8-ref", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, ByteVector.class, Integer.class, Integer.class);
      ByteVector v1 = (ByteVector) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      Integer i = (Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar();
      if (i.isNegative() || i.greaterThan(MAX)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Byte value too large " + i)));
      v1.getVector().set(k.asInt(), i.asByte());
      return v1;
    }
  }
  public class ByteVectorSet extends BuiltInProcedure {
    public ByteVectorSet(Environment env) { super("bytevector-u8-set!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, ByteVector.class, Integer.class);
      ByteVector v1 = (ByteVector) ((Pair)arguments).getCar();
      Integer k = (Integer) ((Pair)((Pair)arguments).getCdr()).getCar();
      v1.getVector().get(k.asInt());
      return v1;
    }
  }
  public class ByteVectorCopy extends BuiltInProcedure {
    public ByteVectorCopy(Environment env) { super("bytevector-copy", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 3, ByteVector.class, Integer.class, Integer.class);
      List<Byte> v = ((ByteVector) ((Pair)arguments).getCar()).getVector();
      int from = 0, to = v.size();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size() ||
          to < from || to > v.size())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Index out of range")));
      List<Byte> result = new ArrayList<Byte>(to-from);
      for (int i = from; i < to; i++) {
        result.add(v.get(i));
      }
      return new ByteVector(result);
    }
  }
  public class ByteVectorCopyTo extends BuiltInProcedure {
    public ByteVectorCopyTo(Environment env) { super("bytevector-copy!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 3, 5, ByteVector.class, Integer.class, ByteVector.class, Integer.class, Integer.class);
      ByteVector result = (ByteVector) ((Pair)arguments).getCar();
      List<Byte> dst = result.getVector();
      int at = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
      List<Byte> src = ((ByteVector) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).getVector();
      int from = 0, to = src.size();
      if (((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= src.size() || to < from || to > src.size())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Index out of range")));
      if (at < 0 || at >= dst.size() || at+to-from > dst.size())  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Index out of range")));
      for (int i = from; i < to; i++) {
        dst.set(at + i - from, src.get(i));
      }
      return result;
    }
  }
  public class ByteVectorAppend extends BuiltInProcedure {
    public ByteVectorAppend(Environment env) { super("bytevector-append", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, ByteVector.class);
      List<Byte> result = new ArrayList<Byte>();
      while (arguments.isPair()) {
        result.addAll(((ByteVector)((Pair)arguments).getCar()).getVector());
        arguments = ((Pair)arguments).getCdr();
      }
      return new ByteVector(result);
    }
  }
  public class Utf8ToString extends BuiltInProcedure {
    public Utf8ToString(Environment env) { super("utf8->string", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 3, ByteVector.class, Integer.class, Integer.class);
      List<Byte> v = ((ByteVector) ((Pair)arguments).getCar()).getVector();
      int from = 0, to = v.size();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= v.size() || to < from || to > v.size()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Index out of range")));
      StringBuilder result = new StringBuilder();
      for (int i = from; i < to; i++) {
        byte b = v.get(i);
        if (b < 128) {
          result.append((char) b);
        } else if ((b & 192) == 128) {
          throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid utf-8")));
        } else {
          int bytes = 2;
          byte b2 =(byte) (b << 2);
          while ((b & 128) != 0) {
            bytes++;
            b2 = (byte) (b2 << 1);
          }
          long c = b & ((1 << bytes)-1);
          for (int j = 1; j < bytes; j++) {
            if (j >= to) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid utf-8")));
            b = v.get(i+j);
            if ((b & 192) != 128) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid utf-8")));
            c = (c << 6) | (b & 63);
          }
          // TODO if c > 0x1ffff ??
          result.append((char) c);
        }
      }
      return new String(result);
    }
  }
  public class String2Utf8 extends BuiltInProcedure {
    public String2Utf8(Environment env) { super("string->utf8", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 3, String.class, Integer.class, Integer.class);
      StringBuilder s = ((String) ((Pair)arguments).getCar()).getStringBuilder();
      int from = 0, to = s.length();
      if (((Pair)arguments).getCdr().isPair()) {
        from = ((Integer) ((Pair)((Pair)arguments).getCdr()).getCar()).asInt();
        if (((Pair)((Pair)arguments).getCdr()).getCdr().isPair()) {
          to = ((Integer) ((Pair)((Pair)((Pair)arguments).getCdr()).getCdr()).getCar()).asInt();
        }
      }
      if (from < 0 || from >= s.length() || to < from || to > s.length()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Index out of range")));
      List<Byte> result = new ArrayList<Byte>();
      for (int i = from; i < to; i++) {
        char c = s.charAt(i);
        if (c < 0x80) {
          result.add((byte) c);
        } else if (c <  0x1000) {
          result.add((byte)((c >> 6) | 192));
          result.add((byte)((c & 63) | 128));
        } else  {
          result.add((byte)((c >> 12) | 192));
          result.add((byte)((c >> 6) | 192));
          result.add((byte)((c & 63) | 128));
        }
      }
      return new ByteVector(result);
    }
  }
}
