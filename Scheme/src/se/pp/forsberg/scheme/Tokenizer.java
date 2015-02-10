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
import se.pp.forsberg.scheme.values.errors.ReadError;
import se.pp.forsberg.scheme.values.numbers.Number;

public class Tokenizer {
  
  private PushbackReader reader;
  private final Parser parser;
  private Token pushedBack = null;
  private boolean foldCase = false;
  private int offset = 0;
  private boolean commentsReturned;
  
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
    i = read(); if (i < 0) return new Token(Token.Type.EOF, start, 0);
    c = (char) i;
    switch (c) {
    case '(': return new Token(Token.Type.LEFT_PAREN, start, 1);
    case ')': return new Token(Token.Type.RIGHT_PAREN, start, 1);
    case '\'': return new Token(Token.Type.QUOTE, start, 1);
    case '`': return new Token(Token.Type.QUASI_QUOTE, start, 1);
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
      Number result = Number.parse(java.lang.Character.toString(c) + readUntilDelimiter());
      return new Token(result, start, offset-start);
    }
    Value result = Identifier.parse(java.lang.Character.toString(c) + readUntilDelimiter(), foldCase);
    return new Token(result, start, offset-start);
  }
  // After ,
  protected Token parseUnquote() throws IOException {
    int start = offset - 1;
    int i = read(); if (i < 0) return new Token(Token.Type.UNQUOTE, start, 1);
    char c = (char) i;
    if (c == '@') return new Token(Token.Type.UNQUOTE_SPLICING, start, 2);
    unread(i);
    return new Token(Token.Type.UNQUOTE, start, 1);
  }
  // After .
  // Can be number or identifier
  protected Token parseDot() throws IOException {
    int start = offset - 1;
    int i = read(); if (i < 0) return new Token(Token.Type.DOT, start, 1);
    char c = (char) i;
    if (!isDotSubsequent(c)) {
      if (isDigit(c)) {
        Number result = Number.parse("." + c + readUntilDelimiter());
        return new Token(result, start, offset-start);
      }
      unread(i);
      return new Token(Token.Type.DOT, start, 1);
    }
    Value result = Identifier.parse("." + c + readUntilDelimiter(), foldCase);
    return new Token(result, start, offset - start);
  }
  // After +-
  // Can be number or identifier
  protected Token parseSign(char c) throws IOException {
    int start = offset - 1;
    // Tricky business,
    // +j is an identifier, +i is a number as is +inf.0
    // +.a is an identifier +.5 is a number
    int i;
    char sign = c;
    //java.lang.StringBuffer identifier = new StringBuffer();
    //identifier.append(c);
    i = read(); if (i < 0) {
      Value result = Identifier.parse(java.lang.Character.toString(sign), foldCase);
      return new Token(result, start, offset - start);
    }
    c = (char) i;
    if (isDelimiter(c)) {
      unread(i);
      return new Token(Identifier.parse(java.lang.Character.toString(sign), foldCase), start, 1);
    }
    java.lang.String token = java.lang.Character.toString(sign) + c + readUntilDelimiter(); 
    if (c == '.') {
      // +.
      if (!isDotSubsequent(token.charAt(2))) {
        return new Token(Number.parse(token), start, offset - start);
      }
      return new Token(Identifier.parse(token, foldCase), start, offset - start);
    }
    if (!isSignSubsequent(c)) {
      return new Token(Number.parse(token), start, offset - start);
    }
    return new Token(Identifier.parse(token, foldCase), start, offset - start);
  }
  // After #
  protected Token parseOctothorpe() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    // Tricky, several things start with #
    // #t #true #f #false
    // #\x #\xFFFE #\t #\tab
    // #( #u8(
    //int hash = '#';
    int i = read(); if (i < 0) throw new SyntaxErrorException("Expected boolean, character, number or vector");
    char c = (char) i;
    if (c == '(') return new Token(Token.Type.BEGIN_VECTOR, start, 2);
    if (c >= '0' && c <= '9') {
      // label 
      int x = 0;
      while (c >= '0' && c <= '9') {
        x = x * 10 + (c - '0');
        i = read(); if (i < 0) throw new SyntaxErrorException("Expected label");
        c = (char) i;
      }
      switch (c) {
      case '=': return new Token(new Label(x, false), start, offset - start);
      case '#': return new Token(new Label(x, true), start, offset - start);
      default: throw new SyntaxErrorException("Expected label");
      }
    }
    if (c == 'u' || c == 'U') {
      i = read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector");
      c = (char) i; if (c != '8')  throw new SyntaxErrorException("Expected bytevector");
      i = read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector");
      c = (char) i; if (c != '(')  throw new SyntaxErrorException("Expected bytevector");
      return new Token(Token.Type.BEGIN_BYTEVECTOR, start, 4);
    }
    if ("tTfF".indexOf(c) >= 0) {
      Value result = Boolean.parse("#" + c + readUntilDelimiter());
      return new Token(result, start, offset - start);
    }
    if (c == '\\') {
      Value result = Character.parse("#\\" + readUntilDelimiter());
      return new Token(result, start, offset - start);
    }
    if ("eEiIbBoOdDxX".indexOf(c) >= 0) {
      Value result = Number.parse("#" + c + readUntilDelimiter());
      return new Token(result, start, offset - start);
    }
    throw new SyntaxErrorException("Expected boolean, character, number or vector");
  }
  // After "
  protected Token parseString() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    java.lang.StringBuffer string = new StringBuffer();
    int i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
    char c = (char) i;
    string.append('"');
    while (c != '"') {
      string.append(c);
      if (c == '\\') {
        i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
        c = (char) i;
        string.append(c);
      }
      i = read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
      c = (char) i;
    }
    string.append('"');
    Value result = String.parse(string.toString());
    return new Token(result, start, offset - start);
  }
  // After |
  protected Token parseVerticalLineIdentifier() throws IOException, SyntaxErrorException {
    int start = offset - 1;
    int i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
    char c = (char) i;
    StringBuffer identifier = new StringBuffer();
    identifier.append('|');
    while (c != '|') {
      identifier.append(c);
      if (i == '\\') {
        i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
        c = (char) i;
        identifier.append(c);
      }
      i = read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
      c = (char) i;
    }
    identifier.append('|');
    Value result = Identifier.parse(identifier.toString(), foldCase);
    return new Token(result, start, offset - start);
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
    if (result >= 0) offset++;
    return result;
  }
  private void unread(int i) throws IOException {
    reader.unread(i);
    offset--;
  }

  Token skipIntertokenSpace() throws IOException {
    int start = offset;
    int i;
    char c;
    while (true) {
      i = read(); if (i < 0) return null;
      c = (char) i;
      switch (c) {
      // <whitespace> -> <intraline whitespace> | <line ending>
      // <intraline whitespace> -> <space or tab>
      // <line ending> -> <newline> | <return> <newline> | <return>
      case ' ': case '\t': case '\r': case '\n': break;
      // <comment> -> ; <all subsequent characters up to a line ending>
      case ';':
        i = read(); if (i < 0) return new Token(Token.Type.COMMENT, start, 1);
        c = (char) i;
        while (c != '\r' && c != '\n') {
          i = read(); if (i < 0) return new Token(Token.Type.COMMENT, start, offset - start);
          c = (char) i;
        }
        if (c == '\r') {
          i = read(); if (i < 0) return new Token(Token.Type.COMMENT, start, offset - start);
          c = (char) i;
          if (c != '\n') {
            unread(i);
          }
        }
        if (commentsReturned)  return new Token(Token.Type.COMMENT, start, offset - start);
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
          int commentLevel = 1;
          while (commentLevel > 0) {
            i = read(); if (i < 0)  return new Token(Token.Type.COMMENT, start, offset - start);
            c = (char) i;
            if (c == '#') {
              int i2 = read(); if (i2 < 0) return new Token(Token.Type.COMMENT, start, offset - start);
              char c2 = (char) i2;
              if (c2 == '|') {
                commentLevel++;
              } else {
                unread(i2);
              }
            } else if (c == '|') {
              int i2 = read(); if (i2 < 0) return new Token(Token.Type.COMMENT, start, offset - start);
              char c2 = (char) i2;
              if (c2 == '#') {
                commentLevel--;
              } else {
                unread(i2);
              }
            }
          }
          if (commentsReturned)  return new Token(Token.Type.COMMENT, start, offset - start);
          break;
        case ';':
          // <comment> -> #; <intertoken space> <datum>
          parser.read();
          if (commentsReturned)  return new Token(Token.Type.COMMENT, start, offset - start);
          break;
        case '!':
          java.lang.String directive = readUntilDelimiter();
          if (directive.equals("fold-case")) {
              foldCase = true;
          } else if (directive.equals("no-fold-case")) {
            foldCase = false;
          } else {
            throw new SchemeException(new ReadError(new IllegalArgumentException("Unknown directive #! " +directive)));
          }
          if (commentsReturned) return new Token(Token.Type.DIRECTIVE, start, offset - start);
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

  public void setCommentsReturned(boolean b) {
    commentsReturned = b;
  }
  
}
