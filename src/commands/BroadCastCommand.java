package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.Controller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import packets.Broadcast;
import realmConnection.RealmConnector;

public class BroadCastCommand extends LeetCommand{

  public BroadCastCommand(Controller plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player p = (Player) sender;
    
    if(!p.hasPermission("controller.mod")){
      return true;
    }
     
    if(args.length != 1){
      p.sendMessage("/b <message>");
      return true;
    }
    RealmConnector.write(new Broadcast(args[0]));
    return true;
  }


}
