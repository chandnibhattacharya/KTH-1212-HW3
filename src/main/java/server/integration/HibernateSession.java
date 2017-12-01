package server.integration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Handles the hibernate <code>SessionFactory</code>.
 */
public abstract class HibernateSession {

  // Exceptions aren't thrown if initialized in static constructor...
  private static SessionFactory sessionFactory;

  /**
   * Initializes a <code>SessionFactory</code>.
   */
  public static void initSessionFactory() {
    if (sessionFactory == null)
      sessionFactory = new Configuration().configure().buildSessionFactory();
  }

  public static Session getSession() {
    return sessionFactory.openSession();
  }
}
