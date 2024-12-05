package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

import static chess.ChessGame.TeamColor.*;
import static java.util.Objects.isNull;
import static websocket.messages.ServerMessage.ServerMessageType.*;


@WebSocket
public class WebSocketHandler {

  private final ConnectionManager connections = new ConnectionManager();
  private final AuthDataAccess authAccess = new AuthDatabaseAccess();
  private final GameDataAccess gameAccess = new GameDatabaseAccess();
  //private ChessClient client = new ChessClient();

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException {
    UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
    switch (action.getCommandType()) {
      case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
      case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID());
      case LEAVE -> leave(action.getAuthToken(), action.getGameID());
      case RESIGN -> resign(action.getAuthToken(), action.getGameID());
      case REDRAW -> redraw(action.getAuthToken(), action.getGameID());
    }
  }

  private void connect(String auth, int id, Session session) throws IOException {
    connections.add(auth, session, id);
    if (isNull(authAccess.getAuthT(auth))) {
      var errorNotification = new ErrorServerMessage("Bad auth token.");
      connections.broadcast(auth, errorNotification, id);
    }
    if (isNull(gameAccess.getGameI(id))) {
      var errorNotification = new ErrorServerMessage("Bad game ID.");
      connections.broadcast(auth, errorNotification, id);
    }
    String username = authAccess.getAuthT(auth).username();
    ChessGame game = gameAccess.getGameI(id).game();
    String color = "observer";
    String whiteU = gameAccess.getGameI(id).whiteUsername();
    String blackU = gameAccess.getGameI(id).blackUsername();
    if (Objects.equals(username, gameAccess.getGameI(id).whiteUsername())) {
      color = "white";
    } else if (Objects.equals(username, gameAccess.getGameI(id).blackUsername())) {
      color = "black";
    }
    var message = String.format("%s joined the game as %s", username, color);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification, id);
    //var gameString = "\n" + game.getBoard().getBoardString(WHITE);
    if (color == "observer") {
      color = "none";
    }
    ChessGame.TeamColor realColor = ChessGame.TeamColor.valueOf(color.toUpperCase());
    var notification2 = new LoadGameServerMessage(game, realColor);
    connections.broadcast(auth, notification2, id);
  }

  private void makeMove(String auth, int id) throws IOException {
    if (isNull(authAccess.getAuthT(auth))) {
      var errorNotification = new ErrorServerMessage("Bad auth token.");
      connections.broadcast(auth, errorNotification, id);
      return;
    }
    //check valid move

    //make move and update database

    String username = authAccess.getAuthT(auth).username();
    var message = String.format("%s made a move", username);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification, id);
  }

  private void leave(String auth, int id) throws IOException {
    GameData game = gameAccess.getGameI(id);
    String username = authAccess.getAuthT(auth).username();
    //ChessGame.TeamColor color = NONE;
    if (Objects.equals(username, gameAccess.getGameI(id).whiteUsername())) {
      GameData newGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
      try {
        gameAccess.updateGame(newGame);
      } catch (DataAccessException e) {
        var errorNotification = new ErrorServerMessage("Yeah the database wants to kill itself.");
        connections.broadcast(auth, errorNotification, id);
      }
    } else if (Objects.equals(username, gameAccess.getGameI(id).blackUsername())) {
      GameData newGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
      try {
        gameAccess.updateGame(newGame);
      } catch (DataAccessException e) {
        var errorNotification = new ErrorServerMessage("Yeah the database wants to kill itself.");
        connections.broadcast(auth, errorNotification, id);
      }
    }

    connections.remove(auth);
    var message = String.format("%s left the game", username);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification, id);
  }

  private void resign(String auth, int id) throws IOException {
    String username = authAccess.getAuthT(auth).username();
    GameData game = gameAccess.getGameI(id);

    if (Objects.equals(username, gameAccess.getGameI(id).whiteUsername()) ||
            Objects.equals(username, gameAccess.getGameI(id).blackUsername())) {
      if (game.isOver()) {
        var errorNotification = new ErrorServerMessage("You cannot resign after the game is over.");
        connections.broadcast(auth, errorNotification, id);
        return;
      }
      GameData newGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
      try {
        gameAccess.updateGame(newGame);
      } catch (DataAccessException e) {
        var errorNotification = new ErrorServerMessage("Yeah the database wants to kill itself.");
        connections.broadcast(auth, errorNotification, id);
      }
      var message = String.format("%s has resigned. The game is over.", username);
      var notification = new NotificationServerMessage(message);
      connections.broadcastAll(notification, id);
    } else {
      var errorNotification = new ErrorServerMessage("You cannot resign if you are not a player.");
      connections.broadcast(auth, errorNotification, id);
    }
    /*var message = String.format("%s has resigned", username);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification, id);*/
  }

  private void redraw(String auth, int id) throws IOException {
    String username = authAccess.getAuthT(auth).username();
    ChessGame game = gameAccess.getGameI(id).game();
    if (Objects.equals(username, gameAccess.getGameI(id).blackUsername())) {
      var notification2 = new LoadGameServerMessage(game, BLACK);
      connections.broadcast(auth, notification2, id);
    } else {
      var notification2 = new LoadGameServerMessage(game, WHITE);
      connections.broadcast(auth, notification2, id);
    }

  }

}