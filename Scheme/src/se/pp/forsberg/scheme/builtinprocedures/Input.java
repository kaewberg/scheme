package se.pp.forsberg.scheme.builtinprocedures;

import java.util.ArrayList;
import java.util.List;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Eof;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Input extends Library {
  public static Value getName() {
    return makeName("scheme-impl", "input");
  }

  public class Read extends BuiltInProcedure {
    public Read(Environment env) { super("read", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.read();
    }
  }
  public class ReadChar extends BuiltInProcedure {
    public ReadChar(Environment env) { super("read-char", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.readChar();
    }
  }
  public class PeekChar extends BuiltInProcedure {
    public PeekChar(Environment env) { super("peek-char", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.peekChar();
    }
  }
  public class ReadLine extends BuiltInProcedure {
    public ReadLine(Environment env) { super("read-line", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.readLine();
    }
  }
  public class IsEofObject extends BuiltInProcedure {
    public IsEofObject(Environment env) { super("eof-object?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1);
      Value v = ((Pair)arguments).getCar();
      return v.isEof()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class EofObject extends BuiltInProcedure {
    public EofObject(Environment env) { super("eof-object", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      return Eof.EOF;
    }
  }
  public class IsCharReady extends BuiltInProcedure {
    public IsCharReady(Environment env) { super("char-ready?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.isCharReady();
    }
  }
  public class ReadString extends BuiltInProcedure {
    public ReadString(Environment env) { super("read-line", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      Integer k = (Integer) ((Pair) arguments).getCar(); 
      Port port = null; // TODO current-input-port 
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      StringBuilder result = new StringBuilder();
      Value c = port.readChar();
      if (c.isEof()) return Eof.EOF;
      while (k.isPositive()) {
        if (c.isEof()) break;
        result.append(((Character)c).getCharacter());
        c = port.readChar();
        k = k.minus(LongInteger.ONE);
      }
      return new String(result);
    }
  }
  public class ReadU8 extends BuiltInProcedure {
    public ReadU8(Environment env) { super("read-u8", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.readByte();
    }
  }
  public class PeekU8 extends BuiltInProcedure {
    public PeekU8(Environment env) { super("peek-u8", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.peekByte();
    }
  }
  public class IsU8Ready extends BuiltInProcedure {
    public IsU8Ready(Environment env) { super("u8-ready?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null; // TODO current-input-port 
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      return port.isByteReady();
    }
  }
  public class ReadByteVector extends BuiltInProcedure {
    public ReadByteVector(Environment env) { super("read-bytevector", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      Integer k = (Integer) ((Pair) arguments).getCar(); 
      Port port = null; // TODO current-input-port 
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      List<Byte> result = new ArrayList<Byte>();
      Value b = port.readByte();
      if (b.isEof()) return Eof.EOF;
      while (k.isPositive()) {
        if (b.isEof()) break;
        result.add(((LongInteger) b).asByte());
        b = port.readByte();
        k = k.minus(LongInteger.ONE);
      }
      return new ByteVector(result);
    }
  }
  public class ReadByteVectorSet extends BuiltInProcedure {
    public ReadByteVectorSet(Environment env) { super("read-bytevector!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 4, ByteVector.class, Port.class, Integer.class, Integer.class);
      List<Byte> v = ((ByteVector) ((Pair) arguments).getCar()).getVector(); 
      Port port = null; // TODO current-input-port 
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair)arguments).getCar();
      }
      arguments = ((Pair) arguments).getCdr();
      int from = 0;
      if (!arguments.isNull()) {
        from = ((Integer) ((Pair)arguments).getCar()).asInt();
      }
      int to = v.size();
      if (!arguments.isNull()) {
        to = ((Integer) ((Pair)arguments).getCar()).asInt();
      }
      Value b = port.readByte();
      if (b.isEof()) return Eof.EOF;
      int i = from;
      while (i < to) {
        if (b.isEof()) break;
        v.set(i, ((LongInteger) b).asByte());
        b = port.readByte();
        i++;
      }
      return Value.UNSPECIFIED;
    }
  }

}
