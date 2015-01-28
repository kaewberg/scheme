package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Value;

public class Token { 
  static public enum Type {
    LEFT_PAREN, RIGHT_PAREN, BEGIN_VECTOR, BEGIN_BYTEVECTOR, QUOTE, QUASI_QUOTE, UNQUOTE, UNQUOTE_SPLICING, DOT,
    VALUE, EOF
  }
  public static Token LEFT_PAREN = new Token(Type.LEFT_PAREN);
  public static Token RIGHT_PAREN = new Token(Type.RIGHT_PAREN);
  public static Token BEGIN_VECTOR = new Token(Type.BEGIN_VECTOR);
  public static Token BEGIN_BYTEVECTOR = new Token(Type.BEGIN_BYTEVECTOR);
  public static Token QUOTE = new Token(Type.QUOTE);
  public static Token QUASI_QUOTE = new Token(Type.QUASI_QUOTE);
  public static Token UNQUOTE = new Token(Type.UNQUOTE);
  public static Token UNQUOTE_SPLICING = new Token(Type.UNQUOTE_SPLICING);
  public static Token DOT = new Token(Type.DOT);
  public static Token EOF = new Token(Type.EOF);
  
  private final Type type;
  private final Value value;
  public Token(Value value) {
    this.type = Type.VALUE;
    this.value = value;
  }
  private Token(Type type) {
    this.type = type;
    this.value = null;
  }
  
  public Type getType() { return type; }
  public Value getValue() { return value; }
}
