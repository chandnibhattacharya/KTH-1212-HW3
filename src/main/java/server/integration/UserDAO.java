package server.integration;

import common.dto.CredentialDTO;
import common.exceptions.RegisterException;
import server.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;

/**
 * 
 */

public class UserDAO {
  private User userDao;

  private boolean userWithUsernameExists(String username) {
    try (Session session = User.getSession()) {
      Query query = session.createQuery("Select ua from User ua where ua.username=:username");
      query.setParameter("username", username);

      query.getSingleResult();

      return true;
    } catch (NoResultException e) {
      return false;
    }
  }

  /**
   * Registers a user using the <code>CredentialDTO</code> from the constructor.
   *
   * @throws RemoteException When something with the communication goes wrong.
   * @param credentials The credentials used to register.
   */
  public void register(CredentialDTO credentials) throws RemoteException, RegisterException {
    userDao = new User();

    // Not needed on certain databases.
    if (userWithUsernameExists(credentials.getUsername()))
      throw new RegisterException("Username already exists! try different one...");

    Session session = User.getSession();

    try {
      userDao.setUsername(credentials.getUsername());
      userDao.setPassword(credentials.getPassword());

      session.beginTransaction();
      session.save(userDao);

      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      e.printStackTrace();
      throw e;
    } finally {
      session.close();
    }
  }

  /**
   * Authenticates users.
   *
   * @return The id of the authenticated in user.
   * @throws LoginException When invalid credentials were provided.
   * @throws RemoteException When something with the communication goes wrong.
   */
  public User login(CredentialDTO credentials) throws LoginException, RemoteException {
    try (Session session = User.getSession()) {
      session.beginTransaction();
      Query query = session.createQuery("Select ua from User ua where ua.username=:username and ua.password=:password");

      query.setParameter("username", credentials.getUsername());
      query.setParameter("password", credentials.getPassword());

      return (User) query.getSingleResult();
    } catch (NoResultException e) {
      throw new LoginException("Wrong username or password!");
    }
  }
}
