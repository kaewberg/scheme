package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.macros.Keyword;

public class KeywordDecision implements Decision {

  final private Keyword keyword;
  final private Pair pair;
  final private Environment env;

  public KeywordDecision(Keyword keyword, Pair pair, Environment env) {
    this.keyword = keyword;
    this.pair = pair;
    this.env = env;
  }
  
  public Keyword getKeyword() {
    return keyword;
  }

  public Pair getPair() {
    return pair;
  }

  public Environment getEnvironment() {
    return env;
  }


}
