package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {
  private final ChessMove move;

  public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
    super(CommandType.MAKE_MOVE, authToken, gameID);
    this.move = Objects.requireNonNull(move, "Move cannot be null");
  }

  public ChessMove getMove() {
    return move;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MakeMoveCommand)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    MakeMoveCommand that = (MakeMoveCommand) o;
    return Objects.equals(getMove(), that.getMove());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getMove());
  }

  @Override
  public String toString() {
    return "MakeMoveCommand{" +
            "commandType=" + getCommandType() +
            ", authToken='" + getAuthToken() + '\'' +
            ", gameID=" + getGameID() +
            ", move=" + move +
            '}';
  }
}
