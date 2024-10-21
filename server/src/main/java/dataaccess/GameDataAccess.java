package dataaccess;

import model.AuthData;
import model.GameData;
import model.GameDataMini;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDataAccess {
  public GameData getGame(String gameName);

  public void addGame(GameData newGame);

  public GameData getGameI(int ID);

  public void updateGame(GameData updatedGame);

  public Collection<GameDataMini> listGames();

  public void clear();
}
