package dataaccess;

import model.UserData;

public class UserDatabaseAccess implements DataAccess {
  public UserData getUser(String username) {

    return new UserData("u", "p", "e");
  }

  public void addUser(UserData newUser) {

  }

  public void clear() {

  }
}
