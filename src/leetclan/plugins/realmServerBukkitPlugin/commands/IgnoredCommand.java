package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmServerConnector;

public class IgnoredCommand extends LeetCommand{

  public IgnoredCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean onCommand() {
    RealmServerConnector.write(new CommandMessage(sender.getName(), "ignored"));
    return false;
  }


}
