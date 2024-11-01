package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDatabaseAccess implements AuthDataAccess {

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
    DatabaseUtil.executeUpdate(statement, params);
  }
}
