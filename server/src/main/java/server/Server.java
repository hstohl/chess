package server;

import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import services.UserService;
import spark.*;

import java.util.Map;

public class Server {
  private final DataAccess dataAccess = new MemoryDataAccess();
  private final UserService service = new UserService(dataAccess);
  private final Gson serializer = new Gson();

  public int run(int desiredPort) {
    Spark.port(desiredPort);

    Spark.staticFiles.location("web");

    // Register your endpoints and handle exceptions here.
    /*Spark.post("/user", (req, res) ->
            (new RegisterHandler()).handleRequest(req, res));*/

    //This line initializes the server and can be removed once you have a functioning endpoint
    Spark.init();

    Spark.post("/user", this::createUser);
    Spark.delete("/db", (req, response) -> deleteDB());
    Spark.post("/session", this::loginUser);
    Spark.delete("/session", this::logoutUser);
    Spark.get("/game", this::getGames);
    Spark.post("/game", this::createGame);
    Spark.put("/game", this::joinGame);


    Spark.awaitInitialization();
    return Spark.port();
  }

  private String createUser(Request req, Response res) throws ServiceException {
    try {
      var newUser = serializer.fromJson(req.body(), UserData.class);
      var result = service.registerUser(newUser);
      return serializer.toJson(result);
    } catch (ServiceException e) {
      if (e.getMessage() == "Error: already taken") {
        res.status(403);
        var errorResponse = Map.of("message", "Error: already taken");
        return serializer.toJson(errorResponse);
      } else if (e.getMessage() == "Error: bad request") {
        res.status(400);
        var errorResponse = Map.of("message", "Error: bad request");
        return serializer.toJson(errorResponse);
      } else {
        res.status(500);
        var errorResponse = Map.of("message", "Error");
        return serializer.toJson(errorResponse);
      }
    }
  }

  private String deleteDB() {
    service.clear();
    return "{}";
  }

  private String loginUser(Request req, Response res) throws ServiceException {
    var user = serializer.fromJson(req.body(), UserData.class);
    var result = service.login(user);
    return serializer.toJson(result);
  }

  private String logoutUser(Request req, Response res) throws ServiceException {
    var auth = req.headers("Authorization");
    service.logout(auth);
    return "{}";
  }

  private String getGames(Request req, Response res) throws ServiceException {
    var result = service.listGames(req.headers("Authorization"));
    return serializer.toJson(result);
  }

  private String createGame(Request req, Response res) throws ServiceException {
    var gameName = serializer.fromJson(req.body(), NewGameRequest.class);
    var result = service.createGame(gameName, req.headers("Authorization"));
    return serializer.toJson(result);
  }

  private String joinGame(Request req, Response res) throws ServiceException {
    var joinGameReq = serializer.fromJson(req.body(), JoinGameRequest.class);
    var result = service.joinGame(req.headers("Authorization"), joinGameReq);

    return serializer.toJson(result);
  }


  public void stop() {
    Spark.stop();
    Spark.awaitStop();
  }
}
