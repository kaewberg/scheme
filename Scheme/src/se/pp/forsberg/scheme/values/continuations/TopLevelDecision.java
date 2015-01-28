package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Environment;

public class TopLevelDecision implements Decision {

  private Environment env;
  
  public TopLevelDecision(Environment env) {
    this.env = env;
  }
  public Environment getEnvironment() { return env; }

}
