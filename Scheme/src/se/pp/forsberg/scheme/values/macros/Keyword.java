package se.pp.forsberg.scheme.values.macros;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;

public abstract class Keyword extends Value {

  private String keyword;
  protected Keyword(String keyword) { this.keyword = keyword; }
  
  public String getKeyword() { return keyword; }
  
  public abstract Value apply(Pair expression, Environment env) throws SchemeException;
  public abstract Op apply(Op parent, Pair expression, Environment env);
  //public abstract Value replayApply(Pair pair, Environment environment, Continuation replay, Continuation continuation);
  
  @Override public boolean equal(Value value) { return eqv(value); }
  @Override public boolean eq(Value value) { return eqv(value); }
  @Override public boolean eqv(Value value) { return this == value; }
  
  @Override
  public String toString() {
    return "[Keyword " + keyword + "]";
  }

  @Override
  public int hashCode() {
    return keyword.hashCode();
  }

}
