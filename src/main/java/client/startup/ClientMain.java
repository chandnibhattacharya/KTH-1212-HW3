package client.startup;

import client.view.Interpreter;
import common.FileServer;

import java.rmi.Naming;
/*  
*/
public class ClientMain {
  public static void main(String[] args) {
    try {
      FileServer server = (FileServer) Naming.lookup(FileServer.REGISTRY_NAME);
      new Interpreter().start(server);
    } catch (Exception e) {
      System.err.println("Failed to connect to server!");
      e.printStackTrace();
    }
  }
}
