package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Environment;

public class QuasiQuoteDecision implements Decision {

  private final Environment env;
  private final int level;
  
  public QuasiQuoteDecision(Environment env, int level) {
    this.env = env;
    this.level = level;
  }

  public Environment getEnvironment() {
    return env;
  }

  public int getLevel() {
    return level;
  }


}
