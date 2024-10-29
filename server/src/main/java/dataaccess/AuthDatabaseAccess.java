package dataaccess;

import model.AuthData;

public class AuthDatabaseAccess implements AuthDataAccess {
  public AuthData getAuth(String username) {

    return new AuthData("t", "u");
  }

  public AuthData getAuthT(String auth) {

    return new AuthData("t", "u");
  }

  public void addAuth(AuthData newAuth) {

  }

  public void removeAuth(AuthData auth) {

  }

  public void clear() {

  }
}
