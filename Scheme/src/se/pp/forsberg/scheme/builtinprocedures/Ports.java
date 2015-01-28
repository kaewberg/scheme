package se.pp.forsberg.scheme.builtinprocedures;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.FileError;

public class Ports extends Library {
  public static Value getName() {
    return makeName("scheme-impl", "ports");
  }

  public class CallWithPort extends BuiltInProcedure {
    public CallWithPort(Environment env) { super("call-with-port", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Port.class, Procedure.class);
      Port port = (Port) ((Pair)arguments).getCar();
      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
      try {
        return proc.apply(port); 
      } finally {
        port.close();
      }
    }
  }
  public class CallWithInputFile extends BuiltInProcedure {
    public CallWithInputFile(Environment env) { super("call-with-input-file", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class, Procedure.class);
      String file = (String) ((Pair)arguments).getCar();
      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
      Port port = null;
      try {
        port = new Port(new FileInputStream(file.getString()));
        return proc.apply(port); 
      } catch (FileNotFoundException e) {
        throw new SchemeException(new FileError(e));
      } finally {
        if (port != null) port.close();
      }
    }
  }
  public class CallWithOutputFile extends BuiltInProcedure {
    public CallWithOutputFile(Environment env) { super("call-with-output-file", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class, Procedure.class);
      String file = (String) ((Pair)arguments).getCar();
      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
      Port port = null;
      try {
        port = new Port(new FileOutputStream(file.getString()));
        return proc.apply(port); 
      } catch (FileNotFoundException e) {
        throw new SchemeException(new FileError(e));
      } finally {
        if (port != null) port.close();
      }
    }
  }
  public class IsInputPort extends BuiltInProcedure {
    public IsInputPort(Environment env) { super("input-port?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      if (!value.isPort()) return Boolean.FALSE;
      Port port = (Port) value;
      return port.isInputPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsOutputPort extends BuiltInProcedure {
    public IsOutputPort(Environment env) { super("output-port?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      if (!value.isPort()) return Boolean.FALSE;
      Port port = (Port) value;
      return port.isOutputPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsPort extends BuiltInProcedure {
    public IsPort(Environment env) { super("port?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      return value.isPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }

}
