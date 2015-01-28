package se.pp.forsberg.scheme.values.numbers;

import se.pp.forsberg.scheme.values.Value;

public abstract class Number extends Value {
  
  public static class Prefix {
    private final int radix;
    private final boolean exact;
    public Prefix(int radix, boolean exact) {
      this.radix = radix;
      this.exact = exact;
    }
    public int getRadix() { return radix; }
    public boolean isExact() { return exact; }
  }
  
  private boolean exact;
  protected Number(boolean exact) { this.exact = exact; }
  public boolean isExact() { return exact; }
  public abstract Number toExact();
  public abstract Number toInexact();
  abstract public Real getRealPart();
  abstract public Real getImaginaryPart();
  abstract public Number plus(Number n);
  abstract public Number minus(Number n);
  abstract public Number times(Number n);
  abstract public Number divide(Number n);
  abstract public Number expt(Number n);
  abstract public boolean isZero();
  abstract public Number simplify();
  abstract public Number negate();
  abstract public Number invert();
  abstract public Real abs();
  abstract public Number sqrt();
  public abstract Number exp();
  public abstract Number log();
  public abstract Number log(Number base);
  public abstract Number cos();
  public abstract Number sin();
  public abstract Number tan();
  public abstract Number acos();
  public abstract Number asin();
  public abstract Number atan();
  public Real angle() { return getImaginaryPart().divide(getRealPart()).atan(); }
  public Real magnitude() { return getRealPart().times(getRealPart()).plus(getImaginaryPart().times(getImaginaryPart())).sqrt(); }
  
  public static boolean isExact(Number n1, Number n2) { return n1.exact && n2.exact; }

  @Override
  public boolean isNumber() {
    return true;
  }
  @Override
  public boolean isComplex() {
    return true;
  }
  public static Number parse(java.lang.String string) {
    boolean exact = true;
    boolean hasExcactness = false, hasRadix = false;
    int radix = 10;
    for (int i = 0; i < 2; i++) {
      if (string.charAt(0) != '#') break;
      switch (java.lang.Character.toLowerCase(string.charAt(1))) {
      case 'e':
        if (hasExcactness) throw new IllegalArgumentException("Invalid number " + string);
        exact = true; 
        hasExcactness = true;
        break;
      case 'i':
        if (hasExcactness) throw new IllegalArgumentException("Invalid number " + string);
        exact = false; 
        hasExcactness = true;
        break;
      case 'b':
        if (hasRadix) throw new IllegalArgumentException("Invalid number " + string);
        radix = 2; 
        hasRadix = true;
        break;
      case 'o':
        if (hasRadix) throw new IllegalArgumentException("Invalid number " + string);
        radix = 8; 
        hasRadix = true;
        break;
      case 'd':
        if (hasRadix) throw new IllegalArgumentException("Invalid number " + string);
        radix = 10; 
        hasRadix = true;
        break;
      case 'x':
        if (hasRadix) throw new IllegalArgumentException("Invalid number " + string);
        radix = 16; 
        hasRadix = true;
        break;
        default:
          throw new IllegalArgumentException("Invalid number " + string);
      }
      string = string.substring(2);
    }
    
    int n = string.indexOf('@');
    if (n > 0) {
      Real angle = parseReal(string.substring(0, n), radix, exact);
      Real magnitude = parseReal(string.substring(n+1), radix, exact);
      return Complex.makePolar(angle, magnitude);
    }
    n = string.indexOf('+', 1);
    if (n < 0) n = string.indexOf('-', 1);
    if (n < 0) {
      if (java.lang.Character.toLowerCase(string.charAt(string.length()-1)) != 'i') {
        return parseReal(string, radix, exact);
      } else {
        return new Complex(LongInteger.ZERO, parseReal(string.substring(0, string.length()-1), radix, exact), exact).simplify();
      }
    }
    if (java.lang.Character.toLowerCase(string.charAt(string.length()-1)) != 'i') {
      throw new IllegalArgumentException("Invalid number " + string);
    }
    java.lang.String r = string.substring(0, n);
    java.lang.String i = string.substring(n, string.length()-1);
    //System.out.println("r: " + r + " i: " + i);
    if (i.equalsIgnoreCase("+")) return new Complex(parseReal(r, radix, exact), LongInteger.ONE, exact);
    if (i.equalsIgnoreCase("-")) return new Complex(parseReal(r, radix, exact), LongInteger.MINUS_ONE, exact);
    return new Complex(parseReal(r, radix, exact), parseReal(i, radix, exact), exact).simplify();
  }
  protected static Real parseReal(java.lang.String s, int radix, boolean exact) {
    if (s.equalsIgnoreCase("+inf.0")) return new DoubleReal(Double.POSITIVE_INFINITY);
    if (s.equalsIgnoreCase("-inf.0")) return new DoubleReal(Double.NEGATIVE_INFINITY);
    if (s.equalsIgnoreCase("+nan.0")) return new DoubleReal(Double.NaN);
    if (s.equalsIgnoreCase("-nan.0")) return new DoubleReal(Double.NaN);
    int n = s.indexOf('.');
    if (n < 0) n = s.indexOf('e');
    if (n < 0) n = s.indexOf('E');
    if (n >= 0) {
      if (radix != 10) throw new IllegalArgumentException("Invalid number " + s);
      return parseDecimal(s, exact);
    }
    n = s.indexOf('/');
    if (n > 0) {
      switch (s.charAt(n+1)) {
      case '+':
      case '-':
        throw new IllegalArgumentException("Invalid number " + s);
      }
      return new RationalPair(parseInteger(s.substring(0, n), radix, exact), parseInteger(s.substring(n+1), radix, exact), exact).simplify();
    }
    return parseInteger(s, radix, exact);
  }
  protected static Real parseDecimal(java.lang.String s, boolean exact) {
    if (s.charAt(0) == '+') s = s.substring(1);
    return new DoubleReal(Double.parseDouble(s)).simplify();
  }
  protected static Integer parseInteger(java.lang.String s, int radix, boolean exact) {
    if (s.charAt(0) == '+') s = s.substring(1);
    return new LongInteger(Long.parseLong(s, radix), exact).simplify();
  }
}
