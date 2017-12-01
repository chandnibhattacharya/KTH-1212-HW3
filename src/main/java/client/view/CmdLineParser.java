package client.view;

import java.util.Arrays;

/**
 
 * Parses client commands.
 */
class CmdLineParser {
  private static final String PARAM_DELIMITER = " ";
  private String[] params;
  private Command cmd;

  CmdLineParser(String enteredLine) throws InvalidCommandException {
    parseCommands(enteredLine);
    parseArgs(enteredLine);
  }

  private void parseArgs(String enteredLine) {
    if (enteredLine == null) {
      params = null;
      return;
    }

    params = enteredLine.split(PARAM_DELIMITER);
    // Remove the command
    params = Arrays.copyOfRange(params, 1, params.length);
  }

  private void parseCommands(String enteredLine) throws InvalidCommandException {
    if (enteredLine == null) return;

    String[] splitText = enteredLine.split(PARAM_DELIMITER);

    try {
      cmd = Command.valueOf(splitText[0].toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException("\"" + splitText[0].toLowerCase() + "\" is not a valid command!");
    }
  }

  /**
   * @return The current parsed command.
   */
  Command getCmd() {
    return cmd;
  }

  /**
   * Retrieves the argument at the specified index
   *
   * @param i The index of the argument
   * @return The argument or null if it doesn't exist
   */
  String getArg(int i) throws InvalidCommandException {
    if (params == null) return null;
    if (params.length <= i || params.length == 0) throw new InvalidCommandException("Command misuse!");

    return params[i];
  }
}
