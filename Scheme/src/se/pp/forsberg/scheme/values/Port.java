package se.pp.forsberg.scheme.values;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.errors.Error;
import se.pp.forsberg.scheme.values.errors.FileError;
import se.pp.forsberg.scheme.values.errors.ReadError;
import se.pp.forsberg.scheme.values.numbers.LongInteger;

public class Port extends Value {
  public enum Mode { IN, OUT; }
  
  private InputStream realIn;
  private PushbackInputStream in;
  private BufferedReader reader;
  private OutputStream out;
  private Writer writer;
  private boolean inputOpen, outputOpen;
  
  Parser parser;
  public final static Port STDIO = new Port(System.in, System.out);
  public final static Port STDERR = new Port(null, System.err);
//  private static Port inputPort = new Port(System.in, null);
//  private static Port outputPort = new Port(null, System.out);
//  private static Port errorPort = new Port(null, System.err);
  
  public Port(InputStream in, OutputStream out) {
    setInput(in);
    setOutput(out);
  }
  public Port(OutputStream out) {
    setOutput(out);
  }
  public Port(InputStream in) {
    setInput(in);
  }
  public Port(File file, Mode mode) throws SchemeException {
    try {
      if (mode == Mode.IN) {
        setInput(new FileInputStream(file));
      } else {
        setOutput(new FileOutputStream(file));
      }
    } catch (Exception x) {
      throw new SchemeException(new FileError(x));
    }
  }

  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }

  @Override
  public boolean eqv(Value value) {
    return this == value;
  }

  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }

  protected void setInput(InputStream in) {
    this.realIn = in;
    if (in instanceof PushbackInputStream) {
      this.in = (PushbackInputStream) in;
    } else {
      this.in = new PushbackInputStream(realIn);
    }
    reader = new BufferedReader(new InputStreamReader(this.in));
    parser = new Parser(this.in);  
    inputOpen = true;
  }
  protected void setOutput(OutputStream out) {
    this.out = out;
    writer = new PrintWriter(out);
    outputOpen = true;
  }
  
  public Value read() throws SchemeException {
    if (in == null) throw new SchemeException(new FileError("Port is write only"));
    return parser.read();
  }
  public void write(Value value) throws SchemeException  {
    if (out == null) throw new SchemeException(new FileError("Port is read only"));
    try {
      writer.write(value.toStringSafe());
      newline();
    } catch (IOException e) {
      throw new SchemeException(new FileError(e));
    }
  }
  public void writeShared(Value value) throws SchemeException {
    if (out == null) throw new SchemeException(new FileError("Port is read only"));
    try {
      writer.write(value.toStringShared());
      newline();
    } catch (IOException e) {
      throw new SchemeException(new FileError(e));
    }
  }
  public void writeSimple(Value value) throws SchemeException {
    if (out == null) throw new SchemeException(new FileError("Port is read only"));
    try {
      writer.write(value.toStringSimple());
      newline();
    } catch (IOException e) {
      throw new SchemeException(new FileError(e));
    }
  }
  public void close() {
    //Error error = null;
    if (in != null && inputOpen) {
      try {
        in.close();
      } catch (IOException e) {
        //error = new FileError(e);
      }
      inputOpen = false;
    }
    if (out != null && outputOpen) {
      try {
        out.close();
      } catch (IOException e) {
        //error = new FileError(e);
      }
      outputOpen = false;
    }
    //if (error != null) throw new SchemeException(error);
  }
  public void closeInput() throws SchemeException {
    //Error error = null;
    if (in == null) throw new SchemeException("Not an input port", this);
    if (inputOpen) {
      try {
        in.close();
      } catch (IOException e) {
        //error = new FileError(e);
      }
      inputOpen = false;
    }
  }

  public void closeOutput() throws SchemeException {
    if (out == null) throw new SchemeException("Not an input port", this);
    if (outputOpen) {
      try {
        out.close();
      } catch (IOException e) {
        //error = new FileError(e);
      }
      outputOpen = false;
    }
    //if (error != null) throw new SchemeException(error);
  }
  public boolean isInputPort() {
    return in != null;
  }
  public boolean isOutputPort() {
    return out != null;
  }
  
  @Override
  public int hashCode() {
    int result = 0;
    if (in != null) result ^= in.hashCode();
    if (out != null) result ^= out.hashCode();
    if (inputOpen) result ^= 17;
    if (outputOpen) result ^= 4711;
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Port) || obj == null) return false;
    Port p = (Port) obj;
    if (in != p.in) return false;
    if (out != p.out) return false;
    if (inputOpen != p.inputOpen) return false;
    if (outputOpen != p.outputOpen) return false;
    return true;
  }
  @Override
  public java.lang.String toString() {
    return "[Port]";
  }
  public static Port openInputFile(String filename) throws SchemeException {
    try {
     return new Port(new FileInputStream(filename.getString()), null);
    } catch (FileNotFoundException x) {
      throw new SchemeException(new FileError(x));
    }
  }
  public static Port openOutputFile(String filename) throws SchemeException {
    try {
     return new Port(null, new FileOutputStream(filename.getString()));
    } catch (FileNotFoundException x) {
      throw new SchemeException(new FileError(x));
    }
  }
  public String getOutputString() throws SchemeException {
    if (out == null || !(out instanceof ByteArrayOutputStream)) {
      throw new SchemeException("Not a string/bytevector port", this);
    }
    try {
      return new String(((ByteArrayOutputStream) out).toString("utf-8"));
    } catch (UnsupportedEncodingException e) {
      throw new SchemeException(new ReadError(e));
    }
  }
  public ByteVector getOutputBytevector() throws SchemeException {
    if (out == null || !(out instanceof ByteArrayOutputStream)) {
      throw new SchemeException("Not a string/bytevector port", this);
    }
    return new ByteVector(((ByteArrayOutputStream) out).toByteArray());
  }
  public Value readChar() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    int c;
    try {
      c = reader.read();
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    if (c == -1) return Eof.EOF;
    return new Character((char) c);
  }
  public Value peekChar() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    int c;
    try {
      reader.mark(1);
      c = reader.read();
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    if (c == -1) return Eof.EOF;
    try {
      reader.reset();
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    return new Character((char) c);
  }
  public Value readLine() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    try {
      return new String(reader.readLine());
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
  }
  public Value isCharReady() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port");
    try {
      return (realIn.available() > 0)? Boolean.TRUE : Boolean.FALSE;
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
  }
  public Value readByte() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    int c;
    try {
      c = in.read();
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    if (c == -1) return Eof.EOF;
    return new LongInteger(c, true);
  }
  public Value peekByte() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    int c;
    try {
      c = in.read();
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    if (c == -1) return Eof.EOF;
    try {
      in.unread(c);
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
    return new LongInteger(c, true);
  }
  public Value isByteReady() throws SchemeException {
    if (in == null) throw new SchemeException("Not an input port", this);
    try {
      return (realIn.available() > 0)? Boolean.TRUE : Boolean.FALSE;
    } catch (IOException e) {
      throw new SchemeException(new ReadError(e));
    }
  }
  public void display(Value v) throws SchemeException {
    if (out == null) throw new SchemeException("Not an output port", this);
    try {
      if (v.isChar()) {
        writer.write(((Character) v).getCharacter());
        newline();
      } else if (v.isString()) {
        writer.write(((String) v).getString());
        newline();
      } else if (v.isIdentifier()) {
        writer.write(((Identifier) v).getIdentifier());
        newline();
      } else {
        write(v);
      }
    } catch (IOException x) {
      throw new SchemeException(new Error(x));
    }
  }
  public void newline() throws SchemeException {
    if (out == null) throw new SchemeException("Not an output port", this);
    try {
      writer.write("\n");
    } catch (IOException e) {
      throw new SchemeException(new Error(e));
    }
  }
  public void write(java.lang.String s) throws  SchemeException {
    if (out == null) throw new SchemeException("Not an output port", this);
    try {
      writer.write(s);
    } catch (IOException e) {
      throw new SchemeException(new Error(e));
    }
  }
  public void write(byte b) throws SchemeException {
    if (out == null) throw new SchemeException("Not an output port", this);
    try {
      out.write(b);
    } catch (IOException e) {
      throw new SchemeException(new Error(e));
    }
  }
  public void flush() throws SchemeException {
    if (out == null) throw new SchemeException("Not an output port", this);
    try {
      out.flush();
    } catch (IOException e) {
      throw new SchemeException(new Error(e));
    }
  }
  public boolean isInputPortOpen() {
    return inputOpen;
  }
  public boolean isOutputPortOpen() {
    return outputOpen;
  }
}
