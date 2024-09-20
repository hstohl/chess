package chess;

import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;
    int spaceIt[] = new int[]{-2, -1, 1, 2};
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        if (myPosition.getRow() + spaceIt[i] <= 8 && myPosition.getRow() + spaceIt[i] > 0 && myPosition.getColumn() + spaceIt[j] <= 8 && myPosition.getColumn() + spaceIt[j] > 0) {
          newPos = new ChessPosition(myPosition.getRow() + spaceIt[i], myPosition.getColumn() + spaceIt[j]);
          if (ChessBoard.getPiece(newPos) == null) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          } else if (/*ChessBoard.getPiece(newPos) != null && */ChessBoard.getPiece(newPos).getTeamColor() != ChessBoard.getPiece(myPosition).getTeamColor()) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          }
        }
      }
    }

    return moves;
  }
}
