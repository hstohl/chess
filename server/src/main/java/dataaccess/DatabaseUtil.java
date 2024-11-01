package dataaccess;

import java.sql.SQLException;

public class DatabaseUtil {

  private DatabaseUtil() {
    // Private constructor to prevent instantiation
  }

  public static void executeUpdate(String statement, Object... params) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      try (var ps = conn.prepareStatement(statement)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          if (param instanceof String p) {
            ps.setString(i + 1, p);
          } else if (param instanceof Integer p) {
            ps.setInt(i + 1, p);
          } else if (param == null) {
            ps.setNull(i + 1, java.sql.Types.NULL);
          }
        }
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }
}

