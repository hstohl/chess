package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

  private static Server server;
  static ServerFacade facade;

  @BeforeAll
  public static void init() {
    server = new Server();
    var port = server.run(0);
    System.out.println("Started test HTTP server on " + port);
    String url = "http://localhost:" + port;
    facade = new ServerFacade(url);
  }

  @AfterAll
  static void stopServer() {
    server.stop();
  }

  @BeforeEach
  void clear() throws Exception {
    facade.clear();
  }

  @Test
  void register() throws Exception {
    var authData = facade.register(new UserData("hstohl", "password", "email@email.com"));
    assertTrue(authData.authToken().length() > 10);
  }

  @Test
  public void sampleTest() {
    assertTrue(true);
  }

}
