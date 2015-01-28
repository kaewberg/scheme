package se.pp.forsberg.scheme.values;

public class Label extends Identifier {
  private int label;
  public Label(int label) {
    super("#" + label + "#");
    this.label = label;
  }
  public int getLabel() { return label; }
  @Override
  public java.lang.String toString() {
    return "#" + label + "#";
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Label)) return false;
    return label == ((Label)obj).label;
  }
}
