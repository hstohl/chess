
import java.util.Scanner;

import facade.NotificationHandler;
import ui.EscapeSequences.*;
import websocket.messages.ServerMessage;

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
    System.out.println(SET_TEXT_COLOR_RED + notification.getServerMessageType());
    printPrompt();
  }

}