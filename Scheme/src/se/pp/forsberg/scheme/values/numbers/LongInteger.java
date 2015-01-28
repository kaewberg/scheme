package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public class LongInteger extends Integer {
  public static final LongInteger ZERO = new LongInteger(0, true);
  public static final LongInteger ONE = new LongInteger(1, true);
  public static final LongInteger MINUS_ONE = new LongInteger(-1, true);
  
  private long i;
  public LongInteger(long i, boolean exact) {
    super(exact);
    this.i = i;
  }
  
  // NB needs a lot of changes when BigNum is implemented...
  
  // private long getI(Integer n) { return ((LongInteger) n).i; }
  
  @Override public Integer negate() { return new LongInteger(-i, isExact()); }
  @Override public Real invert() {
    if (i == 0) return new DoubleReal(Double.NaN);
    if (i == 1) return this;
    return new RationalPair(LongInteger.ONE, this, isExact());
  }
  @Override public boolean isNegative() { return i < 0; }
  @Override public boolean isZero() { return i == 0; }
  @Override public boolean isPositive() { return i > 0; }
  @Override public byte asByte() { return (byte) i; }
  @Override public int asInt() { return (int) i; }
  @Override public long asLong() { return i; }
  @Override public double asDouble() { return i; }
  @Override public Integer simplify() { return this; }
  @Override public Integer getNumerator() { return this; }
  @Override public Integer getDenominator() { return ONE; }
  @Override public Real getRealPart() { return this; }
  @Override public Real getImaginaryPart() { return ZERO; }

  public boolean greaterThan(LongInteger n) { return i > n.i; }
  public boolean lessThan(LongInteger n) { return i < n.i; }
  public boolean greaterThanOrEqual(LongInteger n) { return i >= n.i; }
  public boolean lessThanOrEqual(LongInteger n) { return i <= n.i; }
  public boolean equal(LongInteger n) {
    if (i != n.i) return false;
    if (isExact() != n.isExact()) return false;
    return true;
  }
  public Integer plus(LongInteger n) { return new LongInteger(i + n.i, isExact(this, n)).simplify(); }
  public Integer minus(LongInteger n) { return new LongInteger(i - n.i, isExact(this, n)).simplify(); }
  public Integer times(LongInteger n) { return new LongInteger(i * n.i, isExact(this, n)).simplify(); }
  public Real divide(LongInteger n) {
    if (n.i == 0) return new DoubleReal(i > 0? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    return new RationalPair(this, n, isExact(this, n)).simplify();
  }
//  public Integer div(LongInteger n) { return new LongInteger(i / n.i, isExact(this, n)).simplify(); }
//  public Integer mod(LongInteger n) { return new LongInteger(i % n.i, isExact(this, n)).simplify(); }
  public Pair floorDivide(LongInteger n) {
    long quotient = i / n.i;
    long reminder = i % n.i;
    if (quotient < 0) {
      quotient--;
      reminder = -reminder;
    }
    return new Pair(new LongInteger(quotient, isExact(this, n)), new Pair(new LongInteger(reminder, isExact(this, n)), Nil.NIL));
  }
  public Pair truncateDivide(LongInteger n) {
    long quotient = i / n.i;
    long reminder = i % n.i;
    return new Pair(new LongInteger(quotient, isExact(this, n)), new Pair(new LongInteger(reminder, isExact(this, n)), Nil.NIL));
  }
  @Override public Integer gcd(Integer n) {
    if (n.isZero()) return this.abs();
    return n.gcd((Integer) ((Pair)floorDivide(n).getCdr()).getCar()).abs();
  }
  @Override public Integer lcm(Integer n) {
    if (isZero() && n.isZero()) return LongInteger.ZERO;
    return (Integer) times(n).abs().truncateDivide(gcd(n)).getCar();
  }
  
  @Override public Number plus(Number n) { return n.isReal()? plus((Real) n) : new Complex(this).plus(n); }
  @Override public Number minus(Number n) { return n.isReal()? minus((Real) n) :new Complex(this).minus(n); }
  @Override public Number times(Number n) { return n.isReal()? times((Real) n) : new Complex(this).times(n); }
  @Override public Number divide(Number n) { return n.isReal()? divide((Real) n) : new Complex(this).divide(n); }
  @Override public Number expt(Number n) { return n.isReal()? expt((Real) n) : new Complex(this).expt(n); }
  @Override public Real plus(Real n) { return n.isRational()? plus((Rational) n) : new DoubleReal(this).plus(n); }
  @Override public Real minus(Real n) { return n.isRational()? minus((Rational) n) : new DoubleReal(this).minus(n); }
  @Override public Real times(Real n) { return n.isRational()? times((Rational) n) : new DoubleReal(this).times(n); }
  @Override public Real divide(Real n) { return n.isRational()? divide((Rational) n) : new DoubleReal(this).divide(n); }
  @Override public Real expt(Real n) { return new DoubleReal(this.asDouble()).expt(n); }
  @Override public boolean greaterThan(Real n) { return n.isRational()? greaterThan((Rational) n) :  new DoubleReal(this).greaterThan(n); }
  @Override public boolean lessThan(Real n) { return n.isRational()? lessThan((Rational) n) : new DoubleReal(this).lessThan(n); }
  @Override public boolean greaterThanOrEqual(Real n) { return n.isRational()? greaterThanOrEqual((Rational) n) : new DoubleReal(this).greaterThanOrEqual(n); }
  @Override public boolean lessThanOrEqual(Real n) { return n.isRational()? lessThanOrEqual((Rational) n) : new DoubleReal(this).lessThanOrEqual(n); }
  @Override public boolean equal(Real n) { return n.isRational()? equal((Rational) n) : new DoubleReal(this).equal(n); }
  @Override public Rational plus(Rational n) { return n.isInteger()? plus((Integer) n) : new RationalPair(this).plus(n); }
  @Override public Rational minus(Rational n) { return n.isInteger()? minus((Integer) n) : new RationalPair(this).minus(n); }
  @Override public Rational times(Rational n) { return n.isInteger()? times((Integer) n) : new RationalPair(this).times(n); }
  @Override public Real divide(Rational n) { return n.isInteger()? divide((Integer) n) : new RationalPair(this).divide(n); }
  @Override public Real expt(Rational n) { return new DoubleReal(this).expt(n); }
  @Override public boolean greaterThan(Rational n) { return n.isInteger()? greaterThan((Integer) n) : new RationalPair(this).greaterThan(n); }
  @Override public boolean lessThan(Rational n) { return n.isInteger()? lessThan((Integer) n) : new RationalPair(this).lessThan(n); }
  @Override public boolean greaterThanOrEqual(Rational n) { return n.isInteger()? greaterThanOrEqual((Integer) n) : new RationalPair(this).greaterThanOrEqual(n); }
  @Override public boolean lessThanOrEqual(Rational n) { return n.isInteger()? lessThanOrEqual((Integer) n) : new RationalPair(this).lessThanOrEqual(n); }
  @Override public boolean equal(Rational n) { return n.isInteger()? equal((Integer) n) : new RationalPair(this).equal(n); }
  
  // Bignum handover here
  @Override public boolean greaterThan(Integer n) { return greaterThan((LongInteger) n); }
  @Override public boolean lessThan(Integer n) { return lessThan((LongInteger) n); }
  @Override public boolean greaterThanOrEqual(Integer n) { return greaterThanOrEqual((LongInteger) n); }
  @Override public boolean lessThanOrEqual(Integer n) { return lessThanOrEqual((LongInteger) n); }
  @Override public boolean equal(Integer n) { return equal((LongInteger) n); }
  @Override public Integer plus(Integer n) { return plus((LongInteger) n); }
  @Override public Integer minus(Integer n) { return minus((LongInteger) n); }
  @Override public Integer times(Integer n) { return times((LongInteger) n); }
  @Override public Real divide(Integer n) { return divide((LongInteger) n); }
//  @Override public Integer div(Integer n) { return div((LongInteger) n); }
//  @Override public Integer mod(Integer n) { return mod((LongInteger) n); }
  @Override public Pair floorDivide(Integer n) { return floorDivide((LongInteger) n); }
  @Override public Pair truncateDivide(Integer n) { return truncateDivide((LongInteger) n); }

  @Override
  protected java.lang.String toStringRecursive(boolean sign) {
    return toStringInternal(false, sign);
  }
  protected java.lang.String toStringInternal(boolean exactness, boolean sign) {
    java.lang.String result = "";
    //if (exactness && !isExact()) result += "#i";
    if (sign && i > 0) result += "+";
    result += Long.toString(i);
    if (!isExact()) result += ".0";
    return result;
  }
  @Override
  public java.lang.String toString() {
    return toStringInternal(true, false);
  }
  
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }

  @Override
  public boolean eqv(Value value) {
    if (!(value instanceof LongInteger)) return false;
    LongInteger n = (LongInteger) value;
    if (isExact() != n.isExact()) return false;
    return i == n.i;
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
  
  @Override public Pair exactSqrt() {
    if (isNegative()) throw new IllegalArgumentException("Cannot calculate exact sqrt of negative number");
    Integer root = (Integer) sqrt().floor().toExact();
    return new Pair(root, new Pair(this.minus(root.times(root)), Nil.NIL));
  }

  @Override
  public Number toExact() {
    return new LongInteger(i, true);
  }

  @Override
  public Number toInexact() {
    return new LongInteger(i, false);
  }

  @Override
  public int hashCode() {
    return (int) ((i & 0xffffffff) ^ ((i >> 32) & 0xffffffff));
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof LongInteger)) return false;
    LongInteger other = (LongInteger) obj;
    return i == other.i;
  }
}
