package client.view;

import common.*;
import common.dto.CredentialDTO;
import common.dto.FileDTO;
import common.dto.SocketIdentifierDTO;
import common.exceptions.RegisterException;
import common.net.FileTransferHandler;
import server.integration.FileDAO;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter implements Runnable {
  private FileServer server;
  private boolean running = false;
  private Console console;
  private CmdLineParser parser;
  private long userId;
  private SocketChannel socket;

  /**
   * Author Syed Arif Rahman
   * arifkh77@yahoo.com
   * Starts a new interpreter on a separate thread.
   *
   * @param server The server registry to communicate with.
   */
  public void start(FileServer server) throws RemoteException {
    this.server = server;

    if (running) return;

    running = true;
    console = new Console();
    new Thread(this).start();
  }

  /**
   * Main interpreter loop on a separate thread.
   * Waits for user input and then evaluates the command accordingly.
   */
  @Override
  public void run() {
    while (running) {
      try {
        parser = new CmdLineParser(console.readNextLine());

        switch (parser.getCmd()) {
          case LOGIN:     login();    break;
          case REGISTER:  register(); break;
          case LIST:      list();     break;
          case UPLOAD:    upload();   break;
          case DOWNLOAD:  download(); break;
          case TRACE:     console.printTrace(); break;
          case QUIT:
            server.logout(userId);
            console.disconnect();
            UnicastRemoteObject.unexportObject(console, true);
            running = false;
            break;
          default:
            throw new InvalidCommandException("The provided command does not exist!");
        }
      } catch (Exception e) {
        console.error(e.getMessage(), e);
      }
    }
  }

  private void download() throws IOException, IllegalAccessException, InvalidCommandException {
    try {
      String filename = parser.getArg(0);

      FileDTO serverFileInfo = server.getFileInfo(userId, filename);
      server.download(userId, filename);

      Path savePath = Paths.get("client_files/" + filename);
      FileTransferHandler.receiveFile(socket, savePath, serverFileInfo.getSize());
      console.print("Download Complete!");
    } catch (InvalidCommandException e) {
      throw new InvalidCommandException(
        "Invalid download command!\n" +
          "the correct command is:\n" +
          "download <server filename:string>");
    }
  }

  private void upload() throws IOException, InvalidCommandException, IllegalAccessException {
    try {
      String localFilename = parser.getArg(0);

      Path filePath = Paths.get(String.format("client_files/%s", localFilename));

      if (!Files.exists(filePath))
        throw new FileNotFoundException(String.format("The file \"%s\" does not exist!", localFilename));

      long fileSize = Files.size(filePath);

      String serverFilename = parser.getArg(1);
      boolean publicAccess = Boolean.valueOf(parser.getArg(2));
      boolean readable = Boolean.valueOf(parser.getArg(3));
      boolean writable = Boolean.valueOf(parser.getArg(4));

      FileDTO serverFile = new FileDTO(userId, serverFilename, fileSize, publicAccess, readable, writable);

      server.upload(userId, serverFile);

      FileTransferHandler.sendFile(socket, filePath);
    } catch (InvalidCommandException e) {
      throw new InvalidCommandException(
        "Invalid upload command!\n" +
          "the correct command is:\n" +
          "upload <local filename:string> <upload filename:string> <public:boolean> <read:boolean> <write:boolean>");
    }
  }

  private void list() throws RemoteException, IllegalAccessException {
    server.list(userId);
  }

  private void register() throws IOException, RegisterException, InvalidCommandException, LoginException {
    try {
      CredentialDTO credentialDTO = createCredentials(parser);
      server.register(console, credentialDTO);
      login();
    } catch (InvalidCommandException e) {
      throw new InvalidCommandException(
        "Invalid register command!\n" +
          "the correct command is:\n" +
          "register <username:string> <password:string>");
    }
  }

  private void login() throws IOException, LoginException, InvalidCommandException {
    try {
      userId = server.login(console, createCredentials(parser));

      createServerSocket(userId);
    } catch (InvalidCommandException e) {
      throw new InvalidCommandException(
        "Invalid login command!\n" +
          "The correct command is:\n" +
          "login <username> <password>");
    }
  }

  private void createServerSocket(long userId) throws IOException {
    // Create the actual socket
    socket = SocketChannel.open();
    socket.connect(new InetSocketAddress(Constants.SERVER_ADDRESS, Constants.SERVER_SOCKET_PORT));

    // Lets identify this socket with the current user to the server.
    ObjectOutputStream output = new ObjectOutputStream(socket.socket().getOutputStream());

    output.writeObject(new SocketIdentifierDTO(userId));
    output.flush();
  }

  private CredentialDTO createCredentials(CmdLineParser parser) throws InvalidCommandException {
    String username = parser.getArg(0);
    String password = parser.getArg(1);

    return new CredentialDTO(username, password);
  }

  public class Console extends UnicastRemoteObject implements Listener {
    private static final String PROMPT = "> ";
    private final ThreadSafeStdOut outMsg = new ThreadSafeStdOut();
    private final Scanner console = new Scanner(System.in);
    private final Stack<Exception> exceptionList = new Stack<>();

    Console() throws RemoteException {
    }

    @Override
    public void print(String msg) throws RemoteException {
      outMsg.println("\r" + msg);
      outMsg.print(PROMPT);
    }

    @Override
    public void error(String error, Exception e) {
      exceptionList.push(e);

      outMsg.println("ERROR:");
      outMsg.println(error);
    }

    @Override
    public void disconnect() throws RemoteException {
      print("You are now disconnected!");
    }

    String readNextLine() throws RemoteException {
      outMsg.print(PROMPT);

      return console.nextLine();
    }

    void printTrace() throws RemoteException {
      exceptionList.pop().printStackTrace();
    }
  }
}
