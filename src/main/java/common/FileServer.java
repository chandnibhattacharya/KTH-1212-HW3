package common;

import common.dto.CredentialDTO;
import common.dto.FileDTO;
import common.exceptions.RegisterException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Registry for RMI.
 */
public interface FileServer extends Remote {
  String REGISTRY_NAME = "file_server";

  /**
   * Users should be able to login.
   *
   * @param console The observer that should be notified of what happens.
   * @param credentialDTO The credentials used to login.
   * @return The id of the logged in user -> used for authentication.
   * @throws RemoteException When something goes wrong with the communication.
   * @throws LoginException When something goes wrong with the authentication.
   */
  long login(Listener console, CredentialDTO credentialDTO) throws RemoteException, LoginException;

  /**
   * Users should be able to register.
   *
   * @param console The observer that should be notified of what happens.
   * @param credentialDTO Credentials used to register.
   * @throws RemoteException When something goes wrong with the communication.
   * @throws RegisterException When something goes wrong with the registration.
   */
  void register(Listener console, CredentialDTO credentialDTO) throws RemoteException, RegisterException, LoginException;

  /**
   * Users should be able to view their files.
   *
   * @param userId The id of the user who wants to see their files or public files.
   * @throws RemoteException When something goes wrong with the communication.
   * @throws IllegalAccessException When the userId is invalid.
   */
  void list(long userId) throws RemoteException, IllegalAccessException;

  /**
   * Uploads the specified file.
   *
   * @param userId Id of the user who wants to upload the file.
   * @param fileDTO Container for file information.
   */
  void upload(long userId, FileDTO fileDTO) throws RemoteException, IllegalAccessException;

  FileDTO getFileInfo(long userId, String filename) throws RemoteException, IllegalAccessException;

  void download(long userId, String filename) throws IOException, IllegalAccessException;

  /**
   * @param userId The user to logout.
   */
  void logout(long userId) throws RemoteException, IllegalAccessException;
}
