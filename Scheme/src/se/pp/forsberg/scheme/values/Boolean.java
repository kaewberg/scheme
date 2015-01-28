package se.pp.forsberg.scheme.values;



public class Boolean extends Value {
  private boolean b;
  public final static Boolean TRUE = new Boolean(true);
  public final static Boolean FALSE = new Boolean(false);
  private Boolean(boolean b) {
    this.b = b;
  }
  public boolean getBoolean() {
    return b;
  }
  @Override
  public boolean isBoolean() {
    return true;
  }
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  @Override
  public boolean asBoolean() {
    return b;
  }
  public static Value parse(java.lang.String string) {
    if (string.equalsIgnoreCase("#t") || string.equalsIgnoreCase("#true")) return TRUE;
    if (string.equalsIgnoreCase("#f") || string.equalsIgnoreCase("#false")) return FALSE;
    throw new IllegalArgumentException("Not a valid boolean " + string);
  }
  @Override
  public int hashCode() {
    return b? 1 : 0;
  }
  @Override
  public java.lang.String toString() {
    return b? "#t" : "#f";
  }

}
