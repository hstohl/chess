package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
  private final DataAccess access = new UserDatabaseAccess();
  private final AuthDataAccess authAccess = new AuthDatabaseAccess();
  private final GameDataAccess gameAccess = new GameDatabaseAccess();
  private final GameDataAccess gameAccessMem = new GameMemoryDataAccess();


  @Test
  public void addAndGetUser() throws DataAccessException {
    access.clear();
    UserData user = new UserData("name", "password", "email");
    access.addUser(user);
    Assertions.assertEquals(user, access.getUser("name"));
  }

  @Test
  public void getUserNotThere() throws DataAccessException {
    access.clear();
    UserData user = new UserData("name", "password", "email");
    access.addUser(user);
    Assertions.assertNull(access.getUser("username"));
  }

  @Test
  public void addUserBad() {
    access.clear();
    UserData user = new UserData("name", null, "email");
    try {
      access.addUser(user);
    } catch (Exception e) {
      Assertions.assertTrue(e.getMessage().contains("unable to update database:"));
    }
  }


  @Test
  public void addAndGetAuth() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdhks", "username");
    authAccess.addAuth(auth);
    Assertions.assertEquals("askdhks", authAccess.getAuth("username").authToken());
  }

  @Test
  public void getAuthNotThere() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdhks", "username");
    authAccess.addAuth(auth);
    Assertions.assertNull(authAccess.getAuth("name"));
  }

  @Test
  public void addAuthBad() {
    authAccess.clear();
    AuthData auth = new AuthData(null, "username");
    try {
      authAccess.addAuth(auth);
    } catch (Exception e) {
      Assertions.assertTrue(e.getMessage().contains("unable to update database:"));
    }
  }

  @Test
  public void getAuthT() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdhksd", "username23");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username23", authAccess.getAuthT("askdhksd").username());
  }

  @Test
  public void getAuthTNotThere() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdhksd", "username23");
    authAccess.addAuth(auth);
    Assertions.assertNull(authAccess.getAuthT("lxns;ps"));
  }

  @Test
  public void removeAuth() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdh", "username27");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username27", authAccess.getAuthT("askdh").username());
    authAccess.removeAuth(auth);
    Assertions.assertNull(authAccess.getAuthT("askdh"));
  }

  @Test
  public void removeAuthNotThere() throws DataAccessException {
    authAccess.clear();
    AuthData auth = new AuthData("askdh", "username27");
    AuthData authFake = new AuthData("bb890", "myName");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username27", authAccess.getAuthT("askdh").username());
    try {
      authAccess.removeAuth(authFake);
    } catch (DataAccessException e) {
      Assertions.assertTrue(e.getMessage().contains("unable to update database:"));
    }
  }

  @Test
  public void addAndGetGame() throws DataAccessException {
    gameAccess.clear();
    GameData game = new GameData(2002, "whiteplayer", "blackplayer", "name66", new ChessGame());
    gameAccess.addGame(game);
    Assertions.assertEquals(game.game().getBoard(), gameAccess.getGame("name66").game().getBoard());
    Assertions.assertEquals(game.game().getBoard(), gameAccess.getGameI(2002).game().getBoard());
  }

  @Test
  public void getGameNotThere() throws DataAccessException {
    gameAccess.clear();
    GameData game = new GameData(2002, "whiteplayer", "blackplayer", "name66", new ChessGame());
    gameAccess.addGame(game);
    Assertions.assertNull(gameAccess.getGame("name400"));
    Assertions.assertNull(gameAccess.getGameI(2008));
  }


  @Test
  public void addGameBad() {
    gameAccess.clear();
    GameData game = new GameData(2002, "whiteplayer", "blackplayer", null, new ChessGame());
    try {
      gameAccess.addGame(game);
    } catch (DataAccessException e) {
      Assertions.assertTrue(e.getMessage().contains("unable to update database:"));
    }
  }

  @Test
  public void listGames() throws DataAccessException {
    gameAccess.clear();
    gameAccessMem.clear();
    GameData game1 = new GameData(2002, "whiteplayer1", "blackplayer1", "name1", new ChessGame());
    GameData game2 = new GameData(2005, "whiteplayer2", "blackplayer2", "name2", new ChessGame());
    GameData game3 = new GameData(2008, "whiteplayer3", "blackplayer3", "name3", new ChessGame());
    gameAccess.addGame(game1);
    gameAccessMem.addGame(game1);
    gameAccess.addGame(game2);
    gameAccessMem.addGame(game2);
    gameAccess.addGame(game3);
    gameAccessMem.addGame(game3);
    Assertions.assertEquals(gameAccess.listGames(), gameAccessMem.listGames());
  }

  @Test
  public void listGamesNotRight() throws DataAccessException {
    gameAccess.clear();
    gameAccessMem.clear();
    GameData game1 = new GameData(2002, "whiteplayer1", "blackplayer1", "name1", new ChessGame());
    GameData game2 = new GameData(2005, "whiteplayer2", "blackplayer2", "name2", new ChessGame());
    GameData game3 = new GameData(2008, "whiteplayer3", "blackplayer3", "name3", new ChessGame());
    GameData game4 = new GameData(2008, "whiteplayer7", "blackplayer3", "name3", new ChessGame());
    gameAccess.addGame(game1);
    gameAccessMem.addGame(game1);
    gameAccess.addGame(game2);
    gameAccessMem.addGame(game2);
    gameAccess.addGame(game3);
    gameAccessMem.addGame(game4);
    Assertions.assertNotEquals(gameAccess.listGames(), gameAccessMem.listGames());
  }

  @Test
  public void updateGame() throws DataAccessException {
    gameAccess.clear();
    GameData game = new GameData(1234, "whitey", "", "bigname", new ChessGame());
    GameData newGame = new GameData(1234, "whitey", "Blacky", "bigname", new ChessGame());
    gameAccess.addGame(game);

    gameAccess.updateGame(newGame);
    Assertions.assertEquals("Blacky", gameAccess.getGameI(1234).blackUsername());
  }

  @Test
  public void updateGameBad() throws DataAccessException {
    gameAccess.clear();
    GameData game = new GameData(1234, "whitey", "RealBlacky", "bigname", new ChessGame());
    GameData newGame = new GameData(1234, "whitey", "Blacky", null, new ChessGame());
    gameAccess.addGame(game);

    try {
      gameAccess.updateGame(newGame);
    } catch (DataAccessException e) {
      Assertions.assertTrue(e.getMessage().contains("unable to update database:"));
    }
  }
}
