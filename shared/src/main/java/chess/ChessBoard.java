package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import static chess.EscapeSequences.*;
import static java.lang.Math.abs;

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

  ChessBoard(ChessBoard copy) {
    //this.squares = copy.squares;
    this.squares = new ChessPiece[copy.squares.length][];
    for (int i = 1; i < 9; ++i) {
      this.squares[i] = new ChessPiece[copy.squares[i].length];
      for (int j = 1; j < 9; ++j) {
        this.squares[i][j] = copy.squares[i][j];
      }
    }
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
    String s = "ChessBoard{" + "squares=";
    for (int i = 1; i < 9; ++i) {
      for (int j = 1; j < 9; ++j) {
        if (squares[i][j] != null) {
          s = s + squares[i][j].toString() + ", \n";
        }
      }
    }
    return s;
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


  public String getBoardString(ChessGame.TeamColor color) {
    if (color == NONE) {
      color = WHITE;
    }
    String string = "";
    String bgColor;
    String character;
    String txtColor;
    for (int i = 0; i < 10; ++i) {
      for (int j = 0; j < 10; ++j) {
        bgColor = SET_BG_COLOR_BLACK;
        character = EMPTY;
        txtColor = SET_TEXT_COLOR_BLACK;
        //make board
        if (i == 0 || i == 9 || j == 0 || j == 9) {
          bgColor = SET_BG_COLOR_LIGHT_GREY;
          character = posChar(color, i, j);
        } else if (i % 2 == 1 && j % 2 == 1) {
          bgColor = SET_BG_COLOR_WHITE;
        } else if (i % 2 == 0 && j % 2 == 0) {
          bgColor = SET_BG_COLOR_WHITE;
        }
        if (i != 0 && i != 9 && j != 0 && j != 9) {
          character = piecesOnBoard(i, j, color);
        }

        string = string + bgColor + character;
      }
      string = string + SET_BG_COLOR_BLACK + "\n";
    }

    return string;
  }

  private String posChar(ChessGame.TeamColor color, int i, int j) {
    String character = EMPTY;
    if (color == BLACK) {
      if ((j == 0 || j == 9) && i != 0 && i != 9) {
        character = SET_TEXT_COLOR_BLACK + " " + i + " ";
      }
      if ((i == 0 || i == 9) && j != 0 && j != 9) {
        character = SET_TEXT_COLOR_BLACK + " " + Character.toString((char) 96 + 9 - j) + " ";
      }
    } else if (color == WHITE) {
      if ((j == 0 || j == 9) && i != 0 && i != 9) {
        character = SET_TEXT_COLOR_BLACK + " " + (9 - i) + " ";
      }
      if ((i == 0 || i == 9) && j != 0 && j != 9) {
        character = SET_TEXT_COLOR_BLACK + " " + Character.toString((char) 96 + j) + " ";
      }
    }

    return character;
  }

  private String piecesOnBoard(int i, int j, ChessGame.TeamColor color) {
    String txtColor = "";
    String character = "";
    int dif = 0;
    if (color == WHITE) {
      dif = 9;
    }
    if (i != 0 && i != 9 && j != 0 && j != 9) {
      ChessPiece piece = this.getPiece(new ChessPosition(abs(dif - i), abs(j - dif)));
      if (piece != null) {
        if (piece.getTeamColor() == WHITE) {
          txtColor = SET_TEXT_COLOR_LIGHT_GREY;
        } else if (piece.getTeamColor() == BLACK) {
          txtColor = SET_TEXT_COLOR_BLUE;
        }

        PieceCharacter pieceChar = PieceCharacter.valueOf(piece.getPieceType().name());
        character = pieceChar.getCharacter(piece.getTeamColor());
      } else {
        txtColor = SET_TEXT_COLOR_BLACK;
        character = EMPTY;
      }
    }
    return txtColor + character;
  }

  enum PieceCharacter {
    PAWN(WHITE_PAWN, BLACK_PAWN),
    ROOK(WHITE_ROOK, BLACK_ROOK),
    KNIGHT(WHITE_KNIGHT, BLACK_KNIGHT),
    BISHOP(WHITE_BISHOP, BLACK_BISHOP),
    QUEEN(WHITE_QUEEN, BLACK_QUEEN),
    KING(WHITE_KING, BLACK_KING);

    private final String whiteCharacter;
    private final String blackCharacter;

    PieceCharacter(String whiteCharacter, String blackCharacter) {
      this.whiteCharacter = whiteCharacter;
      this.blackCharacter = blackCharacter;
    }

    public String getCharacter(ChessGame.TeamColor color) {
      if (color == WHITE) {
        return whiteCharacter;
      } else {
        return blackCharacter;
      }
    }
  }
}
