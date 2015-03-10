package se.pp.forsberg.scheme;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Label;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.Number;

public class Tokenizer {
  
  private PushbackReader reader;
  private final Parser parser;
  private Token pushedBack = null;
  private boolean foldCase = false;
  private int offset = 0;
  private int line, column, lastColumn;
  // Eclipse mode
  // 1) Return tokens for comments and directives
  // 2) Convert SchemeExceptions from recursive call to parser.read() back to SyntaxErrorExceptions
  private boolean eclipseMode;
  
  public Tokenizer(Parser parser, Reader reader) {
    this.parser = parser;
    this.reader = new PushbackReader(reader, 3);
  }

  public void pushback(Token token) { pushedBack = token; }
  public Token readToken() throws IOException, SyntaxErrorException {
    if (pushedBack != null) {
      Token result = pushedBack;
      pushedBack = null;
      return result;
    }
    Token ws = skipIntertokenSpace();
    if (ws != null) return ws;
    int i;
    char c;
    
    int start = offset;
    int line = this.line;
    int column = this.column;
    i = read(); if (i < 0) return new Token(Token.Type.EOF, start, 0, line, column);
    c = (char) i;
    switch (c) {
    case '(': return new Token(Token.Type.LEFT_PAREN, start, 1, line, column);
    case ')': return new Token(Token.Type.RIGHT_PAREN, start, 1, line, column);
    case '\'': return new Token(Token.Type.QUOTE, start, 1, line, column);
    case '`': return new Token(Token.Type.QUASI_QUOTE, start, 1, line, column);
    case ',': return parseUnquote();
    case '.': return parseDot();
    case '+':
    case '-': return parseSign(c);
    case '"': return parseString();
    case '|': return parseVerticalLineIdentifier();
    case '#': return parseOctothorpe();
    }
    // Not boolean, character, java.lang.String or simple token
    // Can be number or identifier
    if (!isInitial(c)) {
      java.lang.String rest = java.lang.Character.toString(c) + readUntilDelimiter();
      try {
        Number result = Number.parse(rest);
        return new Token(result, start, offset-start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(LongInteger.ZERO, start, offset - start, line, column));
      }
    }
    java.lang.String rest = java.lang.Character.toString(c) + readUntilDelimiter();
    try {
      Value result = Identifier.parse(rest, foldCase);
      return new Token(result, start, offset-start, line, column);
    } catch (Exception x) {
      throw new SyntaxErrorException(x.getMessage(), new Token(new Identifier(rest), start, offset - start, line, column));
    }
  }
  // After ,
  protected Token parseUnquote() throws IOException {
    int line = this.line;
    int column = this.column;
    int start = offset - 1;
    int i = read(); if (i < 0) return new Token(Token.Type.UNQUOTE, start, 1, line, column);
    char c = (char) i;
    if (c == '@') return new Token(Token.Type.UNQUOTE_SPLICING, start, 2, line, column);
    unread(i);
    return new Token(Token.Type.UNQUOTE, start, 1, line, column);
  }
  // After .
  // Can be number or identifier
  protected Token parseDot() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int line = this.line;
    int column = this.column;
    int i = read(); if (i < 0) return new Token(Token.Type.DOT, start, 1, line, column);
    char c = (char) i;
    if (!isDotSubsequent(c)) {
      if (isDigit(c)) {
        java.lang.String rest = "." + c + readUntilDelimiter();
        try {
          Number result = Number.parse(rest);
          return new Token(result, start, offset-start, line, column);
        } catch (Exception x) {
          throw new SyntaxErrorException(x.getMessage(), new Token(LongInteger.ZERO, start, offset - start, line, column));
        }
      }
      unread(i);
      return new Token(Token.Type.DOT, start, 1, line, column);
    }
    java.lang.String rest = "." + c + readUntilDelimiter();
    try {
      Value result = Identifier.parse(rest, foldCase);
      return new Token(result, start, offset - start, line, column);
    } catch (Exception x) {
      throw new SyntaxErrorException(x.getMessage(), new Token(new Identifier(rest), start, offset - start, line, column));
    }
  }
  // After +-
  // Can be number or identifier
  protected Token parseSign(char c) throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int line = this.line;
    int column = this.column;
    // Tricky business,
    // +j is an identifier, +i is a number as is +inf.0
    // +.a is an identifier +.5 is a number
    int i;
    char sign = c;
    //java.lang.StringBuffer identifier = new StringBuffer();
    //identifier.append(c);
    i = read(); if (i < 0) {
      Value result = Identifier.parse(java.lang.Character.toString(sign), foldCase);
      return new Token(result, start, offset - start, line, column);
    }
    c = (char) i;
    if (isDelimiter(c)) {
      unread(i);
      return new Token(Identifier.parse(java.lang.Character.toString(sign), foldCase), start, 1, line, column);
    }
    java.lang.String token = java.lang.Character.toString(sign) + c + readUntilDelimiter(); 
    if (c == '.') {
      // +.
      if (!isDotSubsequent(token.charAt(2))) {
        try {
          return new Token(Number.parse(token), start, offset - start, line, column);
        } catch (Exception x) {
          throw new SyntaxErrorException(x.getMessage(), new Token(LongInteger.ZERO, start, offset - start, line, column));
        }
      }
      try {
        return new Token(Identifier.parse(token, foldCase), start, offset - start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(new Identifier(token), start, offset - start, line, column));
      }
    }
    if (!isSignSubsequent(c)) {
      try {
        return new Token(Number.parse(token), start, offset - start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(LongInteger.ZERO, start, offset - start, line, column));
      }
    }
    try {
      return new Token(Identifier.parse(token, foldCase), start, offset - start, line, column);
    } catch (Exception x) {
      throw new SyntaxErrorException(x.getMessage(), new Token(new Identifier(token), start, offset - start, line, column));
    }
  }
  // After #
  protected Token parseOctothorpe() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int line = this.line;
    int column = this.column;
    // Tricky, several things start with #
    // #t #true #f #false
    // #\x #\xFFFE #\t #\tab
    // #( #u8(
    //int hash = '#';
    int i = read(); if (i < 0) throw new SyntaxErrorException("Expected boolean, character, number or vector", new Token(new Identifier("#"), start, 1, line, column));
    char c = (char) i;
    if (c == '(') return new Token(Token.Type.BEGIN_VECTOR, start, 2, line, column);
    if (c >= '0' && c <= '9') {
      // label 
      int x = 0;
      while (c >= '0' && c <= '9') {
        x = x * 10 + (c - '0');
        i = read(); if (i < 0) throw new SyntaxErrorException("Expected label", new Token(new Label(x, true), start, offset-start, line, column));
        c = (char) i;
      }
      switch (c) {
      case '=': return new Token(new Label(x, false), start, offset - start, line, column);
      case '#': return new Token(new Label(x, true), start, offset - start, line, column);
      default: throw new SyntaxErrorException("Expected label", new Token(new Label(x, true), start, offset - start - 1, line, column));
      }
    }
    if (c == 'u' || c == 'U') {
      i = read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector", new Token(Token.Type.BEGIN_BYTEVECTOR, start, offset - start, line, column));
      c = (char) i; if (c != '8')  throw new SyntaxErrorException("Expected bytevector", new Token(Token.Type.BEGIN_BYTEVECTOR, start, offset - start, line, column));
      i = read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector", new Token(Token.Type.BEGIN_BYTEVECTOR, start, offset - start, line, column));
      c = (char) i; if (c != '(')  throw new SyntaxErrorException("Expected bytevector", new Token(Token.Type.BEGIN_BYTEVECTOR, start, offset - start, line, column));
      return new Token(Token.Type.BEGIN_BYTEVECTOR, start, 4, line, column);
    }
    if ("tTfF".indexOf(c) >= 0) {
      java.lang.String rest = "#" + c + readUntilDelimiter();
      try {
        Value result = Boolean.parse(rest);
        return new Token(result, start, offset - start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(Boolean.FALSE, start, offset - start, line, column));
      } 
    }
    if (c == '\\') {
      i = read(); if (i < 0) throw new SyntaxErrorException("Expected character", new Token(new Character('a'), start, offset - start, line, column));
      c = (char) i;
      if (isDelimiter(c)) {
        return new Token(new Character(c), start, offset - start, line, column);
      }
      java.lang.String rest = "#\\" + c + readUntilDelimiter();
      try {
        Value result = Character.parse(rest);
        return new Token(result, start, offset - start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(new Character('a'), start, offset - start, line, column));
      }
    }
    if ("eEiIbBoOdDxX".indexOf(c) >= 0) {
      java.lang.String rest = "#" + c + readUntilDelimiter();
      try {
        Value result = Number.parse(rest);
        return new Token(result, start, offset - start, line, column);
      } catch (Exception x) {
        throw new SyntaxErrorException(x.getMessage(), new Token(LongInteger.ZERO, start, offset - start, line, column));
      }
    }
    throw new SyntaxErrorException("Expected boolean, character, number or vector", new Token(new Identifier("#" + c), start, 2, line, column));
  }
  // After "
  protected Token parseString() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int line = this.line;
    int column = this.column;
    java.lang.StringBuffer string = new StringBuffer();
    int i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal", new Token(new String(string), start, offset - start, line, column));
    char c = (char) i;
    string.append('"');
    while (c != '"') {
      string.append(c);
      if (c == '\\') {
        i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal", new Token(new String(string), start, offset - start, line, column));
        c = (char) i;
        string.append(c);
      }
      i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal", new Token(new String(string), start, offset - start, line, column));
      c = (char) i;
    }
    string.append('"');
    try {
      Value result = String.parse(string.toString());
      return new Token(result, start, offset - start, line, column);
    } catch (Exception x) {
      throw new SyntaxErrorException(x.getMessage(), new Token(new String(string), start, offset - start, line, column)); 
    }
  }
  // After |
  protected Token parseVerticalLineIdentifier() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int line = this.line;
    int column = this.column;
    int i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier", new Token(new Identifier(""), start, start - offset, line, column));
    char c = (char) i;
    StringBuffer identifier = new StringBuffer();
    identifier.append('|');
    while (c != '|') {
      identifier.append(c);
      if (i == '\\') {
        i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier", new Token(new Identifier(identifier), start, start - offset, line, column));
        c = (char) i;
        identifier.append(c);
      }
      i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier", new Token(new Identifier(identifier), start, start - offset, line, column));
      c = (char) i;
    }
    identifier.append('|');
    try {
      Value result = Identifier.parse(identifier.toString(), foldCase);
      return new Token(result, start, offset - start, line, column);
    } catch (Exception x) {
      throw new SyntaxErrorException(x.getMessage(), new Token(new Identifier(identifier), start, start - offset, line, column));
    }
  }
 
  protected static boolean isDelimiter(char c) {
    return " \t\r\n()|\";".indexOf(c) >= 0;
  }
  protected static boolean isInitial(char c) {
    return isLetter(c) || isSpecialInitial(c);
  }
  protected static boolean isLetter(char c) {
    // TODO Unicode
    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'; 
  }
  protected static boolean isDigit(char c) {
    return c >= '0' && c <= '9'; 
  }
  protected static boolean isSpecialInitial(char c) {
    return "!$%&*/:<=>?^_~".indexOf(c) >= 0;
  }
  protected static boolean isExplicitSign(char c) {
    return c == '+' || c == '-';
  }
  protected static boolean isSpecialSubsequent(char c) {
    return isExplicitSign(c) || c == '.' || c == '@';
  }
  protected static boolean isDotSubsequent(char c) {
    return isSignSubsequent(c) || c == '.';
  }
  protected static boolean isSignSubsequent(char c) {
    return isInitial(c) || isExplicitSign(c) || c == '@';
  }
  
  protected java.lang.String readUntilDelimiter() throws IOException {
    StringBuffer result = new StringBuffer();
    int i = read(); if (i < 0) return result.toString();
    char c = (char) i;
    while (!isDelimiter(c)) {
      result.append(c);
      i = read(); if (i < 0) return result.toString();
      c = (char) i;
    }
    unread(i);
    return result.toString();
  }
  private int read() throws IOException {
    int result = reader.read();
    if (result >= 0) {
      offset++;
      column++;
      if (result == '\n') {
        lastColumn = column;
        column = 0;
        line++;
      }
    }
    return result;
  }
  private void unread(int i) throws IOException {
    reader.unread(i);
    offset--;
    column--;
    if (i == '\n') {
      column = lastColumn;
      line--;
    }
  }

  Token skipIntertokenSpace() throws IOException, SyntaxErrorException {
    int i;
    char c;
    while (true) {
      int start = offset;
      int line = this.line;
      int column = this.column;
      i = read(); if (i < 0) return null;
      c = (char) i;
      StringBuilder text;
      switch (c) {
      // <whitespace> -> <intraline whitespace> | <line ending>
      // <intraline whitespace> -> <space or tab>
      // <line ending> -> <newline> | <return> <newline> | <return>
      case ' ': case '\t': case '\r': case '\n': break;
      // <comment> -> ; <all subsequent characters up to a line ending>
      case ';':
        text = new StringBuilder();
        text.append(';');
        i = read(); if (i < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, 1, line, column) : null;
        c = (char) i;
        while (c != '\r' && c != '\n') {
          i = read(); if (i < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column) : null;
          c = (char) i;
          text.append(c);
        }
        if (c == '\r') {
          i = read(); if (i < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column) : null;
          c = (char) i;
          if (c != '\n') {
            unread(i);
          }
        }
        if (eclipseMode) return new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column);
        break;
      case '#':
        i = read(); if (i < 0) return null;
        c = (char) i;
        switch (c) {
        // <comment> -> <nested comment>
        // <nested comment> -> #| <comment text> <comment cont>* |#
        // <comment text> -> <character sequence not containing #| or |#>
        // <comment cont> -> <nested comment> <comment text>
        case '|':
          text = new StringBuilder();
          text.append("#|");
          int commentLevel = 1;
          while (commentLevel > 0) {
            i = read(); if (i < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column) : null;
            c = (char) i;
            text.append(c);
            if (c == '#') {
              int i2 = read(); if (i2 < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column) : null;
              char c2 = (char) i2;
              if (c2 == '|') {
                commentLevel++;
              } else {
                unread(i2);
              }
            } else if (c == '|') {
              int i2 = read(); if (i2 < 0) return eclipseMode? new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column) : null;
              char c2 = (char) i2;
              if (c2 == '#') {
                commentLevel--;
              } else {
                unread(i2);
              }
            }
          }
          if (eclipseMode) return new Token(Token.Type.COMMENT, text.toString(), start, offset - start, line, column);
          break;
        case ';':
          // <comment> -> #; <intertoken space> <datum>
          Value datum = null;
          try {
            datum = parser.read();
          } catch (SchemeException x) {
            rethrow(x);
          }
          if (eclipseMode) return new Token(Token.Type.COMMENT, "#; " + datum.toStringSafe(), start, offset - start, line, column);
          break;
        case '!':
          java.lang.String directive = readUntilDelimiter();
          if (directive.equals("fold-case")) {
            foldCase = true;
          } else if (directive.equals("no-fold-case")) {
            foldCase = false;
          } else {
            throw new SyntaxErrorException("Unknown directive #! " + directive, new Token(Token.Type.DIRECTIVE, start, offset-start, line, column));
          }
          if (eclipseMode) return new Token(Token.Type.DIRECTIVE, "#!" + directive, start, offset - start, line, column);
          break;
        default:
          unread(i);
          unread('#');
          return null;
        }
        break;
      default:
        unread(i);
        return null;
      }
    }
  }
  protected void rethrow(SchemeException x) throws SyntaxErrorException {
    //if (!eclipseMode) throw x;
    Value v = x.getError();
    if (v != null && v.isError()) {
      se.pp.forsberg.scheme.values.errors.Error e = (se.pp.forsberg.scheme.values.errors.Error) v;
      Throwable t = e.getThrowable();
      if (t != null && t instanceof SyntaxErrorException) {
        throw (SyntaxErrorException) t;
      }
    }
    throw new SyntaxErrorException("Unplaceable SchemeExeception " + x.getMessage(), new Token(Token.Type.EOF, 0, 0, 0, 0));
  }

  public void setEclipseMode(boolean b) {
    eclipseMode = b;
  }

  public boolean isEclipseMode() {
    return eclipseMode;
  }
  
}
