package server.model;

import server.integration.HibernateSession;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Data access object used to communicate with the database.
 */
@Entity(name = "User")
public class User extends HibernateSession {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Column(unique = true, nullable = false)
  private String username;
  @Column(nullable = false)
  private String password;
  @OneToMany(mappedBy = "owner")
  private Collection<File> files = new ArrayList<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Collection<File> getFiles() {
    return files;
  }

  public void setFiles(Collection<File> files) {
    this.files = files;
  }
}
