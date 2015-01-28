package se.pp.forsberg.scheme.values;



public class Eof extends Value {

  public final static Eof EOF = new Eof();
  
  private Eof() {
  }
  
  @Override
  public boolean isEof() {
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
  public int hashCode() {
    return 0;
  }
  @Override
  public java.lang.String toString() {
    return "[EOF]";
  }
}
