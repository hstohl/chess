package dataaccess;

import model.AuthData;
import model.LogoutRequest;

public interface AuthDataAccess {
  public AuthData getAuth(String username);

  public AuthData getAuthT(String auth);

  public void addAuth(AuthData newAuth);

  public void removeAuth(AuthData auth);

  public void clear();
}
