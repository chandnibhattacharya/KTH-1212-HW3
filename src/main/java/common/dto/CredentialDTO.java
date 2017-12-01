package common.dto;

import java.io.Serializable;

/**
 * Author Syed Arif Rahman
 * arifkh77@yahoo.com
 * Class used to encapsulate the login credentials.
 */
public class CredentialDTO implements Serializable {
  private String username;
  private String password;

  public CredentialDTO(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
