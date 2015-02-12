package se.pp.forsberg.scheme.values;

import java.util.Locale;

import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.numbers.Complex;
import se.pp.forsberg.scheme.values.numbers.LongInteger;
import se.pp.forsberg.scheme.values.numbers.Number;

public class Identifier extends Value {
  private final java.lang.String value;
  public Identifier(CharSequence value) {
    this.value = value.toString();
  }
  public Identifier(CharSequence value, boolean foldCase) {
    if (foldCase) value = foldCase(value);
    this.value = value.toString();
  }
  protected static java.lang.String foldCase(CharSequence s) {
    // TODO better
    return s.toString().toLowerCase(Locale.US);
  }
  public java.lang.String getIdentifier() { return value; }
  @Override
  public Value eval(Environment env) throws SchemeException {
    //if (getIdentifier().equals("<undefined>")) return null;
    Value value = env.lookup(this);
    if (value == null) {
      throw new SchemeException("Undefined identifier", this);
    }
    return value;
  }
  
  protected static boolean isInitial(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || "!$%&*/:<=>?^_~".indexOf(c) >= 0;
  }
  protected static boolean isSubsequent(char c) {
    return isInitial(c) || c >= '0' && c <= '9' || "+-.@".indexOf(c) >= 0;
  }
  protected static boolean isDotSubsequent(char c) {
    return isSignSubsequent(c) || c == '.';
  }
  protected static boolean isSignSubsequent(char c) {
    return isInitial(c) || c == '+' || c == '-' || c == '@'; 
  }
  protected boolean isSimple() {
    char c = value.charAt(0);
    if (c == '.') {
      if (value.length() < 2) return false;
      c = value.charAt(1);
      if (!isDotSubsequent(c)) return false;
      for (int i = 2; i < value.length(); i++) {
        c = value.charAt(i);
        if (!isSubsequent(c)) {
          return false;
        }
      }
      return true;
    }
    if (c == '+' || c == '-') {
      if (value.length() < 2) return true;
      c = value.charAt(1);
      if (c == '.') {
        if (value.length() < 3) return false;
        c = value.charAt(2);
        if (!isDotSubsequent(c)) return false;
        for (int i = 3; i < value.length(); i++) {
          c = value.charAt(i);
          if (!isSubsequent(c)) {
            return false;
          }
        }
        return true;
      }
      if (!isSignSubsequent(c)) return false;
      for (int i = 2; i < value.length(); i++) {
        c = value.charAt(i);
        if (!isSubsequent(c)) {
          return false;
        }
      }
      return true;
    }
    if (!isInitial(c)) {
      return false;
    }
    for (int i = 0; i < value.length(); i++) {
      c = value.charAt(i);
      if (!isSubsequent(c)) {
        return false;
      }
    }
    return true;
  }
  @Override
  public boolean isIdentifier() {
    return true;
  }
  
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    if (this.getClass() != value.getClass()) return false;
    Identifier other = (Identifier) value;
    return this.value.equals(other.value);
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  /*
  protected boolean isInitial(char c) {
    return isLetter(c) || isSpecialInitial(c);
  }
  protected boolean isLetter(char c) {
    return c >= 'a' && c <= '<' || c >= 'A' && c <= 'Z';
  }
  protected boolean isDigit(char c) {
    reutrn c >= '0' && c <= '9';
  }
  protected boolean isSpecialInitial(char c) {
    return "!$%&* /:<=>?^_~".indexOf(c) >= 0;
  }
*/
  
