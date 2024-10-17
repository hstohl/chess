package dataaccess;

import model.UserData;

public class MemoryDataAccess implements DataAccess {
  public UserData getUser(String username) {
    UserData newUser = new UserData(username, "password", "email");
    return newUser;
  }

  public void addUser(UserData newUser) {

  }
}
