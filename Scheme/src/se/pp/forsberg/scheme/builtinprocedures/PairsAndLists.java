package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class PairsAndLists extends Library {
  
  public PairsAndLists() {
    Library.load(Library.makeName("scheme-impl", "equal"), getEnvironment());
  }
  
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("pairs-and-lists"), Nil.NIL));
  }

  public class IsPair extends BuiltInProcedure {
    public IsPair(Environment env) { super("pair?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class);
      return ((Pair)arguments).getCar().isPair()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class Cons extends BuiltInProcedure {
    public Cons(Environment env) { super("cons", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, Value.class);
      Value car = ((Pair)arguments).getCar();
      Value cdr = ((Pair) ((Pair)arguments).getCdr()).getCar();
      return new Pair(car, cdr);
    }
  }
  public class Car extends BuiltInProcedure {
    public Car(Environment env) { super("car", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      return pair.getCar();
    }
  }
  public class Cdr extends BuiltInProcedure {
    public Cdr(Environment env) { super("cdr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      return pair.getCdr();
    }
  }
  public class SetCar extends BuiltInProcedure {
    public SetCar(Environment env) { super("set-car!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class, Value.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      Value car = ((Pair) ((Pair)arguments).getCdr()).getCar();
      // TODO constant lists
      pair.setCar(car);
      return Nil.NIL;
    }
  }
  public class SetCdr extends BuiltInProcedure {
    public SetCdr(Environment env) { super("set-cdr!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class, Value.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      Value cdr = ((Pair) ((Pair)arguments).getCdr()).getCar();
      // TODO constant lists
      pair.setCdr(cdr);
      return Nil.NIL;
    }
  }
  /*
  public class Caar extends BuiltInProcedure {
    public Caar(Environment env) { super("caar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cadr extends BuiltInProcedure {
    public Cadr(Environment env) { super("cadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cdar extends BuiltInProcedure {
    public Cdar(Environment env) { super("cdar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Cddr extends BuiltInProcedure {
    public Cddr(Environment env) { super("cddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }

  public class Caaar extends BuiltInProcedure {
    public Caaar(Environment env) { super("caaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdaar extends BuiltInProcedure {
    public Cdaar(Environment env) { super("cdaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Caadr extends BuiltInProcedure {
    public Caadr(Environment env) { super("caadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdadr extends BuiltInProcedure {
    public Cdadr(Environment env) { super("cdadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Cadar extends BuiltInProcedure {
    public Cadar(Environment env) { super("cadar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cddar extends BuiltInProcedure {
    public Cddar(Environment env) { super("cddar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }
  public class Caddr extends BuiltInProcedure {
    public Caddr(Environment env) { super("caddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cdddr extends BuiltInProcedure {
    public Cdddr(Environment env) { super("cdddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }

  public class Caaaar extends BuiltInProcedure {
    public Caaaar(Environment env) { super("caaaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdaaar extends BuiltInProcedure {
    public Cdaaar(Environment env) { super("cdaaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Cadaar extends BuiltInProcedure {
    public Cadaar(Environment env) { super("cadaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cddaar extends BuiltInProcedure {
    public Cddaar(Environment env) { super("cddaar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }
  public class Caaadr extends BuiltInProcedure {
    public Caaadr(Environment env) { super("caaadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdaadr extends BuiltInProcedure {
    public Cdaadr(Environment env) { super("cdaadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Cadadr extends BuiltInProcedure {
    public Cadadr(Environment env) { super("cadadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cddadr extends BuiltInProcedure {
    public Cddadr(Environment env) { super("cddadr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }
  public class Caadar extends BuiltInProcedure {
    public Caadar(Environment env) { super("caadar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdadar extends BuiltInProcedure {
    public Cdadar(Environment env) { super("cdadar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Caddar extends BuiltInProcedure {
    public Caddar(Environment env) { super("caddar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cdddar extends BuiltInProcedure {
    public Cdddar(Environment env) { super("cdddar", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }
  public class Caaddr extends BuiltInProcedure {
    public Caaddr(Environment env) { super("caaddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCar();
    }
  }
  public class Cdaddr extends BuiltInProcedure {
    public Cdaddr(Environment env) { super("cdaddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCar();
      return pair.getCdr();
    }
  }
  public class Cadddr extends BuiltInProcedure {
    public Cadddr(Environment env) { super("cadddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCar();
    }
  }
  public class Cddddr extends BuiltInProcedure {
    public Cddddr(Environment env) { super("cddddr", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Pair.class);
      Pair pair = (Pair) ((Pair)arguments).getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid argument to " + getName())));
      pair = (Pair) pair.getCdr();
      return pair.getCdr();
    }
  }
  */
  public class IsNull extends BuiltInProcedure {
    public IsNull(Environment env) { super("null?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class);
      return ((Pair)arguments).getCar().isNull()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class IsList extends BuiltInProcedure {
    public IsList(Environment env) { super("list?", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class);
      Value v = ((Pair)arguments).getCar();
      while (v.isPair()) {
        v = ((Pair) v).getCdr();
      }
      return v.isNull()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  public class MakeList extends BuiltInProcedure {
    public MakeList(Environment env) { super("make-list", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1, 2);
      if (!((Pair)arguments).getCar().isInteger()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected integer")));
      Integer k = (Integer) ((Pair)arguments).getCar();
      if (k.isNegative()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected positive integer")));
      Value v = Nil.NIL;
      if (((Pair)arguments).getCdr().isPair()) v = ((Pair)((Pair)arguments).getCdr()).getCar();
      if (k.isZero()) return Nil.NIL;
      Pair p = new Pair(v, Nil.NIL);
      k = k.minus(LongInteger.ONE);
      while (k.isPositive()) {
        p = new Pair(v, p);
        k = k.minus(LongInteger.ONE);
      }
      return p;
    }
  }
  public class List extends BuiltInProcedure {
    public List(Environment env) { super("list", env); }
    @Override public Value apply(Value arguments) {
      if (arguments.isNull()) return Nil.NIL;
      if (!arguments.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      Pair p = (Pair) arguments;
      return new Pair(p.getCar(), apply(p.getCdr()));
    }
  }
  public class Length extends BuiltInProcedure {
    public Length(Environment env) { super("length", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1);
      Value list = ((Pair) arguments).getCar();
      Integer result = LongInteger.ZERO;
      while (list.isPair()) {
        result = result.plus(LongInteger.ONE);
        list = ((Pair) list).getCdr();
      }
      if (!list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return result;
    }
  }
  public class Append extends BuiltInProcedure {
    public Append(Environment env) { super("append", env); }
    @Override public Value apply(Value arguments) {
      if (arguments.isNull()) return Nil.NIL;
      Value list = ((Pair) arguments).getCar();
      if (((Pair) arguments).getCdr().isNull()) return list;
      if (list.isNull()) return apply(((Pair) arguments).getCdr());
      return new Pair(((Pair)list).getCar(), apply(new Pair(((Pair)list).getCdr(), ((Pair)arguments).getCdr())));
      //(append (1 2) (3 4))
      //(1 . (append (2) (3 4))
      //(1 2 . (append () (3 4))
      //(1 2 . (append (3 4))
      //(1 2 3 4)
    }
  }
  public class Reverse extends BuiltInProcedure {
    private Value reverse(Value src, Value dst) {
      if (src.isNull()) return dst;
      if (!src.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      Pair p = (Pair)src;
      return reverse(p.getCdr(), new Pair(p.getCar(), dst));
    }
    public Reverse(Environment env) { super("reverse", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1);
      return reverse(((Pair)arguments).getCar(), Nil.NIL);
    }
  }
  protected Value listTail(Value list, Integer k) {
    if (k.isZero()) return list;
    if (list.isNull()) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to list-tail, list is too short")));
    }
    return listTail(((Pair)list).getCdr(), k.minus(LongInteger.ONE));
  }
  public class ListTail extends BuiltInProcedure {
    public ListTail(Environment env) { super("list-tail", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Integer.class);
      Value list = ((Pair) arguments).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      Integer k = (Integer) ((Pair)((Pair) arguments).getCdr()).getCar();
      if (k.isNegative()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected positive integer")));
      return listTail(list, k);
    }
  }
  public class ListRef extends BuiltInProcedure {
    public ListRef(Environment env) { super("list-ref", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Integer.class);
      Value list = ((Pair) arguments).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      Integer k = (Integer) ((Pair)((Pair) arguments).getCdr()).getCar();
      if (k.isNegative()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected positive integer")));
      Value v = listTail(list, k);
      if (!v.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " list is too short")));
      return ((Pair)v).getCar();
    }
  }
  public class ListSet extends BuiltInProcedure {
    public ListSet(Environment env) { super("list-set!", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Integer.class, Value.class);
      Value list = ((Pair) arguments).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      Integer k = (Integer) ((Pair)((Pair) arguments).getCdr()).getCar();
      Value v = ((Pair)((Pair)((Pair) arguments).getCdr()).getCdr()).getCar();
      if (k.isNegative()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected positive integer")));
      Value tail = listTail(list, k);
      if (!tail.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " list is too short")));
      ((Pair)tail).setCar(v);
      return Nil.NIL;
    }
  }
  interface Predicate<T> {
    boolean equal(T o1, T o2);
  }
  protected Value member(Value obj, Value list, Procedure compare) {
    if (list.isNull()) return Boolean.FALSE;
    if (!list.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed list in member")));
    Pair p = (Pair) list;
    if (compare.apply(new Pair(p.getCar(), new Pair(obj, Nil.NIL))) != Boolean.FALSE) {
      return list;
    }
    return member(obj, p.getCdr(), compare);
  }
  public class Memq extends BuiltInProcedure {
    public Memq(Environment env) { super("memq", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Value.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return member(obj, list, (Procedure) getEnvironment().lookup(new Identifier("eq?")));
    }
  }
  public class Memv extends BuiltInProcedure {
    public Memv(Environment env) { super("memv", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Value.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return member(obj, list, (Procedure) getEnvironment().lookup(new Identifier("eqv?")));
    }
  }
  public class Member extends BuiltInProcedure {
    public Member(Environment env) { super("member", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, 3, Value.class, Value.class, Procedure.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      Procedure compare = (Procedure) getEnvironment().lookup(new Identifier("equal?"));
      if (!((Pair)((Pair) arguments).getCdr()).getCdr().isNull()) {
        compare = (Procedure) ((Pair)((Pair)((Pair) arguments).getCdr()).getCdr()).getCar();
      }
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return member(obj, list, compare);
    }
  }
  protected Value assoc(Value obj, Value list, Procedure compare) {
    if (list.isNull()) return Boolean.FALSE;
    if (!list.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed list in assoc")));
    Pair p = (Pair) list;
    if (!p.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed alist in assoc")));
    p = (Pair) p.getCar();
    if (compare.apply(new Pair(p.getCar(), new Pair(obj, Nil.NIL))) != Boolean.FALSE) {
      return p;
    }
    return assoc(obj, ((Pair)list).getCdr(), compare);
  }
  public class Assq extends BuiltInProcedure {
    public Assq(Environment env) { super("assq", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Value.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return assoc(obj, list, (Procedure) getEnvironment().lookup(new Identifier("eq?")));
    }
  }
  public class Assv extends BuiltInProcedure {
    public Assv(Environment env) { super("assv", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, Value.class, Value.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return assoc(obj, list, (Procedure) getEnvironment().lookup(new Identifier("eqv?")));
    }
  }
  public class Assoc extends BuiltInProcedure {
    public Assoc(Environment env) { super("assoc", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 2, 3, Value.class, Value.class, Procedure.class);
      Value obj = ((Pair) arguments).getCar();
      Value list = ((Pair)((Pair) arguments).getCdr()).getCar();
      Procedure compare = (Procedure) getEnvironment().lookup(new Identifier("equal?"));
      if (!((Pair)((Pair) arguments).getCdr()).getCdr().isNull()) {
        compare = (Procedure) ((Pair)((Pair)((Pair) arguments).getCdr()).getCdr()).getCar();
      }
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return assoc(obj, list, compare);
    }
  }
  public class ListCopy extends BuiltInProcedure {
    private Value listCopy(Value list) {
      if (!list.isPair()) return list;
      Pair p = (Pair) list;
      return new Pair(p.getCar(), listCopy(p.getCdr()));
    }
    public ListCopy(Environment env) { super("list-copy", env); }
    @Override public Value apply(Value arguments) {
      checkArguments(this, arguments, 1);
      Value list = ((Pair) arguments).getCar();
      if (!list.isPair() && !list.isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument call to " + getName() + " expected list")));
      return listCopy(list);
    }
  }
}
    
