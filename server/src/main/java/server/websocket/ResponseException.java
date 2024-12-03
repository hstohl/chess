package server.websocket;

public class ResponseException extends Exception {
  final private int statusCode;
  private String message;

  public ResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}