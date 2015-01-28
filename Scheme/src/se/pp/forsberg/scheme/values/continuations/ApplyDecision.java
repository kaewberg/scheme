package se.pp.forsberg.scheme.values.continuations;

import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.Value;

public class ApplyDecision implements Decision {

  private final Procedure operator;
  private final Value operands;
  
  public ApplyDecision(Procedure operator, Value operands) {
    this.operator = operator;
    this.operands = operands;
  }

  public Procedure getOperator() {
    return operator;
  }

  public Value getOperands() {
    return operands;
  }
}
