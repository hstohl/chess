package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
  private ChessPiece[][] squares = new ChessPiece[9][9];

  public ChessBoard() {

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChessBoard that = (ChessBoard) o;
    return Objects.deepEquals(squares, that.squares);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(squares);
  }

  @Override
  public String toString() {
    return "ChessBoard{" +
            "squares=" + Arrays.toString(squares) +
            '}';
  }

  /**
   * Adds a chess piece to the chessboard
   *
   * @param position where to add the piece to
   * @param piece    the piece to add
   */
  public void addPiece(ChessPosition position, ChessPiece piece) {
    squares[position.getRow()][position.getColumn()] = piece;
  }

  public void removePiece(ChessPosition position) {
    squares[position.getRow()][position.getColumn()] = null;
  }

  /**
   * Gets a chess piece on the chessboard
   *
   * @param position The position to get the piece from
   * @return Either the piece at the position, or null if no piece is at that
   * position
   */
  public ChessPiece getPiece(ChessPosition position) {
    return squares[position.getRow()][position.getColumn()];
  }

  /**
   * Sets the board to the default starting board
   * (How the game of chess normally starts)
   */
  public void resetBoard() {
    ChessPiece wPawn = new ChessPiece(WHITE, PAWN);
    ChessPiece wRook = new ChessPiece(WHITE, ROOK);
    ChessPiece wKnight = new ChessPiece(WHITE, KNIGHT);
    ChessPiece wBishop = new ChessPiece(WHITE, BISHOP);
    ChessPiece wQueen = new ChessPiece(WHITE, QUEEN);
    ChessPiece wKing = new ChessPiece(WHITE, KING);

    addPiece(new ChessPosition(1, 1), wRook);
    addPiece(new ChessPosition(1, 8), wRook);
    addPiece(new ChessPosition(1, 2), wKnight);
    addPiece(new ChessPosition(1, 7), wKnight);
    addPiece(new ChessPosition(1, 3), wBishop);
    addPiece(new ChessPosition(1, 6), wBishop);
    addPiece(new ChessPosition(1, 4), wQueen);
    addPiece(new ChessPosition(1, 5), wKing);


    ChessPiece bPawn = new ChessPiece(BLACK, PAWN);
    ChessPiece bRook = new ChessPiece(BLACK, ROOK);
    ChessPiece bKnight = new ChessPiece(BLACK, KNIGHT);
    ChessPiece bBishop = new ChessPiece(BLACK, BISHOP);
    ChessPiece bQueen = new ChessPiece(BLACK, QUEEN);
    ChessPiece bKing = new ChessPiece(BLACK, KING);


    addPiece(new ChessPosition(8, 1), bRook);
    addPiece(new ChessPosition(8, 8), bRook);
    addPiece(new ChessPosition(8, 2), bKnight);
    addPiece(new ChessPosition(8, 7), bKnight);
    addPiece(new ChessPosition(8, 3), bBishop);
    addPiece(new ChessPosition(8, 6), bBishop);
    addPiece(new ChessPosition(8, 4), bQueen);
    addPiece(new ChessPosition(8, 5), bKing);

    for (int i = 1; i < 9; i++) {
      addPiece(new ChessPosition(2, i), wPawn);
      addPiece(new ChessPosition(7, i), bPawn);
    }

  }
}
