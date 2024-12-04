package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;

import static chess.ChessGame.TeamColor.WHITE;
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
    }
  }

  private void connect(String auth, int id, Session session) throws IOException {
    connections.add(auth, session);
    String username = authAccess.getAuthT(auth).username();
    ChessGame game = gameAccess.getGameI(id).game();
    var message = String.format("%s joined the game", username);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification);
    var gameString = "\n" + game.getBoard().getBoardString(WHITE);
    var notification2 = new LoadGameServerMessage(game);
    connections.broadcast(auth, notification2);
  }

  private void makeMove(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification);
  }

  private void leave(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification);
  }

  private void resign(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new NotificationServerMessage(message);
    connections.broadcast(auth, notification);
  }

  public void makeNoise(String petName, String sound) throws ResponseException {
    try {
      var message = String.format("%s says %s", petName, sound);
      var notification = new NotificationServerMessage(message);
      connections.broadcast("", notification);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }
}