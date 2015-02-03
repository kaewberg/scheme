package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.Error;
import se.pp.forsberg.scheme.values.macros.Keyword;

public abstract class Op {
  protected final Evaluator evaluator;
  protected final Op parent;
  protected final Environment env;

  public Op(Evaluator evaluator, Op parent, Environment env) {
    this.evaluator = evaluator;
    this.parent = parent;
    this.env = env;
  }
  public Op(Op parent, Environment env) {
    this(parent.evaluator, parent, env);
  }
  public Op(Op parent) {
    this(parent.evaluator, parent, parent.env);
  }

  public Op(Evaluator evaluator, Op parent) {
    this(evaluator, parent, parent.env);
  }
  //public abstract Op apply(Value v[]);
  public abstract Op apply(Value v);
  @Override
  public java.lang.String toString() {
    java.lang.String description = getDescription();
    if (parent != null) {
      description = parent.toString() + "\n" + description;
    }
    return description;
  }
  abstract protected java.lang.String getDescription();

  // Continuations
  // This file contains the basic ops needed for list evaluation
  // See keyword.apply and procedure.apply for more

  static class Done extends Op {

    public Done(Evaluator evaluator) {
      super(evaluator, null, null);
    }

    public Done(Evaluator evaluator, Environment env) {
      super(evaluator, null, env);
    }

    @Override
    public Op apply(Value v) {
      setValue(v);
      return this;
    }

    @Override
    protected java.lang.String getDescription() {
      return "Done";
    }
  }

  /*
   * Eval x
   * 
   * Eval id = parent
   *           value = lookup id
   * Eval not-pair = parent
   *                 value = not-pair
   * Eval (keyword . x) = this
   *                      ApplyKeyword
   *                      value = x
   * Eval (quote y) = parent
   *                  value = y
   * Eval (x . y) = parent
   *                Apply (car value) (cd value)
   *                Cons value (ListEval y)
   *                Eval
   *                value = x
   */
  public static class Eval extends Op {

    Eval(Evaluator evaluator, Op parent, Environment env) {
      super(evaluator, parent, env);
    }

    public Eval(Op parent, Environment env) {
      super(parent, env);
    }

    public Eval(Op parent) {
      super(parent);
    }

    @Override
    public Op apply(Value v) {
      //if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
      //Value v = vs[0];
      if (v.isIdentifier()) {
        Value result = env.lookup((Identifier) v);
        if (result == null) {
          return evaluator.error("Undefined identifier", v);
        }
        evaluator.setValue(result);
        return parent;
      }
      if (!v.isPair()) {
        evaluator.setValue(v);
        return parent;
      }
      Pair p = (Pair) v;
      v = p.getCar();
      if (v.isIdentifier()) {
        Identifier id = (Identifier) v;
        if (id.getIdentifier().equals("quote")) {
          Value cdr = p.getCdr();
          if (!cdr.isPair()) return error("Malformed quote", cdr);
          setValue(((Pair)cdr).getCar());
          return parent;
        }
        v = env.lookup(id);
        if (v == null) {
          return evaluator.error("Undefined identifier", id);
        }
        if (v instanceof Keyword) {
          setValue(p);
          return new ApplyKeyword(this, env, (Keyword) v);
        }
      }
      
      Op result = new Apply(evaluator, parent, env);
      result = new Cons(evaluator, result, env, p.getCdr());
      result = new Eval(result, env);
      evaluator.setValue(p.getCar());
      return result;
    }

    @Override
    protected
    java.lang.String getDescription() {
      return "Eval";
    }
  }

  /*
   * Cons value cdr = parent
   *                  Cons2 x
   *                  ListEval x
   *                  value = cdr
   */
  static class Cons extends Op {
    private Value cdr;

    Cons(Evaluator evaluator, Op parent, Environment env, Value cdr) {
      super(evaluator, parent, env);
      this.cdr = cdr;
    }

    @Override
    public Op apply(Value v) {
      //if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
      //Value v = vs[0];
      Op result = new Cons2(evaluator, parent, env, v);
      result = new ListEval(result, env);
      evaluator.setValue(cdr);
      return result;
    }

    @Override
    protected
    java.lang.String getDescription() {
      return "Cons x " + cdr;
    }
  }

  /*
   * Cons2 car value = parent
   *                   value = cons car x
   */
  static class Cons2 extends Op {
    private Value car;

