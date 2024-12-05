package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;

public class LoadGameServerMessage extends ServerMessage {
  private ChessGame game;
  private ChessGame.TeamColor userColor;

  public LoadGameServerMessage(ChessGame game, ChessGame.TeamColor userColor) {
    serverMessageType = ServerMessageType.LOAD_GAME;
    this.game = game;
    this.userColor = userColor;
  }

  public String getMessage() {
    return game.getBoard().getBoardString(userColor, null, null);
  }

  public ChessGame getGame() {
    return game;
  }

  public ChessGame.TeamColor getUserColor() {
    return userColor;
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
    return Objects.equals(game, that.game) && userColor == that.userColor;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), game, userColor);
  }
}
