package chess;

import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {
  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;
    int spaceIt1[] = new int[]{-1, 1};
    int spaceIt2[] = new int[]{-2, 2};
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        if (myPosition.getRow() + spaceIt1[i] <= 8 && myPosition.getRow() + spaceIt1[i] > 0 && myPosition.getColumn() + spaceIt2[j] <= 8 && myPosition.getColumn() + spaceIt2[j] > 0) {
          newPos = new ChessPosition(myPosition.getRow() + spaceIt1[i], myPosition.getColumn() + spaceIt2[j]);
          if (board.getPiece(newPos) == null) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          } else if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          }
        }
      }
    }
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        if (myPosition.getRow() + spaceIt2[i] <= 8 && myPosition.getRow() + spaceIt2[i] > 0 && myPosition.getColumn() + spaceIt1[j] <= 8 && myPosition.getColumn() + spaceIt1[j] > 0) {
          newPos = new ChessPosition(myPosition.getRow() + spaceIt2[i], myPosition.getColumn() + spaceIt1[j]);
          if (board.getPiece(newPos) == null) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          } else if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            move = new ChessMove(myPosition, newPos, null);
            moves.add(move);
          }
        }
      }
    }

    return moves;
  }
}
