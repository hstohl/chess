package dataaccess;

import model.AuthData;
import model.GameData;
import model.GameDataMini;

import java.util.ArrayList;
import java.util.Collection;
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

  public GameData getGameI(int id) {
    GameData myGame = null;
    for (GameData game : myGameData) {
      if (Objects.equals(game.gameID(), id)) {
        myGame = game;
      }
    }
    return myGame;
  }

  public void updateGame(GameData updatedGame) {
    GameData myGame = null;
    for (GameData game : myGameData) {
      if (Objects.equals(game.gameID(), updatedGame.gameID())) {
        myGame = game;
      }
    }
    myGameData.remove(myGame);
    myGameData.add(updatedGame);
  }

  public Collection<GameDataMini> listGames() {
    ArrayList<GameDataMini> myGameDataMini = new ArrayList<>();
    for (GameData game : myGameData) {
      myGameDataMini.add(new GameDataMini(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
    }
    return myGameDataMini;
  }

  public void clear() {
    myGameData.clear();
  }
}
