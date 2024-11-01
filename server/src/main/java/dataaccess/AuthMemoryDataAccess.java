package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class AuthMemoryDataAccess implements AuthDataAccess {
  private ArrayList<AuthData> myAuthData = new ArrayList<>();

  public AuthData getAuth(String username) {
    AuthData newAuth = null;
    for (AuthData auth : myAuthData) {
      if (Objects.equals(auth.username(), username)) {
        newAuth = auth;
      }
    }
    return newAuth;
  }

  public AuthData getAuthT(String token) {
    AuthData myAuth = null;
    for (AuthData auth : myAuthData) {
      if (Objects.equals(auth.authToken(), token)) {
        myAuth = auth;
      }
    }
    return myAuth;
  }

  public void addAuth(AuthData newAuth) {
    myAuthData.add(newAuth);
  }

  public void removeAuth(AuthData auth) {
    myAuthData.remove(auth);
  }

  public void removeAuthByUsername(String username) {

  }

  public void clear() {
    myAuthData.clear();
  }
}
