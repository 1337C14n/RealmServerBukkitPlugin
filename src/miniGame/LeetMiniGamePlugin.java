package miniGame;

import leetclan.plugins.realmServerBukkitPlugin.Game;
import minigame.MiniGameStatus;

import org.bukkit.plugin.java.JavaPlugin;

import packets.ClientLogin;
import packets.MiniGameStatusPacket;
import realmConnection.RealmServerConnector;

public abstract class LeetMiniGamePlugin extends JavaPlugin {
  
  String gameType;
  String serverName;
  
  /**
   * Registers game type with master server
   * @param gameType
   */
  public void registerGame(String gameType, String serverName){
    this.gameType = gameType;
    this.serverName = serverName;
    Game.setGameType(gameType);
    Game.setName(serverName);
  }
  
  public void connect(){
    System.out.println("[Controller] Registered game: " + gameType + " On: " + serverName);
    //Send login
    RealmServerConnector.write(new ClientLogin(gameType, serverName));
    
  }
  /**
   * OPEN state allows any and all players to join the server
   */
  public void open(){
    RealmServerConnector.write(new MiniGameStatusPacket(MiniGameStatus.OPEN));
  }
  /**
   * Updates mini game to loading status
   */
  public void loading(){
    RealmServerConnector.write(new MiniGameStatusPacket(MiniGameStatus.LOADING));
  }
  
  /**
   * Reports to the master server that the game is now accepting players
   * @param numberOfPlayers 
   */
  public void nowAcceptingPlayers(int numberOfPlayer){
    RealmServerConnector.write(new MiniGameStatusPacket(MiniGameStatus.WAITING_FOR_PLAYERS));
  }
  
  /**
   * Reports that the game is started and is not accepting any more players
   */
  public void gameStarted(){
    RealmServerConnector.write(new MiniGameStatusPacket(MiniGameStatus.STARTED));
  }
  
  /**
   * Game ended
   */
  public void gameEnded(){
    RealmServerConnector.write(new MiniGameStatusPacket(MiniGameStatus.ENDED));
  }
  
}
