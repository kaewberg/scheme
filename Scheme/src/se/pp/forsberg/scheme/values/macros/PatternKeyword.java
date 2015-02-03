package se.pp.forsberg.scheme.values.macros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.errors.RuntimeError;

/**
 * A syntactic keyword that can follow different patters.
 * 
 * For instance,
 * (define id expr)
 * (define (id args ...) body)
 * (define (id args ... tailcar. tailcdr) body)
 * 
 *
 */
public class PatternKeyword extends Keyword {

  private static final Identifier MATCH_ANY = new Identifier("_");
  private Identifier ellipsis = new Identifier("...");
  
  protected interface Action {
    //Value match(Environment env, Value pattern, Value expression, Map<Identifier, Binding> bindings);
    Value match(Environment env, Value pattern, Value expression, Bindings bindings);
    Op match(Op parent, Environment env, Value pattern, Value expression, Bindings bindings);
  }

  // A simple action is one that doesn't need recursion or evaluation
  public abstract class SimpleAction implements Action {
    @Override
    public Op match(Op op, Environment env, Value pattern, Value expression, Bindings bindings) {
      Value v = match(env, pattern, expression, bindings);
      op.getEvaluator().setValue(v);
      return op.getParent();
    }
  }
  
  protected class Rule {
    private Value pattern;
    private Action action;
    private Set<Identifier> allIdentifiers = new HashSet<Identifier>();
    public Rule(Value pattern, Action action) {
      this.pattern = pattern;
      this.action = action;
      checkIdentifiers();
    }
    public Value getPattern() {
      return pattern;
    }
    public Action getAction() {
      return action;
    }
    protected void checkIdentifiers() {
      checkIdentifiers(pattern);
    }
    protected void checkIdentifiers(Value pattern) {
      if (pattern.isIdentifier()) {
        Identifier id = (Identifier) pattern;
        if (id.eqv(MATCH_ANY) || id.equals(ellipsis)) return;
        if (!literals.contains(id)) {
          allIdentifiers.add(id);
        }
        return;
      }
      if (pattern.isPair()) {
        Pair p = (Pair) pattern;
        checkIdentifiers(p.getCar());
        checkIdentifiers(p.getCdr());
        return;
      }
      if (pattern.isVector()) {
        Vector vec = (Vector) pattern;
        for (Value v: vec.getVector()) {
          checkIdentifiers(v);
        }
        return;
      }
    }
    public Set<Identifier> getAllIdentifiers() {
      return allIdentifiers;
    }
  }
  private List<Rule> rules = new ArrayList<Rule>();
  protected void addRule(Rule rule) { rules.add(rule); }
  private Set<Identifier> literals = new HashSet<Identifier>();
  
  protected Value getEllipsis() {
    return ellipsis;
  }
  protected void setEllipsis(Identifier ellipsis) {
    this.ellipsis = ellipsis;
  }
  protected void addLiteral(Identifier literal) {
    literals.add(literal);
  }
    
  protected class Bindings {
    Map<Identifier, Value> values = new HashMap<Identifier, Value>();
    List<Bindings> repetitions = new ArrayList<Bindings>();
    Set<Identifier> allIdentifiers = new HashSet<Identifier>();

