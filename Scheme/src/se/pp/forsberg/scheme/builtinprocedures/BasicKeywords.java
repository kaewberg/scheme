package se.pp.forsberg.scheme.builtinprocedures;

import java.util.List;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.macros.BuiltInKeyword;
import se.pp.forsberg.scheme.values.macros.Macro;

public class BasicKeywords extends Library {

  
  public static Value getName() {
    return new Pair(new Identifier("scheme-impl"), new Pair(new Identifier("base"), Nil.NIL));
  }
  
  
  public class Define extends BuiltInKeyword {

    class DefineOp extends Op {
      private Identifier id;
      DefineOp(Op parent, Identifier id) { super(parent); this.id = id; }
      @Override
      public Op apply(Value v) {
        env.define(id, v);
        setValue(Value.UNSPECIFIED);
        return parent;
      }
      @Override
      protected String getDescription() {
        return "Define " + id;
      }
    };
    
    public Define() {
      super("define");
      final Identifier define = new Identifier("define");
      final Identifier x = new Identifier("x");
      final Identifier y = new Identifier("y");
      final Identifier arg = new Identifier("arg");
      final Identifier lambda = new Identifier("lambda");
      
      // (define (x arg ...) y) --> x = (lambda (arg ...) y)
      Value pattern = Pair.makeList(new Value[] { define, Pair.makeList(new Value[] { x, arg, getEllipsis() }), y});
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value xv = bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value argvs = bindings.getValuesAsList(arg);
          Value yv = bindings.get(y);
          Value value = Pair.makeList(new Value[] { lambda, argvs, yv });
          env.define((Identifier) xv, value.eval(env));
          return Value.UNSPECIFIED;
        }

        @Override
        // parent
        // Define x
        // Eval
        // value = (lambda args y)
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          Value xv = bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value argvs = bindings.getValuesAsList(arg);
          Value yv = bindings.get(y);
          Value value = Pair.makeList(new Value[] { lambda, argvs, yv });
          Op result = new DefineOp(parent, (Identifier) xv);
          result = new Op.Eval(result);
          result.setValue(value);
          return result;
        }
      }));
      // (define (x . arg) y) --> x = (lambda arg y)
      pattern = Pair.makeList(new Value[] { define, new Pair(x, arg), y});
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value xv =  bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value argv = bindings.get(arg);
          Value yv = bindings.get(y);
          Value value = Pair.makeList(new Value[] { lambda, argv, yv} );
          env.define((Identifier) xv, value.eval(env));
          return Value.UNSPECIFIED;
        }

        @Override
        // parent
        // Define x
        // Eval
        // value = (lambda arg y)
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          final Value xv =  bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value argv = bindings.get(arg);
          Value yv = bindings.get(y);
          Value value = Pair.makeList(new Value[] { lambda, argv, yv} );
          Op result = new DefineOp(parent, (Identifier) xv);
          result = new Op.Eval(result);
          result.setValue(value);
          return result;
        }
      }));
      // (define x y) --> x = y
      pattern = Pair.makeList(new Value[] { define, x, y });
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value xv = bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value yv = bindings.get(y);
          env.define((Identifier) xv, yv.eval(env));
          return Value.UNSPECIFIED;
        }

        @Override
        // parent
        // Define x
        // Eval
        // value = y
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          Value xv = bindings.get(x);
          if (!xv.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid define, expected identifier" + expression)));
          Value yv = bindings.get(y);
          Op result = new DefineOp(parent, (Identifier) xv);
          result = new Op.Eval(result);
          result.setValue(yv);
          return result;
        }
      }));
    }

    @Override
    public Value apply(Pair expression, Environment env) {
      switch (env.getContext()) {
      case TOP_LEVEL:
      case REPL:
      case START_BODY:
      case LIBRARY:
        return super.apply(expression, env);
      default:
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid context for define")));
      }
    }
