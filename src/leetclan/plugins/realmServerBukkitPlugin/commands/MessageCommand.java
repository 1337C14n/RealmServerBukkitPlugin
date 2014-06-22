package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmConnector;

/**
 * Handles the /msg /tell commands
 * 
 * @author x4n4th
 */
public class MessageCommand extends LeetCommand{

  public MessageCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    
    RealmConnector.write(new CommandMessage(sender.getName(), "msg", args));
    
    return true;
  }


}
