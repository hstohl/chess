package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Request;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
  private final String serverUrl;

  public ServerFacade(String url) {
    serverUrl = url;
  }

  public AuthData register(UserData newUser) throws ResponseException {
    var path = "/user";
    return this.makeRequest("POST", path, newUser, AuthData.class, null);
  }

  public SuccessResponse clear() throws ResponseException {
    var path = "/db";
    return this.makeRequest("DELETE", path, null, null, null);
  }

  public AuthData login(UserData user) throws ResponseException {
    var path = "/session";
    return this.makeRequest("POST", path, user, AuthData.class, null);
  }

  public SuccessResponse logout(String token) throws ResponseException {
    var path = "/session";

    return this.makeRequest("DELETE", path, token, null, token);
  }

  public NewGameResult createGame(NewGameRequest newGameN, String token) throws ResponseException {
    var path = "/game";
    return this.makeRequest("POST", path, newGameN, NewGameResult.class, token);
  }

  public SuccessResponse joinGame(String token, JoinGameRequest req) throws ResponseException {
    var path = "/game";
    return this.makeRequest("PUT", path, req, SuccessResponse.class, token);
  }

  public GameList listGames(String token) throws ResponseException {
    var path = "/game";
    return this.makeRequest("GET", path, token, GameList.class, token);
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
    try {
      URL url = (new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);
      http.setRequestProperty("Authorization", auth);

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  private static void writeBody(Object request, HttpURLConnection http) throws IOException {
    if (request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData = new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      var message = http.getResponseMessage();

      try (InputStream errorStream = http.getErrorStream()) {
        if (errorStream != null) {
          message += ": " + new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
        }
      }

      throw new ResponseException(status, message);
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() < 0) {
      try (InputStream respBody = http.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(respBody);
        if (responseClass != null) {
          response = new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
