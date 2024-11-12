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

    return "";
  }

  public String login(String... params) throws ResponseException {

    return "";
  }

  public String listGames(String... params) throws ResponseException {

    return "";
  }

  public String logout(String... params) throws ResponseException {

    return "";
  }

  public String createGame(String... params) throws ResponseException {

    return "";
  }

  public String joinGame(String... params) throws ResponseException {

    return "";
  }

  public String observeGame(String... params) throws ResponseException {

    return "";
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
