package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Value;

public class Complex extends Number {
  private final Real r, i;
  public Complex(Real realPart, Real imaginaryPart, boolean exact) {
    super(exact);
    r = realPart;
    i = imaginaryPart;
  }
  static public Complex makePolar(Real angle, Real magnitude) {
    return new Complex(angle.cos().times(magnitude), angle.sin().times(magnitude), false);
  }
  public Complex(Real convertee) {
    super(convertee.isExact());
    r = convertee;
    i = LongInteger.ZERO;
  }
  @Override public Real getRealPart() { return r; }
  @Override public Real getImaginaryPart() { return i; }
  
  protected static Complex asComplex(Number n) { return (n instanceof Complex)? ((Complex) n) : new Complex(n.getRealPart(), new LongInteger(0, true), n.isExact()); }
  
  @Override public Number plus(Number n) { return plus(asComplex(n)).simplify(); }
  @Override public Number minus(Number n) { return minus(asComplex(n)).simplify(); }
  @Override public Number times(Number n) { return times(asComplex(n)).simplify(); }
  @Override public Number divide(Number n) { return divide(asComplex(n)).simplify(); }
  @Override public Number expt(Number n) { return pow(asComplex(n)).simplify(); }
  
  @Override public Number simplify() {
    if (i.isZero()) return r;
    return this;
  }
  
  public Complex plus(Complex c) { return new Complex(r.plus(c.r), i.plus(c.i), isExact(this, c)); }
  public Complex minus(Complex c) { return new Complex(r.minus(c.r), i.minus(c.i), isExact(this, c)); }
  public Complex times(Complex c) { return new Complex(r.times(c.r).minus(i.times(c.i)), r.times(c.i).plus(i.times(c.r)), isExact(this, c)); }
  // (a+bi)/(c+di)
  // (a+bi)(c-di)/(c+di)(c-di)
  // (ac+bd - adi +bci)/(c^2+d^2)
  public Complex divide(Complex c) {
    Real denominator = c.r.times(c.r).plus(c.i.times(c.i));
    return new Complex(r.times(c.r).plus(i.times(c.i)).divide(denominator),
                       i.times(c.r).minus(r.times(c.i)).divide(denominator),
                       isExact(this, c));
  }
  public Complex pow(Complex z) {
    Real a = r, b = i, c = z.r, d = z.i;
    // (a+bi)^(c+di) =
    // (a2+b2)^(c/2) * e^(-d*atan2(b,a)) * cos(c*atan2(b,a)+0.5*d*ln(a2+b2) )
    //   +
    // (a2+b2)^(c/2) * e^(-d*atan2(b,a)) * sin (c*atan2(b,a)+0.5*d*ln(a2+b2) ) i
    Real x = a.times(a).plus(b.times(b)).expt(c.divide(new LongInteger(2, true))).
        times(new DoubleReal(Math.E).expt(d.negate().times(b.atan(a))));
    Real y = c.times(b.atan(a).plus(new DoubleReal(0.5).times(d).times(a.times(a).plus(b.times(b)).log())));
    return new Complex(x.times(y.cos()), x.times(y.sin()), false);
  }
  @Override public Real abs() {
    return r.times(r).plus(i.times(i)).sqrt();
  }
  @Override public Number sqrt() {
    return makePolar(angle().divide(new LongInteger(2, true)), magnitude().sqrt());
  }
  @Override public boolean isZero() { return r.isZero() && i.isZero(); }
  @Override public Number negate() { return new Complex(r.invert(), i.invert(), isExact()).simplify(); }
  @Override public Number invert() { return new Complex(LongInteger.ONE, LongInteger.ZERO, true).divide(this); }
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    if (this == value) return true;
    if (!(value instanceof Complex)) return false;
    Complex other = (Complex) value;
    if (isExact() != other.isExact()) return false;
    return r.eqv(other.r) && i.eqv(other.i);
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  @Override
  public Number exp() {
    return new Complex(new DoubleReal(Math.E), LongInteger.ZERO, false).pow(this);
  }
  @Override
  public Number log() {
    throw new IllegalArgumentException("TODO complex logarithm");
  }
  @Override
  public Number log(Number base) {
    throw new IllegalArgumentException("TODO complex logarithm");
  }
  @Override
  public Number cos() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number sin() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number tan() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number acos() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number asin() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number atan() {
    throw new IllegalArgumentException("TODO complex trig functions");
  }
  @Override
  public Number toExact() {
    return new Complex((Real)r.toExact(), (Real)i.toExact(), true);
  }
  @Override
  public Number toInexact() {
    return new Complex((Real)r.toInexact(), (Real)i.toInexact(), false);
  }
  @Override
  public java.lang.String toString() {
    java.lang.String result = "";
    if (!isExact()) result += "#i";
    if (!r.equal(LongInteger.ZERO)) result += r.toStringRecursive(false);
    if (i.equal(LongInteger.ONE)) {
      result += "+";
    } else if (i.equal(LongInteger.MINUS_ONE)) {
      result += "-";
    } else {
      result += i.toStringRecursive(true);
    }
    result += 'i';
    return result;
  }
  @Override
  public int hashCode() {
    return i.hashCode() ^ r.hashCode();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Complex)) return false;
    Complex c = (Complex) obj;
    return r.equals(c.r) && i.equals(c.i);
  }
}
