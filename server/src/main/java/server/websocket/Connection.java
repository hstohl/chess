package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
  public String visitorName;
  public Session session;
  public int id;

  public Connection(String visitorName, Session session, int id) {
    this.visitorName = visitorName;
    this.session = session;
    this.id = id;
  }

  public void send(String msg) throws IOException {
    session.getRemote().sendString(msg);
  }
}