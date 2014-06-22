package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmConnector;

public class IgnoredCommand extends LeetCommand{

  public IgnoredCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean onCommand() {
    RealmConnector.write(new CommandMessage(sender.getName(), "ignored"));
    return false;
  }


}
