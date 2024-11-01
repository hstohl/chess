package dataaccess;

import model.AuthData;
import model.LogoutRequest;

public interface AuthDataAccess {
  public AuthData getAuth(String username);

  public AuthData getAuthT(String auth);

  public void addAuth(AuthData newAuth) throws DataAccessException;

  public void removeAuth(AuthData auth) throws DataAccessException;

  public void clear();
}