//    
//    private Environment env = new Environment();
//    protected SchemeParser createParser(java.lang.String s) {
//      ANTLRInputStream stream = new ANTLRInputStream(s);
//      SchemeLexer lexer = new SchemeLexer(stream);
//      return new SchemeParser(new CommonTokenStream(lexer));
//    }
//    protected Value eval(Value value) {
//      return value.eval(env);
//    }
//    protected Value eval(java.lang.String source) {
//      return createParser(source).datumWs().value.eval(env);
//    }
//    @Test
//    public void testDefine() {
//      Keyword define = new Define();
//      define.apply((Pair) eval("'(define x 17)"), env);
//      assertEquals(eval("17"), eval("x"));
//      define.apply((Pair) eval("'(define (y x) x)"), env);
//      assertEquals(eval("17"), eval("(y 17)"));
//      define.apply((Pair) eval("'(define (z . x) x)"), env);
//      assertEquals(eval("'(17)"), eval("(z 17)"));
//    }

  }
  

  public class Lambda extends BuiltInKeyword {
    public Lambda() {
      super("lambda");
      final Identifier lambda = new Identifier("lambda");
      final Identifier formals = new Identifier("formals");
      final Identifier body = new Identifier("body");
      
      // (lambda formals body ...)
      Value pattern = Pair.makeList(new Value[] { lambda, formals, body, getEllipsis()});
      addRule(new Rule(pattern, new SimpleAction() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          return new se.pp.forsberg.scheme.values.Lambda(bindings.get(formals), bindings.getValuesAsList(body), env);
        }
      }));
    }
  }
  public class If extends BuiltInKeyword {
    class IfOp extends Op {
      private Value consequent, alternative;
      public IfOp(Op parent, Value consequent, Value alternative) {
        super(parent);
        this.consequent = consequent;
        this.alternative = alternative;
      }
      @Override
      public Op apply(Value v) {
        Op result = parent;
        //result = new Op.Eval(result);
        if (v.asBoolean()) {
          result.setValue(consequent);
        } else if (alternative != null){
          result.setValue(alternative);
        } else {
          result.setValue(Value.UNSPECIFIED);
        }
        return result;
      }
      @Override
      protected String getDescription() {
        return "If " + consequent + " " + alternative;
      }
    }
    public If() {
      super("if");
      final Identifier iff = new Identifier("if");
      final Identifier test = new Identifier("test");
      final Identifier consequent = new Identifier("consequent");
      final Identifier alternate = new Identifier("alternate");
      
      // (if test consequent alternative)
      Value pattern = Pair.makeList(new Value[] { iff, test, consequent, alternate });
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vTest = bindings.get(test);
          Value vConsequent = bindings.get(consequent);
          Value vAlternate = bindings.get(alternate);
          if (vTest.eval(env).asBoolean()) {
            return vConsequent;
          } else {
            return vAlternate;
          }
        }

        @Override
        // parent
        // If consequent alternative
        // Eval
        // test
        //
        // If true:
        // parent
        // Eval
        // consequent
        // If false:
        // parent
        // Eval
        // consequent
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vTest = bindings.get(test);
          Value vConsequent = bindings.get(consequent);
          Value vAlternate = bindings.get(alternate);
          Op result = new IfOp(parent, vConsequent, vAlternate);
          result = new Op.Eval(result);
          result.setValue(vTest);
          return result;
        }
      }));
      // (if test consequent)
      pattern = Pair.makeList(new Value[] { iff, test, consequent });
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vTest = bindings.get(test);
          Value vConsequent = bindings.get(consequent);
          if (vTest.eval(env).asBoolean()) {
            return vConsequent;
          } else {
            return Value.UNSPECIFIED;
          }
        }

        @Override
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vTest = bindings.get(test);
          Value vConsequent = bindings.get(consequent);
          Op result = new IfOp(parent, vConsequent, null);
          result = new Op.Eval(result);
          result.setValue(vTest);
          return result;
        }
      }));
    }
  }
  
  public class Set extends BuiltInKeyword {
    class SetOp extends Op {
      private Identifier id;
      public SetOp(Op parent, Identifier id) {
        super(parent);
        this.id = id;
      }
      @Override
      public Op apply(Value v) {
        setValue(Value.UNSPECIFIED);
        env.set(id, v);
        return parent;
      }
      @Override
      protected String getDescription() {
        return "Set " + id;
      }
    }
    public Set() {
      super("set!");
      final Identifier set = new Identifier("set!");
      final Identifier variable = new Identifier("variable");
      final Identifier expression = new Identifier("expression");
      
      // (set! variable expression)
      Value pattern = Pair.makeList(new Value[] { set, variable, expression });
      addRule(new Rule(pattern, new Action() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vVariable = bindings.get(variable);
          if (!vVariable.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid set!, expected variable, not " + vVariable)));
          env.set((Identifier) vVariable, bindings.get((Identifier) vVariable).eval(env));
          return Value.UNSPECIFIED;
        }

        @Override
        // parent
        // Set variable
        // Eval
        // value = expression
        public Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vVariable = bindings.get(variable);
          if (!vVariable.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid set!, expected variable, not " + vVariable)));
          Value vExpression = bindings.get((Identifier) vVariable);
          Op result = new SetOp(parent, (Identifier) vVariable);
          result = new Op.Eval(result);
          result.setValue(vExpression);
          return result;
        }
      }));
    }
  }
  
  public class DefineSyntax extends BuiltInKeyword {
    public DefineSyntax() {
      super("define-syntax");
      final Identifier defineSyntax = new Identifier("define-syntax");
      final Identifier syntaxRules = new Identifier("syntax-rules");
      final Identifier keyword = new Identifier("keyword");
      final Identifier ellipsis = new Identifier("ellipsis");
      final Identifier literal = new Identifier("literal");
      final Identifier rule = new Identifier("rule");
      
      // (define-syntax keyword (syntax-rules (literal ...) rule ...)))
      Value pattern = Pair.makeList(new Value[] {
          defineSyntax, keyword, Pair.makeList(new Value[] {
              syntaxRules, Pair.makeList(new Value[] { literal, getEllipsis() }), rule, getEllipsis() })});
      addRule(new Rule(pattern, new SimpleAction() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vKeyword = bindings.get(keyword);
          List<Value> vLiterals = bindings.getValues(literal);
          List<Value> vRules = bindings.getValues(rule);
          if (!vKeyword.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected keyword in define-syntax, not " + vKeyword)));
          Macro macro = new Macro(((Identifier) vKeyword).getIdentifier(), vLiterals, vRules);
          env.define((Identifier) vKeyword, macro);
          return Value.UNSPECIFIED;
        }
      }));
      // (define-syntax keyword (syntax-rules ellipsis (literal ...) rule ...)))
      pattern = Pair.makeList(new Value[] {
          defineSyntax, keyword, Pair.makeList(new Value[] {
              syntaxRules, ellipsis, Pair.makeList(new Value[] { literal, getEllipsis() }), rule, getEllipsis() })});
      addRule(new Rule(pattern, new SimpleAction() {
        @Override public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
          Value vKeyword = bindings.get(keyword);
          Value vEllipsis = bindings.get(ellipsis);
          List<Value> vLiterals = bindings.getValues(literal);
          List<Value> vRules = bindings.getValues(rule);
          if (!vKeyword.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected keyword in define-syntax, not " + vKeyword)));
          if (!vEllipsis.isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected ellipsis identifier in define-syntax, not " + vEllipsis)));
          Macro macro = new Macro(((Identifier) vKeyword).getIdentifier(), (Identifier) vEllipsis, vLiterals, vRules);
          env.define((Identifier) vKeyword, macro);
          return Value.UNSPECIFIED;
        }
      }));
    }
  }
}