    Cons2(Evaluator evaluator, Op parent, Environment env, Value car) {
      super(evaluator, parent, env);
      this.car = car;
    }

    @Override
    public Op apply(Value v) {
      //if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
      //Value v = vs[0];
      Op result = parent;
      setValue(new Pair(car, v));
      return result;
    }

    @Override
    protected
    java.lang.String getDescription() {
      return "Cons2 " + car + " x";
    }
  }

  /*
   * ListEval (x . y) = parent
   *                    Cons value (ListEval y)
   *                    Eval
   *                    value = x
   * ListEval x       = parent
   *                    value = x
   */
  static class ListEval extends Op {
    ListEval(Op parent, Environment env) {
      super(parent, env);
    }

    @Override
    public Op apply(Value v) {
//      if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      Op result = parent;
      if (!v.isPair()) {
        setValue(v);
        return result;
      }
      Pair p = (Pair) v;
      result = new Cons(evaluator, result, env, p.getCdr());
      result = new Eval(result, env);
      setValue(p.getCar());
      return result;
    }

    @Override
    protected
    java.lang.String getDescription() {
      return "ListEval";
    }
  }

  /*
   * EvalKeyword k = parent
   *                 apply k value
   */
  static class ApplyKeyword extends Op {
    Keyword k;

    ApplyKeyword(Op parent, Environment env, Keyword k) {
      super(parent, env);
      this.k = k;
    }

    @Override
    public Op apply(Value v) {
//      if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      if (!v.isPair()) {
        return evaluator.error("Expected pair in keyword apply", v);
      }
      Op result = k.apply(parent, (Pair) v, env);
      //setValue(v);
      return result;
    }

    @Override
    protected
    java.lang.String getDescription() {
      return "ApplyKeyword " + k; 
    }
  }

  /*
   * Apply (x . y) = parent
   *                 apply x on y
   *                 value
   */
  public static class Apply extends Op {
    Apply(Op parent, Environment env) {
      super(parent, env);
    }

    public Apply(Evaluator evaluator, Op parent, Environment env) {
      super(evaluator, parent, env);
    }

    @Override
    public Op apply(Value v) {
//      if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      if (!v.isPair())
        return error("Expected pair in procedure apply", v);
      Pair p = (Pair) v;
      if (!p.getCar().isProcedure())
        return error("Expected procedure in procedure apply", p.getCar());
      Procedure proc = (Procedure) p.getCar();
      //int i = 2;
      Op result = proc.apply(parent, env, p.getCdr());
      return result;
    }

    @Override
    protected java.lang.String getDescription() {
      return "Apply";
    }
  }

  public void setValue(Value v) {
    evaluator.setValue(v);
  }
//  protected void setValue(Value v[]) {
//    evaluator.setValue(v);
//  }
  protected Op error(java.lang.String string, Value v) {
    return evaluator.error(new String(string), v);
  }
  protected Op error(String string, Value v) {
    return evaluator.error(string, v);
  }
  public Op getParent() {
    return parent;
  }
  protected Op error(Value error) {
    return evaluator.error(error);
  }
  public Evaluator getEvaluator() {
    return evaluator;
  }
  public Environment getEnvironment() {
    return env;
  }
  
  // An op that just sets the current value to a preevaluated value
  public static class SetValue extends Op {
    private final Value returnValue;

    public SetValue(Op parent, Environment environment, Value returnValue) {
      super(parent, environment);
      this.returnValue = returnValue;
    }

    @Override
    public Op apply(Value unused) {
      setValue(returnValue);
      return parent;
    }

    @Override
    protected java.lang.String getDescription() {
      return "SetValue " + returnValue;
    }
    
  }
  public static class ErrorOp extends Op {
    private Value error;
    private boolean continuable;
    public ErrorOp(Evaluator evaluator, String message, Value irritants) {
      super(evaluator, new Op.Done(evaluator));
      this.error = new Error(message, irritants);
    }

    public ErrorOp(Evaluator evaluator, Value error) {
      super(evaluator, new Op.Done(evaluator));
      this.error = error;
    }

    public ErrorOp(Evaluator evaluator, Value error, boolean continuable) {
      super(evaluator, new Op.Done(evaluator));
      this.error = error;
      this.continuable = continuable;
    }

    @Override
    public Op apply(Value v) {
      return env.getErrorHandler().apply(v, continuable);
    }

    @Override
    protected java.lang.String getDescription() {
      return "Error " + error;
    }

    public Value getError() {
      return error;
    }
    
  }
}