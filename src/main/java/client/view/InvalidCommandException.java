package client.view;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Exception for invalid commands.
 */
class InvalidCommandException extends Exception {
  InvalidCommandException(String message) {
    super(message);
  }
}
