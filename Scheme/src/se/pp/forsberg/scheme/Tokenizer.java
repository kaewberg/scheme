package se.pp.forsberg.scheme;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Label;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.errors.ReadError;
import se.pp.forsberg.scheme.values.numbers.Number;

public class Tokenizer {
  
  private PushbackReader reader;
  private final Parser parser;
  private Token pushedBack = null;
  boolean foldCase = false;
  
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
    skipIntertokenSpace();
    int i;
    char c;
    
    i = reader.read(); if (i < 0) return Token.EOF;
    c = (char) i;
    switch (c) {
    case '(': return Token.LEFT_PAREN;
    case ')': return Token.RIGHT_PAREN;
    case '\'': return Token.QUOTE;
    case '`': return Token.QUASI_QUOTE;
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
      return new Token(Number.parse(java.lang.Character.toString(c) + readUntilDelimiter()));
    }
    return new Token(Identifier.parse(java.lang.Character.toString(c) + readUntilDelimiter(), foldCase));
  }
  // After ,
  protected Token parseUnquote() throws IOException {
    int i = reader.read(); if (i < 0) return Token.UNQUOTE;
    char c = (char) i;
    if (c == '@') return Token.UNQUOTE_SPLICING;
    reader.unread(i);
    return Token.UNQUOTE;
  }
  // After .
  // Can be number or identifier
  protected Token parseDot() throws IOException {
    int i = reader.read(); if (i < 0) return Token.DOT;
    char c = (char) i;
    if (!isDotSubsequent(c)) {
      if (isDigit(c)) return new Token(Number.parse("." + c + readUntilDelimiter()));
      reader.unread(i);
      return Token.DOT;
    }
    return new Token(Identifier.parse("." + c + readUntilDelimiter(), foldCase));
  }
  // After +-
  // Can be number or identifier
  protected Token parseSign(char c) throws IOException {
    // Tricky business,
    // +j is an identifier, +i is a number as is +inf.0
    // +.a is an identifier +.5 is a number
    int i;
    char sign = c;
    //java.lang.StringBuffer identifier = new StringBuffer();
    //identifier.append(c);
    i = reader.read(); if (i < 0) return new Token(Identifier.parse(java.lang.Character.toString(sign), foldCase));
    c = (char) i;
    if (isDelimiter(c)) {
      reader.unread(i);
      return new Token(Identifier.parse(java.lang.Character.toString(sign), foldCase));
    }
    java.lang.String token = java.lang.Character.toString(sign) + c + readUntilDelimiter(); 
    if (c == '.') {
      // +.
      if (!isDotSubsequent(token.charAt(2))) return new Token(Number.parse(token));
      return new Token(Identifier.parse(token, foldCase));
    }
    if (!isSignSubsequent(c)) {
      return new Token(Number.parse(token));
    }
    return new Token(Identifier.parse(token, foldCase));
  }
  // After #
  protected Token parseOctothorpe() throws IOException, SyntaxErrorException {
    // Tricky, several things start with #
    // #t #true #f #false
    // #\x #\xFFFE #\t #\tab
    // #( #u8(
    //int hash = '#';
    int i = reader.read(); if (i < 0) throw new SyntaxErrorException("Expected boolean, character, number or vector");
    char c = (char) i;
    if (c == '(') return Token.BEGIN_VECTOR;
    if (c >= '0' && c <= '9') {
      // label 
      int x = 0;
      while (c >= '0' && c <= '9') {
        x = x * 10 + (c - '0');
        i = reader.read(); if (i < 0) throw new SyntaxErrorException("Expected label");
        c = (char) i;
      }
      switch (c) {
      case '=': return new Token(new Label(x));
      // TODO case '#': return new Token(new LabelReference(x));
      }
    }
    if (c == 'u' || c == 'U') {
      i = reader.read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector");
      c = (char) i; if (c != '8')  throw new SyntaxErrorException("Expected bytevector");
      i = reader.read(); if (i < 0) throw new SyntaxErrorException("Expected bytevector");
      c = (char) i; if (c != '(')  throw new SyntaxErrorException("Expected bytevector");
      return Token.BEGIN_BYTEVECTOR;
    }
    if ("tTfF".indexOf(c) >= 0) return new Token(Boolean.parse("#" + c + readUntilDelimiter()));
    if (c == '\\') {
      return new Token(Character.parse("#\\" + readUntilDelimiter()));
    }
    if ("eEiIbBoOdDxX".indexOf(c) >= 0) {
      return new Token(Number.parse("#" + c + readUntilDelimiter()));
    }
    throw new SyntaxErrorException("Expected boolean, character, number or vector");
  }
  // After "
  protected Token parseString() throws IOException, SyntaxErrorException {
    java.lang.StringBuffer string = new StringBuffer();
    int i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
    char c = (char) i;
    string.append('"');
    while (c != '"') {
      string.append(c);
      if (c == '\\') {
        i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
        c = (char) i;
        string.append(c);
      }
      i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in String literal");
      c = (char) i;
    }
    string.append('"');
    return new Token(String.parse(string.toString()));
  }
  // After |
  protected Token parseVerticalLineIdentifier() throws IOException, SyntaxErrorException {
    int i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
    char c = (char) i;
    StringBuffer identifier = new StringBuffer();
    identifier.append('|');
    while (c != '|') {
      identifier.append(c);
      if (i == '\\') {
        i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
        c = (char) i;
        identifier.append(c);
      }
      i = reader.read(); if (i < 0) throw new SyntaxErrorException("EOF in vertical line delimited identifier");
      c = (char) i;
    }
    identifier.append('|');
    return new Token(Identifier.parse(identifier.toString(), foldCase));
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
    int i = reader.read(); if (i < 0) return result.toString();
    char c = (char) i;
    while (!isDelimiter(c)) {
      result.append(c);
      i = reader.read(); if (i < 0) return result.toString();
      c = (char) i;
    }
    reader.unread(i);
    return result.toString();
  }

  void skipIntertokenSpace() throws IOException {
    int i;
    char c;
    while (true) {
      i = reader.read(); if (i < 0) return;
      c = (char) i;
      switch (c) {
      // <whitespace> -> <intraline whitespace> | <line ending>
      // <intraline whitespace> -> <space or tab>
      // <line ending> -> <newline> | <return> <newline> | <return>
      case ' ': case '\t': case '\r': case '\n': break;
      // <comment> -> ; <all subsequent characters up to a line ending>
      case ';':
        i = reader.read(); if (i < 0) return;
        c = (char) i;
        while (c != '\r' && c != '\n') {
          i = reader.read(); if (i < 0) return;
          c = (char) i;
        }
        if (c == '\r') {
          i = reader.read(); if (i < 0) return;
          c = (char) i;
          if (c != '\n') {
            reader.unread(i);
          }
        }
        break;
      case '#':
        i = reader.read(); if (i < 0) return;
        c = (char) i;
        switch (c) {
        // <comment> -> <nested comment>
        // <nested comment> -> #| <comment text> <comment cont>* |#
        // <comment text> -> <character sequence not containing #| or |#>
        // <comment cont> -> <nested comment> <comment text>
        case '|':
          int commentLevel = 1;
          while (commentLevel > 0) {
            i = reader.read(); if (i < 0) return;
            c = (char) i;
            if (c == '#') {
              int i2 = reader.read(); if (i2 < 0) return;
              char c2 = (char) i2;
              if (c2 == '|') {
                commentLevel++;
              } else {
                reader.unread(i2);
              }
            } else if (c == '|') {
              int i2 = reader.read(); if (i2 < 0) return;
              char c2 = (char) i2;
              if (c2 == '#') {
                commentLevel--;
              } else {
                reader.unread(i2);
              }
            }
          }
          break;
        case ';':
          // <comment> -> #; <intertoken space> <datum>
          parser.read();
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
          break;
        default:
          reader.unread(i);
          reader.unread('#');
          return;
        }
        break;
      default:
        reader.unread(i);
        return;
      }
    }
  }
  
}
