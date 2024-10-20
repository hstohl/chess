package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class GameMemoryDataAccess implements GameDataAccess {
  private ArrayList<GameData> myGameData = new ArrayList<>();

  public GameData getGame(String gameName) {
    GameData newGame = null;
    for (GameData game : myGameData) {
      if (Objects.equals(game.gameName(), gameName)) {
        newGame = game;
      }
    }
    return newGame;
  }

  public void addGame(GameData newGame) {
    myGameData.add(newGame);
  }

  public GameData getGameI(int ID) {
    GameData myGame = null;
    for (GameData game : myGameData) {
      if (Objects.equals(game.gameID(), ID)) {
        myGame = game;
      }
    }
    return myGame;
  }

  public void clear() {
    myGameData.clear();
  }
}
