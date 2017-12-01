package client.view;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Defines all commands that can be performed by a user.
 */
public enum Command {
  /**
   * Login.
   */
  LOGIN,
  /**
   * Register.
   */
  REGISTER,
  /**
   * Lists all files in the directory
   */
  LIST,
  /**
   * Downloads the specified file.
   */
  DOWNLOAD,
  /**
   * Uploads the specified file.
   */
  UPLOAD,
  /**
   * Leave the application.
   */
  QUIT,
  /**
   * Prints the last stacktrace
   */
  TRACE,
}