  public static Value parse(java.lang.String s, boolean foldCase) {
    if (s.equalsIgnoreCase("+i")) return new Complex(LongInteger.ZERO, LongInteger.ONE, true);
    //if (s.equalsIgnoreCase("i")) return new Complex(LongInteger.ZERO, LongInteger.ONE, true);
    if (s.equalsIgnoreCase("-i")) return new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, true);
    if (s.length() >= 6) {
      java.lang.String prefix = s.substring(0, 6).toLowerCase();
      if (prefix.equals("+inf.0") || prefix.equals("-inf.0") || prefix.equals("+nan.0") || prefix.equals("+nan.0")) {
        return Number.parse(s);
      }
    }
    char c = s.charAt(0);
    switch (c) {
    case '|': return parseQuoted(s, foldCase);
    case '.': return parseDot(s, foldCase);
    case '+':
    case '-': return parseSign(s, foldCase);
    }
    if (!isInitial(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    for (int i = 1; i < s.length(); i++) {
      c = s.charAt(i);
      if (!isSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    }
    if (foldCase) s = foldCase(s);
    
    return new Identifier(s);
  }
  private static Value parseDot(java.lang.String s, boolean foldCase) {
    if (s.length() < 2) throw new IllegalArgumentException("Invalid identifier " + s);
    char c = s.charAt(1);
    if (!isDotSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    for (int i = 2; i < s.length(); i++) {
      c = s.charAt(i);
      if (!isSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    }
    if (foldCase) s = foldCase(s);
    return new Identifier(s);
  }
  private static Value parseSign(java.lang.String s, boolean foldCase) {
    if (s.length() < 2) return new Identifier(s);
    char c = s.charAt(1);
    if (c == '.') return parseSignDot(s, foldCase);
    if (!isSignSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    for (int i = 2; i < s.length(); i++) {
      c = s.charAt(i);
      if (!isSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    }
    if (foldCase) s = foldCase(s);
    return new Identifier(s);
    
  }
  private static Value parseSignDot(java.lang.String s, boolean foldCase) {
    if (s.length() < 3) throw new IllegalArgumentException("Invalid identifier " + s);
    char c = s.charAt(2);
    if (!isDotSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    for (int i = 3; i < s.length(); i++) {
      c = s.charAt(i);
      if (!isSubsequent(c)) throw new IllegalArgumentException("Invalid identifier " + s);
    }
    if (foldCase) s = foldCase(s);
    return new Identifier(s);
  }
  private static Value parseQuoted(java.lang.String s, boolean foldCase) {
    if (s.charAt(s.length()-1) != '|') throw new IllegalArgumentException("Invalid identifier " + s);
    StringBuffer result = new StringBuffer();
    for (int i = 1; i < s.length()-1; i++) {
      char c = s.charAt(i);
      switch (c) {
      case '|':  throw new IllegalArgumentException("Invalid identifier " + s);
      case '\\':
        c = s.charAt(++i);
        switch (c) {
        case '|': result.append(c); break;
        case 'a': result.append('\007'); break;
        case 'b': result.append('\b'); break;
        case 't': result.append('\t'); break;
        case 'n': result.append('\n'); break;
        case 'r': result.append('\r'); break;
        case 'x':
          int val = 0;
          c = java.lang.Character.toLowerCase(s.charAt(++i));
          while (c >= '0' &&  c <= '9' || c >= 'a' && c <= 'f') {
            if (c >= '0' &&  c <= '9') {
              val = val * 16 + (c-'0');
            } else {
              val = val * 16 + (c-'a') + 10;
            }
            c = java.lang.Character.toLowerCase(s.charAt(++i));
            if (val > java.lang.Character.MAX_VALUE) {
              throw new IllegalArgumentException("Invalid identifier " + s);
            }
          }
          if (c != ';') throw new IllegalArgumentException("Invalid identifier " + s);
          result.append((char) val);
          break;
        default: throw new IllegalArgumentException("Invalid identifier " + s);
        }
        break;
      default: result.append(c);
      }
    }
    return new Identifier(result.toString());
  }
  @Override
  public int hashCode() {
    return value.hashCode();
  }
  
  @Override
  public java.lang.String toString() {
    if (isSimple()) {
      return value;
    }
    StringBuffer result = new StringBuffer();
    result.append('|');
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
      case '|': result.append("\\|"); break;
      case '\07': result.append("\\a"); break;
      case '\b': result.append("\\b"); break;
      case '\t': result.append("\\t"); break;
      case '\n': result.append("\\n"); break;
      case '\r': result.append("\\r"); break;
      default:  
        if (java.lang.Character.isISOControl(c) || c == '\\') {
          result.append("\\x");
          result.append(java.lang.Integer.toString(c, 16));
          result.append(';');
        } else {
          result.append(c);
        }
      }
    }
    result.append('|');
    return result.toString();
  }
  @Override
  public boolean equals(Object obj) {
    //System.out.println(this.toString() + " == " + obj); 
    if ((!(obj instanceof Identifier)) || obj instanceof Label) return false;
    return value.equals(((Identifier)obj).value);
  }
}
