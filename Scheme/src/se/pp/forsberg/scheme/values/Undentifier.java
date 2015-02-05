package se.pp.forsberg.scheme.values;

public class Undentifier extends Identifier {

  public static final Identifier INPUT_PORT = new Undentifier("current-input-port");
  public static final Identifier OUTPUT_PORT = new Undentifier("current-output-port");
  public static final Identifier ERROR_PORT = new Undentifier("current-error-port");
  public static final Identifier ERROR_HANDLER = new Undentifier("current-error-handler");
  public static final Identifier WIND = new Undentifier("current-wind");

  private Undentifier(CharSequence value) {
    super(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Undentifier)) return false;
    return super.equals(obj);
  }

}
