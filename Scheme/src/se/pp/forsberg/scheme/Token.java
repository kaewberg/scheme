package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Value;

public class Token { 
  static public enum Type {
    LEFT_PAREN, RIGHT_PAREN, BEGIN_VECTOR, BEGIN_BYTEVECTOR, QUOTE, QUASI_QUOTE, UNQUOTE, UNQUOTE_SPLICING, DOT,
    VALUE, EOF, COMMENT, DIRECTIVE
  }
//  public static Token LEFT_PAREN = new Token(Type.LEFT_PAREN);
//  public static Token RIGHT_PAREN = new Token(Type.RIGHT_PAREN);
//  public static Token BEGIN_VECTOR = new Token(Type.BEGIN_VECTOR);
//  public static Token BEGIN_BYTEVECTOR = new Token(Type.BEGIN_BYTEVECTOR);
//  public static Token QUOTE = new Token(Type.QUOTE);
//  public static Token QUASI_QUOTE = new Token(Type.QUASI_QUOTE);
//  public static Token UNQUOTE = new Token(Type.UNQUOTE);
//  public static Token UNQUOTE_SPLICING = new Token(Type.UNQUOTE_SPLICING);
//  public static Token DOT = new Token(Type.DOT);
//  public static Token EOF = new Token(Type.EOF);
//  
  private final Type type;
  private final Value value;
  private final int offset, length;
  public Token(Value value, int offset, int length) {
    this.type = Type.VALUE;
    this.value = value;
    this.offset = offset;
    this.length = length;
  }
  Token(Type type, int offset, int length) {
    this.type = type;
    this.value = null;
    this.offset = offset;
    this.length = length;
  }
  
  public Type getType() { return type; }
  public Value getValue() { return value; }
  public int getOffset() { return offset; }
  public int getLength() { return length; }
}
