package server.model;

import common.Listener;
import common.dto.CredentialDTO;
import common.exceptions.RegisterException;
import server.integration.UserDAO;

import javax.security.auth.login.LoginException;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 
 * Handles a connected client.
 */
public class ClientManager {
  private final static UserDAO userDAO = new UserDAO();
  private User userInfo;
  private SocketChannel socketChannel;
  private List<Listener> listeners = new ArrayList<>();

  /**
   * @see UserDAO#login(CredentialDTO)
   */
  public long login(CredentialDTO credentials) throws LoginException, RemoteException {
    userInfo = userDAO.login(credentials);
    alertListeners("Welcome "+ userInfo.getUsername()+" Login Success");

    return userInfo.getId();
  }

  /**
   * @see UserDAO#register(CredentialDTO)
   */
  public void register(CredentialDTO credentials) throws RemoteException, RegisterException {
    userDAO.register(credentials);

    alertListeners("Successfully registered...");
  }

  /**
   * Attaches the provided <code>SocketChannel</code> to this user.
   *
   * @param socketChannel The client to attach to the user.
   * @throws RemoteException When something goes wrong with the connection.
   */
  public void attachSocketHandler(SocketChannel socketChannel) throws RemoteException {
    this.socketChannel = socketChannel;
    alertListeners("A socket has been connect to you client!");
  }

  /**
   * Alerts all observers of an event.
   *
   * @param message The message to pass to the listeners.
   * @throws RemoteException When something with the communication goes wrong.
   */
  public void alertListeners(String message) throws RemoteException {
    CompletableFuture.runAsync(() -> listeners.forEach(listener -> {
      try {
        listener.print(message);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }));
  }

  /**
   * Adds an observer to be alerted on events.
   *
   * @param console Where to print.
   */
  public void addListener(Listener console) {
    listeners.add(console);
  }

  public User getUser() {
    return userInfo;
  }

  public SocketChannel getSocketChannel() {
    return socketChannel;
  }
}
