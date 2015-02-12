package se.pp.forsberg.scheme.builtinprocedures;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.Number;
import se.pp.forsberg.scheme.values.numbers.Real;

public class Complex extends Library {

  public Complex() throws SchemeException {
    super();
  }
  public static Value getName() {
    return makeName("scheme", "complex");
  }
  class MakeRectangular extends BuiltInProcedure {
    public MakeRectangular(Environment env) { super("make-rectangular", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Real.class);
      Real x = (Real) ((Pair)arguments).getCar();
      Real y = (Real) ((Pair) ((Pair)arguments).getCdr()).getCar();
      return new se.pp.forsberg.scheme.values.numbers.Complex(x, y, Number.isExact(x, y)).simplify();
    }
  }
  class MakePolar extends BuiltInProcedure {
    public MakePolar(Environment env) { super("make-polar", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 2, Real.class);
      Real magnitude = (Real) ((Pair)arguments).getCar();
      Real angle = (Real) ((Pair) ((Pair)arguments).getCdr()).getCar();
      return se.pp.forsberg.scheme.values.numbers.Complex.makePolar(angle, magnitude);
    }
  }
  class RealPart extends BuiltInProcedure {
    public RealPart(Environment env) { super("real-part", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.angle();
    }
  }
  class ImagPart extends BuiltInProcedure {
    public ImagPart(Environment env) { super("imag-part", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.angle();
    }
  }
  class Angle extends BuiltInProcedure {
    public Angle(Environment env) { super("angle", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.angle();
    }
  }
  class Magnitude extends BuiltInProcedure {
    public Magnitude(Environment env) { super("magnitude", env); }
    @Override public Value apply(Value arguments) throws SchemeException {
      checkArguments(this, arguments, 1, Number.class);
      Number z = (Number) ((Pair)arguments).getCar();
      return z.magnitude();
    }
  }
}