   // public Bindings() {}
    public Bindings(Set<Identifier> allIdentifiers) {
      this.allIdentifiers = allIdentifiers;
    }
    public void add(Identifier id, Value v) {
      //if (allIdentifiers.contains(id)) {
      //  throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " occurs more than once in syntax pattern")));
      //}
      values.put(id, v);
      allIdentifiers.add(id);
    }
    public boolean contains(Identifier id) { return allIdentifiers.contains(id); }
//    public Bindings createSubBinding() {
//      Bindings result = new Bindings();
//      repetitions.add(result);
//      result.allIdentifiers = allIdentifiers;
//      return result;
//    }
//    public Bindings getSubBindings(int i) {
//      if (i >= repetitions.size()) return null;
//      return repetitions.get(i);
//    }
    // nth occurrence of everything in subBindings
    public void setSubBindings(int i, Bindings subBindings) {
      // ((x)... y)...   in (d)  ((e) f) ((a) (b) c) 
      // Suppose we are in setSubBindings(2,  y=c x=[a b])
      // Current state y=[d f] x=[[] [e]]
      // Above loop sets repetitions[2].y=c
      // state y=[d f c] x=[[] [e]]
      // This loop sets repetitions[2].x=[a b]
      // state y=[d f c] x=[[] [e] [a b]]
      if (i >= repetitions.size() + 1) throw new IllegalArgumentException("Bad programmer setSubBindings");
      if (i >= repetitions.size()) repetitions.add(new Bindings(allIdentifiers));
      mergeSubBindings(repetitions.get(i), subBindings);
    }
    private void mergeSubBindings(Bindings dst, Bindings src) {
      for (Identifier id: src.values.keySet()) {
        dst.add(id, src.values.get(id));
      }
      for (int i = 0; i < src.repetitions.size(); i++) {
        if (i >= dst.repetitions.size()) {
          dst.allIdentifiers.addAll(src.repetitions.get(i).allIdentifiers);
          src.repetitions.get(i).allIdentifiers = dst.allIdentifiers;
          dst.repetitions.add(src.repetitions.get(i));
        } else {
          mergeSubBindings(dst.repetitions.get(i), src.repetitions.get(i));
        }
      }
    }
    public Value get(Identifier id) {
      return values.get(id);
    }
    public List<Value> getValues(Identifier id) {
      List<Value> result = new ArrayList<Value>();
      for (Bindings b: repetitions) {
        Value v = b.get(id);
        if (v == null) break;
        result.add(v);
      }
      return result;
    }
    public Value getValuesAsList(Identifier id) {
      return getValuesAsList(id, 0);
    }
    public Value getValuesAsList(Identifier id, int pos) {
      if (pos >= repetitions.size()) return Nil.NIL;
      Value v = repetitions.get(pos).get(id);
      if (v == null) return Nil.NIL;
      return new Pair(v, getValuesAsList(id, pos+1));
    }
  }
  
//  protected class Binding {
//    private Identifier identifier;
//    private List<Value> values = new ArrayList<Value>();
//    private boolean repeated;
//    public Binding(Identifier identifier, Value value) {
//      this.identifier = identifier;
//      this.values.add(value);
//      this.repeated = false;
//    }
//    public Binding(Identifier identifier) {
//      this.identifier = identifier;
//      this.repeated = true;
//    }
//    public Identifier getIdentifier() { return identifier; }
//    public Value getValue() { return values.get(0); }
//    public void addValue(Value value) { values.add(value); }
//    public List<Value> getValues() { return values; }
//    public boolean isRepeated() { return repeated; }
//    public Value getValuesAsList() {
//      return Pair.makeList(values);
//    }
//  }
  // Match a non-repeated pattern
  protected boolean match(Value expr, Value pattern, Bindings bindings) {
    if (pattern.isIdentifier()) {
      Identifier id = (Identifier) pattern;
      if (id.eqv(MATCH_ANY)) return true;
      if (!literals.contains(id)) {
        bindings.add(id, expr);
        return true;
      }
      return expr.isIdentifier() && id.eqv(expr);
    }
    if (pattern.isPair()) {
      Pair pPattern = (Pair) pattern;
      if (nextEllipsis(pPattern)) {
        expr = matchRepeated(expr, pPattern.getCar(), bindings);
        pattern = ((Pair) pPattern.getCdr()).getCdr();
        return match(expr, pattern, bindings);
      }
      if (!expr.isPair()) return false;
      Pair pExpr = (Pair) expr;
      return match(pExpr.getCar(), pPattern.getCar(), bindings) &&
             match(pExpr.getCdr(), pPattern.getCdr(), bindings);
    }
    if (pattern.isVector()) {
      if (!expr.isVector()) return false;
      List<Value> vExpr = ((Vector) expr).getVector();
      List<Value> vPattern = ((Vector) pattern).getVector();
      int indexExpr = 0;
      for (int indexPattern = 0; indexPattern < vExpr.size(); indexPattern++) {
        if (indexExpr >= vExpr.size()) return false;
        pattern = vPattern.get(indexPattern);
        if (nextEllipsis(vPattern, indexPattern)) {
          indexExpr += matchRepeated(vExpr, indexExpr, vPattern.get(indexPattern), bindings);
        } else {
          if (!match(pattern, vExpr.get(indexExpr++), bindings)) return false;
        }
      }
      if (indexExpr != vExpr.size()) return false;
      return true;
    }
    return pattern.equal(expr);
  }
  // Match subpattern ... inside a vector, eg. #((x y)...)   in   #((a b) (c d) x)
  // return number of elements matched
  private int matchRepeated( List<Value> vExpr, int indexExpr, Value pattern, Bindings bindings) {
    int length = 0;
    Bindings subBindings = new Bindings(bindings.allIdentifiers);
    while (match(vExpr.get(indexExpr + length), pattern, subBindings)) {
      bindings.setSubBindings(length++, subBindings);
      subBindings = new Bindings(bindings.allIdentifiers);
    }
    return length;
  }
  // Match subpattern ... inside a list, eg ((x y)...)   in   ((a b) (c d) . x)
  // return pointer to first value not matched
  private Value matchRepeated(Value expr, Value pattern, Bindings bindings) {
    if (!expr.isPair()) return expr;
    int length = 0;
    Bindings subBindings = new Bindings(bindings.allIdentifiers);
    Pair pExpr = (Pair) expr;
    while (match(pExpr.getCar(), pattern, subBindings)) {
      bindings.setSubBindings(length++, subBindings);
      subBindings = new Bindings(bindings.allIdentifiers);
      if (!pExpr.getCdr().isPair()) return pExpr.getCdr();
      pExpr = (Pair) pExpr.getCdr();
    }
    return pExpr;
  }
//  protected boolean match(Value expr, Value pattern, Map<Identifier, Binding> bindings) {
//    return match(((Pair)expr).getCdr(), ((Pair)pattern).getCdr(), false, bindings);
//  }
//  protected boolean match(Value expr, Value pattern, boolean repeated, Map<Identifier, Binding> bindings) {
//    if (pattern.isIdentifier()) {
//      Identifier id = (Identifier) pattern;
//      if (id.eqv(MATCH_ANY)) return true;
//      if (!literals.contains(id)) {
//        addBinding(id, expr, repeated, bindings);
//        return true;
//      }
//      return expr.isIdentifier() && id.eqv(expr);
//    }
//    if (pattern.isPair()) {
//      Pair pPattern = (Pair) pattern;
//      if (nextEllipsis(pPattern)) {
//        // Need to handle match of empty list seperately
//        if (expr.isNull() && pPattern.getCar().isIdentifier()) {
//          Identifier id = (Identifier) pPattern.getCar();
//          if (!literals.contains(id)) {
//            addEmptyBinding(id, bindings);
//          }
//        } else {
//          while (!expr.isNull()) {
//            if (!expr.isPair()) break;;
//            Pair pExpr = (Pair) expr;
//            if (!match(pExpr.getCar(), pPattern.getCar(), true, bindings)) break;
//            expr= pExpr.getCdr();
//          }
//        }
//        pattern = ((Pair) pPattern.getCdr()).getCdr();
//        return match(expr, pattern, repeated, bindings);
//      }
//      if (!expr.isPair()) return false;
//      Pair pExpr = (Pair) expr;
//      return match(pExpr.getCar(), pPattern.getCar(), repeated, bindings) &&
//             match(pExpr.getCdr(), pPattern.getCdr(), repeated, bindings);
//    }
//    if (pattern.isVector()) {
//      if (!expr.isVector()) return false;
//      List<Value> vExpr = ((Vector) expr).getVector();
//      List<Value> vPattern = ((Vector) pattern).getVector();
//      int indexExpr = 0;
//      for (int indexPattern = 0; indexPattern < vExpr.size(); indexPattern++) {
//        if (indexExpr >= vExpr.size()) return false;
//        pattern = vPattern.get(indexPattern);
//        if (nextEllipsis(vPattern, indexPattern)) {
//          while (indexExpr < vExpr.size() && match(vExpr.get(indexExpr), pattern, true, bindings)) {
//            indexExpr++;
//          }
//        } else {
//          match(pattern, vExpr.get(indexExpr++), repeated, bindings);
//        }
//      }
//      if (indexExpr != vExpr.size()) return false;
//      return true;
//    }
//    return pattern.equal(expr);
//  }
//  private void addEmptyBinding(Identifier id, Map<Identifier, Binding> bindings) {
//    if (bindings.containsKey(id)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " occurs more than once in syntax pattern")));
//    bindings.put(id, new Binding(id));
//  }
//  protected void addBinding(Identifier id, Value expr, boolean repeated, Map<Identifier, Binding> bindings) {
//    if (repeated) {
//      Binding binding = bindings.get(id);
//      if (binding == null) {
//        binding = new Binding(id);
//        bindings.put(id, binding);
//      }
//      if (!binding.isRepeated()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " occurs more than once in syntax pattern")));
//      binding.addValue(expr);
//    } else {
//      if (bindings.containsKey(id)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Identifier " + id + " occurs more than once in syntax pattern")));
//      bindings.put(id, new Binding(id, expr));
//    }
//  }
  protected boolean nextEllipsis(Pair p) {
    if (!p.getCdr().isPair()) return false;
    p = (Pair) p.getCdr();
    return p.getCar().eqv(ellipsis);
  }
  protected boolean nextEllipsis(List<Value> v, int i) {
    if (i >= v.size()-1) return false;
    return v.get(i+1).eqv(ellipsis);
  }
    
