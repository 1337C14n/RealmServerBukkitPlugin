package miniGame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import leetclan.plugins.realmServerBukkitPlugin.Game;
import minigame.MiniGameStatus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import packets.ClientLogin;
import packets.MiniGameStatusPacket;
import packets.PlayerProxyPacket;
import realmConnection.RealmConnector;

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
    RealmConnector.write(new ClientLogin(gameType, serverName));
    
  }
  /**
   * OPEN state allows any and all players to join the server
   */
  public void open(){
    RealmConnector.write(new MiniGameStatusPacket(MiniGameStatus.OPEN));
  }
  /**
   * Updates mini game to loading status
   */
  public void loading(){
    RealmConnector.write(new MiniGameStatusPacket(MiniGameStatus.LOADING));
  }
  
  /**
   * Reports to the master server that the game is now accepting players
   * @param numberOfPlayers 
   */
  public void nowAcceptingPlayers(int numberOfPlayer){
    RealmConnector.write(new MiniGameStatusPacket(MiniGameStatus.WAITING_FOR_PLAYERS));
  }
  
  /**
   * Reports that the game is started and is not accepting any more players
   */
  public void gameStarted(){
    RealmConnector.write(new MiniGameStatusPacket(MiniGameStatus.STARTED));
  }
  
  /**
   * Game ended
   */
  public void gameEnded(){
    RealmConnector.write(new MiniGameStatusPacket(MiniGameStatus.ENDED));
  }
  
  public void returnToHub(String player){

    PlayerProxyPacket message = new PlayerProxyPacket(player);  
    
    RealmConnector.write(message);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try {
      dos.writeUTF("Connect");
      dos.writeUTF("Hub");
      Player p = Bukkit.getPlayer(player);
      p.sendPluginMessage(this, "BungeeCord", baos.toByteArray());
      baos.close();
      dos.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
