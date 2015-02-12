package se.pp.forsberg.scheme.builtinprocedures;

import java.util.List;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Undentifier;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Output extends Library {
  public Output() throws SchemeException {
    super();
  }
  public static Value getName() {
    return makeName("scheme-impl", "output");
  }

  public class Write extends BuiltInProcedure {
    public Write(Environment env) { super("write", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.write(v);
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else
        try {
          port.write(v);
        } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
        }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteShared extends BuiltInProcedure {
    public WriteShared(Environment env) { super("write-shared", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.writeShared(v);
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.writeShared(v);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteSimple extends BuiltInProcedure {
    public WriteSimple(Environment env) { super("write-simple", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.writeSimple(v);
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.writeSimple(v);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class Display extends BuiltInProcedure {
    public Display(Environment env) { super("display", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.display(v);
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Value.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Value v = ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.display(v);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class Newline extends BuiltInProcedure {
    public Newline(Environment env) { super("newline", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.newline();
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.newline();
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteChar extends BuiltInProcedure {
    public WriteChar(Environment env) { super("write-char", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Character.class, Port.class);
      Character v = (Character) ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.write(java.lang.Character.toString(v.getCharacter()));
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Character.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Character v = (Character) ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.write(java.lang.Character.toString(v.getCharacter()));
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteString extends BuiltInProcedure {
    public WriteString(Environment env) { super("write-string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 4, String.class, Port.class, Integer.class, Integer.class);
      java.lang.String v = ((String) ((Pair) arguments).getCar()).getString();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      int from = 0, to = v.length()-1;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
        arguments = ((Pair) arguments).getCdr();
        if (!arguments.isNull()) {
          from = ((Integer) ((Pair) arguments).getCar()).asInt();
          arguments = ((Pair) arguments).getCdr();
          if (!arguments.isNull()) {
            to = ((Integer) ((Pair) arguments).getCar()).asInt();
          }
        }
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      if (from < 0 || from >= v.length() || to < from || to >= v.length()) {
        throw new SchemeException(new RuntimeError(new StringIndexOutOfBoundsException(
            "String index out of bounds")));
      }
      port.write(v.substring(from, to+1));
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 4, String.class, Port.class, Integer.class, Integer.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      java.lang.String v = ((String) ((Pair) arguments).getCar()).getString();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      int from = 0, to = v.length()-1;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
        arguments = ((Pair) arguments).getCdr();
        if (!arguments.isNull()) {
          from = ((Integer) ((Pair) arguments).getCar()).asInt();
          arguments = ((Pair) arguments).getCdr();
          if (!arguments.isNull()) {
            to = ((Integer) ((Pair) arguments).getCar()).asInt();
          }
        }
      }
      if (from < 0 || from >= v.length() || to < from || to >= v.length()) {
        return op.getEvaluator().error("String index out of bounds", Pair.makeList(new String(v), new LongInteger(from, true), new LongInteger(to, true)));
      }
      try {
        port.write(v.substring(from, to+1));
      } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteU8 extends BuiltInProcedure {
    public WriteU8(Environment env) { super("write-u8", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 2, Integer.class, Port.class);
      Integer v = (Integer) ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.write(v.asByte());
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 2, Character.class, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Integer v = (Integer) ((Pair) arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.write(v.asByte());
      } catch (SchemeException e) {
          return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class WriteByteVector extends BuiltInProcedure {
    public WriteByteVector(Environment env) { super("write-bytevector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, 4, ByteVector.class, Port.class, Integer.class, Integer.class);
      List<Byte> v = ((ByteVector) ((Pair) arguments).getCar()).getVector();
      arguments = ((Pair) arguments).getCdr();
      Port port = null;
      int from = 0, to = v.size()-1;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
        arguments = ((Pair) arguments).getCdr();
        if (!arguments.isNull()) {
          from = ((Integer) ((Pair) arguments).getCar()).asInt();
          arguments = ((Pair) arguments).getCdr();
          if (!arguments.isNull()) {
            to = ((Integer) ((Pair) arguments).getCar()).asInt();
          }
        }
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      if (from < 0 || from >= v.size() || to < from || to >= v.size()) {
        throw new SchemeException(new RuntimeError(new IndexOutOfBoundsException(
            "Vector index out of bounds")));
      }
      for (int i = from; i <= to; i++) {
        port.write(v.get(i));
      }
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 1, 4, ByteVector.class, Port.class, Integer.class, Integer.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      List<Byte> v = ((ByteVector) ((Pair) arguments).getCar()).getVector();
      arguments = ((Pair) arguments).getCdr();
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      int from = 0, to = v.size()-1;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
        arguments = ((Pair) arguments).getCdr();
        if (!arguments.isNull()) {
          from = ((Integer) ((Pair) arguments).getCar()).asInt();
          arguments = ((Pair) arguments).getCdr();
          if (!arguments.isNull()) {
            to = ((Integer) ((Pair) arguments).getCar()).asInt();
          }
        }
      }
      if (from < 0 || from >= v.size() || to < from || to >= v.size()) {
        return op.getEvaluator().error("String index out of bounds", Pair.makeList(new ByteVector(v), new LongInteger(from, true), new LongInteger(to, true)));
      }
      try {
        for (int i = from; i <= to; i++) {
          port.write(v.get(i));
        }
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
  public class FlushOutputPort extends BuiltInProcedure {
    public FlushOutputPort(Environment env) { super("flush-output-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, 1, Port.class);
      Port port = null;
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      } else {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "current-output-port needs op-based eval")));
      }
      port.flush();
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      try {
        checkArguments(this, arguments, 0, 1, Port.class);
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      Op result = op;
      Port port = (Port) env.lookup(Undentifier.OUTPUT_PORT);
      if (!arguments.isNull()) {
        port = (Port) ((Pair) arguments).getCar();
      }
      try {
        port.flush();
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      result.setValue(Value.UNSPECIFIED);
      return result;
    }
  }
}
