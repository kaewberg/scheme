package se.pp.forsberg.scheme;

import se.pp.forsberg.scheme.values.Procedure;

public class DynamicWind {
  public DynamicWind(DynamicWind parent, Procedure before, Procedure after) {
    super();
    this.parent = parent;
    this.before = before;
    this.after = after;
  }
  private DynamicWind parent;
  private Procedure before, after;
  public DynamicWind getParent() {
    return parent;
  }
  public Procedure getBefore() {
    return before;
  }
  public Procedure getAfter() {
    return after;
  }
}
