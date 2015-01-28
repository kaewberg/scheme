package se.pp.forsberg.scheme.values.numbers;

public abstract class Real extends Number {
  public Real(boolean exact) {
    super(exact);
  }
  public abstract Real negate();
  public abstract Real invert();
  public abstract Real simplify();
  public abstract Real plus(Real other);
  public abstract Real minus(Real other);
  public abstract Real times(Real other);
  public abstract Real divide(Real other);
  public abstract Real expt(Real other);
  public abstract Real sqrt();
  public abstract Real exp();
  public abstract Real log();
  public abstract Real log(Real base);
  public abstract Real cos();
  public abstract Real sin();
  public abstract Real tan();
  public abstract Real acos();
  public abstract Real asin();
  public abstract Real atan();
  public abstract Real atan(Real r2);
  public abstract Integer floor();
  public abstract Integer ceiling();
  public abstract Integer truncate();
  public abstract Integer round();
  public abstract double asDouble();
  public abstract boolean greaterThan(Real other);
  public abstract boolean lessThan(Real other);
  public abstract boolean greaterThanOrEqual(Real other);
  public abstract boolean lessThanOrEqual(Real other);
  public abstract boolean equal(Real other);
  abstract public boolean isPositive();
  abstract public boolean isZero();
  public abstract boolean isNegative();
  public abstract boolean isInfinite() ;
  public abstract boolean isNaN() ;
  protected abstract java.lang.String toStringRecursive(boolean sign);
  
  @Override
  public boolean isReal() {
    return true;
  }
  @Override
  public Real angle() { return LongInteger.ZERO; }
  @Override
  public Real magnitude() { return this; }
  public Rational rationalize(Real precision) {
    if (isZero()) return LongInteger.ZERO;
   
    Integer n = LongInteger.ONE;  // numerator
    Integer d = LongInteger.ONE;  // denominator
    Rational fraction = new RationalPair(n, d, true);
    Real value = this;
    while (fraction.minus(value).abs().greaterThan(precision)) {
        if (fraction.lessThan(value)) {
          n = n.plus(LongInteger.ONE);
        } else {
          d = d.plus(LongInteger.ONE);
          n = (Integer) value.times(d).round().toExact();
        }
        fraction = new RationalPair(n, d, true);
    }
    return fraction;
  }
}
