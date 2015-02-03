package se.pp.forsberg.scheme.builtinprocedures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.BuiltInProcedure;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Port;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.errors.FileError;
import se.pp.forsberg.scheme.values.errors.RuntimeError;
import se.pp.forsberg.scheme.values.macros.BuiltInKeyword;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Library {

  public Library() {
    addBuiltIns(true);
  }
  protected static class LibraryDefinition {
    private final Class<? extends Library> libraryClass;
    private final String resource;
    public LibraryDefinition(Class<? extends Library> libraryClass) { this.libraryClass = libraryClass; resource = null; }
    public LibraryDefinition(String resource) { this.libraryClass = null; this.resource = resource; }
    public LibraryDefinition(Class<? extends Library> libraryClass, String resource) { this.libraryClass = libraryClass; this.resource = resource; }
    public Class<? extends Library> getLibraryClass() {
      return libraryClass;
    }
    public String getResource() {
      return resource;
    }
    
  }
  
  private static Map<Value, LibraryDefinition> libraries = new HashMap<Value, LibraryDefinition>();
  static {
    libraries.put(BasicKeywords.getName(), new LibraryDefinition(BasicKeywords.class));
    libraries.put(DerivedExpressions.getName(), new LibraryDefinition(DerivedExpressions.class, "derived_expressions.scheme"));
    libraries.put(EquivalencePredicates.getName(), new LibraryDefinition(EquivalencePredicates.class));
    libraries.put(Numbers.getName(), new LibraryDefinition(Numbers.class));
    libraries.put(Booleans.getName(), new LibraryDefinition(Booleans.class));
    libraries.put(PairsAndLists.getName(), new LibraryDefinition(PairsAndLists.class, "pairs_and_lists.scheme"));
    libraries.put(makeName("scheme", "cxr"), new LibraryDefinition("cxr.scheme"));
    libraries.put(Complex.getName(), new LibraryDefinition(Complex.class));
    libraries.put(Characters.getName(), new LibraryDefinition(Characters.class, "characters.scheme"));
    libraries.put(Symbols.getName(), new LibraryDefinition(Symbols.class));
    libraries.put(Strings.getName(), new LibraryDefinition(Strings.class, "strings.scheme"));
    libraries.put(Vectors.getName(), new LibraryDefinition(Vectors.class));
    libraries.put(ByteVectors.getName(), new LibraryDefinition(ByteVectors.class));
    libraries.put(Inexact.getName(), new LibraryDefinition(Inexact.class));
    libraries.put(Control.getName(), new LibraryDefinition(Control.class));
    libraries.put(Exceptions.getName(), new LibraryDefinition(Exceptions.class));
    libraries.put(EnvironmentsAndEvaluation.getName(), new LibraryDefinition(EnvironmentsAndEvaluation.class));
    libraries.put(Ports.getName(), new LibraryDefinition(Ports.class));
    
    libraries.put(makeName("scheme-impl", "scheme-report-environment", 7), new LibraryDefinition("scheme_report_environment_7.scheme"));
  }
  
  private Value name;
  private Environment env = new Environment(null, Environment.Context.LIBRARY);
  private Map<Identifier, Identifier> exportSpecs = new HashMap<Identifier, Identifier>();
  
  public Value getLibraryName() { return name; }
  private void setName(Value name) {
    this.name = name;
  }
  public Environment getEnvironment() { return env; }
  protected void addExportSpec(Identifier identifier, Identifier exportAs) {
    exportSpecs.put(identifier, exportAs);
  }
  
  public static Pair makeName(String string, String string2) {
   return new Pair(new Identifier(string), new Pair(new Identifier(string2), Nil.NIL));
  }
  public static Pair makeName(String string, String string2, int v) {
    return new Pair(new Identifier(string), new Pair(new Identifier(string2), new Pair(new LongInteger(v,true), Nil.NIL)));
   }
  
  protected void checkArguments(BuiltInProcedure proc, Value arguments, int n) {
    checkArguments(proc, arguments, n, n, Value.class);
  }
  protected void checkArguments(BuiltInProcedure proc, Value arguments, int n, Class<?> theClass) {
    checkArguments(proc, arguments, n, n,theClass);
  }
  protected void checkArguments(BuiltInProcedure proc, Value arguments, int min, int max) {
    checkArguments(proc, arguments, min, max, Value.class);
  }
  protected void checkArguments(BuiltInProcedure proc, Value arguments, Class<?>... classList) {
    checkArguments(proc, arguments, classList.length, classList.length, classList);
  }
  protected void checkArguments(BuiltInProcedure proc, Value arguments, int min, int max, Class<?>... classList) {
    int i = 0;
    Value oldArguments = arguments;
    while (!arguments.isNull()) {
      if (!arguments.isPair()) {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Malformed call of built-in procedure " + proc.getName() + ", arguments do not form a list " + oldArguments)));
      }
      Class<?> theClass = (classList.length > 1)? classList[i] : classList[0];
      if (!theClass.isAssignableFrom(((Pair)arguments).getCar().getClass())) {
        throw new SchemeException(new RuntimeError(new IllegalArgumentException("Illegal argument type in call to " + proc.getName() + ", expected " + theClass.getName() + " got " + ((Pair)arguments).getCar().getClass().getName() + " " + ((Pair)arguments).getCar())));
      }
      i++;
      arguments = ((Pair)arguments).getCdr();
    }
    if (i < min) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected at least " + min + " arguments in call to " + proc.getName() + ", got " + i + " " + oldArguments)));
    }
    if (i > max) {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Expected no more than " + max + " arguments in call to " + proc.getName() + ", got " + i + " " + oldArguments)));
    }
  }
  
  public void addBuiltIns(boolean export) {
    try {
      for (Class<?> c: getClass().getDeclaredClasses()) {
        if (BuiltInKeyword.class.isAssignableFrom(c)) {
          BuiltInKeyword keyword = (BuiltInKeyword) c.getConstructor(getClass()).newInstance(this);
          Identifier id = new Identifier(keyword.getKeyword());
          env.define(id, keyword);
          if (export) addExportSpec(id, id);
        }
        if (BuiltInProcedure.class.isAssignableFrom(c)) {
          BuiltInProcedure proc = (BuiltInProcedure) c.getConstructor(getClass(), Environment.class).newInstance(this, env);
          Identifier id = new Identifier(proc.getName());
          env.define(id, proc);
          if (export) addExportSpec(id, id);
        }
      }
    } catch (Exception x) {
      throw new SchemeException(new RuntimeError(x));
    }
  }
  public static void load(Pair libraryName, Environment env) {
    LibraryDefinition def = libraries.get(libraryName);
    if (def == null) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Unknown library " + libraryName)));
    if (def.getLibraryClass() != null) { 
      try {
        Library library = def.getLibraryClass().newInstance();
        library.addBuiltIns(false);
        if (def.getResource() != null) library.load(def.getResource());
        env.importLibrary(library);
      } catch (SchemeException e) {
        throw e;
      } catch (Exception e) {
        throw new SchemeException(new RuntimeError(e));
      }
    } else {
      Library library = loadResource(def.getResource());
      env.importLibrary(library);
    }
  }
  // For use with built in-libraries
  // Read and evaluate all statements from a resource 
  protected void load(java.lang.String resource) {
    InputStream stream = getClass().getResourceAsStream(resource);
    if (stream == null) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Missing resource " + resource)));
    load(new Port(stream));
  }
  
  public Map<Identifier, Value> getExports() {
    Map<Identifier, Value> result = new HashMap<Identifier, Value>();
    for (Identifier id: exportSpecs.keySet()) {
      Identifier exportAs = exportSpecs.get(id);
      Value value = env.lookup(id);
      if (value == null) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Undefined value " + id)));
      result.put(exportAs, value);
    }
    return result;
  }
  public void load(File file) {
    Port port;
    if (!file.isAbsolute()) {
      file = concatFile(env.getCurrentDirectory(), file);
    }
    try {
      port = new Port(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new SchemeException(new FileError(e));
    }
    load(port);
  }
  private File concatFile(File dir, File file) {
    List<java.lang.String> parts = new ArrayList<java.lang.String>();
    for (File fileDir = file; fileDir != null; fileDir = fileDir.getParentFile()) {
      parts.add(fileDir.getName());
    }
    File result = dir;
    for (int i = parts.size()-1; i >= 0; i--) {
      result = new File(result, parts.get(i));
    }
    return result;
  }
  protected void load(Port port) {
    Value value = port.read();
    port.close();
    if (!value.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid library, expected (define-library ...")));
    Pair pair = (Pair) value;
    if (!pair.getCar().eqv(new Identifier("define-library"))) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid library, expected (define-library ...")));
    if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid library, expected library name")));
    pair = (Pair) pair.getCdr();
    if (!pair.getCar().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid library, expected library name")));
    setName(pair.getCar());
    load(pair.getCdr());
  }
  private void load(Value declarations) {
    while (!declarations.isNull()) {
      if (!declarations.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid declaration " + declarations)));
      Pair pair = (Pair) declarations;
      Value declaration = pair.getCar();
      declarations = pair.getCdr();
      if (!declaration.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid declaration " + declaration)));
      parseDeclaration((Pair) declaration);
    }
  }

  private void parseDeclaration(Pair declaration) {
    if (declaration.getCar().eqv(new Identifier("export"))) {
      parseExport(declaration.getCdr());
    } else if (declaration.getCar().eqv(new Identifier("import"))) {
      parseImport(declaration);
    } else if (declaration.getCar().eqv(new Identifier("begin"))) {
      parseBegin(declaration.getCdr());
    } else if (declaration.getCar().eqv(new Identifier("include"))) {
      parseInclude(declaration.getCdr());
    } else if (declaration.getCar().eqv(new Identifier("include-library-declarations"))) {
      parseIncludeLibraryDeclarations(declaration.getCdr());
    } else if (declaration.getCar().eqv(new Identifier("cond-expand"))) {
      parseCondExpand(declaration.getCdr());
    } else throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid declaration " + declaration)));
  }

  private void parseCondExpand(Value cdr) {
    throw new SchemeException(new RuntimeError(new IllegalArgumentException("TODO cond-expand")));
  }

  private void parseIncludeLibraryDeclarations(Value cdr) {
    throw new SchemeException(new RuntimeError(new IllegalArgumentException("TODO include-library-declarations")));
    
  }

  private void parseInclude(Value cdr) {
    throw new SchemeException(new RuntimeError(new IllegalArgumentException("TODO include")));
  }

  private void parseBegin(Value begin) {
    while (!begin.isNull()) {
      if (!begin.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid begin body " + begin)));
      Pair pair = (Pair) begin;
      begin = pair.getCdr();
      pair.getCar().eval(env);
    }
  }

  private void parseImport(Value importStatement) {
    getEnvironment().importLibrary(importStatement);
  }

  private void parseExport(Value exportSpecs) {
    while (!exportSpecs.isNull()) {
      if (!exportSpecs.isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpecs)));
      Pair pair = (Pair) exportSpecs;
      exportSpecs = pair.getCdr();
      parseExportSpec(pair.getCar()); 
    }
  }

  private void parseExportSpec(Value exportSpec) {
    if (exportSpec.isIdentifier()) {
      Identifier id = (Identifier) exportSpec;
      addExportSpec(id, id);
    } else if (exportSpec.isPair()) {
      Pair pair = (Pair) exportSpec;
      if (!pair.getCar().isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpec)));
      Identifier id = (Identifier) pair.getCar();
      if (!pair.getCdr().isPair()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpec)));
      pair = (Pair) pair.getCdr();
      if (!pair.getCdr().isNull()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpec)));
      if (!pair.getCar().isIdentifier()) throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpec)));
      Identifier exportAs = (Identifier) pair.getCar();
      addExportSpec(id, exportAs);
    } else {
      throw new SchemeException(new RuntimeError(new IllegalArgumentException("Invalid export spec " + exportSpec)));
    }
  }
  public static Library loadResource(String resource) {
    Library library = new Library();
    library.load(resource);
    return library;
  }
}
