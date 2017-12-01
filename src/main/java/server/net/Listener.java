package server.net;

import common.Constants;
import server.controller.Controller;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

/**
 * 
 * Handles incoming file transfer sockets.
 */
public class Listener implements Runnable {
  private Controller controller;

  public Listener(Controller controller) {
    this.controller = controller;

    Thread listener = new Thread(this);
    listener.setPriority(Thread.MAX_PRIORITY);
    listener.start();
  }

  @Override
  public void run() {
    try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
      serverSocket.socket().bind(new InetSocketAddress(Constants.SERVER_SOCKET_PORT));

      while (true) {
        System.out.println("Listening...");
        SocketChannel client = serverSocket.accept();
        System.out.println("Connection established: " + client.getRemoteAddress());

        CompletableFuture.runAsync(() -> {
          try {
            new ClientHandler(controller, client);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      }
    } catch (BindException e) {
      System.err.println(String.format("Port %d is already in use!", Constants.SERVER_SOCKET_PORT));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
