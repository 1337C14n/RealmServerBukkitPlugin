package leetclan.plugins.realmServerBukkitPlugin.tasks;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import packets.PlayerLogin;
import realmConnection.RealmServerConnector;
 
public class LoginTask extends BukkitRunnable {
 
    private final PlayerJoinEvent event;
    
    public LoginTask(PlayerJoinEvent event) {
        this.event = event;
    }
 
    public void run() {
      String name = event.getPlayer().getName();  
      
      if(name != null){     
        PlayerLogin player = new PlayerLogin(name, true);
        
        RealmServerConnector.write(player);
      } else {
        System.out.println("Player Logged out before we could log them in");
      }

    }
 
}