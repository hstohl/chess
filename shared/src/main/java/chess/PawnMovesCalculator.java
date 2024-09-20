package chess;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class PawnMovesCalculator implements PieceMovesCalculator {
  public void legalMoveCheckerForward(ArrayList<ChessMove> moves, ChessPosition newPos, ChessBoard board, ChessPosition myPosition) {
    ChessMove move;
    if (board.getPiece(newPos) == null) {
      if (newPos.getRow() != 8 && newPos.getRow() != 1) {
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP);
        moves.add(move);
      }
    }
  }

  public void legalMoveCheckerDiagonal(ArrayList<ChessMove> moves, ChessPosition newPos, ChessBoard board, ChessPosition myPosition) {
    ChessMove move;
    if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
      if (newPos.getRow() != 8 && newPos.getRow() != 1) {
        move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
      } else {
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT);
        moves.add(move);
        move = new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP);
        moves.add(move);
      }
    }
  }

  public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> moves = new ArrayList<>();
    ChessPosition newPos;
    ChessMove move;
    if (board.getPiece(myPosition).getTeamColor() == WHITE) {
      newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
      legalMoveCheckerForward(moves, newPos, board, myPosition);
      newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
      legalMoveCheckerDiagonal(moves, newPos, board, myPosition);
      newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
      legalMoveCheckerDiagonal(moves, newPos, board, myPosition);
    } else if (board.getPiece(myPosition).getTeamColor() == BLACK) {
      newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
      legalMoveCheckerForward(moves, newPos, board, myPosition);
      newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
      legalMoveCheckerDiagonal(moves, newPos, board, myPosition);
      newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
      legalMoveCheckerDiagonal(moves, newPos, board, myPosition);
    }

    if (board.getPiece(myPosition).getTeamColor() == WHITE && myPosition.getRow() == 2) {
      newPos = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
      if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
        legalMoveCheckerForward(moves, newPos, board, myPosition);
      }
    } else if (board.getPiece(myPosition).getTeamColor() == BLACK && myPosition.getRow() == 7) {
      newPos = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
      if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
        legalMoveCheckerForward(moves, newPos, board, myPosition);
      }
    }

    return moves;
  }
}
