package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameServerMessage extends ServerMessage {
  private ChessGame game;

  public LoadGameServerMessage(ChessGame game) {
    serverMessageType = ServerMessageType.LOAD_GAME;
    this.game = game;
  }

  public ChessGame getGame() {
    return game;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof LoadGameServerMessage)) {
      return false;
    }
    LoadGameServerMessage that = (LoadGameServerMessage) o;
    return Objects.equals(game, that.game);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), game);
  }
}
