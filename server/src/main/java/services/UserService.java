package services;

import chess.ChessGame;
import com.google.protobuf.ServiceException;
import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.Random;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class UserService {
  private final DataAccess dataAccess;
  private final AuthDataAccess authDataAccess;
  private final GameDataAccess gameDataAccess;

  public UserService(DataAccess dataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
    this.dataAccess = dataAccess;
    this.authDataAccess = authDataAccess;
    this.gameDataAccess = gameDataAccess;
  }

  public SuccessResponse clear() {
    dataAccess.clear();
    authDataAccess.clear();
    gameDataAccess.clear();
    return new SuccessResponse();
  }

  public AuthData registerUser(UserData newUser) throws ServiceException {
    if (dataAccess.getUser(newUser.username()) != null) {
      throw new ServiceException("Error: already taken");
    }

    if (newUser.password() == null) {
      throw new ServiceException("Error: bad request");
    }

    try {
      dataAccess.addUser(newUser);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }

    AuthData auth = newAuth(newUser.username());
    authDataAccess.addAuth(auth);

    return authDataAccess.getAuth(newUser.username());
  }

  public AuthData newAuth(String username) { //40 to 122
    Random rnd = new Random();
    StringBuilder authToken = new StringBuilder();
    boolean unique = false;
    while (!unique) {
      authToken = new StringBuilder();
      for (int i = 0; i < 11; ++i) {
        int rndAsciiValue = rnd.nextInt(89) + 33;
        authToken.append((char) rndAsciiValue);
      }
      if (authDataAccess.getAuthT(authToken.toString()) == null) {
        unique = true;
      }
    }

    return new AuthData(authToken.toString(), username);
  }

  public AuthData login(UserData user) throws ServiceException {
    UserData realUser = dataAccess.getUser(user.username());

    if (dataAccess.getUser(user.username()) == null) {
      throw new ServiceException("Error: unauthorized");
    }
    if (!Objects.equals(realUser.password(), user.password())) {
      throw new ServiceException("Error: unauthorized");
    }

    AuthData auth = newAuth(user.username());
    authDataAccess.addAuth(auth);

    return authDataAccess.getAuth(user.username());
  }

  public SuccessResponse logout(String token) throws ServiceException {
    AuthData authData = authDataAccess.getAuthT(token);

    if (authData == null) {
      throw new ServiceException("Error: unauthorized");
    }

    authDataAccess.removeAuth(authData);
    return new SuccessResponse();
  }

  public NewGameResult createGame(NewGameRequest newGameN, String token) throws ServiceException {
    AuthData authData = authDataAccess.getAuthT(token);

    if (authData == null) {
      throw new ServiceException("Error: unauthorized");
    }

    Random rnd = new Random();
    int gameID = 1111;
    boolean unique = false;
    while (!unique) {
      gameID = rnd.nextInt(10000);
      if (gameDataAccess.getGameI(gameID) == null) {
        unique = true;
      }
    }
    GameData newGame = new GameData(gameID, null,
            null, newGameN.gameName(), new ChessGame());
    gameDataAccess.addGame(newGame);

    return new NewGameResult(newGame.gameID());
  }

  public SuccessResponse joinGame(String token, JoinGameRequest req) throws ServiceException {
    AuthData authData = authDataAccess.getAuthT(token);
    if (authData == null) {
      throw new ServiceException("Error: unauthorized");
    }

    GameData gameData = gameDataAccess.getGameI(req.gameID());
    if (gameData == null) {
      throw new ServiceException("Error: bad request");
    }

    if (req.playerColor() == WHITE && gameData.whiteUsername() != null) {
      throw new ServiceException("Error: already taken");
    }
    if (req.playerColor() == BLACK && gameData.blackUsername() != null) {
      throw new ServiceException("Error: already taken");
    }

    if (req.playerColor() == WHITE) {
      GameData updatedGame = new GameData(gameData.gameID(), authData.username(),
              gameData.blackUsername(), gameData.gameName(), gameData.game());
      gameDataAccess.updateGame(updatedGame);
    } else if (req.playerColor() == BLACK) {
      GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(),
              authData.username(), gameData.gameName(), gameData.game());
      gameDataAccess.updateGame(updatedGame);
    } else {
      throw new ServiceException("Error: bad request");
    }

    return new SuccessResponse();
  }

  public GameList listGames(String token) throws ServiceException, DataAccessException {
    AuthData authData = authDataAccess.getAuthT(token);

    if (authData == null) {
      throw new ServiceException("Error: unauthorized");
    }

    return new GameList(gameDataAccess.listGames());
  }
}
