package leetclan.plugins.realmServerBukkitPlugin;

public class Game {
  private static String gameType;
  private static String name;

  public static String getGameType() {
    return gameType;
  }

  public static void setGameType(String gameType) {
    Game.gameType = gameType;
  }

  public static String getName() {
    return name;
  }

  public static void setName(String name) {
    Game.name = name;
  }
}
