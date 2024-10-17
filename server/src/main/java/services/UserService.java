package services;

import com.google.protobuf.ServiceException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

public class UserService {
  private final DataAccess dataAccess;

  public UserService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public UserData registerUser(UserData newUser) throws ServiceException {
    if (dataAccess.getUser(newUser.username()) != null) {
      throw new ServiceException("User Already Exists");
    }

    dataAccess.addUser(newUser);

    return dataAccess.getUser(newUser.username());
  }

  public AuthData login(UserData user) {

    return new AuthData("a", "u");
  }

  public void logout(AuthData auth) {

  }
}
