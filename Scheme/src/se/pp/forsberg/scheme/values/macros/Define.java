//package se.pp.forsberg.scheme.values.macros;
//
//import static org.junit.Assert.*;
//
//import java.util.Map;
//
//import org.antlr.v4.runtime.ANTLRInputStream;
//import org.antlr.v4.runtime.CommonTokenStream;
//import org.junit.Test;
//
//import se.pp.forsberg.scheme.Environment;
//import se.pp.forsberg.scheme.Environment.Context;
//import se.pp.forsberg.scheme.SchemeException;
//import se.pp.forsberg.scheme.antlr.SchemeLexer;
//import se.pp.forsberg.scheme.antlr.SchemeParser;
//import se.pp.forsberg.scheme.values.Identifier;
//import se.pp.forsberg.scheme.values.Nil;
//import se.pp.forsberg.scheme.values.Pair;
//import se.pp.forsberg.scheme.values.Value;
//import se.pp.forsberg.scheme.values.errors.RuntimeError;
//
//public class Define extends BuiltInKeyword {
//
//  public Define() {
//    super("define");
//    final Identifier define = new Identifier("define");
//    final Identifier x = new Identifier("x");
//    final Identifier y = new Identifier("y");
//    final Identifier arg = new Identifier("arg");
//    final Identifier lambda = new Identifier("lambda");
//    
//    // (define (x arg ...) y) --> x = (lambda (arg ...) y)
//    Value pattern = Pair.makeList(new Value[] { define, Pair.makeList(new Value[] { x, arg, getEllipsis() }), y});
//    addRule(new Rule(pattern, new Action() {
//      @Override public Value match(Environment env, Value pattern, Value expression, Map<Identifier, Binding> bindings) {
//        Binding xb = bindings.get(x);
//        Value xv = xb.getValue();
//        if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
//        Binding argb = bindings.get(arg);
//        Binding yb = bindings.get(y);
//        Value yv = yb.getValue();
//        Value value = Pair.makeList(new Value[] { lambda, Pair.makeList(argb.getValues()), yv });
//        env.define((Identifier) xv, value.eval(env));
//        return Value.UNSPECIFIED;
//      }
//    }));
//    // (define (x . arg) y) --> x = (lambda arg y)
//    pattern = Pair.makeList(new Value[] { define, new Pair(x, arg), y});
//    addRule(new Rule(pattern, new Action() {
//      @Override public Value match(Environment env, Value pattern, Value expression, Map<Identifier, Binding> bindings) {
//        Binding xb = bindings.get(x);
//        Value xv = xb.getValue();
//        if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
//        Binding argb = bindings.get(arg);
//        Binding yb = bindings.get(y);
//        Value yv = yb.getValue();
//        Value value = Pair.makeList(new Value[] { lambda, argb.getValue(), yv} );
//        env.define((Identifier) xv, value.eval(env));
//        return Value.UNSPECIFIED;
//      }
//    }));
//    // (define x y) --> x = y
//    pattern = Pair.makeList(new Value[] { define, x, y });
//    addRule(new Rule(pattern, new Action() {
//      @Override public Value match(Environment env, Value pattern, Value expression, Map<Identifier, Binding> bindings) {
//        Binding xb = bindings.get(x);
//        Value xv = xb.getValue();
//        if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
//        Binding yb = bindings.get(y);
//        Value yv = yb.getValue();
//        env.define((Identifier) xv, yv.eval(env));
//        return Value.UNSPECIFIED;
//      }
//    }));
//  }
//
//
//  @Override
//  public Value apply(Pair expression, Environment env) {
//    switch (env.getContext()) {
//    case TOP_LEVEL:
//    case REPL:
//    case START_BODY:
//      return super.apply(expression, env);
//    default:
//      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid context for define")));
//    }
//  }
//  
//  private Environment env = new Environment();
//  protected SchemeParser createParser(java.lang.String s) {
//    ANTLRInputStream stream = new ANTLRInputStream(s);
//    SchemeLexer lexer = new SchemeLexer(stream);
//    return new SchemeParser(new CommonTokenStream(lexer));
//  }
//  protected Value eval(Value value) {
//    return value.eval(env);
//  }
//  protected Value eval(java.lang.String source) {
//    return createParser(source).datumWs().value.eval(env);
//  }
//  @Test
//  public void testDefine() {
//    Keyword define = new Define();
//    define.apply((Pair) eval("'(define x 17)"), env);
//    assertEquals(eval("17"), eval("x"));
//    define.apply((Pair) eval("'(define (y x) x)"), env);
//    assertEquals(eval("17"), eval("(y 17)"));
//    define.apply((Pair) eval("'(define (z . x) x)"), env);
//    assertEquals(eval("'(17)"), eval("(z 17)"));
//  }
//
//}
