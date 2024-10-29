package dataaccess;

import model.GameData;
import model.GameDataMini;

import java.util.ArrayList;
import java.util.Collection;

public class GameDatabaseAccess implements GameDataAccess {
  public GameData getGame(String gameName) {

    return new GameData(0, null, null, null, null);
  }

  public void addGame(GameData newGame) {

  }

  public GameData getGameI(int id) {

    return new GameData(0, null, null, null, null);
  }

  public void updateGame(GameData updatedGame) {

  }

  public Collection<GameDataMini> listGames() {

    return new ArrayList<>();
  }

  public void clear() {

  }
}
