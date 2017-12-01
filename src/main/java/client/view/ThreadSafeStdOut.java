package client.view;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Thread safe printing.
 */
class ThreadSafeStdOut {
  /**
   * Thread safe printing.
   *
   * @param output The output message to print.
   */
  synchronized void print(String output) {
    System.out.print(output);
  }

  /**
   * Thread safe printing with line break.
   *
   * @param output The output message to print.
   */
  synchronized void println(String output) {
    System.out.println(output);
  }
}
