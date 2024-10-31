package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDatabaseAccess implements DataAccess {

  public UserDatabaseAccess() throws DataAccessException {
    configureDatabase();
  }

  public UserData getUser(String username) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT username, json FROM pet WHERE id=?";
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

  public void addUser(UserData newUser) {

  }

  public void clear() {

  }

  private UserData readUser(ResultSet rs) throws SQLException {
    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
  }


  private final String[] createStatements = {
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

  private void configureDatabase() throws DataAccessException {
    try {
      DatabaseManager.createDatabase();
      try (var conn = DatabaseManager.getConnection()) {
        for (var statement : createStatements) {
          try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
    }
  }
}
