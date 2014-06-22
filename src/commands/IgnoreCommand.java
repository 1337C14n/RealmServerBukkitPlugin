package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.Controller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmConnector;

public class IgnoreCommand extends LeetCommand{

  public IgnoreCommand(Controller plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    if(args.length == 1){
      CommandMessage message = new CommandMessage(sender.getName(), "ignore", args[0]);
      RealmConnector.write(message);
    } else {
      return false;
    }
    
    return true;
  }


}
