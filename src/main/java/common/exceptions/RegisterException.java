package common.exceptions;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Exception thrown the server failed to register a user with the provided credentials.
 */
public class RegisterException extends Exception {
  public RegisterException(String message) {
    super(message);
  }
}
