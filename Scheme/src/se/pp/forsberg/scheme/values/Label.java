package se.pp.forsberg.scheme.values;

public class Label extends Identifier {
  private int label;
  private boolean reference;
  public Label(int label, boolean reference) {
    super("#" + label + "#");
    this.label = label;
    this.reference = reference;
  }
  public int getLabel() { return label; }
  public boolean isReference() { return reference; }
  @Override
  public java.lang.String toString() {
    return "#" + label + (reference? "#" : "=");
  }
  
  @Override
  public int hashCode() {
    return label;
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Label)) return false;
    return label == ((Label)obj).label; // && reference == ((Label)obj).reference;
  }
}
