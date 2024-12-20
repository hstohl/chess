package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDatabaseAccess implements DataAccess {

  public UserDatabaseAccess() {
    configureDatabase();
  }

  public UserData getUser(String username) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT username, password, email FROM user WHERE username = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, username);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readUser(rs);
          }
        }
      }
    } catch (Exception e) {
      //throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  public void addUser(UserData newUser) throws DataAccessException {
    var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
    executeUpdate(statement, newUser.username(), newUser.password(), newUser.email());
    //return new UserData(newUser.username(), newUser.password(), newUser.email());
  }

  public void clear() {
    var statement = "TRUNCATE user";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private UserData readUser(ResultSet rs) throws SQLException {
    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
  }

  private void executeUpdate(String statement, Object... params) throws DataAccessException {
    DatabaseUtil.executeUpdate(statement, params);
  }


  private final String[] createStatements = {
          /*"""
            DROP TABLE IF EXISTS game;
            """,*/
          """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(45) NOT NULL,
              `chessGame` TEXT DEFAULT NULL,
              isOver BOOLEAN DEFAULT FALSE,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
          /*"""
            DROP TABLE IF EXISTS auth;
            """, */
          """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `created_at` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
              PRIMARY KEY (`authToken`),
              UNIQUE KEY `authToken_UNIQUE` (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
          /*"""
            DROP TABLE IF EXISTS user;
            """,*/
          """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(4096) NOT NULL,
              `email` varchar(45) NOT NULL,
              PRIMARY KEY (`username`),
              UNIQUE KEY `username_UNIQUE` (`username`)
            ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
  };

  private void configureDatabase() {
    try {
      DatabaseManager.createDatabase();
      try (var conn = DatabaseManager.getConnection()) {
        for (var statement : createStatements) {
          try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      return;
    }
  }
}
