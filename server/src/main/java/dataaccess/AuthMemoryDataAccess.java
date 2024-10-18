package dataaccess;

import model.AuthData;
import model.UserData;

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

  public void addAuth(AuthData newAuth) {
    myAuthData.add(newAuth);
  }
}
