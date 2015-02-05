//package se.pp.forsberg.scheme.values;
//
//public class LabelReference extends Identifier {
//  private int label;
//  public LabelReference(int label) {
//    super("#" + label + "#");
//    this.label = label;
//  }
//  public int getLabel() { return label; }
//  @Override
//  public java.lang.String toString() {
//    return "#" + label + "#";
//  }
//  
//  @Override
//  public boolean equals(Object obj) {
//    if (!(obj instanceof LabelReference)) return false;
//    return label == ((LabelReference)obj).label;
//  }
//}
