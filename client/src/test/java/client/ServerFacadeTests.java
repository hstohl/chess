package client;

import chess.ChessGame;
import com.google.protobuf.ServiceException;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

  private static Server server;
  static ServerFacade facade;
  private final DataAccess userAccess = new UserDatabaseAccess();
  private final AuthDataAccess authAccess = new AuthDatabaseAccess();
  private final GameDataAccess gameAccess = new GameDatabaseAccess();


  @BeforeAll
  public static void init() {
    server = new Server();
    var port = server.run(0);
    System.out.println("Started test HTTP server on " + port);
    String url = "http://localhost:" + port;
    facade = new ServerFacade(url);
  }

  @AfterAll
  static void stopServer() {
    server.stop();
  }

  @BeforeEach
  void clear() throws Exception {
    facade.clear();
  }

  @Test
  void register() throws Exception {
    var authData = facade.register(new UserData("hstohl", "password", "email@email.com"));
    assertTrue(authData.authToken().length() > 10);
    assertEquals("hstohl", authAccess.getAuthT(authData.authToken()).username());
  }

  @Test
  void registerFail() {
    try {
      facade.register(new UserData("hstohl", "b", "email"));
      facade.register(new UserData("hstohl", "b", "email"));
    } catch (ResponseException e) {
      Assertions.assertTrue(e.getMessage().contains("Error: already taken"));
    }
  }

  @Test
  void clearTest() throws ResponseException {
    facade.register(new UserData("hstohl", "b", "email"));
    assertNotNull(userAccess.getUser("hstohl"));
    facade.clear();
    assertNull(userAccess.getUser("hstohl"));
  }

  @Test
  void login() throws ResponseException {
    facade.register(new UserData("hstohl", "password", "email"));
    var authData = facade.login(new UserData("hstohl", "password", "email@email.com"));
    assertTrue(authData.authToken().length() > 10);
    assertEquals(authData.authToken(), authAccess.getAuth("hstohl").authToken());
  }

  @Test
  void loginFail1() {
    try {
      facade.register(new UserData("hstohl", "b", "email"));
      facade.login(new UserData("hstohl", "p", "email"));
    } catch (ResponseException e) {
      Assertions.assertTrue(e.getMessage().contains("Error: unauthorized"));
    }
  }

  @Test
  void loginFail2() {
    try {
      facade.register(new UserData("hstohl", "b", "email"));
      facade.login(new UserData("hsthl", "p", "eml"));
    } catch (ResponseException e) {
      Assertions.assertTrue(e.getMessage().contains("Error: unauthorized"));
    }
  }

  @Test
  void logout() throws ResponseException {
    UserData user1 = new UserData("u", "p", "e");
    facade.register(user1);
    facade.logout(authAccess.getAuth("u").authToken());
    Assertions.assertNull(authAccess.getAuth("u"));
  }

  @Test
  public void logoutTestFail() throws ResponseException {
    UserData user1 = new UserData("u", "p", "e");
    facade.register(user1);
    UserData loginReq = new UserData("u", "p", "e");
    try {
      facade.login(loginReq);
      facade.logout("aaaaaa");
    } catch (ResponseException e) {
      Assertions.assertTrue(e.getMessage().contains("Error: unauthorized"));
      Assertions.assertNotNull(authAccess.getAuth("u"));
    }
  }

  @Test
  public void createGame() throws ResponseException {
    UserData user1 = new UserData("u", "p", "e");
    facade.register(user1);
    UserData loginReq = new UserData("u", "p", "e");
    facade.login(loginReq);

    NewGameRequest gameN = new NewGameRequest("MY GAME!");
    facade.createGame(gameN, authAccess.getAuth("u").authToken());

    Assertions.assertNotNull(gameAccess.getGame(gameN.gameName()));
    Assertions.assertEquals("MY GAME!", gameAccess.getGame(gameN.gameName()).gameName());
  }

  @Test
  public void createGameFail() throws ResponseException {
    UserData user1 = new UserData("u", "p", "e");
    facade.register(user1);
    UserData loginReq = new UserData("u", "p", "e");
    facade.login(loginReq);

    try {
      NewGameRequest gameN = new NewGameRequest("MY GAME!");
      facade.createGame(gameN, "gg");
    } catch (ResponseException e) {
      Assertions.assertTrue(e.getMessage().contains("Error: unauthorized"));
    }
  }

  @Test
  void join() throws ResponseException, DataAccessException {
    GameData newGame = new GameData(1234, null,
            null, "My Named Game", new ChessGame());
    gameAccess.addGame(newGame);
    authAccess.addAuth(new AuthData("g", "New User"));

    JoinGameRequest joinGame = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);

    facade.joinGame("g", joinGame);

    Assertions.assertEquals("New User", gameAccess.getGame("My Named Game").whiteUsername());
  }

  @Test
  public void sampleTest() {
    assertTrue(true);
  }

}
