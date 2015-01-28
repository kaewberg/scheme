package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Value;

public class DoubleReal extends Real {
  final double r;
  public DoubleReal(Real real) { this(real.asDouble()); }
  public DoubleReal(double real) { super(false); r = real; }
  private DoubleReal(double real, boolean exact) { super(exact); r = real; }
  public DoubleReal(se.pp.forsberg.scheme.values.numbers.Integer prefix, java.lang.String suffix, 
      se.pp.forsberg.scheme.values.numbers.Integer exponent) {
    super(false);
    // TODO Better
    if (suffix.isEmpty()) {
      r = prefix.asDouble() * Math.pow(10, exponent.asLong());
    } else {
      Long suffixLong = Long.parseLong(suffix);
      double suffixScale = Math.pow(10, -suffix.length());
      r = (prefix.asDouble() + suffixLong*suffixScale) * Math.pow(10, exponent.asLong());
    }
  }
  public DoubleReal(Rational convertee) {
    super(false);
    r = convertee.asDouble();
  }
  public double getReal() { return r; }
  
  @Override public boolean isExact() { return false; }
  @Override public boolean isNegative() { return r < 0; }
  @Override public boolean isPositive() { return r > 0; }
  @Override public boolean isZero() { return r == 0; }
  @Override public boolean isNaN() { return Double.isNaN(r); }
  @Override public boolean isInfinite() { return Double.isInfinite(r); }
  @Override public double asDouble() { return r; }
  @Override public boolean greaterThan(Real other) { return r > other.asDouble(); }
  @Override public boolean lessThan(Real other) { return r < other.asDouble(); }
  @Override public boolean greaterThanOrEqual(Real other) { return r >= other.asDouble(); }
  @Override public boolean lessThanOrEqual(Real other) { return r <= other.asDouble(); }
  @Override public boolean equal(Real other) {
    if (other.isExact()) return false;
    return r == other.asDouble();
  }
  @Override public Real negate() { return new DoubleReal(-r).simplify(); }
  @Override public Real invert() { return new DoubleReal(1/r).simplify(); }
  @Override public Real plus(Real other) { return new DoubleReal(r + other.asDouble()).simplify(); }
  @Override public Real minus(Real other) { return new DoubleReal(r - other.asDouble()).simplify(); }
  @Override public Real times(Real other) { return new DoubleReal(r * other.asDouble()).simplify(); }
  @Override public Real divide(Real other) { return new DoubleReal(r / other.asDouble()).simplify(); }
  @Override public Real expt(Real other) { return new DoubleReal(Math.pow(r, other.asDouble())).simplify(); }

  @Override public Real simplify() {
    if (Double.isInfinite(r) || Double.isNaN(r)) return this;
    Long i = Math.round(r);
    if (i == r) return new LongInteger(i, false);
    return this;
  }
  
  @Override public Number plus(Number n) { return n.isReal()? plus((Real) n) : new Complex(this).plus(n); }
  @Override public Number minus(Number n) { return n.isReal()? minus((Real) n) : new Complex(this).minus(n); }
  @Override public Number times(Number n) { return n.isReal()? times((Real) n) : new Complex(this).times(n); }
  @Override public Number divide(Number n) { return n.isReal()? plus((Real) n) : new Complex(this).divide(n); }
  @Override public Number expt(Number n) { return n.isReal()? expt((Real) n) : new Complex(this).expt(n); }
  @Override public Number log(Number base) { return base.isReal()? log((Real) base) : new Complex(this).log(base); }
  
  @Override public Real getRealPart() { return this; }
  @Override public Real getImaginaryPart() { return LongInteger.ZERO; }
  @Override
  protected java.lang.String toStringRecursive(boolean sign) {
    return toStringInternal(false, sign);
  }
  @Override
  public java.lang.String toString() {
    return toStringInternal(true, false);
  }
  protected java.lang.String toStringInternal(boolean exactness, boolean sign) {
    if (r == Double.POSITIVE_INFINITY) return "+inf.0";
    if (r == Double.NEGATIVE_INFINITY) return "-inf.0";
    if (r == Double.NaN) return "+nan.0";
    java.lang.String result = "";
    if (exactness) result += "#i";
    if (r > 0 && sign) result += "+";
    result += Double.toString(r);
    return result;
  }
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    if (!(value instanceof DoubleReal)) return false;
    DoubleReal other = (DoubleReal) value;
    return r == other.r;
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  @Override public Real sqrt() { return new DoubleReal(Math.sqrt(r)).simplify(); }
  @Override public Real exp() { return new DoubleReal(Math.exp(r)).simplify(); }
  @Override public Real log() { return new DoubleReal(Math.log(r)).simplify(); }
  @Override public Real log(Real base) { return new DoubleReal(Math.log(r) / Math.log(base.asDouble())).simplify(); }
  @Override public Real cos() { return new DoubleReal(Math.cos(r)).simplify(); }
  @Override public Real sin() { return new DoubleReal(Math.sin(r)).simplify(); }
  @Override public Real tan() { return new DoubleReal(Math.tan(r)).simplify(); }
  @Override public Real acos() { return new DoubleReal(Math.acos(r)).simplify(); }
  @Override public Real asin() { return new DoubleReal(Math.asin(r)).simplify(); }
  @Override public Real atan() { return new DoubleReal(Math.atan(r)).simplify(); }
  @Override public Real atan(Real r2) { return new DoubleReal(Math.atan2(r, r2.asDouble())).simplify(); }
  @Override public Real abs() {
    if (isPositive()) return this;
    return this.negate();
  }
  @Override public Integer floor() { return new LongInteger((long)Math.floor(r), false); }
  @Override public Integer ceiling() { return new LongInteger((long)Math.ceil(r), false); }
  @Override public Integer truncate() { return new LongInteger((long) r, false); }
  @Override public Integer round() { return new LongInteger((long)Math.round(r), false); }

  @Override
  public Number toExact() {
    return new DoubleReal(r, true);
  }
  @Override
  public Number toInexact() {
    return new DoubleReal(r);
  }
  @Override
  public int hashCode() {
    long l = Double.doubleToLongBits(r);
    return (int) ((l & 0xffffffff) ^ ((l >> 32) & 0xffffffff));
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof DoubleReal)) return false;
    DoubleReal other = (DoubleReal) obj;
    return r == other.r;
  }
}
