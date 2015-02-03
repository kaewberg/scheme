package se.pp.forsberg.scheme.values.macros;

import java.util.ArrayList;
import java.util.List;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.Op.Eval;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

public class Macro extends PatternKeyword {

  public Macro(java.lang.String keyword, List<Value> literals, List<Value> rules) {
    this(keyword, null, literals, rules);
  }

  public Macro(java.lang.String keyword, Identifier ellipsis, List<Value> literals, List<Value> rules) {
    super(keyword);
    if (ellipsis != null)
      setEllipsis(ellipsis);
    addLiterals(literals);
    addRules(rules);
  }

  protected void addLiterals(List<Value> literals) {
    for (Value value : literals) {
      if (!value.isIdentifier())
        throw new SchemeException(new RuntimeError(new IllegalArgumentException(
            "Invalid syntax rule, expected literals " + literals)));
      addLiteral((Identifier) value);
    }
  }

  private void addRules(List<Value> rules) {
    for (Value value : rules) {
      addRule(value);
    }
  }

  private void addRule(Value rule) {
    if (!rule.isPair())
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid syntax rule, expected rule not "
          + rule)));
    Pair pair = (Pair) rule;
    Value pattern = pair.getCar();
    if (!pair.getCdr().isPair())
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid syntax rule, expected rule not "
          + rule)));
    pair = (Pair) pair.getCdr();
    if (!pair.getCdr().isNull())
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid syntax rule, expected rule not "
          + rule)));
    final Value template = pair.getCar();

    super.addRule(new Rule(pattern, new MacroAction(template)));
  }

  protected class MacroAction implements Action {
    private Value template;

    public MacroAction(Value template) {
      this.template = template;
    }

    @Override
    public Value match(Environment env, Value pattern, Value expression, Bindings bindings) {
      return replace(template, bindings);
    }

    // public Value match(Environment env, Value pattern, Value expression,
    // Map<Identifier, Binding> bindings) {
    // return replace(template, bindings, false).eval(env); // TODO Should eval
    // be here?
    // }

    protected Value replace(Value template, Bindings bindings) {
      if (template.isPair()) {
        Pair pTemplate = (Pair) template;
        if (nextEllipsis(pTemplate)) {
          // if (!pTemplate.getCar().isIdentifier()) {
          // throw new RuntimeException("What?!");
          // }
          // Warning!
          // (name2 val2) ... is a legal pattern!
          // It means name2 and val2 are lists of the same length
          Value result = replaceRepeated(pTemplate.getCar(), bindings);
          if (result.isNull()) {
            return replace(((Pair) pTemplate.getCdr()).getCdr(), bindings);
          }
          if (result.isPair()) {
            Pair pair = (Pair) result;
            while (pair.getCdr().isPair()) {
              pair = (Pair) pair.getCdr();
            }
            pair.setCdr(replace(((Pair) pTemplate.getCdr()).getCdr(), bindings));
            return result;
          }
          throw new IllegalArgumentException("Bad programmer, Template replaceRepeated");
        }
        Value car = replace(pTemplate.getCar(), bindings);
        Value cdr = replace(pTemplate.getCdr(), bindings);
        if (car == null || cdr == null) return null;
        return new Pair(car, cdr);
      }
      if (template.isVector()) {
        List<Value> vTemplate = ((Vector) template).getVector();
        List<Value> vResult = new ArrayList<Value>();
        for (int indexTemplate = 0; indexTemplate < vTemplate.size(); indexTemplate++) {
          if (nextEllipsis(vTemplate, indexTemplate)) {
            replaceRepeated(vTemplate.get(indexTemplate), vResult, bindings);
            indexTemplate++;
          } else {
            Value item = replace(vTemplate.get(indexTemplate), bindings);
            if (item == null) return null;
            vResult.add(item);
          }
        }
        return new Vector(vResult);
      }
      if (template.isIdentifier()) {
        Identifier id = (Identifier) template;
        if (bindings.contains(id)) {
          Value value = bindings.get(id);
          return value;
        }
      }
      return template;
    }

    // Replace subpattern ... in list   eg   ((x y)...)
    // Return generated value
    private Value replaceRepeated(Value template, Bindings bindings) {
      return replaceRepeated(template, bindings, 0);
    }
    private Value replaceRepeated(Value template, Bindings bindings, int rep) {
      if (rep >= bindings.repetitions.size()) return Nil.NIL;
      Value v = replace(template, bindings.repetitions.get(rep));
      if (v == null) return Nil.NIL;
      return new Pair(v, replaceRepeated(template, bindings, rep+1));
    }

    // Replace subpattern ... in vector   eg   #((x y)...)
    // Place result i vResult
    private void replaceRepeated(Value value, List<Value> vResult, Bindings bindings) {
      for (Bindings subBindings: bindings.repetitions) {
        Value v = replace(template, subBindings);
        if (v == null) break;
        vResult.add(v);
      }
    }
    // protected Value replace(Value template, Map<Identifier, Binding>
    // bindings, boolean repeated) {
    // if (template.isPair()) {
    // Pair pTemplate = (Pair) template;
    // if (nextEllipsis(pTemplate)) {
    // // if (!pTemplate.getCar().isIdentifier()) {
    // // throw new RuntimeException("What?!");
    // // }
    // // Warning!
    // // (name2 val2) ... is a legal pattern!
    // // It means name2 and val2 are lists of the same length
    // List<Value> values = bindings.get((Identifier)
    // pTemplate.getCar()).getValues();
    // if (values.size() == 0) {
    // return replace(((Pair) pTemplate.getCdr()).getCdr(), bindings);
    // }
    // Pair result = new Pair(values.get(0), Nil.NIL);
    // Pair tail = result;
    // for (int i = 1; i < values.size(); i++) {
    // tail.setCdr(new Pair(values.get(i), Nil.NIL));
    // tail = (Pair) tail.getCdr();
    // }
    // Map<Identifier, Binding> newBindings = new HashMap<Identifier,
    // Binding>(bindings);
    // Value sublist = replace(pTemplate.getCar(), newBindings, true);
    // if (sublist.isNull()) {
    // return replace(((Pair) pTemplate.getCdr()).getCdr(), bindings, repeated);
    // } else {
    // Value result = sublist;
    // Pair rest = (Pair) sublist;
    // while (!rest.getCdr().isNull()) {
    // rest = (Pair) rest.getCdr();
    // }
    // rest.setCdr(replace(((Pair) pTemplate.getCdr()).getCdr(), bindings,
    // repeated));
    // return result;
    // }
    // }
    // return new Pair(replace(pTemplate.getCar(), bindings, repeated),
    // replace(pTemplate.getCdr(), bindings, repeated));
    // }
    // if (template.isVector()) {
    // List<Value> vTemplate = ((Vector) template).getVector();
    // List<Value> vResult = new ArrayList<Value>();
    // for (int indexTemplate = 0; indexTemplate < vTemplate.size();
    // indexTemplate++) {
    // if (nextEllipsis(vTemplate, indexTemplate)) {
    // // TODO
    // // #((a b) ...)
    // for (Value value: bindings.get((Identifier)
    // vTemplate.get(indexTemplate)).getValues()) {
    // vResult.add(value);
    // }
    // indexTemplate++;
    // } else {
    // vResult.add(replace(vTemplate.get(indexTemplate), bindings, repeated));
    // }
    // }
    // return new Vector(vResult);
    // }
    // if (template.isIdentifier()) {
    // Identifier id = (Identifier) template;
    // Binding binding = bindings.get(id);
    // if (binding != null) {
    // return binding.getValue();
    // }
    // }
    // return template;
    // }
    // // This needs to be smart...
    // // Consider:
    // // Match ((x (y z) ... w) ...) in ((1 (2 3) (4 5) 6) (7 8) (9 (10 11) 12)
    // // x = (1 7 9)
    // // y = (2 4 10)
    // // z = (3 5 11)
    // // w = (6 8 12)
    // // Apply to
    // // ((x y z w) ...) -> ((1 2 3 6) (7 4 5 8) (9 10 11 12))
    // // Easiest is probably to let replaceEllipsis consume values from
    // bindings as it goes.
    // // If
    //
    // private Value replaceEllipsis(Value value, Map<Identifier, Binding>
    // bindings) {
    // Map<Identifier, Binding> newBindings = new HashMap<Identifier,
    // Binding>(bindings);
    //
    // Value next = replaceAndConsume(value, newBindings);
    // if (next.isNull()) return Nil.NIL;
    // Pair current = new Pair(next, Nil.NIL);
    // Value result = current;
    // next = replaceAndConsume(value, newBindings);
    // while (!next.isNull()) {
    // current.setCdr(new Pair(next, Nil.NIL));
    // current = (Pair) current.getCdr();
    // next = replaceAndConsume(value, newBindings);
    // }
    // return result;
    // }
    // private Value replaceAndConsume(Value value, Map<Identifier, Binding>
    // newBindings) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    // Macro x y:
    // parent
    // Eval
    // rewrite value
    public Op match(Op op, Environment env, Value pattern, Value expression, Bindings bindings) {
      Op result = new Op.Eval(op.getParent(), env);
      op.getEvaluator().setValue(replace(template, bindings));
      return result;
    }

  }
}
