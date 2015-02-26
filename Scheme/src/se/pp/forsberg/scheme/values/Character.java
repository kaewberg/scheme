package se.pp.forsberg.scheme.values;

import java.util.HashMap;
import java.util.Map;

public class Character extends Value {
  char c;
  private static Map<java.lang.String, java.lang.Character> nameToChar = new HashMap<java.lang.String, java.lang.Character>();
  private static Map<java.lang.Character, java.lang.String> charToName= new HashMap<java.lang.Character, java.lang.String>();
  static {
    nameToChar.put("alarm", '\07');
    charToName.put('\07', "alarm");
    nameToChar.put("backspace", '\b');
    charToName.put('\b', "backspace");
    nameToChar.put("delete", '\177');
    charToName.put('\177', "delete");
    nameToChar.put("escape", '\033');
    charToName.put('\033', "escape");
    nameToChar.put("return", '\r');
    charToName.put('\r', "return");
    nameToChar.put("newline", '\n');
    charToName.put('\n', "newline");
    nameToChar.put("null", '\0');
    charToName.put('\0', "null");
    nameToChar.put("space", ' ');
    charToName.put(' ', "space");
    nameToChar.put("tab", '\t');
    charToName.put('\t', "tab");
  }
  public Character(char c) {
   this.c = c;
  }
  public char getCharacter() {
    return c;
  }
  private java.lang.String hexString(int i) {
    java.lang.String result = "";
    while (i != 0) {
      result = hex(i%16) + result;
      i /= 16;
    }
    return result;
  }
  private java.lang.String hex(int i) {
    if (i < 10) return java.lang.Character.toString((char)('0' + i));
    return java.lang.Character.toString((char)('a' + i - 10));
  }
  @Override
  public boolean isChar() {
    return true;
  }
  @Override
  public boolean equal(Value value) {
    return eqv(value);
  }
  @Override
  public boolean eqv(Value value) {
    if (!value.isChar()) return false;
    Character other = (Character) value;
    return c == other.c;
  }
  @Override
  public boolean eq(Value value) {
    return eqv(value);
  }
  public static Value parse(java.lang.String string) {
    if (!string.startsWith("#\\")) throw new IllegalArgumentException("Invalid character " + string);
    if (string.length() == 3) return new Character(string.charAt(2));
    if (string.charAt(2) == 'x') {
      if (string.length() == 3) return new Character('x');
      try {
        return new Character((char) java.lang.Integer.parseInt(string.substring(3), 16));
      } catch (NumberFormatException x) {
        throw new IllegalArgumentException("Expected hex character value");
      }
    }
    java.lang.String name = string.substring(2).toLowerCase();
    if (nameToChar.containsKey(name)) return new Character(nameToChar.get(name));
    throw new IllegalArgumentException("Invalid character name " + name);
  }
  
@Override
  public int hashCode() {
    return c;
  }
  @Override
  public java.lang.String toString() {
    if (c == ' ' || java.lang.Character.isISOControl(c) || java.lang.Character.isWhitespace(c)) {
      if (charToName.containsKey(c)) return "#\\" + charToName.get(c);
      return "#\\x" + hexString(c);
    }
    return "#\\" + c;
  }
}
