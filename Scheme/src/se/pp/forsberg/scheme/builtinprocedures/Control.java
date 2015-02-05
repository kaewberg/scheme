package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.Evaluator;
import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Continuation;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Control extends Library {
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("control"), Nil.NIL));
  }

  public class IsProcedure extends BuiltInProcedure {
    public IsProcedure(Environment env) { super("procedure?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, Value.class);
      Value v1 = ((Pair)arguments).getCar();
      return v1.isProcedure()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class Apply extends BuiltInProcedure {
    public Apply(Environment env) { super("apply", env); }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, Procedure.class, Value.class);
      Value v = ((Pair)arguments).getCar();
      Procedure proc = (Procedure) v;
      Value args = ((Pair)((Pair)arguments).getCdr()).getCar();
      op.getEvaluator().setValue(new Pair(proc, args));
      return op;
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Procedure.class, Value.class);
      Value v = ((Pair)arguments).getCar();
      Procedure proc = (Procedure) v;
      Value args = ((Pair)((Pair)arguments).getCdr()).getCar();
      return proc.apply(args);
    }
  }
  /*
  public class Map extends BuiltInProcedure {
    public Map(Environment env) { super("map", env); }
    // ((a b c) (1 2 3)) -> ((a 1) . ((b c ) (2 3))
    protected Value listCarCdr(Value listOfLists) {
      if (!listOfLists.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      Value car = listCar(listOfLists);
      if (car.isNull()) return car;
      return new Pair(car, listCdr(listOfLists));
    }
    protected Value listCar(Value listOfLists) {
      if (listOfLists.isNull()) return Nil.NIL;
      Pair pair = (Pair) listOfLists;
      if (!pair.getCar().isNull()) return Nil.NIL;
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      
      return new Pair(((Pair)pair.getCar()).getCar(), listCdr(pair.getCar()));
    }
    protected Value listCdr(Value listOfLists) {
      if (listOfLists.isNull()) return Nil.NIL;
      Pair pair = (Pair) listOfLists;
      if (!pair.getCar().isNull()) return Nil.NIL;
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      
      return new Pair(((Pair)pair.getCar()).getCdr(), listCdr(pair.getCdr()));
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value result = Nil.NIL;
      Value current = null;
      Value argLists = ((Pair) arguments).getCdr();
      while (!argLists.isNull()) {
        Value carCdr = listCarCdr(argLists);
        if (carCdr.isNull()) return result;
        Value args = ((Pair) carCdr).getCar();
        if (result.isNull()) {
          result = new Pair(proc.apply(args), Nil.NIL);
          current = result;
        } else {
          ((Pair) current).setCdr(new Pair(proc.apply(args), Nil.NIL));
          current = ((Pair) current).getCdr();
        }
      }
      return result;
    }
  }
  public class StringMap extends BuiltInProcedure {
    public StringMap(Environment env) { super("string-map", env); }
    // ("abc" "123") -> (("a" "1") . ("bc" "23")
    protected Value elementsAt(Value listOfValues, int i) {
      if (listOfValues.isNull()) return Nil.NIL;
      if (!listOfValues.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected string list " + getName())));
      Pair pair = (Pair) listOfValues;
      Value car = elementAt(pair.getCar(), i);
      if (car.isNull()) return Nil.NIL;
      return new Pair(car, elementsAt(pair.getCdr(), i+1));
    }
    private Value elementAt(Value val, int i) {
      if (!val.isString()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected string list " + getName())));
      StringBuilder v = ((String) val).getStringBuilder();
      if (i >= v.length()) return Nil.NIL;
      return new Character(v.charAt(i));
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value args;
      arguments = ((Pair)arguments).getCdr();
      int i = 0;
      StringBuilder result = new StringBuilder();
      while (!(args = elementsAt(arguments, i)).isNull()) {
        Value part = proc.apply(args);
        if (!v.isChar()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected character return value in " + getName())));
        result.append(((Character)part).getCharacter());
      }
      return new String(result);
    }
  }
  public class VectorMap extends BuiltInProcedure {
    public VectorMap(Environment env) { super("vector-map", env); }
    protected Value elementsAt(Value listOfValues, int i) {
      if (listOfValues.isNull()) return Nil.NIL;
      if (!listOfValues.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected vector list " + getName())));
      Pair pair = (Pair) listOfValues;
      Value car = elementAt(pair.getCar(), i);
      if (car.isNull()) return Nil.NIL;
      return new Pair(car, elementsAt(pair.getCdr(), i+1));
    }
    private Value elementAt(Value val, int i) {
      if (!val.isVector()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected vector list " + getName())));
      List<Value> v = ((Vector) val).getVector();
      if (i >= v.size()) return Nil.NIL;
      return v.get(i);
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value args;
      arguments = ((Pair)arguments).getCdr();
      int i = 0;
      List<Value> result = new ArrayList<Value>();
      while (!(args = elementsAt(arguments, i)).isNull()) {
        Value part = proc.apply(args);
        result.add(part);
      }
      return new Vector(result);
    }
  }
  public class ForEach extends BuiltInProcedure {
    public ForEach(Environment env) { super("for-each", env); }
    // ((a b c) (1 2 3)) -> ((a 1) . ((b c ) (2 3))
    protected Value listCarCdr(Value listOfLists) {
      if (!listOfLists.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      Value car = listCar(listOfLists);
      if (car.isNull()) return car;
      return new Pair(car, listCdr(listOfLists));
    }
    protected Value listCar(Value listOfLists) {
      if (listOfLists.isNull()) return Nil.NIL;
      Pair pair = (Pair) listOfLists;
      if (!pair.getCar().isNull()) return Nil.NIL;
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      
      return new Pair(((Pair)pair.getCar()).getCar(), listCdr(pair.getCar()));
    }
    protected Value listCdr(Value listOfLists) {
      if (listOfLists.isNull()) return Nil.NIL;
      Pair pair = (Pair) listOfLists;
      if (!pair.getCar().isNull()) return Nil.NIL;
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected argument lists " + getName())));
      
      return new Pair(((Pair)pair.getCar()).getCdr(), listCdr(pair.getCdr()));
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value argLists = ((Pair) arguments).getCdr();
      while (!argLists.isNull()) {
        Value carCdr = listCarCdr(argLists);
        if (carCdr.isNull()) return Value.UNSPECIFIED;
        Value args = ((Pair) carCdr).getCar();
        proc.apply(args);
        argLists = ((Pair) carCdr).getCdr();
      }
      return Value.UNSPECIFIED;
    }
  }
  public class StringForEach extends BuiltInProcedure {
    public StringForEach(Environment env) { super("string-for-each", env); }
    protected Value elementsAt(Value listOfValues, int i) {
      if (listOfValues.isNull()) return Nil.NIL;
      if (!listOfValues.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected string list " + getName())));
      Pair pair = (Pair) listOfValues;
      Value car = elementAt(pair.getCar(), i);
      if (car.isNull()) return Nil.NIL;
      return new Pair(car, elementsAt(pair.getCdr(), i+1));
    }
    private Value elementAt(Value val, int i) {
      if (!val.isString()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected string list " + getName())));
      StringBuilder v = ((String) val).getStringBuilder();
      if (i >= v.length()) return Nil.NIL;
      return new Character(v.charAt(i));
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value args;
      arguments = ((Pair)arguments).getCdr();
      int i = 0;
      while (!(args = elementsAt(arguments, i)).isNull()) {
        proc.apply(args);
      }
      return Value.UNSPECIFIED;
    }
  }
  public class VectorForEach extends BuiltInProcedure {
    public VectorForEach(Environment env) { super("vector-for-each", env); }
    protected Value elementsAt(Value listOfValues, int i) {
      if (listOfValues.isNull()) return Nil.NIL;
      if (!listOfValues.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected vector list " + getName())));
      Pair pair = (Pair) listOfValues;
      Value car = elementAt(pair.getCar(), i);
      if (car.isNull()) return Nil.NIL;
      return new Pair(car, elementsAt(pair.getCdr(), i+1));
    }
    private Value elementAt(Value val, int i) {
      if (!val.isVector()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected vector list " + getName())));
      List<Value> v = ((Vector) val).getVector();
      if (i >= v.size()) return Nil.NIL;
      return v.get(i);
    }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value args;
      arguments = ((Pair)arguments).getCdr();
      int i = 0;
      while (!(args = elementsAt(arguments, i)).isNull()) {
        proc.apply(args);
      }
      return Value.UNSPECIFIED;
    }
  }
  */
  public class CallWithCurrentContinuation extends BuiltInProcedure {
    public CallWithCurrentContinuation(Environment env) {
      super("call-with-current-continuation", env);
    }

    @Override
    public Value apply(Value arguments) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("call/cc not supported in recursive eval")));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      Op cc = op;
      Evaluator ev = op.getEvaluator();
      op = new Op.Apply(ev, cc, env);
      Value v = ev.getValue();
