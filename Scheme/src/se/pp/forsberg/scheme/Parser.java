package se.pp.forsberg.scheme;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import se.pp.forsberg.scheme.DebugInformation.AtmosphereType;
import se.pp.forsberg.scheme.Token.Type;
import se.pp.forsberg.scheme.values.ByteVector;
import se.pp.forsberg.scheme.values.Environment;
import se.pp.forsberg.scheme.values.Eof;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.Label;
import se.pp.forsberg.scheme.values.Nil;
import se.pp.forsberg.scheme.values.Pair;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.Vector;
import se.pp.forsberg.scheme.values.errors.ReadError;
import se.pp.forsberg.scheme.values.numbers.Integer;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Parser {
  private static Nil NIL = Nil.NIL;
  private static Eof EOF = Eof.EOF;
    
  protected final Tokenizer tokenizer;
  
  private DebugInformation debugInformation = new DebugInformation();
  
  public Tokenizer getTokenizer() { return tokenizer; }
  
  public Parser() {
    this(System.in, Charset.forName("utf-8"));
  }
  public Parser(InputStream in) {
    this(in, Charset.forName("utf-8"));
  }
  public Parser(InputStream stream, Charset charset) {
    this(new InputStreamReader(stream, charset));
  }
  public Parser(Reader reader) {
    tokenizer = new Tokenizer(this, reader);
  }
  
  private boolean debug() { return tokenizer.isEclipseMode(); }
  
  public Value read() throws SchemeException {
    return read(null, new Environment(null));
  }
  private Token previous;
  Token readToken() throws IOException, SyntaxErrorException {
    Token token = tokenizer.readToken();
    addWhitespace(token);
    while (token.getType() == Token.Type.COMMENT || token.getType() == Token.Type.DIRECTIVE) {
      if (debug()) {
        if (token.getType() == Token.Type.COMMENT) {
          debugInformation.addAtmosphere(AtmosphereType.COMMENT, abridge(token.getValue().toString(), 100), token.getOffset(), token.getLength());
        } else {
          debugInformation.addAtmosphere(AtmosphereType.DIRECTIVE, token.getValue().toString(), token.getOffset(), token.getLength());
        }
      }
      token = tokenizer.readToken();
      addWhitespace(token);
    }
    previous = token;
    return token;
  }
  private String abridge(String string, int i) {
    if (string.length() < i) return string;
    return string.substring(0, i-3) + "...";
  }

  private void addWhitespace(Token token) {
    int endOfPrevious;
    if (debug()) {
      if (previous != null) {
        endOfPrevious = previous.getOffset() + previous.getLength();
        if (endOfPrevious < token.getOffset()) {
          debugInformation.addAtmosphere(AtmosphereType.WHITESPACE, "", endOfPrevious, token.getOffset() - endOfPrevious);
        }
      }
    }
  }

  // Think...
  // A label when defined is valid until the end of the enclosing construct
  // '(#1=foo #1#)
  // The label is also valid within the object labeled
  // '(#1=(foo . #1#))
  // We need to create a new labling env when starting eval of list or vector
  public Value read(Label labelThis, Environment labels) throws SchemeException {
    return read(labelThis, labels, debugInformation.getRoot());
  }
    public Value read(Label labelThis, Environment labels, DebugInformation.Node parentNode) throws SchemeException {
    try {
      // <datum> -> <simple datum> | <compund datum> | <label> = <datum> | <label> #
      // <simple datum> -> <boolean> | <number> | <character> | <string> | <identifier> | <bytevector>
      // <compound datum> -> <list> | <vector> | <abbreviation>
      // <list> -> ( <datum>* ) | ( <datum>+ . )
      // <vector> -> #( <datum>* )
      // <abbreviation> -> <abbreviation prefix> <datum>
      // <abbreviation prefix> -> ' | ` | , | ,@
      // <label> -> # <uinteger 10>
      Token token;
      SyntaxErrorException syntaxError = null;
      try {
        token = readToken();
      } catch (SyntaxErrorException x) {
        if (!debug()) throw x;
        token = x.getToken();
        syntaxError = x;
      }
      if (token.getType() == Type.EOF) {
        return EOF;
      }
      
      Value datum;
      Value result;
      int offset = token.getOffset();
      int length = token.getLength();
      DebugInformation.Node node1 = null, node2 = null, node3 = null;
      switch (token.getType()) {
        case VALUE:
        // number, identifier, boolean, character, string, label
          if (token.getValue() instanceof Label) {
            Label label = (Label) token.getValue();
            if (label.isReference()) {
              datum = labels.lookup(label);
              if (datum == null) throw new SchemeException(new ReadError(new SyntaxErrorException("Undefined label " + label, token)));
            } else { 
              try {
                datum = read(label, labels, parentNode);
              } catch (SchemeException x) {
                if (!debug()) throw x;
                datum = x.getError();
              }
              //labels.define(label, datum);
            }
            if (debug()) {
              DebugInformation.Node child = parentNode.getLastChild();
              length = child.getLength() + child.getOffset() - offset;
              child.setOffset(offset);
              child.setLength(length);
            }
            return datum;
          }
          result = token.getValue();
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          if (debug()) {
            parentNode.add(result, offset, length, syntaxError);
          }
          return result;
        case QUOTE:
          if (debug()) {
            // parent -> (quote . (a . ())
            //           1 2      3 
            node1 = parentNode.add();
            node2 = node1.add();
            node3 = node1.add();
            parentNode = node3;
          }
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          result = quote(datum);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          if (debug()) {
            DebugInformation.Node datumNode = node2.getLastChild();
            node1.setValue(result);
            node1.setOffset(offset);
            node1.setLength(datumNode.getLength() + datumNode.getOffset() - offset);
            node2.setValue(((Pair)result).getCar());
            node2.setOffset(offset);
            node2.setLength(1);
            node3.setValue(((Pair)result).getCdr());
            node3.setOffset(datumNode.getOffset());
            node3.setLength(datumNode.getLength());
            node3.add(NIL, datumNode.getOffset() + datumNode.getLength(), 0);
          }
          return result;
        case QUASI_QUOTE:
          if (debug()) {
            // parent -> (quote . (a . ())
            //           1 2      3 
            node1 = parentNode.add();
            node2 = node1.add();
            node3 = node1.add();
            parentNode = node3;
          }
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          result = quasiQuote(datum);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          if (debug()) {
            DebugInformation.Node datumNode = node2.getLastChild();
            node1.setValue(result);
            node1.setOffset(offset);
            node1.setLength(datumNode.getLength() + datumNode.getOffset() - offset);
            node2.setValue(((Pair)result).getCar());
            node2.setOffset(offset);
            node2.setLength(1);
            node3.setValue(((Pair)result).getCdr());
            node3.setOffset(datumNode.getOffset());
            node3.setLength(datumNode.getLength());
            node3.add(NIL, datumNode.getOffset() + datumNode.getLength(), 0);
          }
          return result;
        case UNQUOTE:
          if (debug()) {
            // parent -> (quote . (a . ())
            //           1 2      3 
            node1 = parentNode.add();
            node2 = node1.add();
            node3 = node1.add();
            parentNode = node3;
          }
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          result = unquote(datum);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          if (debug()) {
            DebugInformation.Node datumNode = node2.getLastChild();
            node1.setValue(result);
            node1.setOffset(offset);
            node1.setLength(datumNode.getLength() + datumNode.getOffset() - offset);
            node2.setValue(((Pair)result).getCar());
            node2.setOffset(offset);
            node2.setLength(1);
            node3.setValue(((Pair)result).getCdr());
            node3.setOffset(datumNode.getOffset());
            node3.setLength(datumNode.getLength());
            node3.add(NIL, datumNode.getOffset() + datumNode.getLength(), 0);
          }
          return result;
        case UNQUOTE_SPLICING:
          if (debug()) {
            // parent -> (quote . (a . ())
            //           1 2      3 
            node1 = parentNode.add();
            node2 = node1.add();
            node3 = node1.add();
            parentNode = node3;
          }
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          result = unquoteSplicing(datum);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          if (debug()) {
            DebugInformation.Node datumNode = node2.getLastChild();
            node1.setValue(result);
            node1.setOffset(offset);
            node1.setLength(datumNode.getLength() + datumNode.getOffset() - offset);
            node2.setValue(((Pair)result).getCar());
            node2.setOffset(offset);
            node2.setLength(1);
            node3.setValue(((Pair)result).getCdr());
            node3.setOffset(datumNode.getOffset());
            node3.setLength(datumNode.getLength());
            node3.add(NIL, datumNode.getOffset() + datumNode.getLength(), 0);
          }
          return result;
        case BEGIN_VECTOR:
          List<Value> vector = new ArrayList<Value>();
          result = new Vector(vector);
          if (debug()) {
            parentNode = parentNode.add(result, offset, 0);
          }
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          labels = new Environment(labels);
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          while (datum != null && !datum.isEof()) {
            vector.add(datum);
            try {
              datum = read(null, labels, parentNode);
            } catch (SchemeException x) {
              if (!debug()) throw x;
              datum = x.getError();
            }
          }
          try {
            token = readToken();
          } catch (SyntaxErrorException x) {
            if (!debug()) throw x;
            token = x.getToken();
          }
          if (token.getType() != Type.RIGHT_PAREN) {
            ReadError error = new ReadError(new SyntaxErrorException("Expected ) to terminate vector, not " + token, token));
            if (!debug()) throw new SchemeException(error);
            vector.add(error);
          }
          if (debug()) {
            parentNode.setLength(token.getOffset() - offset + token.getLength());
          }
          return result;
        case BEGIN_BYTEVECTOR:
          List<Byte> byteVector = new ArrayList<Byte>();
          List<Value> intVector = new ArrayList<Value>();
          result = new ByteVector(byteVector);
          if (debug()) {
            parentNode = parentNode.add(result, offset, 0);
          }
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          try {
            datum = read(null, labels, parentNode);
          } catch (SchemeException x) {
            if (!debug()) throw x;
            datum = x.getError();
          }
          while (datum != null && !datum.isEof()) {
            if (!(datum instanceof Integer)) throw new SchemeException(new ReadError(new SyntaxErrorException("Non-integer value in byte vector", null)));
            Integer integer = (Integer) datum;
            if (!integer.isExact()) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values must be exact", null)));
            if (!integer.lessThan(new LongInteger(256, true))) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values too large", null)));
            byteVector.add(integer.asByte());
            if (debug()) {
              intVector.add(integer);
            }
            try {
              datum = read(null, labels, parentNode);
            } catch (SchemeException x) {
              if (!debug()) throw x;
              datum = x.getError();
            }
          }
          try {
            token = readToken();
          } catch (SyntaxErrorException x) {
            if (!debug()) throw x;
            token = x.getToken();
          }
          if (token.getType() != Type.RIGHT_PAREN) {
            ReadError error = new ReadError(new SyntaxErrorException("Expected ) to terminate bytevector, not " + token, token));
            if (!debug()) throw new SchemeException(error);
            // TODO how to handle error?
            byteVector.add((byte) -17);
          }
          if (debug()) {
            parentNode.setLength(token.getOffset() - offset + token.getLength());
          }
          return result;
        case LEFT_PAREN:
          if (debug()) {
            parentNode = parentNode.add(null, offset, 0);
          }
          return readList(labelThis, labels, parentNode);
        case RIGHT_PAREN:
        case DOT: tokenizer.pushback(token); return null;
        case EOF: return null;
      case COMMENT:
      case DIRECTIVE:
        throw new RuntimeException("Bad programmer");
      default:
        break;
      }
    } catch (IOException x) {
      throw new SchemeException(new ReadError(x));
    } catch (SyntaxErrorException x) {
      if (debug()) {
        // In debug mode all errors should be handled by the parser without aborting.
        // Instead best-effort values marked as erroneus (in the debug information) will be created
        System.err.println("Bad programmer");
      }
      throw new SchemeException(new ReadError(x));
    }
    throw new SchemeException(new ReadError(new SyntaxErrorException("Bad programmer", null)));
  }
  Value readList(Label labelThis, Environment labels, DebugInformation.Node parentNode) throws SchemeException, IOException, SyntaxErrorException {
    Value car = read(null, labels, parentNode);
    if (car == null) {
      Token token;
      try {
        token = readToken();
      } catch (SyntaxErrorException x) {
        if (!debug()) throw x;
        token = x.getToken();
      }
      switch (token.getType()) {
      case RIGHT_PAREN:
        if (debug()) {
          parentNode.add(NIL, token.getOffset(), 1);
        }
        return NIL;
      case DOT:
        Value cdr;
        try {
          cdr = read(null, labels, parentNode);
        } catch (SchemeException x) {
          if (!debug()) throw x;
          cdr = x.getError();
        }
        if (cdr == null) {
          if (!debug()) throw new SchemeException("Expected value after .");
          parentNode.add(new se.pp.forsberg.scheme.values.errors.Error("Expected value after ."), token.getOffset() + token.getLength(), 0);
        }
        try {
          token = readToken();
        } catch (SyntaxErrorException x) {
          if (!debug()) throw x;
          token = x.getToken();
        }
        if (token.getType() != Type.RIGHT_PAREN) {
          if (!debug()) throw new SchemeException("Expected ) after . value" , new se.pp.forsberg.scheme.values.String(token.getType().toString()));
          parentNode.add(new se.pp.forsberg.scheme.values.errors.Error("Expected ) after . value"), token.getOffset() + token.getLength(), 0);
        }
        return cdr;
      default:
        if (!debug()) throw new SchemeException("Unexpected " + token);
        Value error;
        if (token.getType() == Type.VALUE) {
          error = token.getValue();
        } else {
          error = new se.pp.forsberg.scheme.values.errors.Error("Expected . or )");
        }
        parentNode.add(error, token.getOffset(), token.getLength());
      }
    }
    if (car.isEof()) {
      if (!debug()) throw new SchemeException("Unexpected EOF");
      parentNode.add(new se.pp.forsberg.scheme.values.errors.Error("Unexpected EOF"), parentNode.getOffset() + parentNode.getLength(), 0);
      parentNode.add(Nil.NIL, parentNode.getOffset() + parentNode.getLength(), 0);
      return Nil.NIL;
    }
    Pair result = new Pair(car, Nil.NIL);
    DebugInformation.Node cdrNode = parentNode;
    if (debug()) {
      cdrNode = parentNode.add(result, 0, 0);
    }
    if (labelThis != null) {
      labels.define(labelThis, result);
    }
    result.setCdr(readList(null, labels, cdrNode));
    if (debug()) {
      DebugInformation.Node carNode = parentNode.getFirstChild();
      parentNode.add(result, carNode.offset, cdrNode.getLength() + cdrNode.getOffset() - carNode.getOffset());
    }
    return result;
  }
  protected Value quote(Value value) {
    return new Pair(new Identifier("quote"), new Pair(value, NIL));
  }
  protected Value quasiQuote(Value value) {
    return new Pair(new Identifier("quasi-quote"), new Pair(value, NIL));
  }
  protected Value unquote(Value value) {
    return new Pair(new Identifier("unquote"), new Pair(value, NIL));
  }
  protected Value unquoteSplicing(Value value) {
    return new Pair(new Identifier("unquote-splicing"), new Pair(value, NIL));
  }
  public DebugInformation getDebugInformation() {
    return debugInformation;
  }
  
}