  public PatternKeyword(String keyword) {
    super(keyword);
  }

//  public Value apply(Pair expression, Environment env) {
//    for (Rule rule: rules) {
//      Map<Identifier, Binding> bindings = new HashMap<Identifier, Binding>();
//      if (match(expression, rule.getPattern(), bindings)) {
//        return rule.getAction().match(env, rule.getPattern(), expression, bindings);
//      }
//    }
//    throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid use of keyword " + getKeyword())));
//  }
  
  public Value apply(Pair expression, Environment env) {
    for (Rule rule: rules) {
      //int i = 1+1;
      Bindings bindings = new Bindings(rule.getAllIdentifiers());
      if (match(expression, rule.getPattern(), bindings)) {
        return rule.getAction().match(env, rule.getPattern(), expression, bindings);
      }
    }
    throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid use of keyword " + getKeyword())));
  }
  @Override
  /**
   * Continuation based variant.
   * Keyword should never recursively call eval or apply, but rather stack ops
   */
  public Op apply(Op op, Pair expression, Environment env) {
    for (Rule rule: rules) {
      //int i = 1+1;
      Bindings bindings = new Bindings(rule.getAllIdentifiers());
      if (match(expression, rule.getPattern(), bindings)) {
        return rule.getAction().match(op, env, rule.getPattern(), expression, bindings);
      }
    }
    return op.getEvaluator().error("Invalid use of keyword", this);
  }
  

}
