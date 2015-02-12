package se.pp.forsberg.scheme.builtinprocedures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Undentifier;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Ports extends Library {
  
  public Ports() throws SchemeException {
    getEnvironment().define(Undentifier.INPUT_PORT, Port.STDIO);
    getEnvironment().define(Undentifier.OUTPUT_PORT, Port.STDIO);
    getEnvironment().define(Undentifier.ERROR_PORT, Port.STDERR);
  }
  
  public static Value getName() {
    return makeName("scheme-impl", "ports");
  }

//  public class CallWithPort extends BuiltInProcedure {
//    public CallWithPort(Environment env) { super("call-with-port", env); }
//    @Override public Value apply(Value arguments) throws SchemeException {
//      checkArguments(this, arguments, Port.class, Procedure.class);
//      Port port = (Port) ((Pair)arguments).getCar();
//      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
//      try {
//        return proc.apply(port); 
//      } finally {
//        port.close();
//      }
//    }
//  }
//  public class CallWithInputFile extends BuiltInProcedure {
//    public CallWithInputFile(Environment env) { super("call-with-input-file", env); }
//    @Override public Value apply(Value arguments) throws SchemeException {
//      checkArguments(this, arguments, String.class, Procedure.class);
//      String file = (String) ((Pair)arguments).getCar();
//      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
//      Port port = null;
//      try {
//        port = new Port(new FileInputStream(file.getString()));
//        return proc.apply(port); 
//      } catch (FileNotFoundException e) {
//        throw new SchemeException(new FileError(e));
//      } finally {
//        if (port != null) port.close();
//      }
//    }
//  }
//  public class CallWithOutputFile extends BuiltInProcedure {
//    public CallWithOutputFile(Environment env) { super("call-with-output-file", env); }
//    @Override public Value apply(Value arguments) throws SchemeException {
//      checkArguments(this, arguments, String.class, Procedure.class);
//      String file = (String) ((Pair)arguments).getCar();
//      Procedure proc = (Procedure) ((Pair) ((Pair)arguments).getCdr()).getCar();
//      Port port = null;
//      try {
//        port = new Port(new FileOutputStream(file.getString()));
//        return proc.apply(port); 
//      } catch (FileNotFoundException e) {
//        throw new SchemeException(new FileError(e));
//      } finally {
//        if (port != null) port.close();
//      }
//    }
//  }
  public class IsInputPort extends BuiltInProcedure {
    public IsInputPort(Environment env) { super("input-port?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      if (!value.isPort()) return Boolean.FALSE;
      Port port = (Port) value;
      return port.isInputPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsOutputPort extends BuiltInProcedure {
    public IsOutputPort(Environment env) { super("output-port?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      if (!value.isPort()) return Boolean.FALSE;
      Port port = (Port) value;
      return port.isOutputPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsPort extends BuiltInProcedure {
    public IsPort(Environment env) { super("port?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Value.class);
      Value value = ((Pair)arguments).getCar();
      return value.isPort()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsInputPortOpen extends BuiltInProcedure {
    public IsInputPortOpen(Environment env) { super("input-port-open?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      return port.isInputPortOpen()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsOutputPortOpen extends BuiltInProcedure {
    public IsOutputPortOpen(Environment env) { super("output-port-open?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      return port.isOutputPortOpen()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class CurrentInputPort extends BuiltInProcedure {
    public CurrentInputPort(Environment env) { super("current-input-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Only available with op-based eval")));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      op.setValue(env.lookup(Undentifier.INPUT_PORT));
      return op;
    }
  }
  public class CurrentOutputPort extends BuiltInProcedure {
    public CurrentOutputPort(Environment env) { super("current-output-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Only available with op-based eval")));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      op.setValue(env.lookup(Undentifier.OUTPUT_PORT));
      return op;
    }
  }
  public class CurrentErrorPort extends BuiltInProcedure {
    public CurrentErrorPort(Environment env) { super("current-error-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Only available with op-based eval")));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      op.setValue(env.lookup(Undentifier.ERROR_PORT));
      return op;
    }
  }
  // Replace current input in call
  // Implemented by binding the undentifier current-input-file to stdin
  // in top level env. With-input-from-file then creates a new env
  // and reundefines current-input-file.
  // 
  public class WithInputFromFile extends BuiltInProcedure {
    public WithInputFromFile(Environment env) { super("with-input-from-file", env); }
    public Op apply(Op op, Environment env, Value arguments) { 
      try {
        checkArguments(this, arguments, String.class, Procedure.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      String filename = (String) ((Pair)arguments).getCar();
      Procedure thunk = (Procedure) ((Pair)((Pair)arguments).getCdr()).getCar();
      Port port;
      try {
        port = Port.openInputFile(filename);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      
      Environment newEnv = new Environment(env);
      try {
        newEnv.define(Undentifier.INPUT_PORT, Port.openInputFile(filename));
      } catch (SchemeException e) {
        op.getEvaluator().error(e.getError());
      }
      
      Op result = op;
      Op.SetValue setValue = new Op.SetValue(result, op.getEnvironment(), Value.UNSPECIFIED);
      result = setValue;
      result = new Op.Apply(op.getEvaluator(), result, op.getEnvironment());
      result = new Op.SetValue(result, op.getEnvironment(), new Pair(op.getEnvironment().lookup(new Identifier("close-file")), new Pair(port, Nil.NIL)));
      result = new Op.PasteValue(op.getEvaluator(), result, op.getEnvironment(), setValue);
      result = new Op.Apply(op.getEvaluator(), result, newEnv);
      result.setValue(new Pair(thunk, Nil.NIL));
      return result;
    }
    @Override
    public Value apply(Value arguments) throws SchemeException {
      throw new SchemeException("Only available in op-based eval");
    }
    
  }
  public class WithOutputToFile extends BuiltInProcedure {
    public WithOutputToFile(Environment env) { super("with-output-to-file", env); }
    public Op apply(Op op, Environment env, Value arguments) { 
      try {
        checkArguments(this, arguments, String.class, Procedure.class);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      String filename = (String) ((Pair)arguments).getCar();
      Procedure thunk = (Procedure) ((Pair)((Pair)arguments).getCdr()).getCar();
      Port port;
      try {
        port = Port.openOutputFile(filename);
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      
      Environment newEnv = new Environment(env);
      try {
        newEnv.define(Undentifier.OUTPUT_PORT, Port.openInputFile(filename));
      } catch (SchemeException e) {
        return op.getEvaluator().error(e.getError());
      }
      
      Op result = op;
      Op.SetValue setValue = new Op.SetValue(result, op.getEnvironment(), Value.UNSPECIFIED);
      result = setValue;
      result = new Op.Apply(op.getEvaluator(), result, op.getEnvironment());
      result = new Op.SetValue(result, op.getEnvironment(), new Pair(op.getEnvironment().lookup(new Identifier("close-file")), new Pair(port, Nil.NIL)));
      result = new Op.PasteValue(op.getEvaluator(), result, op.getEnvironment(), setValue);
      result = new Op.Apply(op.getEvaluator(), result, newEnv);
      result.setValue(new Pair(thunk, Nil.NIL));
      return result;
      // TODO should the file be closed if you escape with a continuation?
    }
    @Override
    public Value apply(Value arguments) throws SchemeException {
      throw new SchemeException("Only available in op-based eval");
    }
    
  }
  public class OpenInputFile extends BuiltInProcedure {
    public OpenInputFile(Environment env) { super("open-input-file", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, String.class);
      String filename = (String) ((Pair)arguments).getCar();
      return Port.openInputFile(filename);
    }
  }
  public class OpenOutputFile extends BuiltInProcedure {
    public OpenOutputFile(Environment env) { super("open-output-file", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, String.class);
      String filename = (String) ((Pair)arguments).getCar();
      return Port.openOutputFile(filename);
    }
  }
  public class ClosePort extends BuiltInProcedure {
    public ClosePort(Environment env) { super("close-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      port.close();
      return Value.UNSPECIFIED;
    }
  }
  public class CloseInputPort extends BuiltInProcedure {
    public CloseInputPort(Environment env) { super("close-input-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      port.closeInput();
      return Value.UNSPECIFIED;
    }
  }
  public class CloseOutputPort extends BuiltInProcedure {
    public CloseOutputPort(Environment env) { super("close-output-port", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      port.closeOutput();
      return Value.UNSPECIFIED;
    }
  }
  public class OpenInputString extends BuiltInProcedure {
    public OpenInputString(Environment env) { super("open-input-string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return new Port(new ByteArrayInputStream(s.getString().getBytes()), null);
    }
  }
  public class OpenOutputString extends BuiltInProcedure {
    public OpenOutputString(Environment env) { super("open-output-string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0);
      return new Port(null, new ByteArrayOutputStream());
    }
  }
  public class GetOutputString extends BuiltInProcedure {
    public GetOutputString(Environment env) { super("get-output-string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      return port.getOutputString();
    }
  }
  public class OpenInputByteVector extends BuiltInProcedure {
    public OpenInputByteVector(Environment env) { super("open-input-bytevector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, ByteVector.class);
      ByteVector v = (ByteVector) ((Pair)arguments).getCar();
      byte array[] = new byte[v.getVector().size()];
      int i = 0;
      for (Byte b: v.getVector()) {
        array[i++] = b;
      }
      return new Port(new ByteArrayInputStream(array), null);
    }
  }
  public class OpenOutputByteVector extends BuiltInProcedure {
    public OpenOutputByteVector(Environment env) { super("open-output-bytevector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0);
      return new Port(null, new ByteArrayOutputStream());
    }
  }
  public class GetOutputByteVector extends BuiltInProcedure {
    public GetOutputByteVector(Environment env) { super("get-output-bytevector", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Port.class);
      Port port = (Port) ((Pair)arguments).getCar();
      return port.getOutputBytevector();
    }
  }
}
