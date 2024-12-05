package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
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
      case MAKE_MOVE -> {
        MakeMoveCommand moveAction = new Gson().fromJson(message, MakeMoveCommand.class);
        makeMove(moveAction.getAuthToken(), moveAction.getGameID(), moveAction.getMove(), session);
      }
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
      return;
    }
    if (isNull(gameAccess.getGameI(id))) {
      var errorNotification = new ErrorServerMessage("Bad game ID.");
      connections.broadcast(auth, errorNotification, id);
      return;
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
    if (color == "observer") {
      color = "none";
    }
    ChessGame.TeamColor realColor = ChessGame.TeamColor.valueOf(color.toUpperCase());
    var notification2 = new LoadGameServerMessage(game, realColor);
    connections.broadcast(auth, notification2, id);
  }

  private void makeMove(String auth, int id, ChessMove move, Session session) throws IOException {
    if (isNull(authAccess.getAuthT(auth))) {
      connections.add(auth, session, id);
      var errorNotification = new ErrorServerMessage("This is a bad auth token.");
      connections.broadcast(auth, errorNotification, id);
      return;
    }
    GameData game = gameAccess.getGameI(id);
    String username = authAccess.getAuthT(auth).username();
    ChessGame.TeamColor color = NONE;
    ChessGame.TeamColor oppColor = NONE;
    String myName = "";
    String oppName = "";
    if (Objects.equals(username, gameAccess.getGameI(id).whiteUsername())) {
      color = WHITE;
      oppColor = BLACK;
      myName = gameAccess.getGameI(id).whiteUsername();
      oppName = gameAccess.getGameI(id).blackUsername();
    } else if (Objects.equals(username, gameAccess.getGameI(id).blackUsername())) {
      color = BLACK;
      oppColor = WHITE;
      myName = gameAccess.getGameI(id).blackUsername();
      oppName = gameAccess.getGameI(id).whiteUsername();
    }
    if (game.isOver()) {
      var errorNotification = new ErrorServerMessage("You cannot make a move once the game has ended.");
      connections.broadcast(auth, errorNotification, id);
      return;
    }
    if (color == NONE) {
      var errorNotification = new ErrorServerMessage("You can only make moves as a player.");
      connections.broadcast(auth, errorNotification, id);
      return;
    } else if (game.game().getTeamTurn() != color) {
      var errorNotification = new ErrorServerMessage("You can only make moves on your turn.");
      connections.broadcast(auth, errorNotification, id);
      return;
    }
    //check valid move and make move and update database
    try {
      game.game().makeMove(move);
      gameAccess.updateGame(game);
    } catch (InvalidMoveException e) {
      var errorNotification = new ErrorServerMessage("Invalid move.");
      connections.broadcast(auth, errorNotification, id);
      return;
    } catch (DataAccessException e) {
      var errorNotification = new ErrorServerMessage("Yeah the database wants to kill itself.");
      connections.broadcast(auth, errorNotification, id);
    }

    //tell the people what happened
    var notification1 = new LoadGameServerMessage(game.game(), color);
    connections.broadcastAll(notification1, game.gameID());

    String movePretty = game.game().getBoard().getPiece(move.getEndPosition()).getPieceType().toString().toLowerCase()
            + " from " + move.getStartPosition().prettyToString() + " to " + move.getEndPosition().prettyToString();

    var message = String.format("%s made the move: %s", username, movePretty);

    var notification2 = new NotificationServerMessage(message);
    connections.broadcast(auth, notification2, id);

    //check for check, checkmate, and stalemate and send notifications and update game if necessary
    if (game.game().isInCheckmate(oppColor)) {
      message = String.format("%s has been checkmated. The game is over. %s wins.", oppName, myName);
      var notification3 = new NotificationServerMessage(message);
      connections.broadcastAll(notification3, game.gameID());
      setGameOver(game, auth, id);
      return;
    }
    if (game.game().isInStalemate(oppColor)) {
      message = String.format("Stalemate. The game is over.", oppName);
      var notification3 = new NotificationServerMessage(message);
      connections.broadcastAll(notification3, game.gameID());
      setGameOver(game, auth, id);
      return;
    }
    if (game.game().isInCheck(oppColor)) {
      message = String.format("%s is in check", oppName);
      var notification3 = new NotificationServerMessage(message);
      connections.broadcastAll(notification3, game.gameID());
    }
  }

  private void leave(String auth, int id) throws IOException {
    GameData game = gameAccess.getGameI(id);
    String username = authAccess.getAuthT(auth).username();
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
      setGameOver(game, auth, id);
      var message = String.format("%s has resigned. The game is over.", username);
      var notification = new NotificationServerMessage(message);
      connections.broadcastAll(notification, id);
    } else {
      var errorNotification = new ErrorServerMessage("You cannot resign if you are not a player.");
      connections.broadcast(auth, errorNotification, id);
    }
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

  private void setGameOver(GameData game, String auth, int id) throws IOException {
    GameData newGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
    try {
      gameAccess.updateGame(newGame);
    } catch (DataAccessException e) {
      var errorNotification = new ErrorServerMessage("Yeah the database wants to kill itself.");
      connections.broadcast(auth, errorNotification, id);
    }
  }

}