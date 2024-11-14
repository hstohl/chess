import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.UserData;

import java.util.Arrays;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static java.lang.Math.abs;
import static ui.EscapeSequences.*;

public class ChessClient {
  private String visitorName = null;
  private final ServerFacade server;
  private final String serverUrl;
  private final Gson serializer = new Gson();
  //private final NotificationHandler notificationHandler;
  //private WebSocketFacade ws;
  private State state = State.SIGNEDOUT;
  private ChessBoard board = new ChessBoard();

  public ChessClient(String serverUrl/*, NotificationHandler notificationHandler*/) {
    server = new ServerFacade(serverUrl);
    this.serverUrl = serverUrl;
    //this.notificationHandler = notificationHandler;
  }

  public String eval(String input) {
    try {
      var tokens = input.toLowerCase().split(" ");
      var cmd = (tokens.length > 0) ? tokens[0] : "help";
      var params = Arrays.copyOfRange(tokens, 1, tokens.length);
      return switch (cmd) {
        case "register" -> register(params);
        case "login" -> login(params);
        case "list" -> listGames();
        case "logout" -> logout();
        case "create" -> createGame(params);
        case "join" -> joinGame();
        case "observe" -> observeGame();
        case "quit" -> "quit";
        default -> help();
      };
    } catch (ResponseException ex) {
      return ex.getMessage();
    }
  }

  public String register(String... params) throws ResponseException {
    if (params.length == 3) {
      UserData newUser = new UserData(params[0], params[1], params[2]);
      server.register(newUser);
      return "Successfully Registered!";
    }
    throw new ResponseException(400, "Expected <USERNAME> <PASSWORD> <EMAIL>");
  }

  public String login(String... params) throws ResponseException {

    return "log";
  }

  public String listGames(String... params) throws ResponseException {
    assertSignedIn();

    return "list";
  }

  public String logout(String... params) throws ResponseException {
    assertSignedIn();

    return "loggo";
  }

  public String createGame(String... params) throws ResponseException {
    assertSignedIn();

    return "cre";
  }

  public String joinGame(String... params) throws ResponseException {
    assertSignedIn();
    board.resetBoard();

    String string = getBoardString(WHITE);
    string = string + "\n\n\n";
    string = string + getBoardString(BLACK);

    return string;
  }

  public String observeGame(String... params) throws ResponseException {
    assertSignedIn();
    board.resetBoard();

    String string = getBoardString(WHITE);
    string = string + "\n\n\n";
    string = string + getBoardString(BLACK);

    return string;
  }

