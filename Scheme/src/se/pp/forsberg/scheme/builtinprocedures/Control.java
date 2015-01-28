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
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
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
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE);
      Value v = ((Pair)arguments).getCar();
      if (!v.isProcedure()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected procedure in " + getName())));
      Procedure proc = (Procedure) v;
      Value args = ((Pair)arguments).getCdr();
      return proc.apply(args);
    }
  }
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
  public class CallWithCurrentContinuation extends BuiltInProcedure.TODO {
    public CallWithCurrentContinuation(java.lang.String name) {
      super("call-with-current-continuation");
    }
  }
  public class CallCC extends BuiltInProcedure.TODO {
    public CallCC(java.lang.String name) {
      super("call/cc");
    }
  }
  public class CallWithValues extends BuiltInProcedure.TODO {
    public CallWithValues(java.lang.String name) {
      super("call-with-values");
    }
  }
  public class DynamicWind extends BuiltInProcedure.TODO {
    public DynamicWind(java.lang.String name) {
      super("dynamic-wind");
    }
  }
}
