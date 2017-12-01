package server.net;

import common.dto.SocketIdentifierDTO;
import server.controller.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.SocketChannel;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Attaches a <code>SocketChannel</code> to a user who sends their id.
 */
class ClientHandler {
  ClientHandler(Controller controller, SocketChannel socketChannel) throws IOException {
    ObjectInputStream inputStream = new ObjectInputStream(socketChannel.socket().getInputStream());

    attachToUser(controller, inputStream, socketChannel);
  }

  private void attachToUser(Controller controller, ObjectInputStream inputStream, SocketChannel socketChannel) {
    System.out.println("Waiting for user resonse...");

    try {
      SocketIdentifierDTO identifier = (SocketIdentifierDTO) inputStream.readObject();
      controller.attachSocketToUser(identifier.getUserId(), socketChannel);

      System.out.println(String.format("userId: %d was connected with a socket!", identifier.getUserId()));
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
