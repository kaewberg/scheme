package se.pp.forsberg.scheme.values;



public class String extends Value {

  StringBuilder s;
  public String(CharSequence string) {
    s = new StringBuilder(string.toString());
  }
  public java.lang.String getString() {
    return s.toString();
  }
  public StringBuilder getStringBuilder() {
    return s;
  }
  @Override
  public void makeImmutable() {
    setImmutable();
  }
  
  @Override
  public java.lang.String toString() {
    StringBuffer result = new StringBuffer();
    result.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
      case '"':
      case '\\':
         result.append("\\");
         result.append(c);
         break;
      case '\07': result.append("\\a"); break;
      case '\b': result.append("\\b"); break;
      case '\t': result.append("\\t"); break;
      case '\n': result.append("\\n"); break;
      case '\r': result.append("\\r"); break;
      default:
        if (java.lang.Character.isISOControl(c)) {
          result.append("\\x");
          result.append(java.lang.Integer.toString(c, 16));
          result.append(';');
        } else {
          result.append(c);
        }
      }
    }
    result.append('"');
    return result.toString();
  }
  @Override
  public boolean isString() {
    return true;
  }
  @Override
  public boolean equal(Value value) {
    if (eqv(value)) return true;
    if (!value.isString()) return false;
    String other = (String) value;
    return s.toString().equals(other.s.toString());
  }
  @Override
  public boolean eqv(Value value) {
    return this == value;
  }
  @Override
  public boolean eq(Value value) {
    if (eqv(value)) return true;
    if (!value.isString()) return false;
    String other = (String) value;
    return s.length() == 0 && other.s.length() == 0;
  }
  public static Value parse(java.lang.String string) {
    if (string.charAt(0) != '"' || string.charAt(string.length()-1) != '"') {
      throw new IllegalArgumentException("Invalid string " + string);
    }
    StringBuffer result = new StringBuffer();
    for (int i = 1; i < string.length()-1; i++) {
      char c = string.charAt(i);
      switch (c) {
      case '\\':
        c = string.charAt(++i);
        switch (c) {
        case '\\':
        case '"':
          result.append(c); break;
        case ' ':
        case '\t':
        case '\r':
        case '\n':
          while (" \t".indexOf(c) >= 0) {
            c = string.charAt(++i);
          }
          if (c == '\r') c = string.charAt(++i);
          if (c == '\n') c = string.charAt(++i);
          while (" \t".indexOf(c) >= 0) {
            c = string.charAt(++i);
          }
          i--;
          break;
        case 'x':
          int val = 0;
          c = java.lang.Character.toLowerCase(string.charAt(++i));
          while (c >= '0' &&  c <= '9' || c >= 'a' && c <= 'f') {
            if (c >= '0' &&  c <= '9') {
              val = val * 16 + (c-'0');
            } else {
              val = val * 16 + (c-'a') + 10;
            }
            c = java.lang.Character.toLowerCase(string.charAt(++i));
            if (val > java.lang.Character.MAX_VALUE) {
              throw new IllegalArgumentException("Invalid string " + string);
            }
          }
          if (c != ';') throw new IllegalArgumentException("Invalid string " + string);
          result.append((char) val);
          break;
        case 'a': result.append('\007'); break;
        case 'b': result.append('\b'); break;
        case 't': result.append('\t'); break;
        case 'n': result.append('\n'); break;
        case 'r': result.append('\r'); break;
        }
        break;
      default:
        result.append(c);
      }
    }
    return new String(result.toString());
  }
  
  @Override
  public int hashCode() {
    return s.hashCode();
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof String)) return false;
    return s.toString().equals(((String) obj).s.toString());
  }
}
