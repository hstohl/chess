package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameDataMini;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.NULL;

public class GameDatabaseAccess implements GameDataAccess {
  private final Gson serializer = new Gson();

  public GameData getGame(String gameName) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameName = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, gameName);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readGame(rs);
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public void addGame(GameData newGame) {
    var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";

    try {
      executeUpdate(statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(),
              newGame.gameName(), serializer.toJson(newGame.game()));
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public GameData getGameI(int id) {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setInt(1, id);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readGame(rs);
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public void updateGame(GameData updatedGame) throws DataAccessException {
    var statement1 = "DELETE FROM game WHERE gameID=?";
    var statement2 = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
    executeUpdate(statement1, updatedGame.gameID());
    executeUpdate(statement2, updatedGame.gameID(), updatedGame.whiteUsername(), updatedGame.blackUsername(),
            updatedGame.gameName(), serializer.toJson(updatedGame.game()));
  }

  public Collection<GameDataMini> listGames() throws DataAccessException {
    var result = new ArrayList<GameDataMini>();
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";
      try (var ps = conn.prepareStatement(statement)) {
        try (var rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(readSmallGame(rs));
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return result;
  }

  public void clear() {
    var statement = "TRUNCATE game";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private GameData readGame(ResultSet rs) throws SQLException {
    ChessGame game = serializer.fromJson(rs.getString("chessGame"), ChessGame.class);
    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"),
            rs.getString("gameName"), game);
  }

  private GameDataMini readSmallGame(ResultSet rs) throws SQLException {
    return new GameDataMini(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"),
            rs.getString("gameName"));
  }

  private void executeUpdate(String statement, Object... params) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      try (var ps = conn.prepareStatement(statement)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          if (param instanceof String p) ps.setString(i + 1, p);
          else if (param instanceof Integer p) ps.setInt(i + 1, p);
          else if (param == null) ps.setNull(i + 1, Types.VARCHAR);
        }
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }
}
