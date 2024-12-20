package dataaccess;

import model.AuthData;
import model.GameData;
import model.GameDataMini;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDataAccess {
  public GameData getGame(String gameName);

  public void addGame(GameData newGame) throws DataAccessException;

  public GameData getGameI(int id);

  public void updateGame(GameData updatedGame) throws DataAccessException;

  public Collection<GameDataMini> listGames() throws DataAccessException;

  public void clear();
}
