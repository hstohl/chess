package dataaccess;

import model.UserData;

public interface DataAccess {
  public UserData getUser(String username);

  public void addUser(UserData newUser) throws DataAccessException;

  public void clear();
}
