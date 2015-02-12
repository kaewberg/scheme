package se.pp.forsberg.scheme.values;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.pp.forsberg.scheme.DynamicWind;
import se.pp.forsberg.scheme.ErrorHandler;
import se.pp.forsberg.scheme.Op;
import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.builtinprocedures.Library;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Environment extends Value {
  
  public enum Context {
    REPL,
    TOP_LEVEL,
    START_BODY,
    EXPRESSIONS,
    LIBRARY
  }

  private final Environment parent;
  private Deque<Context> contextStack = new ArrayDeque<Context>();
  private Map<Identifier, Value> values = new HashMap<Identifier, Value>();
  private File dir;
  private DynamicWind dynamicWind;
  private ErrorHandler errorHandler;
  protected Environment() {
    this(null, null);
  }
  public Environment(Environment parent) {
    this(parent, Context.TOP_LEVEL);
  }
  public Environment(Environment parent, Context context) {
    this(parent, context, new File(""));
  }
  public Environment(Environment parent, Context context, File dir) {
    if (context == null) {
      if (parent == null) {
        context = Context.TOP_LEVEL;
      } else {
        context = parent.getContext();
      }
    }
    contextStack.push(context);
    this.parent = parent;
//    if (parent == null && context != Context.LIBRARY) {
//      try {
//        addBuiltIns();
//      } catch (Exception x) {
//        throw new RuntimeException("Error loading libraries", x);
//      }
//    }
    this.dir = dir;
  }
  
  public File getCurrentDirectory() { return dir; }
  public void setCurrentDirectory(File dir) { this.dir = dir; }
  public Context getContext() {
    return contextStack.peekFirst();
  }
  public void pushContext(Context context) {
    contextStack.push(context);
  }
  public Context popContext() {
    return contextStack.pop();
  }
  
  public void define(Identifier identifier, Value value) {
    values.put(identifier, value);
  }
  
  public Value lookup(Identifier identifier) {
    if (values.containsKey(identifier)) {
      Value result = values.get(identifier);
      return result;
    }
    if (parent == null) return null;
    return parent.lookup(identifier);
  }
  
//  protected void addBuiltIns() throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.BasicKeywords());
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.DerivedExpressions());
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.EquivalencePredicates());
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.Numbers());
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.Booleans());
//    importLibrary(new se.pp.forsberg.scheme.builtinprocedures.PairsAndLists());
//  }
  

  protected Parser createParser(java.lang.String s) {
    return new Parser(new StringReader(s));
  }
  protected Value eval(Value value) throws SchemeException {
    return value.eval(new Environment());
  }
  protected Value eval(java.lang.String source) throws SchemeException {
    return createParser(source).read().eval(Environment.schemeReportEnvironment(7));
  }
  public void set(Identifier identifier, Value value) {
    if (values.containsKey(identifier)) {
      define(identifier, value);
    } else if (parent != null) {
      parent.set(identifier, value);
    }
  }
  public boolean isTopLevel() {
    return parent == null;
  }
  public boolean contains(Identifier id) {
    return values.containsKey(id);
  }
  public static boolean isImport(Value value) {
    if (!value.isPair()) return false;
    Pair pair = (Pair) value;
    return pair.getCar().eqv(new Identifier("import"));
  }
  public void importLibrary(Value importStatement) throws SchemeException {
    if (!isImport(importStatement)) throw new SchemeException("Invalid import statement", importStatement);
    Value importSets = ((Pair) importStatement).getCdr();
    while (!importSets.isNull()) {
      if (!importSets.isPair()) throw new SchemeException("Invalid import statement", importStatement);
      Pair pair = (Pair) importSets;
      importImportSet(createImportSet(pair.getCar()));
      importSets = pair.getCdr();
    }
  }
  protected static Environment createImportSet(Value importSet) throws SchemeException {
    if (!importSet.isPair()) throw new SchemeException("Invalid import set", importSet);
    Pair pair = (Pair) importSet;
    if (pair.getCar().isIdentifier()) {
      java.lang.String id = ((Identifier) pair.getCar()).getIdentifier();
      if (id.equals("only")) {
        if (!pair.getCdr().isPair()) throw new SchemeException("Invalid import set", importSet);
        pair = (Pair) pair.getCdr();
        return only(createImportSet(pair.getCar()), pair.getCdr());
      }
      if (id.equals("except")) {
        if (!pair.getCdr().isPair()) throw new SchemeException("Invalid import set", importSet);
        pair = (Pair) pair.getCdr();
        return except(createImportSet(pair.getCar()), pair.getCdr());
      }
      if (id.equals("prefix")) {
        if (!pair.getCdr().isPair()) throw new SchemeException("Invalid import set", importSet);
        pair = (Pair) pair.getCdr();
        Value importSetToPrefix = pair.getCar();
        if (!pair.getCdr().isPair()) throw new SchemeException("Invalid import set", importSet);
        pair = (Pair) pair.getCdr();
        if (!pair.getCar().isIdentifier()) throw new SchemeException("Invalid import set", importSet);
        if (!pair.getCdr().isNull()) throw new SchemeException("Invalid import set", importSet);
        Identifier prefix = (Identifier) pair.getCar();
        return prefix(createImportSet(importSetToPrefix), prefix);
      }
      if (id.equals("rename")) {
        if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid import set " + importSet)));
        pair = (Pair) pair.getCdr();
        Value importSetToRenameFrom = pair.getCar();
        Value renamings = pair.getCdr();
        return rename(createImportSet(importSetToRenameFrom), renamings);
      }
    }
    return readLibrary(pair);
  }
  protected static Environment readLibrary(Pair libraryName) throws SchemeException {
    Environment env = new Environment(null); 
    Library.load(libraryName, env);
    return env;
  }
  private static Environment only(Environment importSet, Value identifiers) throws SchemeException {
    Set<Identifier> ids = new HashSet<Identifier>();
    while (!identifiers.isNull()) {
      if (!identifiers.isPair()) throw new SchemeException("Invalid identifier list", identifiers);
      Pair pair = (Pair) identifiers;
      if (!pair.getCar().isIdentifier()) throw new SchemeException("Invalid identifier list", identifiers);
      ids.add((Identifier) pair.getCar());
      identifiers = pair.getCdr();
    }
    Map<Identifier, Value> newValues = new HashMap<Identifier, Value>();
    for (Identifier id: ids) {
      Value value = importSet.values.get(id);
      if (value == null) throw new SchemeException("Import set does not contain identifier", id);
      newValues.put(id, value);
    }
    importSet.values = newValues;
    return importSet;
  }
  private static Environment except(Environment importSet, Value identifiers) throws SchemeException {
    Set<Identifier> ids = new HashSet<Identifier>();
    while (!identifiers.isNull()) {
      if (!identifiers.isPair()) throw new SchemeException("Invalid identifier list", identifiers);
      Pair pair = (Pair) identifiers;
      if (!pair.getCar().isIdentifier()) throw new SchemeException("Invalid identifier list", identifiers);
      ids.add((Identifier) pair.getCar());
      identifiers = pair.getCdr();
    }
    for (Identifier id: ids) {
      if (!importSet.contains(id)) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Import set does not contain identifier " + id)));
      importSet.values.remove(id);
    }
    return importSet;
  }
  private static Environment prefix(Environment importSet, Identifier prefix) {
    Map<Identifier, Value> newValues = new HashMap<Identifier, Value>();
    for (Identifier id: importSet.values.keySet()) {
      newValues.put(new Identifier(prefix + id.getIdentifier()), importSet.values.get(id));
    }
    importSet.values = newValues;
    return importSet;
  }
  private static Environment rename(Environment importSet, Value renamings) throws SchemeException {
    while (!renamings.isNull()) {
      if (!renamings.isPair()) throw new SchemeException("Invalid renamings list", renamings);
      Pair pair = (Pair) renamings;
      renamings = pair.getCdr();
      if (!pair.getCar().isPair()) throw new SchemeException("Invalid renamings list", renamings);
      pair = (Pair) pair.getCar();
      if (!pair.getCar().isIdentifier()) throw new SchemeException("Invalid renamings list", renamings);
      Identifier from = (Identifier) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException("Invalid renamings list", renamings);
      pair = (Pair) pair.getCdr();
      if (!pair.getCar().isIdentifier()) throw new SchemeException("Invalid renamings list", renamings);
      Identifier to = (Identifier) pair.getCar();
      if (!pair.getCdr().isNull()) throw new SchemeException("Invalid renamings list", renamings);
      
      Value value = importSet.values.get(from);
      if (value == null) throw new SchemeException("Import set does not contain identifier", from);
      if (importSet.values.containsKey(to)) throw new SchemeException("Import set already contains identifier", to);
      importSet.values.remove(from);
      importSet.values.put(to, value);
    }
    return importSet;
  }
  protected void importImportSet(Environment importSet) throws SchemeException {
    // TODO last bit of 5.2
    for (Identifier id: importSet.values.keySet()) {
      if (getContext() != Context.REPL && values.containsKey(id) ) throw new SchemeException("Environment already contains identifier", id);
      values.put(id, importSet.values.get(id));
    }
  }
  public void importLibrary(Library library) throws SchemeException {
    importLibrary(library.getExports());
  }
  public void importLibrary(Map<Identifier, Value> exports) throws SchemeException {
    for (Identifier id: exports.keySet()) {
      if (getContext() != Context.REPL && values.containsKey(id)) {
        throw new SchemeException("Environment already contains identifier", id);
      }
      values.put(id, exports.get(id));
    }
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
  
  public static Environment makeEnvironment(Value listOfImports) throws SchemeException {
    Environment result = new Environment(null);
    while (!listOfImports.isNull()) {
      if (!listOfImports.isPair()) throw new SchemeException("Malformed import set list", listOfImports);
      result.importImportSet(createImportSet(((Pair)listOfImports).getCar()));
    }
    return result;
  }
  
  public static Environment nullEnvironment(int version) throws SchemeException {
    Environment result = new Environment(null);
    switch (version) {
    case 5: Library.load(Library.makeName("scheme-impl", "null-environment", 5), result); break;
    case 7: Library.load(Library.makeName("scheme-impl", "null-environment", 7), result); break;
    default:
      throw new SchemeException("Only versions 5 and 7 are supported for null-environment", new LongInteger(version, true));
    }
    return result;
  }
  public static Environment schemeReportEnvironment(int version) throws SchemeException {
    Environment result = new Environment(null);
    switch (version) {
    case 5: Library.load(Library.makeName("scheme", "r5rs"), result); break;
    case 7: Library.load(Library.makeName("scheme-impl", "scheme-report-environment", 7), result); break;
    default:
      throw new SchemeException("Only versions 5 and 7 are supported for scheme-report-environment", new LongInteger(version, true));
    }
    return result;
  }
  public static Environment interactionEnvironment() throws SchemeException {
    Environment result = new Environment(null, Context.REPL);
    Library.load(Library.makeName("scheme-impl", "interaction-environment"), result);
    return result;
  }
  
  @Override
  public int hashCode() {
    int result = 0;
    if (parent != null) result = parent.hashCode();
    return values.hashCode() ^ result; // TODO more?
  }
  @Override
  public java.lang.String toString() {
    java.lang.String result = values.toString();
    if (parent != null) result +=  " -> " + parent.toString();
    return result;
  }
  public Object vals() {
    return values;
  }
  public DynamicWind getDynamicWind() {
    return dynamicWind;
  }
  public ErrorHandler getErrorHandler() {
    return errorHandler;
  }
  public void setDynamicWind(se.pp.forsberg.scheme.DynamicWind dynamicWind) {
    this.dynamicWind = dynamicWind;
  }
  public void addErrorHandler(Procedure handler, Op op) {
    this.errorHandler = new ErrorHandler(this.errorHandler, handler, op);
  }
}
