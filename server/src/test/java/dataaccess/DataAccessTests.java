package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
  private final DataAccess access = new UserDatabaseAccess();
  private final AuthDataAccess authAccess = new AuthMemoryDataAccess();
  private final GameDataAccess gameAccess = new GameMemoryDataAccess();

  public DataAccessTests() throws DataAccessException {
  }

  @Test
  public void addUser() throws DataAccessException {
    access.clear();
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

  @Test
  public void getAuthT() {
    AuthData auth = new AuthData("askdhks", "username");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username", authAccess.getAuthT("askdhks").username());
  }

  @Test
  public void updateGame() {
    GameData game = new GameData(1234, "whitey", "", "bigname", new ChessGame());
    GameData newGame = new GameData(1234, "whitey", "Blacky", "bigname", new ChessGame());
    gameAccess.addGame(game);

    gameAccess.updateGame(newGame);
    Assertions.assertEquals("Blacky", gameAccess.getGameI(1234).blackUsername());
  }

  @Test
  public void updateGameColorTaken() {
    GameData game = new GameData(1234, "whitey", "RealBlacky", "bigname", new ChessGame());
    GameData newGame = new GameData(1234, "whitey", "Blacky", "bigname", new ChessGame());
    gameAccess.addGame(game);

    gameAccess.updateGame(newGame);
    Assertions.assertEquals("Blacky", gameAccess.getGameI(1234).blackUsername());
  }
}
