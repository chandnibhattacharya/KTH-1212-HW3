package server.integration;

import common.dto.FileDTO;
import server.model.ClientManager;
import server.model.File;
import server.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 */

public class FileDAO {
  /**
   * @param filename The file to lookup.
   * @return <code>File</code> containing various file information.
   */
  public File getFileByName(String filename) {
    Session session = File.getSession();

    Query query = session.createQuery("Select file from File file where file.name=:filename");
    query.setParameter("filename", filename);

    return (File) query.getSingleResult();
  }

  /**
   * Inserts a new file record in the database.
   * @param client The user who owns the object.
   * @param fileDTO Information about the file to insert.
   */
  public void insert(ClientManager client, FileDTO fileDTO) {
    Session session = File.getSession();

    try {
      session.beginTransaction();

      File file = new File();
      file.setOwner(client.getUser());
      file.setName(fileDTO.getFilename());
      file.setPublicAccess(fileDTO.isPublicAccess());
      file.setSize(fileDTO.getSize());
      file.setReadable(fileDTO.isReadable());
      file.setWritable(fileDTO.isWritable());

      session.save(file);
      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
    }
  }

  public void update(FileDTO fileDTO) {
    Session session = File.getSession();

    try {
      session.beginTransaction();

      File file = getFileByName(fileDTO.getFilename());
      file.setPublicAccess(fileDTO.isPublicAccess());
      file.setReadable(fileDTO.isReadable());
      file.setWritable(fileDTO.isWritable());
      file.setSize(fileDTO.getSize());

      session.update(file);
      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
    }
  }

  public void updateFileSize(FileDTO fileDTO) {
    Session session = File.getSession();

    try {
      session.beginTransaction();

      File file = getFileByName(fileDTO.getFilename());
      file.setSize(fileDTO.getSize());

      session.update(file);
      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
    }
  }

  @SuppressWarnings("unchecked")
  public List<File> getFiles(User user) {
    Session session = File.getSession();

    Query query = session.createQuery("Select file from File file where file.owner=:user or file.publicAccess = true");
    query.setParameter("user", user);

    return query.getResultList();
  }
}
