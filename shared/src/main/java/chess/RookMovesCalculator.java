package chess;

import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;

    boolean up = true;
    boolean down = true;
    boolean left = true;
    boolean right = true;
    int i = 1;

    while (up || down || left || right) {
      if (myPosition.getRow() + i <= 8 && up) {
        newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          up = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          up = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        up = false;
      }

      if (myPosition.getRow() - i > 0 && down) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          down = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          down = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        down = false;
      }

      if (myPosition.getColumn() + i <= 8 && right) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          right = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          right = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        right = false;
      }

      if (myPosition.getColumn() - i > 0 && left) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          left = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          left = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        left = false;
      }
      ++i;
    }
    return moves;
  }
}
