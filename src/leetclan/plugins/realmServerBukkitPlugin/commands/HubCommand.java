package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.PlayerProxyPacket;
import packets.RequestServer;
import realmConnection.RealmConnector;

/**
 * The /hub command sends a player back to the hub
 * 
 * @author x4n4th
 */
public class HubCommand extends LeetCommand{

  public HubCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    boolean isMaster = false;
    for(World world : Bukkit.getWorlds()){
      if(world.getName().equalsIgnoreCase("hub")){
        isMaster = true;
      }
    }
    
    if(!isMaster){
      PlayerProxyPacket message = new PlayerProxyPacket(sender.getName());  
      RealmConnector.write(message);
      
      RequestServer request = new RequestServer(sender.getName(), "hub");
      RealmConnector.write(request);
    }
    
    return false;
  }

}
