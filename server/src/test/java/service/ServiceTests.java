package service;

import chess.ChessGame;
import com.google.protobuf.ServiceException;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import services.UserService;


public class ServiceTests {

  private final DataAccess dataAccess = new UserDatabaseAccess();
  private final AuthDataAccess authDataAccess = new AuthDatabaseAccess();
  private final GameDataAccess gameDataAccess = new GameDatabaseAccess();
  UserService service = new UserService(dataAccess, authDataAccess, gameDataAccess);

  public ServiceTests() throws DataAccessException {
  }

  @Test
  public void clearTest() throws DataAccessException {
    try {
      dataAccess.addUser(new UserData("u", "p", "e"));
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    authDataAccess.addAuth((new AuthData("a", "u")));
    gameDataAccess.addGame(new GameData(0, "wu",
            "bu", "gn", new ChessGame()));
    service.clear();
    Assertions.assertNull(dataAccess.getUser("u"));
    Assertions.assertNull(authDataAccess.getAuth("u"));
    Assertions.assertNull(gameDataAccess.getGameI(0));
  }

  @Test
  public void registerTest() throws ServiceException, DataAccessException {
    service.clear();
    service.registerUser(new UserData("u", "p", "e"));
    Assertions.assertTrue(BCrypt.checkpw("p", dataAccess.getUser("u").password()));
  }

  @Test
  public void registerFailTest() throws ServiceException, DataAccessException {
    service.clear();
    UserData try1 = new UserData("copy", "p", "e");
    UserData try2 = new UserData("copy", "p", "e");

    service.registerUser(try1);
    try {
      service.registerUser(try2);
    } catch (ServiceException e) {
      Assertions.assertEquals("Error: already taken", e.getMessage());
    }
  }


  @Test
  public void registerFailTest2() {
    service.clear();
    UserData try3 = new UserData("u", "", "e");

    try {
      service.registerUser(try3);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: bad request", e.getMessage());
    }
  }

  @Test
  public void authTest() throws DataAccessException {
    service.clear();

    authDataAccess.addAuth(new AuthData(service.newAuth(), "u"));
    Assertions.assertNotNull(authDataAccess.getAuth("u"));
  }

  @Test
  public void loginTest() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    UserData loginReq = new UserData("u", "p", "e");
    service.login(loginReq);
    Assertions.assertNotNull(authDataAccess.getAuth("u"));
  }

  @Test
  public void loginTestFail1() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    UserData loginReq = new UserData("u", "pas", "e");
    try {
      service.login(loginReq);
    } catch (ServiceException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
    }
  }

  @Test
  public void loginTestFail2() {
    service.clear();
    UserData loginReq = new UserData("u", "pas", "e");
    try {
      service.login(loginReq);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
    }
  }

  @Test
  public void logoutTest() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    service.logout(authDataAccess.getAuth("u").authToken());
    Assertions.assertNull(authDataAccess.getAuth("u"));
  }

  @Test
  public void logoutTestFail() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    UserData loginReq = new UserData("u", "p", "e");
    try {
      service.login(loginReq);
      service.logout("aaaaaa");
    } catch (ServiceException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
      Assertions.assertNotNull(authDataAccess.getAuth("u"));
    }
  }

  @Test
  public void createGame() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    UserData loginReq = new UserData("u", "p", "e");
    service.login(loginReq);

    NewGameRequest gameN = new NewGameRequest("MY GAME!");
    service.createGame(gameN, authDataAccess.getAuth("u").authToken());

    Assertions.assertNotNull(gameDataAccess.getGame(gameN.gameName()));
    Assertions.assertEquals("MY GAME!", gameDataAccess.getGame(gameN.gameName()).gameName());
  }

  @Test
  public void createGameFail() throws ServiceException, DataAccessException {
    service.clear();
    UserData user1 = new UserData("u", "p", "e");
    service.registerUser(user1);
    UserData loginReq = new UserData("u", "p", "e");
    service.login(loginReq);

    try {
      NewGameRequest gameN = new NewGameRequest("MY GAME!");
      service.createGame(gameN, "gg");
    } catch (ServiceException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
    }
  }

  @Test
  public void joinGame() throws ServiceException, DataAccessException {
    service.clear();
    GameData newGame = new GameData(1234, null,
            null, "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    authDataAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);

    service.joinGame("g", joinGame);

    Assertions.assertEquals("New User", gameDataAccess.getGame("My Named Game").whiteUsername());
  }

  @Test
  public void joinGameFail1() throws DataAccessException {
    service.clear();
    GameData newGame = new GameData(1234, null,
            null, "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    authDataAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);

    try {
      service.joinGame("h", joinGame);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
      Assertions.assertNull(gameDataAccess.getGame("My Named Game").whiteUsername());
    }
  }

  @Test
  public void joinGameFail2() throws DataAccessException {
    service.clear();
    GameData newGame = new GameData(1234, null,
            null, "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    authDataAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1237);

    try {
      service.joinGame("g", joinGame);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: bad request", e.getMessage());
      Assertions.assertNull(gameDataAccess.getGame("My Named Game").whiteUsername());
    }
  }

  @Test
  public void joinGameFail3() throws DataAccessException {
    service.clear();
    GameData newGame = new GameData(1234, "o",
            null, "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    authDataAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);

    try {
      service.joinGame("g", joinGame);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: already taken", e.getMessage());
      Assertions.assertNotEquals("New User",
              gameDataAccess.getGame("My Named Game").whiteUsername());
    }
  }

  @Test
  public void joinGameFail4() throws DataAccessException {
    service.clear();
    GameData newGame = new GameData(1234, null,
            "b", "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    authDataAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1234);

    try {
      service.joinGame("g", joinGame);
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: already taken", e.getMessage());
      Assertions.assertNotEquals("New User",
              gameDataAccess.getGame("My Named Game").blackUsername());
    }
  }

  @Test
  public void listGames() throws ServiceException, DataAccessException {
    service.clear();
    authDataAccess.addAuth(new AuthData("g", "New User"));

    GameData newGame = new GameData(1234, null,
            "b", "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    GameData newGame2 = new GameData(1547, "yellow",
            "l", "My New Game", new ChessGame());
    gameDataAccess.addGame(newGame2);

    GameList list = service.listGames("g");
    Assertions.assertEquals(2, list.games().size());
  }

  @Test
  public void listGamesFail() throws DataAccessException {
    service.clear();
    authDataAccess.addAuth(new AuthData("g", "New User"));

    GameData newGame = new GameData(1234, null,
            "b", "My Named Game", new ChessGame());
    gameDataAccess.addGame(newGame);
    GameData newGame2 = new GameData(1547, "yellow",
            "l", "My New Game", new ChessGame());
    gameDataAccess.addGame(newGame2);

    try {
      service.listGames("hjhvugc");
    } catch (ServiceException | DataAccessException e) {
      Assertions.assertEquals("Error: unauthorized", e.getMessage());
    }
  }

}
