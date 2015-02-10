package se.pp.forsberg.scheme;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.pp.forsberg.scheme.values.Boolean;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.RationalPair;

public class TestTokenizer {

  public TestTokenizer() {
    
  }
  protected List<Token> lex(java.lang.String s) {
    Parser parser = new Parser(new StringReader(s));
    Tokenizer tokenizer = parser.getTokenizer();
    List<Token> tokens = new ArrayList<Token>();
    Token token;
    try {
      token = tokenizer.readToken();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (SyntaxErrorException e) {
      throw new RuntimeException(e);
    }
    while (token.getType() != Token.Type.EOF) {
      tokens.add(token);
      try {
        token = tokenizer.readToken();
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (SyntaxErrorException e) {
        throw new RuntimeException(e);
      }
    }
    return tokens;
  }
  @Test
  public void testRationalBug() {
    List<Token> tokens = lex("(12/34)");
    assertEquals(3, tokens.size());
    assertEquals(Token.Type.LEFT_PAREN, tokens.get(0).getType());
    assertEquals(Token.Type.VALUE, tokens.get(1).getType());
    assertEquals(new RationalPair(new LongInteger(6, true), new LongInteger(17, true), true), tokens.get(1).getValue());
    assertEquals(Token.Type.RIGHT_PAREN, tokens.get(2).getType());
  }
  @Test
  public void testAtmosphere1() {
    List<Token> tokens = lex("   a \r\n b ; c  \n d #| e #| f |# g |# h #; (1(2(3))) i");
    assertEquals(5, tokens.size());
    assertEquals(Token.Type.VALUE, tokens.get(0).getType());
    assertEquals(new Identifier("a"), tokens.get(0).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(1).getType());
    assertEquals(new Identifier("b"), tokens.get(1).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(2).getType());
    assertEquals(new Identifier("d"), tokens.get(2).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(3).getType());
    assertEquals(new Identifier("h"), tokens.get(3).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(4).getType());
    assertEquals(new Identifier("i"), tokens.get(4).getValue());
  }
  @Test
  public void testIdentifier1() {
    List<Token> tokens = lex("Hello world");
    assertEquals(2, tokens.size());
    assertEquals(Token.Type.VALUE, tokens.get(0).getType());
    assertEquals(new Identifier("Hello"), tokens.get(0).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(1).getType());
    assertEquals(new Identifier("world"), tokens.get(1).getValue());
  }
  @Test
  public void testIdentifier2() {
    List<Token> tokens = lex("+.hello");
    assertEquals(1, tokens.size());
    assertEquals(Token.Type.VALUE, tokens.get(0).getType());
    assertEquals(new Identifier("+.hello"), tokens.get(0).getValue());
  }
  @Test
  public void testIdentifier3() {
    List<Token> tokens = lex("|H\\ae\\bl\\tl\\no\\rw\\x20;orld|");
    assertEquals(1, tokens.size());
    assertEquals(Token.Type.VALUE, tokens.get(0).getType());
    assertEquals(new Identifier("H\007e\bl\tl\no\rw orld"), tokens.get(0).getValue());
  }
  @Test
  public void testBoolean1() {
    List<Token> tokens = lex("#t #true #f #false");
    assertEquals(4, tokens.size());
    assertEquals(Token.Type.VALUE, tokens.get(0).getType());
    assertEquals(Boolean.TRUE, tokens.get(0).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(1).getType());
    assertEquals(Boolean.TRUE, tokens.get(1).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(2).getType());
    assertEquals(Boolean.FALSE, tokens.get(2).getValue());
    assertEquals(Token.Type.VALUE, tokens.get(3).getType());
    assertEquals(Boolean.FALSE, tokens.get(3).getValue());
  }
  
  /*
  protected List<? extends Token> lex(java.lang.String s) {
    ANTLRInputStream stream = new ANTLRInputStream(s);
    return new Scheme2Lexer_old(stream).getAllTokens();
  }
  @Test
  public void testRationalBug() {
    List<? extends Token> tokens = lex("(12/34)");
    assertEquals(3, tokens.size());
    assertEquals(Scheme2Lexer_old.LeftParen, tokens.get(0).getType());
    assertEquals(Scheme2Lexer_old.Number, tokens.get(1).getType());
    assertEquals("12/34", tokens.get(1).getText());
    assertEquals(Scheme2Lexer_old.RightParen, tokens.get(2).getType());
  }
  @Test
  public void testAtmosphere1() {
    List<? extends Token> tokens = lex("   a \r\n b ; c  \n d #| e #| f |# g |# h");
    assertEquals(4, tokens.size());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(0).getType());
    assertEquals("a", tokens.get(0).getText());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(1).getType());
    assertEquals("b", tokens.get(1).getText());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(2).getType());
    assertEquals("d", tokens.get(2).getText());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(3).getType());
    assertEquals("h", tokens.get(3).getText());
  }
  @Test
  public void testIdentifier1() {
    List<? extends Token> tokens = lex("Hello world");
    assertEquals(2, tokens.size());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(0).getType());
    assertEquals("Hello", tokens.get(0).getText());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(1).getType());
    assertEquals("world", tokens.get(1).getText());
  }
  @Test
  public void testIdentifier2() {
    List<? extends Token> tokens = lex("+.hello");
    assertEquals(1, tokens.size());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(0).getType());
    assertEquals("+.hello", tokens.get(0).getText());
  }
  @Test
  public void testIdentifier3() {
    List<? extends Token> tokens = lex("|H\\ae\\bl\\tl\\no\\rw\\x20;orld|");
    assertEquals(1, tokens.size());
    assertEquals(Scheme2Lexer_old.Identifier, tokens.get(0).getType());
    assertEquals("|H\\ae\\bl\\tl\\no\\rw\\x20;orld|", tokens.get(0).getText());
  }
  @Test
  public void testBoolean1() {
    List<? extends Token> tokens = lex("#t #true #f #false");
    assertEquals(4, tokens.size());
    assertEquals(Scheme2Lexer_old.Bool, tokens.get(0).getType());
    assertEquals("#t", tokens.get(0).getText());
    assertEquals(Scheme2Lexer_old.Bool, tokens.get(1).getType());
    assertEquals("#true", tokens.get(1).getText());
    assertEquals(Scheme2Lexer_old.Bool, tokens.get(2).getType());
    assertEquals("#f", tokens.get(2).getText());
    assertEquals(Scheme2Lexer_old.Bool, tokens.get(3).getType());
    assertEquals("#false", tokens.get(3).getText());
  }
  */
}
 