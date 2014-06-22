package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmServerConnector;

/**
 * The Respond Command /r allows a player to respond to another players private messages
 * 
 * @author x4n4th
 */
public class RespondCommand extends LeetCommand{

  public RespondCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    // trololol ternary
    // /r to open a respond channel
    // else /r <message> to  respond without opening a channel
    RealmServerConnector.write((args.length == 0) ?  new CommandMessage(sender.getName(), "r") : new CommandMessage(sender.getName(), "r", args));
    
    return true;
  }


}
