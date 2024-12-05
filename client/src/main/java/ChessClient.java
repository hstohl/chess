import chess.*;
import facade.NotificationHandler;
import facade.WebSocketFacade;
import model.*;
import facade.ResponseException;
import facade.ServerFacade;

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static chess.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static chess.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;

public class ChessClient {
  private WebSocketFacade ws;
  private final NotificationHandler notificationHandler;
  private AuthData myAuth = null;
  private final ServerFacade server;
  private final String serverUrl;
  private State state = State.SIGNEDOUT;
  private ChessGame theGame = new ChessGame();
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

    return "";
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

    return "";
  }

  public String makeMove(String... params) throws ResponseException {
    assertJoined();
    if ((params.length == 2 || params.length == 3) &&
            params[0].length() == 2 && params[1].length() == 2 && // Ensure start and end positions are exactly two characters
            params[0].charAt(0) >= 'a' && params[0].charAt(0) <= 'h' && // Start file
            params[0].charAt(1) >= '1' && params[0].charAt(1) <= '8' && // Start rank
            params[1].charAt(0) >= 'a' && params[1].charAt(0) <= 'h' && // End file
            params[1].charAt(1) >= '1' && params[1].charAt(1) <= '8' && // End rank
            (params.length != 3 || (
                    params[0].charAt(1) == '7' && params[1].charAt(1) == '8' || // White pawn promotion
                            params[0].charAt(1) == '2' && params[1].charAt(1) == '1'    // Black pawn promotion
            )) &&
            (params.length != 3 || isValidPromotionPiece(params[2]))) {
      //System.out.println("Col Start: " + (params[0].charAt(0) - 'a' + 1) + ". Row Start: " + params[0].charAt(1));
      //System.out.println("Col End: " + (params[1].charAt(0) - 'a' + 1) + ". Row End: " + params[1].charAt(1));
      ChessPosition start = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - 'a' + 1);
      ChessPosition end = new ChessPosition(params[1].charAt(1) - '0', params[1].charAt(0) - 'a' + 1);
      ChessMove move = new ChessMove(start, end, null);
      if (params.length == 3) {
        ChessPiece.PieceType piece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
        move = new ChessMove(start, end, piece);
      }

      ws = new WebSocketFacade(serverUrl, notificationHandler);
      ws.makeChessMove(myAuth.authToken(), currGameId, move);

    } else {
      throw new ResponseException(400, "Expected <column><row> <column><row> <promotion type>. Ensure your columns and rows are correct. " +
              "Promotion type is optional. If you are promoting, ensure you entered a promotion piece.");
    }

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
    if ((params.length == 1) && params[0].length() == 2 &&  // Ensure positions is exactly two characters
            params[0].charAt(0) >= 'a' && params[0].charAt(0) <= 'h' &&
            params[0].charAt(1) >= '1' && params[0].charAt(1) <= '8') {
      ws = new WebSocketFacade(serverUrl, notificationHandler);
      ws.highlight(myAuth.authToken(), currGameId);
    } else {
      throw new ResponseException(400, "Expected <column><row>");
    }

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

  private static boolean isValidPromotionPiece(String piece) {
    return piece.equalsIgnoreCase("queen") ||
            piece.equalsIgnoreCase("rook") ||
            piece.equalsIgnoreCase("bishop") ||
            piece.equalsIgnoreCase("knight");
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
