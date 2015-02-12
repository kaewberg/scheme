package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.Number;
import se.pp.forsberg.scheme.values.numbers.Rational;
import se.pp.forsberg.scheme.values.numbers.Real;

public class Numbers extends Library {

  public Numbers() throws SchemeException {
    super();
  }
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("numbers"), Nil.NIL));
  }
  class IsNumber extends BuiltInProcedure {
    public IsNumber(Environment env) { super("number?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      return ((Pair)arguments).getCar().isNumber()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsComplex extends BuiltInProcedure {
    public IsComplex(Environment env) { super("complex?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      return ((Pair)arguments).getCar().isComplex()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsReal extends BuiltInProcedure {
    public IsReal(Environment env) { super("real?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      return ((Pair)arguments).getCar().isReal()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsRational extends BuiltInProcedure {
    public IsRational(Environment env) { super("rational?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      return ((Pair)arguments).getCar().isRational()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsInteger extends BuiltInProcedure {
    public IsInteger(Environment env) { super("integer?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1);
      return ((Pair)arguments).getCar().isInteger()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsExact extends BuiltInProcedure {
    public IsExact(Environment env) { super("exact?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).isExact()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsInexact extends BuiltInProcedure {
    public IsInexact(Environment env) { super("inexact?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).isExact()? Boolean.FALSE : Boolean.TRUE;
    }
  }
  class IsExactInteger extends BuiltInProcedure {
    public IsExactInteger(Environment env) { super("exact-integer?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number number = (Number) ((Pair)arguments).getCar();
      return number.isExact() && number.isInteger()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class Equals extends BuiltInProcedure {
    public Equals(Environment env) { super("=", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, java.lang.Integer.MAX_VALUE, Number.class);
      Number last = (Number) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Number number = (Number) ((Pair)arguments).getCar(); 
        if (!last.eqv(number)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
        last = number;
      }
      return Boolean.TRUE;
    }
  }
  class LessThan extends BuiltInProcedure {
    public LessThan(Environment env) { super("<", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, java.lang.Integer.MAX_VALUE, Real.class);
      Real last = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real number = (Real) ((Pair)arguments).getCar(); 
        if (!last.lessThan(number)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
        last = number;
      }
      return Boolean.TRUE;
    }
  }
  class GreaterThan extends BuiltInProcedure {
    public GreaterThan(Environment env) { super(">", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, java.lang.Integer.MAX_VALUE, Real.class);
      Real last = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real number = (Real) ((Pair)arguments).getCar(); 
        if (!last.greaterThan(number)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
        last = number;
      }
      return Boolean.TRUE;
    }
  }
  class LessThanOrEqual extends BuiltInProcedure {
    public LessThanOrEqual(Environment env) { super("<=", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, java.lang.Integer.MAX_VALUE, Real.class);
      Real last = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real number = (Real) ((Pair)arguments).getCar(); 
        if (!last.lessThanOrEqual(number)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
        last = number;
      }
      return Boolean.TRUE;
    }
  }
  class GreaterThanOrEqual extends BuiltInProcedure {
    public GreaterThanOrEqual(Environment env) { super(">=", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, java.lang.Integer.MAX_VALUE, Real.class);
      Real last = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real number = (Real) ((Pair)arguments).getCar(); 
        if (!last.greaterThanOrEqual(number)) return Boolean.FALSE;
        arguments = ((Pair)arguments).getCdr();
        last = number;
      }
      return Boolean.TRUE;
    }
  }
  class IsZero extends BuiltInProcedure {
    public IsZero(Environment env) { super("zero?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number) ((Pair)arguments).getCar()).isZero()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsPositive extends BuiltInProcedure {
    public IsPositive(Environment env) { super("positive?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Real.class);
      return ((Real) ((Pair)arguments).getCar()).isPositive()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsNegative extends BuiltInProcedure {
    public IsNegative(Environment env) { super("negative?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Real.class);
      return ((Real) ((Pair)arguments).getCar()).isNegative()? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class IsOdd extends BuiltInProcedure {
    public IsOdd(Environment env) { super("odd?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Integer.class);
      Integer i = (Integer) ((Pair)arguments).getCar();
      return ((Pair) i.floorDivide(new LongInteger(2, i.isExact())).getCdr()).getCar().eqv(new LongInteger(0, i.isExact()))? Boolean.FALSE : Boolean.TRUE;
    }
  }
  class IsEven extends BuiltInProcedure {
    public IsEven(Environment env) { super("even?", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Integer.class);
      Integer i = (Integer) ((Pair)arguments).getCar();
      return ((Pair) i.floorDivide(new LongInteger(2, i.isExact())).getCdr()).getCar().eqv(new LongInteger(0, i.isExact()))? Boolean.TRUE : Boolean.FALSE;
    }
  }
  class Max extends BuiltInProcedure {
    public Max(Environment env) { super("max", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Real.class);
      Real result = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real r = (Real) ((Pair)arguments).getCar();
        if (r.greaterThan(result)) result = r;
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Min extends BuiltInProcedure {
    public Min(Environment env) { super("min", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Real.class);
      Real result = (Real) ((Pair)arguments).getCar();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        Real r = (Real) ((Pair)arguments).getCar();
        if (r.lessThan(result)) result = r;
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }

  class Plus extends BuiltInProcedure {
    public Plus(Environment env) { super("+", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Number.class);
      
      Number result = LongInteger.ZERO;
      while (!arguments.isNull()) {
        result = result.plus((Number) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Minus extends BuiltInProcedure {
    public Minus(Environment env) { super("-", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Number.class);
      Number result = (Number) ((Pair)arguments).getCar();
      if (((Pair)arguments).getCdr().isNull()) return result.negate();
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        result = result.minus((Number) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Times extends BuiltInProcedure {
    public Times(Environment env) { super("*", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Number.class);
      Number result = LongInteger.ONE;
      while (!arguments.isNull()) {
        result = result.times((Number) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Divide extends BuiltInProcedure {
    public Divide(Environment env) { super("/", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, java.lang.Integer.MAX_VALUE, Number.class);
      Number result = (Number) ((Pair)arguments).getCar();
      if (((Pair)arguments).getCdr().isNull()) return result.invert();
      arguments = (Pair) ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        result = result.divide((Number) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Abs extends BuiltInProcedure {
    public Abs(Environment env) { super("abs", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      return ((Number)((Pair)arguments).getCar()).abs();
    }
  }
  class FloorDivide extends BuiltInProcedure {
    public FloorDivide(Environment env) { super("floor/", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).floorDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar());
    }
  }
  class FloorQuotient extends BuiltInProcedure {
    public FloorQuotient(Environment env) { super("floor-quotient", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).floorDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCar();
    }
  }
  class FloorReminder extends BuiltInProcedure {
    public FloorReminder(Environment env) { super("floor-remainder", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).floorDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCdr();
    }
  }
  class TruncateDivide extends BuiltInProcedure {
    public TruncateDivide(Environment env) { super("truncate/", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).truncateDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar());
    }
  }
  class TruncateQuotient extends BuiltInProcedure {
    public TruncateQuotient(Environment env) { super("truncate-quotient", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).truncateDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCar();
    }
  }
  class TruncateReminder extends BuiltInProcedure {
    public TruncateReminder(Environment env) { super("truncate-remainder", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).truncateDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCdr();
    }
  }
  class Quotient extends BuiltInProcedure {
    public Quotient(Environment env) { super("quotient", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).truncateDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCar();
    }
  }
  class Reminder extends BuiltInProcedure {
    public Reminder(Environment env) { super("remainder", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).truncateDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCdr();
    }
  }
  class Modulo extends BuiltInProcedure {
    public Modulo(Environment env) { super("modulo", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).floorDivide((Integer)((Pair)((Pair)arguments).getCdr()).getCar()).getCdr();
    }
  }
  class Gcd extends BuiltInProcedure {
    public Gcd(Environment env) { super("gcd", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Integer.class);
      if (arguments.isNull()) { return LongInteger.ZERO; }
      Integer result = (Integer) ((Pair)arguments).getCar();
      if (((Pair)arguments).getCdr().isNull()) return result;
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        result = result.gcd((Integer) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Lcm extends BuiltInProcedure {
    public Lcm(Environment env) { super("lcm", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 0, java.lang.Integer.MAX_VALUE, Integer.class);
      if (arguments.isNull()) { return LongInteger.ONE; }
      Integer result = (Integer) ((Pair)arguments).getCar();
      if (((Pair)arguments).getCdr().isNull()) return result;
      arguments = ((Pair)arguments).getCdr();
      while (!arguments.isNull()) {
        result = result.lcm((Integer) ((Pair)arguments).getCar()); 
        arguments = ((Pair)arguments).getCdr();
      }
      return result;
    }
  }
  class Numerator extends BuiltInProcedure {
    public Numerator(Environment env) { super("numerator", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Rational.class);
      return ((Rational) ((Pair)arguments).getCar()).getNumerator();
    }
  }
  class Denominator extends BuiltInProcedure {
    public Denominator(Environment env) { super("denominator", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Rational.class);
      return ((Rational) ((Pair)arguments).getCar()).getDenominator();
    }
  }
  class Floor extends BuiltInProcedure {
    public Floor(Environment env) { super("floor", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Real.class);
      return ((Real) ((Pair)arguments).getCar()).floor();
    }
  }
  class Ceiling extends BuiltInProcedure {
    public Ceiling(Environment env) { super("ceiling", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      if (arguments.isNull() || !((Pair)arguments).getCdr().isNull() || !((Pair)arguments).getCar().isReal()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal arguments to " + getName() + ", expected single real")));
      return ((Real) ((Pair)arguments).getCar()).ceiling();
    }
  }
  class Truncate extends BuiltInProcedure {
    public Truncate(Environment env) { super("truncate", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Real.class);
      return ((Real) ((Pair)arguments).getCar()).truncate();
    }
  }
  class Round extends BuiltInProcedure {
    public Round(Environment env) { super("round", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Real.class);
      return ((Real) ((Pair)arguments).getCar()).round();
    }
  }
  class Rationalize extends BuiltInProcedure {
    public Rationalize(Environment env) { super("rationalize", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Real.class);
      return ((Real) ((Pair)arguments).getCar()).rationalize((Real) ((Pair)((Pair)arguments).getCdr()).getCar());
    }
  }
  class Square extends BuiltInProcedure {
    public Square(Environment env) { super("square", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Number.class);
      Number z = (Number)((Pair)arguments).getCar();
      return z.times(z);
    }
  }
  class ExactIntegerSqrt extends BuiltInProcedure {
    public ExactIntegerSqrt(Environment env) { super("exact-integer-sqrt", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, Integer.class);
      return ((Integer)((Pair)arguments).getCar()).exactSqrt();
    }
  }
  class Expt extends BuiltInProcedure {
    public Expt(Environment env) { super("expt", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Number.class);
      Number z1 = (Number) ((Pair)arguments).getCar();
      Number z2 = (Number) ((Pair) ((Pair)arguments).getCdr()).getCar();
      return z1.expt(z2);
    }
  }
  class Exact extends BuiltInProcedure {
    public Exact(Environment env) { super("exact", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.toExact();
    }
  }
  class Inexact extends BuiltInProcedure {
    public Inexact(Environment env) { super("inexact", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.toInexact();
    }
  }
  class NumberToString extends BuiltInProcedure {
    public NumberToString(Environment env) { super("number->string", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return new String(z.toString());
    }
  }
  class StringToNumber extends BuiltInProcedure {
    public StringToNumber(Environment env) { super("string->number", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, String.class);
      String s = (String) ((Pair)arguments).getCar();
      return Number.parse(s.getString());
    }
  }
  
}
