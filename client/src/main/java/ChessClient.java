import chess.ChessGame;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ChessClient {
  private String visitorName = null;
  private final ServerFacade server;
  private final String serverUrl;
  //private final NotificationHandler notificationHandler;
  //private WebSocketFacade ws;
  private State state = State.SIGNEDOUT;

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

    return "reg";
  }

  public String login(String... params) throws ResponseException {

    return "log";
  }

  public String listGames(String... params) throws ResponseException {

    return "list";
  }

  public String logout(String... params) throws ResponseException {

    return "loggo";
  }

  public String createGame(String... params) throws ResponseException {

    return "cre";
  }

  public String joinGame(String... params) throws ResponseException {

    String string = getBoardString(ChessGame.TeamColor.WHITE);
    string = string + "\n\n\n";
    string = string + getBoardString(ChessGame.TeamColor.BLACK);

    return string;
  }

  public static String getBoardString(ChessGame.TeamColor color) {
    String string = "";
    String bgColor;
    String character;
    if (color == ChessGame.TeamColor.BLACK) {
      for (int i = 0; i < 10; ++i) {
        for (int j = 0; j < 10; ++j) {
          bgColor = SET_BG_COLOR_BLACK;
          character = SET_TEXT_COLOR_BLACK + EMPTY;
          //make board white
          if (i == 0 || i == 9 || j == 0 || j == 9) {
            bgColor = SET_BG_COLOR_LIGHT_GREY;
            if ((j == 0 || j == 9) && i != 0 && i != 9) {
              character = " " + i + " ";
            }
            if ((i == 0 || i == 9) && j != 0 && j != 9) {
              character = " " + Character.toString((char) 96 + 9 - j) + " ";
            }
          } else if (i % 2 == 1 && j % 2 == 1) {
            bgColor = SET_BG_COLOR_WHITE;
          } else if (i % 2 == 0 && j % 2 == 0) {
            bgColor = SET_BG_COLOR_WHITE;
          }
          string = string + bgColor + character;
        }
        string = string + SET_BG_COLOR_BLACK + "\n";
      }
    } else if (color == ChessGame.TeamColor.WHITE) {
      for (int i = 0; i < 10; ++i) {
        for (int j = 0; j < 10; ++j) {
          bgColor = SET_BG_COLOR_BLACK;
          character = SET_TEXT_COLOR_BLACK + EMPTY;
          //string = string + "";
          //make board black
          if (i == 0 || i == 9 || j == 0 || j == 9) {
            bgColor = SET_BG_COLOR_LIGHT_GREY;
            if ((j == 0 || j == 9) && i != 0 && i != 9) {
              character = " " + (9 - i) + " ";
            }
            if ((i == 0 || i == 9) && j != 0 && j != 9) {
              character = " " + Character.toString((char) 96 + j) + " ";
            }
          } else if (i % 2 == 1 && j % 2 == 1) {
            bgColor = SET_BG_COLOR_WHITE;
          } else if (i % 2 == 0 && j % 2 == 0) {
            bgColor = SET_BG_COLOR_WHITE;
          }
          string = string + bgColor + character;
        }
        string = string + SET_BG_COLOR_BLACK + "\n";
      }
    }


    return string;
  }

  public String observeGame(String... params) throws ResponseException {

    return "ob";
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
