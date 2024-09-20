package chess;

import java.util.ArrayList;

public class QueenMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;

    boolean upLeft = true;
    boolean downLeft = true;
    boolean upRight = true;
    boolean downRight = true;
    boolean up = true;
    boolean down = true;
    boolean left = true;
    boolean right = true;
    int i = 1;

    while (up || down || left || right || upLeft || downLeft || upRight || downRight) {
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

      if (upRight && myPosition.getRow() + i <= 8 && myPosition.getColumn() + i <= 8) {
        newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          upRight = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          upRight = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        upRight = false;
      }

      if (downRight && myPosition.getRow() - i > 0 && myPosition.getColumn() + i <= 8) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          downRight = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          downRight = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        downRight = false;
      }

      if (upLeft && myPosition.getRow() + i <= 8 && myPosition.getColumn() - i > 0) {
        newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          upLeft = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          upLeft = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        upLeft = false;
      }

      if (downLeft && myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          downLeft = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          downLeft = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        downLeft = false;
      }

      ++i;
    }
    return moves;
  }
}
