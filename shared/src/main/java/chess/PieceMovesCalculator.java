package chess;

import java.util.ArrayList;

public interface PieceMovesCalculator {
  ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

}
