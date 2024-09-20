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
          //maxRow = myPosition.getRow() + i;
          up = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          //maxRow = myPosition.getRow() + i;
          up = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        //System.out.println("upRightFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() + i) + "}");
        up = false;
      }

      if (myPosition.getRow() - i > 0 && down) {
        newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          //maxRow = myPosition.getRow() + i;
          down = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          //maxRow = myPosition.getRow() + i;
          down = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        //System.out.println("upRightFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() + i) + "}");
        down = false;
      }

      if (myPosition.getColumn() + i <= 8 && right) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          //maxRow = myPosition.getRow() + i;
          right = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          //maxRow = myPosition.getRow() + i;
          right = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        //System.out.println("upRightFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() + i) + "}");
        right = false;
      }

      if (myPosition.getColumn() - i > 0 && left) {
        newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
        if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
          //maxRow = myPosition.getRow() + i;
          left = false;
          continue;
        } else if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
          move = new ChessMove(myPosition, newPos, null);
          moves.add(move);
          //maxRow = myPosition.getRow() + i;
          left = false;
          continue;
        }
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        //System.out.println("upRightFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() + i) + "}");
        left = false;
      }
      ++i;
    }
    return moves;
  }
}
