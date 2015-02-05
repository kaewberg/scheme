package se.pp.forsberg.scheme.builtinprocedures;

import java.io.File;
import java.util.Map;

import se.pp.forsberg.scheme.Evaluator;
import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.FileError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.RationalPair;

public class SystemInterface extends Library {
  public static Value getName() {
    return makeName("scheme-impl", "system");
  }

  public class Load extends BuiltInProcedure {
    public Load(Environment env) { super("load", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2, String.class, Environment.class);
      String filename = (String) ((Pair)arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Environment environment;
      if (!arguments.isNull()) {
        environment = (Environment) ((Pair)arguments).getCar();
      } else {
        environment = Environment.interactionEnvironment();
      }
      Port input = Port.openInputFile(filename);
      Value datum = input.read();
      while (!datum.isEof()) {
        datum.eval(environment);
        datum = input.read();
      }
      return Value.UNSPECIFIED;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, 1, 2, String.class, Environment.class);
      String filename = (String) ((Pair)arguments).getCar();
      arguments = ((Pair) arguments).getCdr();
      Environment environment;
      if (!arguments.isNull()) {
        environment = (Environment) ((Pair)arguments).getCar();
      } else {
        environment = Environment.interactionEnvironment();
      }
      Pair p = new Pair(new Identifier("begin"), Nil.NIL);
      Value begin = p;
      Port input = Port.openInputFile(filename);
      Value datum = input.read();
      while (!datum.isEof()) {
        p.setCdr(new Pair(datum, Nil.NIL));
        p = (Pair) p.getCdr();
        datum = input.read();
      }
      Op result = new Op.Eval(op, environment);
      op.setValue(begin);
      return result;
    }
  }
  public class DoesFileExist extends BuiltInProcedure {
    public DoesFileExist(Environment env) { super("file-exist?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class);
      String filename = (String) ((Pair)arguments).getCar();
      return new File(filename.getString()).exists()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class DeleteFile extends BuiltInProcedure {
    public DeleteFile(Environment env) { super("delete-file", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class);
      String filename = (String) ((Pair)arguments).getCar();
      if (!new File(filename.getString()).delete()) {
        throw new SchemeException(new FileError(new IllegalArgumentException("Unable to delete file")));
      };
      return Value.UNSPECIFIED;
    }
  }
  public class Exit extends BuiltInProcedure {
    public Exit(Environment env) { super("exit", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class);
      Value v = ((Pair)arguments).getCar();
      int status = 1;
      if (v.isBoolean()) {
        status = v.asBoolean()? 0:1;
      } else if (v.isInteger()) {
        status = ((Integer) v).asInt();
      }
      System.exit(status);
      return Value.UNSPECIFIED;
    }
    class ExitOp extends Op {
      private int status;
      public ExitOp(Evaluator evaluator, int status) {
        super(evaluator, null);
        this.status = status;
      }
      @Override
      public Op apply(Value v) {
        System.exit(status);
        return null;
      }
      @Override
      protected java.lang.String getDescription() {
        return "Exit " + status;
      }
      
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, Value.class);
      Value v = ((Pair)arguments).getCar();
      int status = 1;
      if (v.isBoolean()) {
        status = v.asBoolean()? 0:1;
      } else if (v.isInteger()) {
        status = ((Integer) v).asInt();
      }
      Op result = new ExitOp(op.getEvaluator(), status);
      return result;
    }
  }
  public class EmergencyExit extends BuiltInProcedure {
    public EmergencyExit(Environment env) { super("emergency-exit", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class);
      Value v = ((Pair)arguments).getCar();
      int status = 1;
      if (v.isBoolean()) {
        status = v.asBoolean()? 0:1;
      } else if (v.isInteger()) {
        status = ((Integer) v).asInt();
      }
      System.exit(status);
      return Value.UNSPECIFIED;
    }
  }
  public class GetEnvironmentVariable extends BuiltInProcedure {
    public GetEnvironmentVariable(Environment env) { super("get-environment-variable", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, String.class);
      String v = (String) ((Pair)arguments).getCar();
      java.lang.String result = System.getenv(v.getString());
      if (result == null) return Boolean.FALSE;
      return new String(result);
    }
  }
  public class GetEnvironmentVariables extends BuiltInProcedure {
    public GetEnvironmentVariables(Environment env) { super("get-environment-variables", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      Map<java.lang.String, java.lang.String> env = System.getenv();
      Value result = Nil.NIL;
      Pair p = null;
      for (java.lang.String var: env.keySet()) {
        Value item = new Pair(new String(var), new Pair(new String(env.get(var)), Nil.NIL));
        if (result.isNull()) {
          p = new Pair(item, Nil.NIL);
          result = p;
        } else {
          p.setCdr(new Pair(item, Nil.NIL));
          p = (Pair) p.getCdr();
        }
      }
      return result;
    }
  }
  public class CurrentSecond extends BuiltInProcedure {
    public CurrentSecond(Environment env) { super("current-second", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      return new RationalPair(new LongInteger(System.currentTimeMillis()-100000, false), new LongInteger(1000, false), false);
    }
  }
  public class CurrentJiffy extends BuiltInProcedure {
    public CurrentJiffy(Environment env) { super("current-jiffy", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      return new LongInteger(System.currentTimeMillis(), true);
    }
  }
  public class JiffiesPerSecond extends BuiltInProcedure {
    public JiffiesPerSecond(Environment env) { super("jiffies-per-second", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 0);
      return new LongInteger(1000, true);
    }
  }
}
