package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Pair;

public abstract class Integer extends Rational {
  public Integer(boolean exact) {
    super(exact);
  }
  abstract public byte asByte();
  abstract public int asInt();
  abstract public long asLong();
  abstract public Integer negate();
  public abstract Integer simplify();
  abstract public Integer plus(Integer other);
  abstract public Integer minus(Integer other);
  abstract public Integer times(Integer other);
  abstract public Real divide(Integer other);
//  abstract public Integer div(Integer other);
//  abstract public Integer mod(Integer other);
  public abstract boolean greaterThan(Integer other);
  public abstract boolean lessThan(Integer other);
  public abstract boolean greaterThanOrEqual(Integer other);
  public abstract boolean lessThanOrEqual(Integer other);
  public abstract boolean equal(Integer other);
  public abstract boolean isZero();
  
  @Override
  public boolean isInteger() {
    return true;
  }
  public Integer abs() {
    if (isPositive()) return this;
    return this.negate();
  }
  public abstract Pair floorDivide(Integer n);
  public abstract Pair truncateDivide(Integer n);
  public abstract Integer gcd(Integer n);
  public abstract Integer lcm(Integer n);
  @Override public Integer floor() { return this; }
  @Override public Integer ceiling() { return this; }
  @Override public Integer truncate() { return this; }
  @Override public Integer round() { return this; }
  public abstract Pair exactSqrt();
}
