package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface GameDataAccess {
  public GameData getGame(String gameName);

  public void addGame(GameData newGame);

  public GameData getGameI(int ID);

  public void clear();
}
