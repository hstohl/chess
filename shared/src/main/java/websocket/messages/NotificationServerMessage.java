package websocket.messages;

import java.util.Objects;

public class NotificationServerMessage extends ServerMessage {
  private String message;

  public NotificationServerMessage(String message) {
    serverMessageType = ServerMessageType.NOTIFICATION;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof NotificationServerMessage)) {
      return false;
    }
    NotificationServerMessage that = (NotificationServerMessage) o;
    return Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), message);
  }
}
