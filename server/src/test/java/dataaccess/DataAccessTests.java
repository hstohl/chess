package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
  private final DataAccess access = new MemoryDataAccess();
  private final AuthDataAccess authAccess = new AuthMemoryDataAccess();

  @Test
  public void addUser() {
    UserData user = new UserData("name", "password", "email");
    access.addUser(user);
    Assertions.assertEquals(user, access.getUser("name"));
  }

  @Test
  public void addAuth() {
    AuthData auth = new AuthData("askdhks", "username");
    authAccess.addAuth(auth);
    Assertions.assertEquals("askdhks", authAccess.getAuth("username").authToken());
  }
}
