package leetclan.plugins.realmServerBukkitPlugin.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
 
public class SendPlayerMessageTask extends BukkitRunnable {
 
    private final String playerName;
    private final String message;
    
    public SendPlayerMessageTask(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }
 
    public void run() {
      Player bukkitPlayer = Bukkit.getPlayer(playerName);
      
      if(bukkitPlayer != null){
        bukkitPlayer.sendMessage(message);
      }

    }
 
}