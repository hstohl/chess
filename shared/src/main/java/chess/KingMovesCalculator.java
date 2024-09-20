package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (myPosition.getRow() + i <= 8 && myPosition.getRow() + i > 0 && myPosition.getColumn() + j <= 8 && myPosition.getColumn() + j > 0) {
          newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
          if (ChessBoard.getPiece(newPos) == null) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          } else if (ChessBoard.getPiece(newPos).getTeamColor() != ChessBoard.getPiece(myPosition).getTeamColor()) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          }
        }
      }
    }
    return moves;
  }
}
