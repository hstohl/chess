package services;

import com.google.protobuf.ServiceException;
import dataaccess.AuthDataAccess;
import dataaccess.AuthMemoryDataAccess;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.LogoutRequest;
import model.UserData;

import java.util.Objects;
import java.util.Random;

public class UserService {
  private final DataAccess dataAccess;
  private final AuthDataAccess authDataAccess = new AuthMemoryDataAccess();

  public UserService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public void clear() {
    dataAccess.clear();
    authDataAccess.clear();
  }

  public AuthData registerUser(UserData newUser) throws ServiceException {
    if (dataAccess.getUser(newUser.username()) != null) {
      throw new ServiceException("User Already Exists");
    }

    dataAccess.addUser(newUser);

    AuthData auth = newAuth(newUser.username());
    authDataAccess.addAuth(auth);

    return authDataAccess.getAuth(newUser.username());
  }

  public AuthData newAuth(String username) { //40 to 122
    Random rnd = new Random();
    String authToken = "";
    for (int i = 0; i < 11; ++i) {
      int rndAsciiValue = rnd.nextInt(83) + 40;
      authToken = authToken + (char) rndAsciiValue;
    }

    return new AuthData(authToken, username);
  }

  public AuthData login(UserData user) throws ServiceException {
    UserData realUser = dataAccess.getUser(user.username());

    if (dataAccess.getUser(user.username()) == null) {
      throw new ServiceException("Error: User Doesn't Exist");
    }
    if (!Objects.equals(realUser.password(), user.password())) {
      throw new ServiceException("Error: Password doesn't match");
    }

    AuthData auth = newAuth(user.username());
    authDataAccess.addAuth(auth);

    return authDataAccess.getAuth(user.username());
  }

  public void logout(String auth) throws ServiceException {
    AuthData authData = authDataAccess.getAuthT(auth);

    if (authData == null) {
      throw new ServiceException("Error: Unauthorized");
    }

    authDataAccess.removeAuth(authData);
  }
}
