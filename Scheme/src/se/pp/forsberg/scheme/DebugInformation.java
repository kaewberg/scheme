package se.pp.forsberg.scheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.pp.forsberg.scheme.values.Value;

/**
 * Information useful for debugging, and for Eclipse.
 * It is generated by Parser. 
 * @author K287750
 *
 */
public class DebugInformation {

  public class Node {
    Value value;
    Node parent;
    List<Node> children = new ArrayList<Node>();
    int offset, length;
    SyntaxErrorException syntaxError;
    public Node(Value value, Node parent, int offset, int length, SyntaxErrorException syntaxError, List<Node> children) {
      this.value = value;
      this.parent = parent;
      this.children.addAll(children);
      this.offset = offset;
      this.length = length;
      this.syntaxError = syntaxError;
    }
    public Node(Value value, Node parent, int offset, int length, SyntaxErrorException syntaxError, Node... children) {
      this(value, parent, offset, length, syntaxError, Arrays.asList(children));
    }
    public Node(Value value, Node parent, int offset, int length) {
      this(value, parent, offset, length, null);
    }
    public Value getValue() { return value; }
    public Node getParent() { return parent; }
    public List<Node> getChildren() { return children; }
    public int getOffset() { return offset; }
    public int getLength() { return length; }
    public boolean isSyntaxError() { return syntaxError != null; }
    public void setOffset(int offset) { this.offset = offset; }
    public void setLength(int length) { this.length = length; }
    public void setValue(Value value) { this.value = value; }
    public void setSyntaxError(SyntaxErrorException syntaxError) { this.syntaxError = syntaxError; }
    
    @Override
    public String toString() {
      if (this == root) return "<root>";
      String clazz = value.getClass().getName();
      int i = clazz.lastIndexOf('.');
      if (i >= 0) clazz = clazz.substring(i+1);
      return clazz + " " + value;
    }
    public Node add(Value value, int offset, int length, SyntaxErrorException syntaxError) {
      Node result = new Node(value, this, offset, length, syntaxError);
      children.add(result);
      return result;
    }
    public Node add(Value value, int offset, int length) {
      return add(value, offset, length, null);
    }
    public Node add() {
      return add(null, 0, 0);
    }
    public Node getLastChild() {
      if (children.size() == 0) return null;
      return children.get(children.size()-1);
    }
    public Node getFirstChild() {
      if (children.size() == 0) return null;
      return children.get(0);
    }
    public SyntaxErrorException getSyntaxError() {
      return syntaxError;
    }
  }
  Node root;

  public enum AtmosphereType {
    COMMENT,
    DIRECTIVE,
    WHITESPACE;
  }
  public class Atmosphere {
    int offset, length;
    AtmosphereType type;
    java.lang.String value;
    public Atmosphere(AtmosphereType type, java.lang.String value, int offset, int length) {
      this.type = type;
      this.value = value;
      this.offset = offset;
      this.length = length;
    }
    public AtmosphereType getType() { return type; }
    public java.lang.String getValue() { return value; }
    public int getOffset() { return offset; }
    public int getLength() { return length; }
  }
  
  public DebugInformation() {
    clear();
  }
  
  public Node getRoot() { return root; }
  public void clear() {
    root = new Node(null, null, 0, 0);
    atmosphere = new ArrayList<Atmosphere>();
  }
  private List<Atmosphere> atmosphere;
  
  public void addAtmosphere(AtmosphereType type, java.lang.String value, int offset, int length) {
    atmosphere.add(new Atmosphere(type, value, offset, length));
  }
  public List<Atmosphere> getAtmosphere() {
    return atmosphere;
  }
  
}
