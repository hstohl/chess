
import java.util.Scanner;

import chess.ChessGame;
import com.google.gson.Gson;
import facade.NotificationHandler;
import ui.EscapeSequences.*;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
  private final ChessClient client;

  public Repl(String serverUrl) {
    client = new ChessClient(serverUrl, this);
  }

  public void run() {
    System.out.println(WHITE_KING + " Welcome to 240 chess. Type \"help\" to get started. " + BLACK_KING);
    System.out.print(client.help());

    Scanner scanner = new Scanner(System.in);
    var result = "";
    while (!result.equals("quit")) {
      printPrompt();
      String line = scanner.nextLine();

      try {
        result = client.eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
      } catch (Throwable e) {
        var msg = e.toString();
        System.out.print(msg);
      }
    }
    System.out.println();
  }

  private void printPrompt() {
    if (client.isSignedIn()) {
      System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    } else {
      System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
  }

  public void notify(ServerMessage notification) {
    System.out.println("\n" + SET_TEXT_COLOR_RED + notification.getMessage());
    printPrompt();
  }

  public void notiNotify(NotificationServerMessage notification) {
    //System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());
    printPrompt();
  }

  public void loadNotify(LoadGameServerMessage notification) {
    //System.out.println(notification.getGame().getBoard().getBoardString());
    printPrompt();
  }

  public void errorNotify(ErrorServerMessage notification) {
    //System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());
    printPrompt();
  }

}