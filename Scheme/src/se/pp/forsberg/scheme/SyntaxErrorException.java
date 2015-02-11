package se.pp.forsberg.scheme;

public class SyntaxErrorException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private Token token;

  public SyntaxErrorException(String message, Token token) {
    super(message);
    this.token = token;
  }
  
  public Token getToken() { return token; }
}
