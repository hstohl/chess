package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import chess.ChessBoard;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

  private final ChessGame.TeamColor pieceColor;
  private final PieceType type;

  public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    this.pieceColor=pieceColor;
    this.type=type;
  }

  /**
   * The various different chess piece options
   */
  public enum PieceType {
    KING,
    QUEEN,
    BISHOP,
    KNIGHT,
    ROOK,
    PAWN
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChessPiece that=(ChessPiece) o;
    return pieceColor == that.pieceColor && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pieceColor, type);
  }

  @Override
  public String toString() {
    return "ChessPiece{" +
            "pieceColor=" + pieceColor +
            ", type=" + type +
            '}';
  }

  /**
   * @return Which team this chess piece belongs to
   */
  public ChessGame.TeamColor getTeamColor() {
    return pieceColor;
  }

  /**
   * @return which type of chess piece this piece is
   */
  public PieceType getPieceType() {
    return type;
  }

  /**
   * Calculates all the positions a chess piece can move to
   * Does not take into account moves that are illegal due to leaving the king in
   * danger
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    //return PieceMovesCalculator.pieceMoves(board, myPosition);
    ArrayList<ChessMove> moves=new ArrayList<>();
    if (type == ChessPiece.PieceType.BISHOP) {
      boolean upLeft=true;
      boolean downLeft=true;
      boolean upRight=true;
      boolean downRight=true;
      int i=1;
      while (upLeft || downLeft || upRight || downRight) {
        ChessPosition newPos;
        ChessMove move;
        if (upRight && myPosition.getRow() + i <= 8 && myPosition.getColumn() + i <= 8) {
          newPos=new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
          if (ChessBoard.getPiece(newPos) != null && ChessBoard.getPiece(newPos).getTeamColor() == pieceColor) {
            upRight=false;
            continue;
          }
          move=new ChessMove(myPosition, newPos, null);
          moves.add(move);
        } else {
          //System.out.println("upRightFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() + i) + "}");
          upRight=false;
        }

        if (downRight && myPosition.getRow() - i > 0 && myPosition.getColumn() + i <= 8) {
          newPos=new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
          if (ChessBoard.getPiece(newPos) != null && ChessBoard.getPiece(newPos).getTeamColor() == pieceColor) {
            downRight=false;
            continue;
          }
          move=new ChessMove(myPosition, newPos, null);
          moves.add(move);
        } else {
          //System.out.println("downLeftFalse: {" + (myPosition.getRow() - i) + "," + (myPosition.getColumn() + i) + "}");
          downRight=false;
        }

        if (upLeft && myPosition.getRow() + i <= 8 && myPosition.getColumn() - i > 0) {
          newPos=new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
          if (ChessBoard.getPiece(newPos) != null && ChessBoard.getPiece(newPos).getTeamColor() == pieceColor) {
            upLeft=false;
            continue;
          }
          move=new ChessMove(myPosition, newPos, null);
          moves.add(move);
        } else {
          //System.out.println("upLeftFalse: {" + (myPosition.getRow() + i) + "," + (myPosition.getColumn() - i) + "}");
          upLeft=false;
        }

        if (downLeft && myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
          newPos=new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
          if (ChessBoard.getPiece(newPos) != null && ChessBoard.getPiece(newPos).getTeamColor() == pieceColor) {
            downLeft=false;
            continue;
          }
          move=new ChessMove(myPosition, newPos, null);
          moves.add(move);
        } else {
          //System.out.println("downLeftFalse: {" + (myPosition.getRow() - i) + "," + (myPosition.getColumn() - i) + "}");
          downLeft=false;
        }
        ++i;
      }
    }
    return moves;
  }


}
