package chess;

import java.util.ArrayList;


public class BishopMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    boolean upLeft = true;
    boolean downLeft = true;
    boolean upRight = true;
    boolean downRight = true;
    int i = 1;

    while (upLeft || downLeft || upRight || downRight) {
      ChessPosition newPos;
      ChessMove move;
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
