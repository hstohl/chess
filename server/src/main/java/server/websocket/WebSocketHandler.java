package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

  private final ConnectionManager connections = new ConnectionManager();

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
    var message = String.format("%s is in the shop", auth);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    connections.broadcast(auth, notification);
  }

  private void makeMove(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    connections.broadcast(auth, notification);
  }

  private void leave(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    connections.broadcast(auth, notification);
  }

  private void resign(String auth, int id) throws IOException {
    connections.remove(auth);
    var message = String.format("%s left the shop", auth);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    connections.broadcast(auth, notification);
  }

  public void makeNoise(String petName, String sound) throws ResponseException {
    try {
      var message = String.format("%s says %s", petName, sound);
      var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR/*, message*/);
      connections.broadcast("", notification);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }
}