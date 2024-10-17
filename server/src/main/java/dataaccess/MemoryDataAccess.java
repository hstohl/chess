package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess {
  private ArrayList<UserData> myUserData = new ArrayList<>();

  public UserData getUser(String username) {
    UserData newUser = null;
    for (UserData data : myUserData) {
      if (Objects.equals(data.username(), username)) {
        newUser = data;
      }
    }
    return newUser;
  }

  public void addUser(UserData newUser) {
    myUserData.add(newUser);
  }
}
