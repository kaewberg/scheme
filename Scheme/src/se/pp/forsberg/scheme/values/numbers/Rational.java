package se.pp.forsberg.scheme.values.numbers;

public abstract class Rational extends Real {

  public Rational(boolean exact) {
   super(exact);
  }
  public abstract Integer getNumerator();
  public abstract Integer getDenominator();
  public abstract Rational negate();
  public abstract Real invert();
  public abstract Rational simplify();
  public abstract Rational plus(Rational other);
  public abstract Rational minus(Rational other);
  public abstract Rational times(Rational other);
  public abstract Real divide(Rational other);
  public abstract Real expt(Rational other);
  public abstract boolean greaterThan(Rational other);
  public abstract boolean lessThan(Rational other);
  public abstract boolean greaterThanOrEqual(Rational other);
  public abstract boolean lessThanOrEqual(Rational other);
  public abstract boolean equal(Rational other);
  
  @Override
  public boolean isRational() {
    return true;
  }
  @Override
  public boolean isNaN() {
    return false;
  }
  @Override
  public boolean isInfinite() {
    return false;
  }
}
