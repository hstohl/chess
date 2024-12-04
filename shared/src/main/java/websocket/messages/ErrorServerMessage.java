package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class ErrorServerMessage extends ServerMessage {
  private String errorMessage;

  public ErrorServerMessage(String errorMessage) {
    serverMessageType = ServerMessageType.ERROR;
    this.errorMessage = errorMessage;
  }

  public String getMessage() {
    return errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof ErrorServerMessage)) {
      return false;
    }
    ErrorServerMessage that = (ErrorServerMessage) o;
    return Objects.equals(errorMessage, that.errorMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), errorMessage);
  }
}
