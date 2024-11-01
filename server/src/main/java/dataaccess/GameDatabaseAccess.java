package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameDataMini;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
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
            return readUser(rs);
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
            return readUser(rs);
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public void updateGame(GameData updatedGame) {

  }

  public Collection<GameDataMini> listGames() {

    return new ArrayList<>();
  }

  public void clear() {
    var statement = "TRUNCATE game";
    try {
      executeUpdate(statement);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private GameData readUser(ResultSet rs) throws SQLException {
    ChessGame game = serializer.fromJson(rs.getString("chessGame"), ChessGame.class);
    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"),
            rs.getString("gameName"), game);
  }

  private void executeUpdate(String statement, Object... params) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      try (var ps = conn.prepareStatement(statement)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          if (param instanceof String p) ps.setString(i + 1, p);
          else if (param instanceof Integer p) ps.setInt(i + 1, p);
          else if (param == null) ps.setNull(i + 1, NULL);
        }
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }
}
