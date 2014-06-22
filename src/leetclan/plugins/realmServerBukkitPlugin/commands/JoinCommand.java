package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import packets.CommandMessage;
import realmConnection.RealmConnector;

public class JoinCommand extends LeetCommand{


  public JoinCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    RealmConnector.write(new CommandMessage(sender.getName(), "join", args));
    return true;
  }

}
