package se.pp.forsberg.scheme.values.errors;

public class FileError extends Error {
  public FileError(Throwable x) {
    super(x);
  }

  public FileError(java.lang.String string) {
    super(string);
  }
}
