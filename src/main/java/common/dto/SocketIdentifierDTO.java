package common.dto;

import java.io.Serializable;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Used to associate a user to a socket.
 */
public class SocketIdentifierDTO implements Serializable {

  private long userId;

  public SocketIdentifierDTO(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    return userId;
  }
}
