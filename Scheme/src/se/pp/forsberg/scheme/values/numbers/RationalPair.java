package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public class RationalPair extends Rational{
  private Integer numerator, denominator;
  public RationalPair(Integer numerator, Integer denominator, boolean exact) {
    super(exact);
    if (denominator.isNegative()) {
      numerator = numerator.negate();
      denominator = denominator.negate();
    }
    if (!numerator.isZero()) {
      Integer g = numerator.gcd(denominator);
      if (!g.equal(LongInteger.ONE)) {
        numerator = (Integer) numerator.truncateDivide(g).getCar();
        denominator = (Integer) denominator.truncateDivide(g).getCar();
      }
    }
    this.numerator = numerator;
    this.denominator = denominator;
  }
  public RationalPair(Integer convertee) {
    super(convertee.isExact());
    numerator = convertee;
    denominator = LongInteger.ONE;
  }
  public Rational simplify() {
    if (numerator.isZero()) return LongInteger.ZERO;
    if (denominator.equal(LongInteger.ONE)) return numerator;
    return this;
  }
  @Override public Rational negate() { return new RationalPair(numerator.negate(), denominator, isExact()).simplify(); }
  @Override public Real invert() {
    if (denominator.isZero()) return new DoubleReal(Double.NaN);
    return new RationalPair(denominator, numerator, isExact()).simplify();
  }
  @Override public Rational plus(Rational other) { return new RationalPair(numerator.times(other.getDenominator()).plus(other.getNumerator().times(denominator)), denominator.times(other.getDenominator()), isExact(this, other)).simplify(); }
  @Override public Rational minus(Rational other) {return new RationalPair(numerator.times(other.getDenominator()).minus(other.getNumerator().times(denominator)), denominator.times(other.getDenominator()), isExact(this, other)).simplify(); }
  @Override public Rational times(Rational other) { return new RationalPair(numerator.times(other.getNumerator()), denominator.times(other.getDenominator()), isExact(this, other)).simplify(); }
  @Override public Rational divide(Rational other) { return times(new RationalPair(other.getDenominator(), other.getNumerator(), other.isExact())).simplify(); }
  @Override public Real expt(Rational other) {
    // TODO better
    return new DoubleReal(this).expt(other);
  }
  @Override public boolean greaterThan(Rational other) {
    return numerator.times(other.getDenominator()).greaterThan(other.getNumerator().times(denominator));
  }
  @Override public boolean lessThan(Rational other) {
    return numerator.times(other.getDenominator()).lessThan(other.getNumerator().times(denominator));
  }
  @Override public boolean greaterThanOrEqual(Rational other) {
    return numerator.times(other.getDenominator()).greaterThanOrEqual(other.getNumerator().times(denominator));
  }
  @Override public boolean lessThanOrEqual(Rational other) {
    return numerator.times(other.getDenominator()).lessThanOrEqual(other.getNumerator().times(denominator));
  }
  @Override public boolean equal(Rational other) {
    if (this.isExact() != other.isExact()) return false;
    return numerator.equal(other.getNumerator()) && denominator.equal(other.getDenominator());
  }
  
  @Override public Number plus(Number other) { return other.isReal()? plus((Real) other) : new Complex(this).plus(other); }
  @Override public Number minus(Number other) { return other.isReal()? minus((Real) other) : new Complex(this).minus(other); }
  @Override public Number times(Number other) { return other.isReal()? times((Real) other) : new Complex(this).times(other); }
  @Override public Number divide(Number other) { return other.isReal()? divide((Real) other) : new Complex(this).divide(other); }
  @Override public Number expt(Number other) { return other.isReal()? expt((Real) other) : new Complex(this).expt(other); }
  @Override public Real plus(Real other) { return other.isRational()? plus((Rational) other) : new DoubleReal(this).plus(other); }
  @Override public Real minus(Real other) { return other.isRational()? minus((Rational) other) : new DoubleReal(this).minus(other); }
  @Override public Real times(Real other) { return other.isRational()? times((Rational) other) : new DoubleReal(this).times(other); }
  @Override public Real divide(Real other) { return other.isRational()? divide((Rational) other) : new DoubleReal(this).divide(other); }
  @Override public Real expt(Real other) { return new DoubleReal(this).expt(other); }
  @Override public double asDouble() { return numerator.asDouble() / denominator.asDouble(); }
  @Override public boolean greaterThan(Real other) { return other.isRational()? greaterThan((Rational) other) : new DoubleReal(asDouble()).greaterThan(other); }
  @Override public boolean lessThan(Real other) { return other.isRational()? lessThan((Rational) other) : new DoubleReal(asDouble()).lessThan(other); }
  @Override public boolean greaterThanOrEqual(Real other) { return other.isRational()? greaterThanOrEqual((Rational) other) : new DoubleReal(asDouble()).greaterThanOrEqual(other); }
  @Override public boolean lessThanOrEqual(Real other) { return other.isRational()? lessThanOrEqual((Rational) other) : new DoubleReal(asDouble()).lessThanOrEqual(other); }
  @Override public boolean equal(Real other) { return other.isRational()? equal((Rational) other) : new DoubleReal(asDouble()).equal(other); }

  @Override public Integer getNumerator() { return numerator; }
  @Override public Integer getDenominator() { return denominator; }
  @Override public boolean isPositive() { return numerator.isPositive(); }
  @Override public boolean isZero() { return numerator.isZero(); }
  @Override public boolean isNegative() { return numerator.isNegative(); }
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
  protected java.lang.String toStringInternal(boolean exact, boolean sign) {
    java.lang.String result = "";
    if (exact && !isExact()) result += "#i";
    if (sign && numerator.isPositive()) result += "+";
    result += numerator.toString();
    result += "/";
    result += denominator.toString();
    return result;
  }
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    if (this == value) return true;
    if (!(value instanceof RationalPair)) return false;
    RationalPair other = (RationalPair) value;
    if (isExact() != other.isExact()) return false;
    return numerator.eqv(other.numerator) && denominator.eqv(other.denominator);
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  @Override public Real sqrt() { return new DoubleReal(this).sqrt(); }
  @Override public Real exp() { return new DoubleReal(this).exp(); }
  @Override public Real log() { return new DoubleReal(this).log(); }
  @Override public Real log(Real base) { return new DoubleReal(this).log(base); }
  @Override public Number log(Number base) { return new DoubleReal(this).log(base); }
  @Override public Real cos() { return new DoubleReal(this).cos(); }
  @Override public Real sin() { return new DoubleReal(this).sin(); }
  @Override public Real tan() { return new DoubleReal(this).tan(); }
  @Override public Real acos() { return new DoubleReal(this).acos(); }
  @Override public Real asin() { return new DoubleReal(this).asin(); }
  @Override public Real atan() { return new DoubleReal(this).atan(); }
  @Override public Real atan(Real r2) { return new DoubleReal(this).atan(r2); }

  @Override
  public Real abs() {
    if (numerator.greaterThanOrEqual(LongInteger.ZERO)) return this;
    return this.negate();
  }
  @Override
  public Integer floor() {
    return (Integer) numerator.floorDivide(denominator).getCar();
  }
  @Override
  public Integer ceiling() {
    Pair divMod = numerator.floorDivide(denominator);
    Integer div = (Integer) divMod.getCar();
    Integer mod = (Integer) ((Pair) divMod.getCdr()).getCar();
    if (!mod.isZero()) return div.plus(LongInteger.ONE);
    return div;
  }
  @Override
  public Integer truncate() {
    return (Integer) numerator.truncateDivide(denominator).getCar();
  }
  @Override
  public Integer round() {
    Pair divMod = numerator.truncateDivide(denominator);
    Integer div = (Integer) divMod.getCar();
    Integer mod = (Integer) ((Pair) divMod.getCdr()).getCar();
    if (!mod.isZero()) return div.plus(LongInteger.ONE);
    return div;
  }
  @Override
  public Number toExact() {
    return new RationalPair((Integer)numerator.toExact(), (Integer)denominator.toExact(), true);
  }
  @Override
  public Number toInexact() {
    return new RationalPair((Integer)numerator.toExact(), (Integer)denominator.toExact(), false);
  }
  @Override
  public int hashCode() {
    return denominator.hashCode() ^ numerator.hashCode();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof RationalPair)) return false;
    RationalPair r = (RationalPair) obj;
    return denominator.equals(r.denominator) && numerator.equals(r.numerator);
  }
}
