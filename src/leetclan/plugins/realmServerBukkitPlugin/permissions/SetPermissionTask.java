package leetclan.plugins.realmServerBukkitPlugin.permissions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class SetPermissionTask extends BukkitRunnable{

  String prefix, playerName;
  List<String> nodes;
  Plugin plugin;
  
  public SetPermissionTask(Plugin plugin, String playerName, String prefix, List<String> nodes) {
    this.plugin = plugin;
    this.playerName = playerName;
    this.nodes = nodes;
    this.prefix = prefix;
  }

  @Override
  public void run() {
    PermissionsEx.getUser(playerName).setPermissions(nodes);
    
    PermissionsEx.getUser(playerName).setOption("prefix", prefix);
    
    for(World world : Bukkit.getWorlds()){
      PermissionsEx.getUser(playerName).setPrefix(prefix, world.getName());
    }
    
  }

}
