package se.pp.forsberg.scheme;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
  
  public Value read() throws SchemeException {
    return read(null, new Environment(null));
  }
  Token readToken() throws IOException, SyntaxErrorException {
    Token token = tokenizer.readToken();
    while (token.getType() == Token.Type.COMMENT || token.getType() == Token.Type.DIRECTIVE) {
      token = tokenizer.readToken();
    }
    return token;
  }
  // Think...
  // A label when defined is valid until the end of the enclosing construct
  // '(#1=foo #1#)
  // The label is also valid within the object labeled
  // '(#1=(foo . #1#))
  // We need to create a new labling env when starting eval of list or vector
  public Value read(Label labelThis, Environment labels) throws SchemeException {
    try {
      // <datum> -> <simple datum> | <compund datum> | <label> = <datum> | <label> #
      // <simple datum> -> <boolean> | <number> | <character> | <string> | <identifier> | <bytevector>
      // <compound datum> -> <list> | <vector> | <abbreviation>
      // <list> -> ( <datum>* ) | ( <datum>+ . )
      // <vector> -> #( <datum>* )
      // <abbreviation> -> <abbreviation prefix> <datum>
      // <abbreviation prefix> -> ' | ` | , | ,@
      // <label> -> # <uinteger 10>
      Token token = readToken();
      if (token.getType() == Type.EOF) {
        return EOF;
      }
      
      Value datum;
      Value result;
      switch (token.getType()) {
        case VALUE:
        // number, identifier, boolean, character, string, label
          if (token.getValue() instanceof Label) {
            Label label = (Label) token.getValue();
            if (label.isReference()) {
              datum = labels.lookup(label);
              if (datum == null) throw new SchemeException(new ReadError(new SyntaxErrorException("Undefined label " + label)));
            } else { 
              datum = read(label, labels);
              //labels.define(label, datum);
            }
            return datum;
          }
          result = token.getValue();
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          return result;
        case QUOTE:
          result = quote(read(null, labels));
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          return result;
        case QUASI_QUOTE:
          result = quasiQuote(read(null, labels));
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          return result;
        case UNQUOTE:
          result = unquote(read(null, labels));
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          return result;
        case UNQUOTE_SPLICING:
          result = unquoteSplicing(read(null, labels));
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          return result;
        case BEGIN_VECTOR: 
          List<Value> vector = new ArrayList<Value>();
          result = new Vector(vector);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          labels = new Environment(labels);
          datum = read(null, labels);
          while (datum != null && !datum.isEof()) {
            vector.add(datum);
            datum = read(null, labels);
          }
          token = readToken();
          if (token.getType() != Type.RIGHT_PAREN) {
            throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) to terminate vector, not " + token)));
          }
          return result;
        case BEGIN_BYTEVECTOR:
          List<Byte> byteVector = new ArrayList<Byte>();
          result = new ByteVector(byteVector);
          if (labelThis != null) {
            labels.define(labelThis, result);
          }
          datum = read(null, labels);
          while (datum != null && !datum.isEof()) {
            if (!(datum instanceof Integer)) throw new SchemeException(new ReadError(new SyntaxErrorException("Non-integer value in byte vector")));
            Integer integer = (Integer) datum;
            if (!integer.isExact()) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values must be exact")));
            if (!integer.lessThan(new LongInteger(256, true))) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values too large")));
            byteVector.add(integer.asByte());
            datum = read(null, labels);
          }
          token = readToken();
          if (token.getType() != Type.RIGHT_PAREN) {
            throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) to terminate bytevector, not " + token)));
          }
          return result;
        case LEFT_PAREN:
          return readList(labelThis, labels);
        case RIGHT_PAREN:
        case DOT: tokenizer.pushback(token); return null;
        case EOF: return null;
      }
    } catch (IOException x) {
      throw new SchemeException(new ReadError(x));
    } catch (SyntaxErrorException x) {
      throw new SchemeException(new ReadError(x));
    }
    throw new SchemeException(new ReadError(new SyntaxErrorException("Cannot happen")));
  }
  Value readList(Label labelThis, Environment labels) throws IOException, SyntaxErrorException {
    Value car = read(null, labels);
    if (car == null) {
      Token token = readToken();
      switch (token.getType()) {
      case RIGHT_PAREN: return NIL;
      case DOT:
        Value cdr = read(null, labels);
        if (cdr == null)  throw new SchemeException(new ReadError(new SyntaxErrorException("Expected value after .")));
        token = readToken();
        if (token.getType() != Type.RIGHT_PAREN) throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) after . value got " + token)));
        return cdr;
      default: throw new SchemeException(new ReadError(new SyntaxErrorException("Unexpected " + token)));
      }
    }
    if (car.isEof()) {
      throw new SchemeException(new ReadError(new SyntaxErrorException("Unexpected EOF")));
    }
     
    if (labelThis != null) {
      Pair result = new Pair(car, Nil.NIL);
      labels.define(labelThis, result);
      result.setCdr(readList(null, labels));
      return result;
    } else {
      return new Pair(car, readList(null, labels));
    }
  }
  protected void defineLabel(Label label, Value value) {
    // TODO actually define label
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
  
  
}
