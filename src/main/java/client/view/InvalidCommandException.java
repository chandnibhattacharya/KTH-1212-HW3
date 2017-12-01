package client.view;

/**
 * 
 * Exception for invalid commands.
 */
class InvalidCommandException extends Exception {
  InvalidCommandException(String message) {
    super(message);
  }
}
