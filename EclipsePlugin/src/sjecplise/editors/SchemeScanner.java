package sjecplise.editors;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.SyntaxErrorException;
import se.pp.forsberg.scheme.Tokenizer;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.macros.Keyword;

public class SchemeScanner implements IPartitionTokenScanner {

  private static final Color BLACK = new Color(Display.getCurrent(), new RGB(0, 0, 0));
  //private static final Color WHITE = new Color(Display.getCurrent(), new RGB(255, 255, 255));
  private static final Color PURPLE = new Color(Display.getCurrent(), new RGB(127, 0, 85));
  private static final Color GREEN = new Color(Display.getCurrent(), new RGB(63, 127, 95));
  private static final Color BRIGHT_BLUE = new Color(Display.getCurrent(), new RGB(42, 0, 255));
  //private static final Color BLUE = new Color(Display.getCurrent(), new RGB(0, 0, 192));
  private static final Color GRAY = new Color(Display.getCurrent(), new RGB(100, 100, 100));
    
  
  enum TokenType {
    COMMENT, DIRECTIVE, KEYWORD, VECTOR, BYTEVECTOR,
    BOOLEAN, CHAR, STRING, NUMBER, IDENTIFIER,
    QUASI_QUOTE, QUOTE, UNQUOTE, UNQUOTE_SPLICING, LEFT_PAREN, RIGHT_PAREN, DOT
  }
  private static final Map<TokenType, TextAttribute> styles;
  static {
    styles = new HashMap<TokenType, TextAttribute>();
    styles.put(TokenType.BYTEVECTOR, new TextAttribute(BLACK));
    styles.put(TokenType.VECTOR, new TextAttribute(BLACK));
    styles.put(TokenType.COMMENT, new TextAttribute(GREEN));
    styles.put(TokenType.DIRECTIVE, new TextAttribute(GRAY));
    styles.put(TokenType.KEYWORD, new TextAttribute(PURPLE, null, SWT.BOLD));
    styles.put(TokenType.BOOLEAN, new TextAttribute(PURPLE, null, SWT.BOLD));
    styles.put(TokenType.NUMBER, new TextAttribute(BLACK));
    styles.put(TokenType.IDENTIFIER, new TextAttribute(BLACK));
    styles.put(TokenType.CHAR, new TextAttribute(BRIGHT_BLUE));
    styles.put(TokenType.STRING, new TextAttribute(BRIGHT_BLUE));
    styles.put(TokenType.LEFT_PAREN, new TextAttribute(BLACK));
    styles.put(TokenType.RIGHT_PAREN, new TextAttribute(BLACK));
    styles.put(TokenType.DOT, new TextAttribute(BLACK));
  }
  private static final Map<TokenType, TokenType> reportedTypes;
  static {
    reportedTypes = new HashMap<TokenType, TokenType>();
    reportedTypes.put(TokenType.BYTEVECTOR, TokenType.BYTEVECTOR);
    reportedTypes.put(TokenType.VECTOR, TokenType.VECTOR);
    reportedTypes.put(TokenType.COMMENT,TokenType.COMMENT);
    reportedTypes.put(TokenType.DIRECTIVE,TokenType.DIRECTIVE);
    reportedTypes.put(TokenType.KEYWORD, TokenType.KEYWORD);
    reportedTypes.put(TokenType.CHAR, TokenType.CHAR);
    reportedTypes.put(TokenType.STRING, TokenType.STRING);
    reportedTypes.put(TokenType.QUASI_QUOTE, TokenType.KEYWORD);
    reportedTypes.put(TokenType.QUOTE, TokenType.KEYWORD);
    reportedTypes.put(TokenType.UNQUOTE, TokenType.KEYWORD);
    reportedTypes.put(TokenType.UNQUOTE_SPLICING, TokenType.KEYWORD);
    reportedTypes.put(TokenType.LEFT_PAREN, TokenType.LEFT_PAREN);
    reportedTypes.put(TokenType.RIGHT_PAREN, TokenType.RIGHT_PAREN);
    reportedTypes.put(TokenType.DOT, TokenType.DOT);
  }
  private static final Map<se.pp.forsberg.scheme.Token.Type, TokenType> typeMap;
  static {
    typeMap = new HashMap<se.pp.forsberg.scheme.Token.Type, TokenType>();
    typeMap.put(se.pp.forsberg.scheme.Token.Type.BEGIN_BYTEVECTOR, TokenType.BYTEVECTOR);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.BEGIN_VECTOR, TokenType.VECTOR);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.COMMENT, TokenType.COMMENT);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.DIRECTIVE, TokenType.DIRECTIVE);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.DOT, TokenType.DOT);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.LEFT_PAREN, TokenType.LEFT_PAREN);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.QUASI_QUOTE, TokenType.QUASI_QUOTE);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.QUOTE, TokenType.QUOTE);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.RIGHT_PAREN, TokenType.RIGHT_PAREN);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.UNQUOTE, TokenType.UNQUOTE);
    typeMap.put(se.pp.forsberg.scheme.Token.Type.UNQUOTE_SPLICING, TokenType.UNQUOTE_SPLICING);
  }
  
  //private IDocument document;
  private Object element;
  private int offset, length;
  private Parser parser;
  private Tokenizer tokenizer;
  private int tokenOffset, tokenLength;
  //private static Set<String> keywords = new HashSet<String>(Arrays.asList(new String[]{"define", "set!", "let", "let*", "letrec", "define-syntax", "let-syntax", "letrec-syntax", "and", "or", "cond", "case", "if"}));
  private Environment env = Environment.schemeReportEnvironment(7);
  private boolean returnStyles = false;
  private static String[] contentTypes;
  static {
    TokenType[] types = TokenType.values();
    contentTypes = new String[types.length];
    for (int i = 0; i < types.length; i++) {
      contentTypes[i] = types[i].toString();
    }
  }
  
  public SchemeScanner(Object element, boolean returnStyles) throws SchemeException {
    this.element = element;
    this.returnStyles = returnStyles;
  }
  public static String[] getContentTypes() {
    return contentTypes;
  }
  @Override
  public void setRange(IDocument document, int offset, int length) {
    //this.document = document;
    this.offset = offset;
    this.length = length;
    try {
      String textToScan = document.get(this.offset, this.length);
      log("Set range:\n-----------------------------\n" + textToScan +"\n----------------------");
      parser = new Parser(new StringReader(textToScan));
    } catch (BadLocationException e) {
      e.printStackTrace();
      return;
    }
    tokenizer = parser.getTokenizer();
    tokenizer.setEclipseMode(true);
  }
  @Override
  public IToken nextToken() {
    se.pp.forsberg.scheme.Token token = null;
    try {
      token = tokenizer.readToken();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (SyntaxErrorException e) {
      token = e.getToken();
      if (element != null) {
        IFileEditorInput fei = (IFileEditorInput) element;
        IFile file = fei.getFile();
        IResource res = (IResource) file;
        IMarker marker;
        try {
          marker = res.createMarker("sjecplise.editors.SchemeSyntaxError");
          marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
          if (token != null) {
            marker.setAttribute(IMarker.CHAR_START, offset + token.getOffset());
            marker.setAttribute(IMarker.CHAR_END, offset + token.getOffset() + token.getLength());
            //marker.setAttribute(IMarker.LOCATION, "Line " + token.getLine() + ", column " + token.getColumn());
          }
          marker.setAttribute(IMarker.MESSAGE,"x"+ e.getMessage());
        } catch (CoreException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
    if (token == null || token.getType() == se.pp.forsberg.scheme.Token.Type.EOF) {
      return Token.EOF;
    }
    tokenOffset = token.getOffset();
    tokenLength = token.getLength();
    Token result = makeToken(token);
    log("Token:\n-----------------------------\n" + token + " " + result.getData() +"\n----------------------");
    return result;
  }
  protected Token makeToken(se.pp.forsberg.scheme.Token token) {
    TokenType type;
    
    if (token.getType() == se.pp.forsberg.scheme.Token.Type.VALUE) {
      Value v = token.getValue();
      if (v.isBoolean()) {
        type = TokenType.BOOLEAN;
      } else if (v.isChar()) {
        type = TokenType.CHAR;
      } else if (v.isIdentifier()) {
        Identifier id = (Identifier) v;
        v = env.lookup(id);
        if (v != null && v instanceof Keyword) {
          type = TokenType.KEYWORD;
        } else {
          type = TokenType.IDENTIFIER;
        }
      } else if (v.isNumber()) {
        type = TokenType.NUMBER;
      } else if (v.isString()) {
        type = TokenType.STRING;
      } else {
        throw new RuntimeException("Bad programmer");
      }
    } else {
      type = reportedTypes.get(typeMap.get(token.getType()));
    }
    Object payload;
    if (returnStyles) {
      payload = styles.get(type);
    } else {
      payload = type.toString();
    }
    return new Token(payload);
  }

  @Override
  public int getTokenOffset() {
    return offset + tokenOffset;
  }

  @Override
  public int getTokenLength() {
    return tokenLength;
  }
  @Override
  public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
    //this.document = document;
    //this.offset = offset;
    this.offset = partitionOffset;
    this.length = length + offset - partitionOffset;
    try {
      String textToScan = document.get(this.offset, this.length);
      log("Partition:\n-----------------------------\n" + textToScan +"\n----------------------");
      parser = new Parser(new StringReader(textToScan));
    } catch (BadLocationException e) {
      e.printStackTrace();
      return;
    }
    tokenizer = parser.getTokenizer();
    tokenizer.setEclipseMode(true);
  }
  
  final static String CONSOLE="Scheme Debug";
  private MessageConsole findConsole(){
    String name = CONSOLE;
    ConsolePlugin plugin = ConsolePlugin.getDefault();
    IConsoleManager conMan = plugin.getConsoleManager();
    IConsole[] existing = conMan.getConsoles();
    for (int i = 0; i < existing.length; i++)
       if (name.equals(existing[i].getName()))
          return (MessageConsole) existing[i];
    //no console found, so create a new one
    MessageConsole myConsole = new MessageConsole(name, null);
    conMan.addConsoles(new IConsole[]{myConsole});
    return myConsole;
 }
  private void log(String message) {
    MessageConsole myConsole = findConsole();
    MessageConsoleStream out = myConsole.newMessageStream();
    out.println(message);
  }


}