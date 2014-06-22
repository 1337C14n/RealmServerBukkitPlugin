package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.Controller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import packets.CommandMessage;
import realmConnection.RealmConnector;

public class PermCommand extends LeetCommand{

  public PermCommand(Controller plugin, CommandSender sender, Command command, String label, String[] args) {
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
    
    RealmConnector.write(new CommandMessage(sender.getName(), "perm", args));

    return false;
  }


}
