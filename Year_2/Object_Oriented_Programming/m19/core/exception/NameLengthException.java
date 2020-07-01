package m19.core.exception;

/**
 * Class for representing a string length error.
 */
public class NameLengthException extends Exception {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201918111243L;

  /**
   * Default constructor
   */
  public NameLengthException() {
    // do nothing
  }

  /**
   * @param description
   */
  public NameLengthException(String description) {
    super(description);
  }

  /**
   * @param cause
   *            cause of exception
   */
  public NameLengthException(Exception cause) {
    super(cause);
  }

}
