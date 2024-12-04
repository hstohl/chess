package facade;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

  Session session;
  NotificationHandler notificationHandler;


  public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
    try {
      url = url.replace("http", "ws");
      URI socketURI = new URI(url + "/ws");
      this.notificationHandler = notificationHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);

      //set message handler
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
          //System.out.println("Parsed ServerMessage: " + notification.getServerMessageType());
          notificationHandler.notify(notification);
        }
      });
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  //Endpoint requires this method, but you don't have to do anything
  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    //System.out.println("Client connected: " + session.getId());
  }

  public void connectToGame(String authToken, int gameID) throws ResponseException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  public void makeChessMove(String authToken, int gameID) throws ResponseException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
      this.session.close();
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  public void leaveGame(String authToken, int gameID) throws ResponseException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
      this.session.close();
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  public void resignGame(String authToken, int gameID) throws ResponseException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
      this.session.close();
    } catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

}
