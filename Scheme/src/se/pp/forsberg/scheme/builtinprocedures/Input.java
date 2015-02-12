package se.pp.forsberg.scheme.builtinprocedures;

import java.util.ArrayList;
import java.util.List;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Eof;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Undentifier;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Input extends Library {
  public Input() throws SchemeException {
    super();
    // TODO Auto-generated constructor stub
  }

  public static Value getName() {
    return makeName("scheme-impl", "input");
  }

  public class Read extends BuiltInProcedure {
    public Read(Environment env) {
      super("read", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-input-port needs op-based eval")));
      }
      return port.read();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.read());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class ReadChar extends BuiltInProcedure {
    public ReadChar(Environment env) {
      super("read-char", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.readChar();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.readChar());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class PeekChar extends BuiltInProcedure {
    public PeekChar(Environment env) {
      super("peek-char", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.peekChar();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.peekChar());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class ReadLine extends BuiltInProcedure {
    public ReadLine(Environment env) {
      super("read-line", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.readLine();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.readLine());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class IsEofObject extends BuiltInProcedure {
    public IsEofObject(Environment env) {
      super("eof-object?", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      Value v = ((Pair) arguments).getCar();
      return v.isEof() ? Boolean.TRUE : Boolean.FALSE;
    }
  }

  public class EofObject extends BuiltInProcedure {
    public EofObject(Environment env) {
      super("eof-object", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0);
      return Eof.EOF;
    }
  }

  public class IsCharReady extends BuiltInProcedure {
    public IsCharReady(Environment env) {
      super("char-ready?", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.isCharReady();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
         op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.isCharReady());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class ReadString extends BuiltInProcedure {
    public ReadString(Environment env) {
      super("read-string", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      Integer k = (Integer) ((Pair) arguments).getCar();
      Port port = null;
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      StringBuilder result = new StringBuilder();
      Value c = port.readChar();
      if (c.isEof())
        return Eof.EOF;
      while (k.isPositive()) {
        if (c.isEof())
          break;
        result.append(((Character) c).getCharacter());
        c = port.readChar();
        k = k.minus(LongInteger.ONE);
      }
      return new String(result);
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Integer k = (Integer) ((Pair) arguments).getCar();
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      StringBuilder s = new StringBuilder();
      Value c;
      try {
        c = port.readChar();
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Op result = op;
      if (c.isEof()) {
        result.setValue(Eof.EOF);
        return result;
      }
      while (k.isPositive()) {
        if (c.isEof())
          break;
        s.append(((Character) c).getCharacter());
        try {
          c = port.readChar();
        } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
        }
        k = k.minus(LongInteger.ONE);
      }
      result.setValue(new String(s));
      return result;
    }
  }

  public class ReadU8 extends BuiltInProcedure {
    public ReadU8(Environment env) {
      super("read-u8", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.readByte();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.readByte());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class PeekU8 extends BuiltInProcedure {
    public PeekU8(Environment env) {
      super("peek-u8", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.peekByte();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.peekByte());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class IsU8Ready extends BuiltInProcedure {
    public IsU8Ready(Environment env) {
      super("u8-ready?", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      return port.isByteReady();
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      Op result = op;
      try {
        result.setValue(port.isByteReady());
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      return result;
    }
  }

  public class ReadByteVector extends BuiltInProcedure {
    public ReadByteVector(Environment env) {
      super("read-bytevector", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      Integer k = (Integer) ((Pair) arguments).getCar();
      Port port = null;
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      List<Byte> result = new ArrayList<Byte>();
      Value b = port.readByte();
      if (b.isEof())
        return Eof.EOF;
      while (k.isPositive()) {
        if (b.isEof())
          break;
        result.add(((LongInteger) b).asByte());
        b = port.readByte();
        k = k.minus(LongInteger.ONE);
      }
      return new ByteVector(result);
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Integer k = (Integer) ((Pair) arguments).getCar();
      Port port = null;
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        op.getEvaluator().error(new Error("current-input-port needs op-based eval"));
      }
      List<Byte> v = new ArrayList<Byte>();
      Value b;
      try {
        b = port.readByte();
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Op result = op;
      if (b.isEof()) {
        result.setValue(Eof.EOF);
        return result;
      }
      while (k.isPositive()) {
        if (b.isEof())
          break;
        v.add(((LongInteger) b).asByte());
        try {
          b = port.readByte();
        } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
        }
        k = k.minus(LongInteger.ONE);
      }
      result.setValue(new ByteVector(v));
      return result;
    }
  }

  public class ReadByteVectorSet extends BuiltInProcedure {
    public ReadByteVectorSet(Environment env) {
      super("read-bytevector!", env);
    }

    @Override
    public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 4, ByteVector.class, Port.class, Integer.class, Integer.class);
      List<Byte> v = ((ByteVector) ((Pair) arguments).getCar()).getVector();
      Port port = null;
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException("current-input-port needs op-based eval");
      }
      arguments = ((Pair) arguments).getCdr();
      int from = 0;
      if (!arguments.isNull()) {
        from = ((Integer) ((Pair) arguments).getCar()).asInt();
      }
      int to = v.size();
      if (!arguments.isNull()) {
        to = ((Integer) ((Pair) arguments).getCar()).asInt();
      }
      Value b = port.readByte();
      if (b.isEof())
        return Eof.EOF;
      int i = from;
      while (i < to) {
        if (b.isEof())
          break;
        v.set(i, ((LongInteger) b).asByte());
        b = port.readByte();
        i++;
      }
      return Value.UNSPECIFIED;
    }

    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 4, ByteVector.class, Port.class, Integer.class, Integer.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      List<Byte> v = ((ByteVector) ((Pair) arguments).getCar()).getVector();
      Port port = (Port) env.lookup(Undentifier.INPUT_PORT);
      arguments = ((Pair) arguments).getCdr();
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      arguments = ((Pair) arguments).getCdr();
      int from = 0;
      if (!arguments.isNull()) {
        from = ((Integer) ((Pair) arguments).getCar()).asInt();
      }
      int to = v.size();
      if (!arguments.isNull()) {
        to = ((Integer) ((Pair) arguments).getCar()).asInt();
      }
      Value b;
      try {
        b = port.readByte();
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      Op result = op;
      if (b.isEof()) {
        result.setValue(Eof.EOF);
        return result;
      }
      int i = from;
      while (i < to) {
        if (b.isEof())
          break;
        v.set(i, ((LongInteger) b).asByte());
        try {
          b = port.readByte();
        } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
        }
        i++;
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
}
