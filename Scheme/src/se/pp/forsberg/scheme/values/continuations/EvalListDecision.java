package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Value;

public class EvalListDecision implements Decision {

  private final Environment env;
  private final Value list; 
  
  public EvalListDecision(Value list, Environment env) {
    this.env = env;
    this.list = list;
  }
  public Environment getEnvironment() {
    return env;
  }
  public Value getList() {
    return list;
  }
  
}
