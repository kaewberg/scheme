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
    try {
      // <datum> -> <simple datum> | <compund datum> | <label> = <datum> | <label> #
      // <simple datum> -> <boolean> | <number> | <character> | <string> | <identifier> | <bytevector>
      // <compound datum> -> <list> | <vector> | <abbreviation>
      // <list> -> ( <datum>* ) | ( <datum>+ . )
      // <vector> -> #( <datum>* )
      // <abbreviation> -> <abbreviation prefix> <datum>
      // <abbreviation prefix> -> ' | ` | , | ,@
      // <label> -> # <uinteger 10>
      Token token = tokenizer.readToken();
      if (token.getType() == Type.EOF) {
        return EOF;
      }
      
      Value datum;
      switch (token.getType()) {
        case VALUE:
        // number, identifier, boolean, character, string, label
          if (token.getValue() instanceof Label) {
            Label label = (Label) token.getValue();
            datum = read();
            defineLabel(label, datum);
            return datum;
          }
          return token.getValue();
        case QUOTE: return quote(read());
        case QUASI_QUOTE: return quasiQuote(read());
        case UNQUOTE: return unquote(read());
        case UNQUOTE_SPLICING: return unquoteSplicing(read());
        case BEGIN_VECTOR: 
          List<Value> vector = new ArrayList<Value>();
          datum = read();
          while (datum != null) {
            vector.add(datum);
            datum = read();
          }
          token = tokenizer.readToken();
          if (token.getType() != Type.RIGHT_PAREN) {
            throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) to terminate vector, not " + token)));
          }
          return new Vector(vector);
        case BEGIN_BYTEVECTOR:
          List<Byte> byteVector = new ArrayList<Byte>();
          datum = read();
          while (datum != null) {
            if (!(datum instanceof Integer)) throw new SchemeException(new ReadError(new SyntaxErrorException("Non-integer value in byte vector")));
            Integer integer = (Integer) datum;
            if (!integer.isExact()) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values must be exact")));
            if (!integer.lessThan(new LongInteger(256, true))) throw new SchemeException(new ReadError(new SyntaxErrorException("Byte vector values too large")));
            byteVector.add(integer.asByte());
            datum = read();
          }
          token = tokenizer.readToken();
          if (token.getType() != Type.RIGHT_PAREN) {
            throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) to terminate bytevector, not " + token)));
          }
          return new ByteVector(byteVector);
        case LEFT_PAREN:
          return readList();
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
  Value readList() throws IOException, SyntaxErrorException {
    Value car = read();
    if (car == null) {
      Token token = tokenizer.readToken();
      switch (token.getType()) {
      case RIGHT_PAREN: return NIL;
      case DOT:
        Value cdr = read();
        if (cdr == null)  throw new SchemeException(new ReadError(new SyntaxErrorException("Expected value after .")));
        token = tokenizer.readToken();
        if (token.getType() != Type.RIGHT_PAREN) throw new SchemeException(new ReadError(new SyntaxErrorException("Expected ) after . value got " + token)));
        return cdr;
      default: throw new SchemeException(new ReadError(new SyntaxErrorException("Unexpected " + token)));
      }
    }
    return new Pair(car, readList());
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
