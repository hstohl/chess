package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
  private TeamColor teamTurn = TeamColor.WHITE;
  private ChessBoard board = new ChessBoard();
  //private ChessBoard tempBoard = new ChessBoard();

  public ChessGame() {
    board.resetBoard();
  }

  /**
   * @return Which team's turn it is
   */
  public TeamColor getTeamTurn() {
    return teamTurn;
  }

  /**
   * Sets which team's turn it is
   *
   * @param team the team whose turn it is
   */
  public void setTeamTurn(TeamColor team) {
    teamTurn = team;
  }

  /**
   * Enum identifying the 2 possible teams in a chess game
   */
  public enum TeamColor {
    WHITE,
    BLACK,
    NONE
  }

  /**
   * Gets a valid moves for a piece at the given location
   *
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   * startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    ChessBoard tempBoard = new ChessBoard(board);
    TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
    Collection<ChessMove> allMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
    Collection<ChessMove> allValidMoves = new ArrayList<>();
    for (ChessMove move : allMoves) {
      tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
      tempBoard.removePiece(move.getStartPosition());
      if (!wouldBeInCheck(teamColor, tempBoard) && !allValidMoves.contains(move)) {
        allValidMoves.add(move);
      }
      tempBoard = new ChessBoard(board);
    }
    return allValidMoves;
  }

  /**
   * Makes a move in a chess game
   *
   * @param move chess move to preform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    if (board.getPiece(move.getStartPosition()) != null && board.getPiece(move.getStartPosition()).getTeamColor() == getTeamTurn()
            && validMoves(move.getStartPosition()).contains(move)) {
      if (move.getPromotionPiece() == null) {
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.removePiece(move.getStartPosition());
      } else {
        ChessPiece newPiece = new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(),
                move.getPromotionPiece());
        board.addPiece(move.getEndPosition(), newPiece);
        board.removePiece(move.getStartPosition());
      }

    } else {
      throw new InvalidMoveException("Invalid Move");
    }
    if (getTeamTurn() == TeamColor.WHITE) {
      setTeamTurn(TeamColor.BLACK);
    } else {
      setTeamTurn(TeamColor.WHITE);
    }
  }


  public ChessPosition kingFinder(TeamColor color, ChessBoard tempBoard) {
    ChessPosition kingPos = new ChessPosition(0, 0);
    for (int i = 1; i < 9; ++i) {
      for (int j = 1; j < 9; ++j) {
        kingPos = new ChessPosition(i, j);
        if (tempBoard.getPiece(kingPos) != null && tempBoard.getPiece(kingPos).getPieceType() == ChessPiece.PieceType.KING
                && tempBoard.getPiece(kingPos).getTeamColor() == color) {
          return kingPos;
        }
      }
    }
    kingPos = new ChessPosition(0, 0);
    return kingPos;
  }

  public boolean wouldBeInCheck(TeamColor teamColor, ChessBoard tempBoard) {
    ChessPosition kingPos;
    kingPos = kingFinder(teamColor, tempBoard);
    if (kingPos.getColumn() == 0 && kingPos.getRow() == 0) {
      return false;
    }
    ChessPosition enemyPos;
    for (int i = 1; i < 9; ++i) {
      for (int j = 1; j < 9; ++j) {
        Collection<ChessMove> enemyMoves;
        enemyPos = new ChessPosition(i, j);
        if (tempBoard.getPiece(enemyPos) != null && tempBoard.getPiece(enemyPos).getTeamColor() != teamColor) {
          enemyMoves = tempBoard.getPiece(enemyPos).pieceMoves(tempBoard, enemyPos); //maybe should be validMoves instead?
          if (threatenedKingChecker(enemyMoves, kingPos)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean threatenedKingChecker(Collection<ChessMove> enemyMoves, ChessPosition kingPos) {
    for (ChessMove move : enemyMoves) {
      if (move.getEndPosition().getColumn() == kingPos.getColumn() && move.getEndPosition().getRow() == kingPos.getRow()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if the given team is in check
   *
   * @param teamColor which team to check for check
   * @return True if the specified team is in check
   */
  public boolean isInCheck(TeamColor teamColor) {
    return wouldBeInCheck(teamColor, board);
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    ChessBoard fakeBoard = new ChessBoard(board);
    ChessPosition kingPos = kingFinder(teamColor, fakeBoard);
    if (!isInCheck(teamColor)) {
      return false;
    }
    ChessPosition friendlyPos;
    if (!possibleMoves(fakeBoard, teamColor)) {
      return false;
    }
    return true;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    ChessBoard fakeBoard = new ChessBoard(board);
    ChessPosition kingPos = kingFinder(teamColor, fakeBoard);
    ChessPosition friendlyPos;
    if (isInCheck(teamColor)) {
      return false;
    } else {
      if (!possibleMoves(fakeBoard, teamColor)) {
        return false;
      }
    }
    return true;
  }


  public boolean possibleMoves(ChessBoard fakeBoard, TeamColor teamColor) {
    ChessPosition friendlyPos;
    for (int i = 1; i < 9; ++i) {
      for (int j = 1; j < 9; ++j) {
        Collection<ChessMove> possibleMoves;
        friendlyPos = new ChessPosition(i, j);
        if (fakeBoard.getPiece(friendlyPos) != null && fakeBoard.getPiece(friendlyPos).getTeamColor() == teamColor) {
          possibleMoves = validMoves(friendlyPos);
          if (!possibleMoves.isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Sets this game's chessboard with a given board
   *
   * @param board the new board to use
   */
  public void setBoard(ChessBoard board) {
    this.board = board;
  }

  /**
   * Gets the current chessboard
   *
   * @return the chessboard
   */
  public ChessBoard getBoard() {
    return board;
  }
}
