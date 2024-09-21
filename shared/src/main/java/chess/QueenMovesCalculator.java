package chess;

import java.util.ArrayList;

public class QueenMovesCalculator implements PieceMovesCalculator {
  public boolean legalMoveChecker(ChessBoard board, ArrayList<ChessMove> moves,
                                  ChessPosition myPosition, ChessPosition newPos) {
    ChessMove move;
    if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
      return true;
    } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
      move = new ChessMove(myPosition, newPos, null);
      moves.add(move);
      return true;
    }
    move = new ChessMove(myPosition, newPos, null);
    moves.add(move);
    return false;
  }

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
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          up = false;
          continue;
        }
      } else {
        up = false;
      }

      if (myPosition.getRow() - i > 0 && down) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          down = false;
          continue;
        }
      } else {
        down = false;
      }

      if (myPosition.getColumn() + i <= 8 && right) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          right = false;
          continue;
        }
      } else {
        right = false;
      }

      if (myPosition.getColumn() - i > 0 && left) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          left = false;
          continue;
        }
      } else {
        left = false;
      }

      if (upRight && myPosition.getRow() + i <= 8 && myPosition.getColumn() + i <= 8) {
        newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          upRight = false;
          continue;
        }
      } else {
        upRight = false;
      }

      if (downRight && myPosition.getRow() - i > 0 && myPosition.getColumn() + i <= 8) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          downRight = false;
          continue;
        }
      } else {
        downRight = false;
      }

      if (upLeft && myPosition.getRow() + i <= 8 && myPosition.getColumn() - i > 0) {
        newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          upLeft = false;
          continue;
        }
      } else {
        upLeft = false;
      }

      if (downLeft && myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
        if (legalMoveChecker(board, moves, myPosition, newPos)) {
          downLeft = false;
          continue;
        }
      } else {
        downLeft = false;
      }

      ++i;
    }
    return moves;
  }
}
