package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
  Gson serializer = new Gson();

  public void add(String visitorName, Session session, int id) {
    var connection = new Connection(visitorName, session, id);
    connections.put(visitorName, connection);
  }

  public void remove(String visitorName) {
    connections.remove(visitorName);
  }

  public void broadcastSelf(String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (c.visitorName.equals(excludeVisitorName)) {
          c.send(serializer.toJson(notification));
        }
      } else {
        removeList.add(c);
      }
    }

    // Clean up any connections that were left open.
    for (var c : removeList) {
      connections.remove(c.visitorName);
    }
  }

  public void broadcastAll(ServerMessage notification, int gameID) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (c.id == gameID) {
          c.send(serializer.toJson(notification));
        }
      } else {
        removeList.add(c);
      }
    }

    for (var c : removeList) {
      connections.remove(c.visitorName);
    }
  }

  public void broadcastSpecific(String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (!c.visitorName.equals(excludeVisitorName) && c.id == gameID) {
          c.send(serializer.toJson(notification));
        }
      } else {
        removeList.add(c);
      }
    }

    for (var c : removeList) {
      connections.remove(c.visitorName);
    }
  }
}