package se.pp.forsberg.scheme.values;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.errors.FileError;

public class Port extends Value {
  public enum Mode { IN, OUT; }
  
  private InputStream in;
  private BufferedReader reader;
  private OutputStream out;
  private PrintWriter writer;
  private boolean inputOpen, outputOpen;
  
  Parser parser;
  public final static Port STDIO = new Port(System.in, System.out);
  
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
  public Port(File file, Mode mode) {
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
    this.in = in;
    reader = new BufferedReader(new InputStreamReader(in));
    parser = new Parser(in);  
    inputOpen = true;
  }
  protected void setOutput(OutputStream out) {
    this.out = out;
    writer = new PrintWriter(out);
    outputOpen = true;
  }
  
  public Value read() {
    if (in == null) throw new SchemeException(new FileError(new IllegalArgumentException("Port is write only")));
    return parser.read();
  }
  public void write(Value value) {
    if (out == null) throw new SchemeException(new FileError(new IllegalArgumentException("Port is read only")));
    writer.println(value);
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
  public void closeInput() {
    //Error error = null;
    if (in != null && inputOpen) {
      try {
        in.close();
      } catch (IOException e) {
        //error = new FileError(e);
      }
      inputOpen = false;
    }
  }

  public void closeOutput() {
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
}