  public String getBoardString(ChessGame.TeamColor color) {
    String string = "";
    String bgColor;
    String character;
    String txtColor;
    if (color == BLACK) {
      for (int i = 0; i < 10; ++i) {
        for (int j = 0; j < 10; ++j) {
          bgColor = SET_BG_COLOR_BLACK;
          character = EMPTY;
          txtColor = SET_TEXT_COLOR_BLACK;
          //make board white
          if (i == 0 || i == 9 || j == 0 || j == 9) {
            bgColor = SET_BG_COLOR_LIGHT_GREY;
            if ((j == 0 || j == 9) && i != 0 && i != 9) {
              character = SET_TEXT_COLOR_BLACK + " " + i + " ";
            }
            if ((i == 0 || i == 9) && j != 0 && j != 9) {
              character = SET_TEXT_COLOR_BLACK + " " + Character.toString((char) 96 + 9 - j) + " ";
            }
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
    } else if (color == WHITE) {
      for (int i = 0; i < 10; ++i) {
        for (int j = 0; j < 10; ++j) {
          bgColor = SET_BG_COLOR_BLACK;
          character = EMPTY;
          txtColor = SET_TEXT_COLOR_BLACK;
          //string = string + "";
          //make board black
          if (i == 0 || i == 9 || j == 0 || j == 9) {
            bgColor = SET_BG_COLOR_LIGHT_GREY;
            if ((j == 0 || j == 9) && i != 0 && i != 9) {
              character = SET_TEXT_COLOR_BLACK + " " + (9 - i) + " ";
            }
            if ((i == 0 || i == 9) && j != 0 && j != 9) {
              character = SET_TEXT_COLOR_BLACK + " " + Character.toString((char) 96 + j) + " ";
            }
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
    }

    return string;
  }

  private String piecesOnBoard(int i, int j, ChessGame.TeamColor color) {
    String txtColor = "";
    String character = "";
    int dif = 0;
    if (color == WHITE) {
      dif = 9;
    }
    if (i != 0 && i != 9 && j != 0 && j != 9) {
      ChessPiece piece = board.getPiece(new ChessPosition(abs(dif - i), abs(j - dif)));
      if (piece != null) {
        if (piece.getTeamColor() == WHITE) {
          txtColor = SET_TEXT_COLOR_LIGHT_GREY;
        } else if (piece.getTeamColor() == BLACK) {
          txtColor = SET_TEXT_COLOR_BLUE;
        }

        PieceCharacter pieceChar = PieceCharacter.valueOf(piece.getPieceType().name());
        character = pieceChar.getCharacter(piece.getTeamColor());
        /*if (piece.getTeamColor() == WHITE) {
          txtColor = SET_TEXT_COLOR_LIGHT_GREY;
          if (piece.getPieceType() == PAWN) {
            character = WHITE_PAWN;
          } else if (piece.getPieceType() == ROOK) {
            character = WHITE_ROOK;
          } else if (piece.getPieceType() == KNIGHT) {
            character = WHITE_KNIGHT;
          } else if (piece.getPieceType() == BISHOP) {
            character = WHITE_BISHOP;
          } else if (piece.getPieceType() == QUEEN) {
            character = WHITE_QUEEN;
          } else if (piece.getPieceType() == KING) {
            character = WHITE_KING;
          }
        } else if (piece.getTeamColor() == BLACK) {
          txtColor = SET_TEXT_COLOR_BLUE;
          if (piece.getPieceType() == PAWN) {
            character = BLACK_PAWN;
          } else if (piece.getPieceType() == ROOK) {
            character = BLACK_ROOK;
          } else if (piece.getPieceType() == KNIGHT) {
            character = BLACK_KNIGHT;
          } else if (piece.getPieceType() == BISHOP) {
            character = BLACK_BISHOP;
          } else if (piece.getPieceType() == QUEEN) {
            character = BLACK_QUEEN;
          } else if (piece.getPieceType() == KING) {
            character = BLACK_KING;
          }
        } */
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
      //return color == ChessGame.TeamColor.WHITE ? whiteCharacter : blackCharacter;
    }
  }

  public String help() {
    if (state == State.SIGNEDOUT) {
      String string = SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- to create an account\n" +
              SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- to play chess\n" +
              SET_TEXT_COLOR_BLUE + "quit " +
              SET_TEXT_COLOR_LIGHT_GREY + "- playing chess\n" +
              SET_TEXT_COLOR_BLUE + "help " +
              SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
      return string;
    }
    String string = SET_TEXT_COLOR_BLUE + "create <NAME> " +
            SET_TEXT_COLOR_LIGHT_GREY + "- a game\n" +
            SET_TEXT_COLOR_BLUE + "list " +
            SET_TEXT_COLOR_LIGHT_GREY + "- games\n" +
            SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] " +
            SET_TEXT_COLOR_LIGHT_GREY + "- a game\n" +
            SET_TEXT_COLOR_BLUE + "observe ‹ID> " +
            SET_TEXT_COLOR_LIGHT_GREY + "- aa game\n" +
            SET_TEXT_COLOR_BLUE + "logout " +
            SET_TEXT_COLOR_LIGHT_GREY + "- when you are done\n" +
            SET_TEXT_COLOR_BLUE + "quit " +
            SET_TEXT_COLOR_LIGHT_GREY + "- playing chess\n" +
            SET_TEXT_COLOR_BLUE + "help " +
            SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
    return string;
  }

  public boolean isSignedIn() {
    if (state == State.SIGNEDIN) {
      return true;
    }
    return false;
  }

  private void assertSignedIn() throws ResponseException {
    if (state == State.SIGNEDOUT) {
      throw new ResponseException(400, "You must sign in");
    }
  }
}
