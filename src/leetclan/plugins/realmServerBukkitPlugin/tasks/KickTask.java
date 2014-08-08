package leetclan.plugins.realmServerBukkitPlugin.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import packets.Kick;
 
public class KickTask extends BukkitRunnable {
 
    Kick packet;
 
    public KickTask(Kick packet) {
        this.packet = packet;
    }
 
    public void run() {
      Player player = Bukkit.getServer().getPlayer(((Kick) packet).getPlayerName());
      if(player != null){
        player.kickPlayer(((Kick) packet).getMessage());
      }
    }
 
}