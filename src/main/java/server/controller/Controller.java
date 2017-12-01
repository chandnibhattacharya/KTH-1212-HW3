package server.controller;

import common.Listener;
import common.*;
import common.dto.CredentialDTO;
import common.dto.FileDTO;
import common.exceptions.RegisterException;
import common.net.FileTransferHandler;
import server.integration.FileDAO;
import server.model.ClientManager;
import server.model.File;

import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Controller for the file server which is directly called by other nodes.
 */
public class Controller extends UnicastRemoteObject implements FileServer {
  private final Map<Long, ClientManager> clients = new ConcurrentHashMap<>();

  public Controller() throws RemoteException {
  }

  private ClientManager auth(long userId) throws IllegalAccessException {
    if (!clients.containsKey(userId))
      throw new IllegalAccessException("You must be logged in to do that!");

    return clients.get(userId);
  }

  /**
   * @see FileServer#login(Listener, CredentialDTO)
   */
  @Override
  public long login(Listener console, CredentialDTO credentialDTO) throws RemoteException, LoginException {
    ClientManager client = new ClientManager();
    client.addListener(console);

    long id = client.login(credentialDTO);
    clients.put(id, client);

    return id;
  }

  /**
   * @see FileServer#register(Listener, CredentialDTO)
   */
  @Override
  public void register(Listener console, CredentialDTO credentials) throws RemoteException, RegisterException, LoginException {
    ClientManager client = new ClientManager();
    client.register(credentials);
  }

  /**
   * @see FileServer#list(long)
   */
  @Override
  public void list(long userId) throws RemoteException, IllegalAccessException {
    ClientManager client = auth(userId);
    FileDAO fileDAO = new FileDAO();

    for (File file : fileDAO.getFiles(client.getUser())) {
      StringJoiner msg = new StringJoiner(", ");
      msg.add("Name: " + file.getName());
      msg.add("Size: " + file.getSize() + " Bytes");
      msg.add("Owner: " + file.getOwner().getUsername());
      msg.add("Public: " + file.isPublicAccess());
      msg.add("Read: " + file.isReadable());
      msg.add("Writable: " + file.isWritable());
      client.alertListeners(msg.toString());
    }
  }

  /**
   * Uploads the provided file.
   *
   * @param userId Id of the user who wants to upload the file.
   * @param fileDTO Container for file information.
   * @throws RemoteException If something goes wrong with the connection.
   * @throws IllegalAccessException If the user is not allowed to upload the file.
   */
  @Override
  public void upload(long userId, FileDTO fileDTO) throws RemoteException, IllegalAccessException {
    ClientManager user = auth(userId);
    FileDAO fileDAO = new FileDAO();

    try {
      File file = fileDAO.getFileByName(fileDTO.getFilename());

      if (file.getOwner().getId() == user.getUser().getId()) {
        fileDAO.update(fileDTO);
        uploadFile(user, fileDTO);
      } else if (!file.isPublicAccess()) {
        throw new IllegalAccessException("You're not the owner and the file is not public!");
      } else if (!file.isWritable()) {
        throw new IllegalAccessException("You're not the owner of the file and the file is not writable!");
      } else {
        fileDAO.updateFileSize(fileDTO);
        uploadFile(user, fileDTO);

        String alertMsg = String.format("The user \"%s\" has updated your public writable file: \"%s\"",
          user.getUser().getUsername(),
          fileDTO.getFilename());

        clients.get(file.getOwner().getId())
          .alertListeners(alertMsg);
      }
    } catch (NoResultException e) {
      // File doesn't exist and we're allowed to do whatever
      fileDAO.insert(user, fileDTO);
      uploadFile(user, fileDTO);
    }
  }

  @Override
  public FileDTO getFileInfo(long userId, String filename) throws RemoteException, IllegalAccessException {
    ClientManager user = auth(userId);
    FileDAO fileDAO = new FileDAO();

    File file = fileDAO.getFileByName(filename);

    if (file.getOwner().getId() == user.getUser().getId()) {
      return new FileDTO(file);
    } else if (!file.isPublicAccess()) {
      throw new IllegalAccessException("You're not the owner and the file is not public!");
    } else if (!file.isReadable()) {
      throw new IllegalAccessException("You're not the owner of the file and the file is not readable!");
    } else {
      return new FileDTO(file);
    }
  }

  @Override
  public void download(long userId, String filename) throws IOException, IllegalAccessException {
    ClientManager user = auth(userId);
    FileDAO fileDAO = new FileDAO();

    File file = fileDAO.getFileByName(filename);
    Path serverFilePath = Paths.get("server_files/" + filename);

    if (file.getOwner().getId() == user.getUser().getId()) {
      FileTransferHandler.sendFile(user.getSocketChannel(), serverFilePath);
    } else if (!file.isPublicAccess()) {
      throw new IllegalAccessException("You're not the owner and the file is not public!");
    } else if (!file.isReadable()) {
      throw new IllegalAccessException("You're not the owner of the file and the file is not readable!");
    } else {
      FileTransferHandler.sendFile(user.getSocketChannel(), serverFilePath);
    }
  }

  @Override
  public void logout(long userId) throws RemoteException, IllegalAccessException {
    auth(userId);

    clients.remove(userId);
  }

  private void uploadFile(ClientManager client, FileDTO file) {
    CompletableFuture.runAsync(() -> {
      try {
        FileTransferHandler
          .receiveFile(client.getSocketChannel(), Paths.get("server_files/" + file.getFilename()), file.getSize());

        client.alertListeners("Your file has been uploaded!");
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * @see ClientManager#attachSocketHandler(SocketChannel)
   */
  public void attachSocketToUser(long userId, SocketChannel socketChannel) throws RemoteException {
    clients.get(userId).attachSocketHandler(socketChannel);
  }
}
