import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.NotificationHandler;
import facade.WebSocketFacade;
import model.*;
import facade.ResponseException;
import facade.ServerFacade;

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static ui.EscapeSequences.*;

public class ChessClient {
  private WebSocketFacade ws;
  private final NotificationHandler notificationHandler;
  private AuthData myAuth = null;
  private final ServerFacade server;
  private final String serverUrl;
  private State state = State.SIGNEDOUT;
  private static ChessBoard board = new ChessBoard();
  private Map<Integer, Integer> gameMap = new HashMap<>();
  private int currGameId;

  public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
    this.notificationHandler = notificationHandler;
    server = new ServerFacade(serverUrl);
    this.serverUrl = serverUrl;
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
        case "join" -> joinGame(params);
        case "observe" -> observeGame(params);
        case "move" -> makeMove(params);
        case "highlight" -> highlight(params);
        case "redraw" -> redraw();
        case "leave" -> leave();
        case "resign" -> resign();
        case "quit" -> quit();
        default -> help();
      };
    } catch (ResponseException ex) {
      return ex.getMessage();
    }
  }

  public String quit() throws ResponseException {
    assertSignedOut();
    return "quit";
  }

  public String register(String... params) throws ResponseException {
    if (params.length == 3) {
      UserData newUser = new UserData(params[0], params[1], params[2]);
      myAuth = server.register(newUser);
      state = State.SIGNEDIN;
      return "Successfully Registered!";
    }
    throw new ResponseException(400, "Expected <USERNAME> <PASSWORD> <EMAIL>");
  }

  public String login(String... params) throws ResponseException {
    assertSignedOut();
    if (params.length == 2) {
      UserData user = new UserData(params[0], params[1], "");
      myAuth = server.login(user);
      state = State.SIGNEDIN;
      return "Logged in!";
    }
    throw new ResponseException(400, "Expected <USERNAME> <PASSWORD>");
  }

  public String listGames(String... params) throws ResponseException {
    int currGameNum = 1;
    assertSignedIn();
    GameList list = server.listGames(myAuth.authToken());
    gameMap.clear();

    String gameList = "";
    for (GameDataMini game : list.games()) {
      //GameDataMini game = list.games().get(i);
      String white = "";
      String black = "";
      if (!isNull(game.whiteUsername())) {
        white = game.whiteUsername();
      }
      if (!isNull(game.blackUsername())) {
        black = game.blackUsername();
      }
      gameMap.put(currGameNum, game.gameID());
      gameList += (currGameNum) + ".  " + "Game Name: " + game.gameName() + "\nWhite Player: " + white + "\nBlack Player: " + black + "\n\n";
      ++currGameNum;
    }

    return gameList;
  }

  public String logout(String... params) throws ResponseException {
    assertSignedIn();
    server.logout(myAuth.authToken());
    state = State.SIGNEDOUT;
    return "Logged out!";
  }

  public String createGame(String... params) throws ResponseException {
    assertSignedIn();
    if (params.length == 1) {
      NewGameRequest req = new NewGameRequest(params[0]);
      NewGameResult game = server.createGame(req, myAuth.authToken());
      return "Game Created. \nGame Name: " + req.gameName();
    }
    throw new ResponseException(400, "Expected <NAME>");
  }

  public String joinGame(String... params) throws ResponseException {
    int gameID = 0;
    assertSignedIn();
    if (params.length == 2 &&
            (Objects.equals(params[1].toUpperCase(), "BLACK") || Objects.equals(params[1].toUpperCase(), "WHITE"))) {
      try {
        gameID = gameMap.get(parseInt(params[0]));
      } catch (Exception e) {
        throw new ResponseException(400, "Please enter a valid ID.");
      }
      JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.valueOf(params[1].toUpperCase()), gameID);
      server.joinGame(myAuth.authToken(), req);
    } else {
      throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
    }

    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.connectToGame(myAuth.authToken(), gameID);

    currGameId = gameID;

    state = State.INGAME;

    board.resetBoard();
    String string = getBoardString(WHITE);
    string = string + "\n\n\n";
    string = string + getBoardString(BLACK);

    return string;
  }

  public String observeGame(String... params) throws ResponseException {
    int gameID = 0;
    assertSignedIn();
    if (params.length == 1) {
      try {
        gameID = gameMap.get(parseInt(params[0]));
      } catch (Exception e) {
        throw new ResponseException(400, "Please enter a valid ID.");
      }
      JoinGameRequest req = new JoinGameRequest(NONE, gameID);
      server.joinGame(myAuth.authToken(), req);
    } else {
      throw new ResponseException(400, "Expected <ID>");
    }

    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.connectToGame(myAuth.authToken(), gameID);

    currGameId = gameID;

    state = State.INGAME;

    board.resetBoard();
    String string = getBoardString(WHITE);
    string = string + "\n\n\n";
    string = string + getBoardString(BLACK);

    return string;
  }

  public String makeMove(String... params) throws ResponseException {
    assertJoined();
    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.makeChessMove(myAuth.authToken(), currGameId);
    return "";
  }

  public String redraw(String... params) throws ResponseException {
    assertJoined();
    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.redraw(myAuth.authToken(), currGameId);
    return "";
  }

  public String highlight(String... params) throws ResponseException {
    assertJoined();
    int id = 0;
    ws = new WebSocketFacade(serverUrl, notificationHandler);
    //ws.highlight(myAuth.authToken(), id);
    return "";
  }

  public String leave(String... params) throws ResponseException {
    assertJoined();
    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.leaveGame(myAuth.authToken(), currGameId);
    state = State.SIGNEDIN;
    return "";
  }

  public String resign(String... params) throws ResponseException {
    assertJoined();
    ws = new WebSocketFacade(serverUrl, notificationHandler);
    ws.resignGame(myAuth.authToken(), currGameId);
    return "";
  }


  public String getBoardString(ChessGame.TeamColor color) {
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
      ChessPiece piece = board.getPiece(new ChessPosition(abs(dif - i), abs(j - dif)));
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

  public String help() {
    String string = "";
    if (state == State.SIGNEDOUT) {
      string = SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- to create an account\n" +
              SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- to play chess\n" +
              SET_TEXT_COLOR_BLUE + "quit " +
              SET_TEXT_COLOR_LIGHT_GREY + "- playing chess\n" +
              SET_TEXT_COLOR_BLUE + "help " +
              SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
    } else if (state == State.SIGNEDIN) {
      string = SET_TEXT_COLOR_BLUE + "create <NAME> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- a game\n" +
              SET_TEXT_COLOR_BLUE + "list " +
              SET_TEXT_COLOR_LIGHT_GREY + "- games\n" +
              SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] " +
              SET_TEXT_COLOR_LIGHT_GREY + "- a game\n" +
              SET_TEXT_COLOR_BLUE + "observe ‹ID> " +
              SET_TEXT_COLOR_LIGHT_GREY + "- aa game\n" +
              SET_TEXT_COLOR_BLUE + "logout " +
              SET_TEXT_COLOR_LIGHT_GREY + "- when you are done\n" +
              SET_TEXT_COLOR_BLUE + "help " +
              SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
    } else if (state == State.INGAME) {
      string = SET_TEXT_COLOR_BLUE + "redraw " +
              SET_TEXT_COLOR_LIGHT_GREY + "- the chess board\n" +
              SET_TEXT_COLOR_BLUE + "leave " +
              SET_TEXT_COLOR_LIGHT_GREY + "- the game\n" +
              SET_TEXT_COLOR_BLUE + "move {move} " +
              SET_TEXT_COLOR_LIGHT_GREY + "- a chess piece\n" +
              SET_TEXT_COLOR_BLUE + "resign " +
              SET_TEXT_COLOR_LIGHT_GREY + "- the game\n" +
              SET_TEXT_COLOR_BLUE + "highlight <position>" +
              SET_TEXT_COLOR_LIGHT_GREY + "- legal moves\n" +
              SET_TEXT_COLOR_BLUE + "help " +
              SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
    } else if (state == State.OBSERVING) {
      string = SET_TEXT_COLOR_BLUE + "redraw " +
              SET_TEXT_COLOR_LIGHT_GREY + "- the chess board\n" +
              SET_TEXT_COLOR_BLUE + "leave " +
              SET_TEXT_COLOR_LIGHT_GREY + "- the game\n" +
              SET_TEXT_COLOR_BLUE + "highlight <position>" +
              SET_TEXT_COLOR_LIGHT_GREY + "- legal moves\n" +
              SET_TEXT_COLOR_BLUE + "help " +
              SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n";
    }
    return string;
  }

  public boolean isSignedIn() {
    return state == State.SIGNEDIN;
  }

  public boolean isGameplay() {
    return (state == State.INGAME || state == State.OBSERVING);
  }

  private void assertSignedIn() throws ResponseException {
    if (state == State.SIGNEDOUT) {
      throw new ResponseException(400, "You must sign in.");
    } else if (state == State.INGAME || state == State.OBSERVING) {
      throw new ResponseException(400, "You must leave game first.");
    }
  }

  private void assertSignedOut() throws ResponseException {
    if (state != State.SIGNEDOUT) {
      throw new ResponseException(400, "Logout before performing this action.");
    }
  }

  private void assertJoined() throws ResponseException {
    if (state != State.INGAME) {
      throw new ResponseException(400, "You must join a game before performing this action.");
    }
  }
}