//      if (vs.length != 1) ev.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      ev.setValue(new Pair(v, new Pair(new Continuation(cc, 1), Nil.NIL)));
      return op;
    }
  }
  public class CallCC extends BuiltInProcedure {
    public CallCC(Environment env) {
      super("call/cc", env);
    }
    @Override
    public Value apply(Value arguments) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("call/cc not supported in recursive eval")));
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      Op cc = op;
      Evaluator ev = op.getEvaluator();
      op = new Op.Apply(ev, cc, env);
      Value v = ev.getValue();
//      if (vs.length != 1) ev.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      ev.setValue(new Pair(v, new Pair(new Continuation(cc, 1), Nil.NIL)));
      return op;
    }
  }
  // This is a biggie.
  // (dynamic-wind before thunk after)
  // Setup a call to thunk in such a way that before is called before entering and
  // after after exiting *even* if continuations are used.
  // In other words,
  // 1) if a continuation is returned from thunk and used later then before should
  // be called when it is used
  // 2) if a continuation is used in thunk to escape the dynamic-wind, then after
  // should be called
  // dynamic-wind can be nested
  
  public class DynamicWind extends BuiltInProcedure {
    public DynamicWind(Environment env) {
      super("dynamic-wind", env);
    }

    @Override
    // Solution was to add linked list of DynamicWind objects in env
    public Value apply(Value arguments) {
      checkArguments(this, arguments, Procedure.class, Procedure.class, Procedure.class);
      Procedure before = (Procedure) ((Pair) arguments).getCar();
      Procedure thunk = (Procedure) ((Pair)((Pair) arguments).getCdr()).getCar();
      Procedure after = (Procedure) ((Pair)((Pair)((Pair) arguments).getCdr()).getCdr()).getCar();
      before.apply(Nil.NIL);
      Value result = thunk.apply(Nil.NIL);
      after.apply(Nil.NIL);
      return result;
    }
    @Override
    public Op apply(Op op, Environment env, Value arguments) {
      checkArguments(this, arguments, Procedure.class, Procedure.class, Procedure.class);
      Procedure before = (Procedure) ((Pair) arguments).getCar();
      Procedure thunk = (Procedure) ((Pair)((Pair) arguments).getCdr()).getCar();
      Procedure after = (Procedure) ((Pair)((Pair)((Pair) arguments).getCdr()).getCdr()).getCar();
      env.setDynamicWind(new se.pp.forsberg.scheme.DynamicWind(env.getDynamicWind(), before, after));
      Op result = op;
      // Use a hack to insert the return value from thunk
      Op.SetValue setValue = new Op.SetValue(result, op.getEnvironment(), Value.UNSPECIFIED);
      result = setValue;
      result = new Op.Apply(op.getEvaluator(), result, op.getEnvironment());
      result = new Op.SetValue(result, op.getEnvironment(), new Pair(after, Nil.NIL));
      result = new Op.PasteValue(op.getEvaluator(), result, op.getEnvironment(), setValue);
      result = new Op.Apply(op.getEvaluator(), result, op.getEnvironment());
      result = new Op.SetValue(result, op.getEnvironment(), new Pair(thunk, Nil.NIL));
      result = new Op.Apply(op.getEvaluator(), result, op.getEnvironment());
      result = new Op.SetValue(result, op.getEnvironment(), new Pair(before, Nil.NIL));
      return result;
    }
  }
}
