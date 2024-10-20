package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
  public AuthData getAuth(String username);

  public void addAuth(AuthData newAuth);

  public void clear();
}
