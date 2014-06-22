package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class LeetCommand {
  
  CommandSender sender;
  Command command;
  String label;
  String[] args;
  RealmServerBukkitPlugin plugin;

  public LeetCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    this.plugin = plugin;
    this.sender = sender;
    this.command = command;
    this.label = label;
    this.args = args;
  }
  
  public abstract boolean onCommand();
}
