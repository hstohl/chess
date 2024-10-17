package server;

import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import services.UserService;
import spark.*;

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
    Spark.delete("/db", (req, response) -> "{}");

    Spark.awaitInitialization();
    return Spark.port();
  }

  private String createUser(Request req, Response res) throws ServiceException {
    var newUser = serializer.fromJson(req.body(), UserData.class);
    var result = service.registerUser(newUser);
    return serializer.toJson(result);

//    var g = new Gson();
//    var newUser = g.fromJson(
//            """
//                    { "username":"", "password":"", "email":"" }
//                    """, UserData.class);
//    return newUser.toString();
  }

  private void deleteDB() {

  }

  public void stop() {
    Spark.stop();
    Spark.awaitStop();
  }
}
