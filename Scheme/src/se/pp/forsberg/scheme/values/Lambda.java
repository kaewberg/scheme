package se.pp.forsberg.scheme.values;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Environment.Context;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Lambda extends Procedure {
  private final Value pattern;
  private final Value body;
  private final Environment env;
  
  public Lambda(Value pattern, Value body, Environment env) {
    super();
    this.pattern = pattern;
    this.body = body;
    this.env = env;
  }

  public Value apply(Value args) {
    Lambda application;
    application = createApplication();
    return application.applyInternal(args);
  }
  protected Lambda createApplication() {
    return new Lambda(pattern, body, new Environment(env));
  }
  protected Value applyInternal(Value args) {
    bind(pattern, args, env);
    Value result = null;
    Value expressions = body;
    env.pushContext(Context.START_BODY);
    boolean hasExpr = false;
    while (!expressions.isNull()) {
      if (!expressions.isPair()) {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid lambda body " + this)));
      }
      Value expr = ((Pair) expressions).getCar();
      if (!isDefinition(expr)) {
        env.popContext();
        env.pushContext(Context.EXPRESSIONS);
        hasExpr = true;
      }
      result = expr.eval(env);
      expressions = ((Pair) expressions).getCdr();
    }
    env.popContext();
    if (!hasExpr) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Lambda lacks expressions " + this)));
    return result;
  }
  protected static boolean isDefinition(Value value) {
    if (!value.isPair()) return false;
    Value car = ((Pair) value).getCar();
    if (!car.isIdentifier()) return false;
    java.lang.String id = ((Identifier) car).getIdentifier();
    if (id.equals("define")) return true;
    return false;
  }
  protected static void bind(Value pattern, Value args, Environment env) {
    if (pattern.isNull()) {
      if (!args.isNull()) {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Pattern mismatch")));
      }
      return;
    }
    if (pattern.isIdentifier()) {
      env.define((Identifier) pattern, args);
      return;
    }
    if (pattern.isPair()) {
      if (!args.isPair()) {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Pattern mismatch")));
      }
      bind(((Pair) pattern).getCar(), ((Pair) args).getCar(), env);
      bind(((Pair) pattern).getCdr(), ((Pair) args).getCdr(), env);
      return;
    }
    throw new SchemeException(new RuntimeError(new IllegalArgumentException("Pattern mismatch")));
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
    return pattern.hashCode() ^ body.hashCode() ^ env.hashCode();
  }
  @Override
  public java.lang.String toString() {
    java.lang.String body = this.body.toString();
    body = body.substring(1, body.length()-1);
    return "(lambda " + pattern + " " + body + ")";
  }

  class Sequence extends Op {
    // Apply proc args:
    // parent
    // (new environment)
    // Sequence (cdr body)
    // Eval
    // value = (car body)
    
    // Sequence (x . y):
    // parent
    // Sequence y
    // Eval
    // value = x
    // Sequence x:
    // parent
    // value = x
    private Value cdr;
    public Sequence(Op parent, Environment env, Value cdr) {
      super(parent, env);
      this.cdr = cdr;
    }
    public Sequence(Op parent, Value cdr) {
      super(parent);
      this.cdr = cdr;
    }

    @Override
    public Op apply(Value v) {
//      if (vs.length != 1) return evaluator.error("Multiple return values used in a single value context", Pair.makeList(vs));
//      Value v = vs[0];
      if (cdr.isNull()) {
        setValue(v);
        return parent;
      }
      Pair p = (Pair) cdr;
      Op result = new Sequence(parent, env, p.getCdr());
      result = new Op.Eval(result);
      Value expr = p.getCar();
      if (!isDefinition(expr)) {
        env.popContext();
        env.pushContext(Context.EXPRESSIONS);
      }
      result.getEvaluator().setValue(expr);
      return result;
    }
    @Override
    protected java.lang.String getDescription() {
      return "Sequence " + cdr;
    }
    
  }
  @Override
  public Op apply(Op parent, Environment env, Value args) {
    Lambda application;
    application = createApplication();
    return application.applyInternal(parent, args);
  }
  protected Op applyInternal(Op parent, Value args) {
    bind(pattern, args, env);
    env.pushContext(Context.START_BODY);
    parent.getEvaluator().setValue(Value.UNSPECIFIED);
    return new Sequence(parent, env, body);
  }
}
