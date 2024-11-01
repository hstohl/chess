package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class AuthDatabaseAccess implements AuthDataAccess {

  /*public AuthDatabaseAccess() throws DataAccessException {
    configureDatabase();
  }*/

  public AuthData getAuth(String username) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT authToken, username FROM auth WHERE username = ? ORDER BY created_at DESC LIMIT 1";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, username);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readAuth(rs);
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public AuthData getAuthT(String auth) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, auth);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readAuth(rs);
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public void addAuth(AuthData newAuth) throws DataAccessException {
    var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
    executeUpdate(statement, newAuth.authToken(), newAuth.username());
  }

  public void removeAuth(AuthData auth) throws DataAccessException {
    var statement = "DELETE FROM auth WHERE authToken=?";
    executeUpdate(statement, auth.authToken());
  }

  public void clear() {
    var statement = "TRUNCATE auth";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private AuthData readAuth(ResultSet rs) throws SQLException {
    return new AuthData(rs.getString("authToken"), rs.getString("username"));
  }

  private void executeUpdate(String statement, Object... params) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      try (var ps = conn.prepareStatement(statement)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          switch (param) {
            case String p -> ps.setString(i + 1, p);
            case Integer p -> ps.setInt(i + 1, p);
            case null -> ps.setNull(i + 1, NULL);
            default -> {
            }
          }
        }
        ps.executeUpdate();

        /*var rs = ps.getGeneratedKeys();
        if (rs.next()) {
          return rs.getInt(1);
        }

        return 0;*/
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }


  /*private final String[] createStatements = {
          """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(4096) NOT NULL,
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
  }*/
}
