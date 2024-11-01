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
    authAccess.clear();
    AuthData auth = new AuthData("askdhks", "username");
    authAccess.addAuth(auth);
    Assertions.assertEquals("askdhks", authAccess.getAuth("username").authToken());
  }

  @Test
  public void getAuthT() {
    AuthData auth = new AuthData("askdhksd", "username23");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username23", authAccess.getAuthT("askdhksd").username());
  }

  @Test
  public void removeAuth() {
    AuthData auth = new AuthData("askdh", "username27");
    authAccess.addAuth(auth);
    Assertions.assertEquals("username27", authAccess.getAuthT("askdh").username());
    authAccess.removeAuth(auth);
    Assertions.assertNull(authAccess.getAuthT("askdh"));
  }

  @Test
  public void addGame() {
    gameAccess.clear();
    GameData game = new GameData(2002, "whiteplayer", "blackplayer", "name66", new ChessGame());
    gameAccess.addGame(game);
    Assertions.assertEquals(game.game().getBoard(), gameAccess.getGame("name66").game().getBoard());
    Assertions.assertEquals(game.game().getBoard(), gameAccess.getGameI(2002).game().getBoard());
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
