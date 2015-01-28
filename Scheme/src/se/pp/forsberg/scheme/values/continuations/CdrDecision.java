package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Value;

public class CdrDecision implements Decision {

  private final Value car;
  
  public CdrDecision(Value car) {
    this.car = car;
  }
  public Value getCar() {
    return car;
  }
  
}
