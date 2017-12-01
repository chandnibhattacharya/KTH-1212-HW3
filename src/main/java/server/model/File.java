package server.model;

import server.integration.HibernateSession;

import javax.persistence.*;

/**
 * 
 * Data access object used to communicate with the database.
 */
@Entity(name = "File")
public class File extends HibernateSession {
  private long id;
  private String name;
  private long size;
  private User owner;
  private boolean publicAccess = false;
  private boolean writable = false;
  private boolean readable = false;

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Column(unique = true, nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(nullable = false)
  public long getSize() {
    return size;
  }

  
  public void setSize(long size) {
    this.size = size;
  }

  public boolean isPublicAccess() {
    return publicAccess;
  }

  public void setPublicAccess(boolean publicAccess) {
    this.publicAccess = publicAccess;
  }

  public boolean isWritable() {
    return writable;
  }

  public void setWritable(boolean write) {
    this.writable = write;
  }

  public boolean isReadable() {
    return readable;
  }

  public void setReadable(boolean read) {
    this.readable = read;
  }

  @ManyToOne(optional = false)
  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }
}
