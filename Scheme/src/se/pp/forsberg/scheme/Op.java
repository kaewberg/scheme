package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Procedure;
import se.pp.forsberg.scheme.values.Value;
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

  abstract protected Op apply(Value v);

  // Continuations
  // This file contains the basic ops needed for list evaluation
  // See keyword.apply and procedure.apply for more

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
    protected
    Op apply(Value v) {
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
        v = env.lookup(id);
        if (v == null) {
          return evaluator.error("Undefined identifier", id);
        }
        if (v instanceof Keyword) {
          setValue(p.getCdr());
          return new ApplyKeyword(this, env, (Keyword) v);
        }
      }
      Op result = parent;
      result = new Apply(result, env);
      result = new Cons(result, env, p.getCdr());
      result = new Eval(result, env);
      evaluator.setValue(p.getCar());
      return result;
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

    Cons(Op parent, Environment env, Value cdr) {
      super(parent, env);
      this.cdr = cdr;
    }

    @Override
    protected
    Op apply(Value v) {
      Op result = new Cons2(parent, env, v);
      result = new ListEval(result, env);
      evaluator.setValue(cdr);
      return result;
    }
  }

  /*
   * Cons2 car value = parent
   *                   value = cons car x
   */
  static class Cons2 extends Op {
    private Value car;

    Cons2(Op parent, Environment env, Value car) {
      super(parent, env);
      this.car = car;
    }

    @Override
    protected
    Op apply(Value v) {
      Op result = parent;
      setValue(new Pair(car, v));
      return result;
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
    protected
    Op apply(Value v) {
      Op result = parent;
      if (!v.isPair()) {
        setValue(v);
        return result;
      }
      Pair p = (Pair) v;
      result = new Cons(result, env, p.getCdr());
      result = new Eval(result, env);
      setValue(p.getCar());
      return result;
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
    protected
    Op apply(Value v) {
      if (!v.isPair()) {
        return evaluator.error("Expected pair in keyword apply", v);
      }
      Op result = k.apply(parent, (Pair) v, env);
      setValue(v);
      return result;
    }
  }

  /*
   * Apply (x . y) = parent
   *                 apply x on y
   *                 value
   */
  static class Apply extends Op {
    Apply(Op parent, Environment env) {
      super(parent, env);
    }

    @Override
    protected
    Op apply(Value v) {
      if (!v.isPair())
        return error("Expected pair in procedure apply", v);
      Pair p = (Pair) v;
      if (!p.getCar().isProcedure())
        return error("Expected procedure in procedure apply", p.getCar());
      Procedure proc = (Procedure) p.getCar();
      Op result = proc.apply(parent, env, p.getCdr());
      evaluator.setValue(v);
      return result;
    }
  }

  public void setValue(Value v) {
    evaluator.setValue(v);
  }
  public Op error(String string, Value v) {
    return evaluator.error(string, v);
  }
  public Op getParent() {
    return parent;
  }
}