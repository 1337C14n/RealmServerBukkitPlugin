package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;
import leetclan.plugins.realmServerBukkitPlugin.PlayerMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import packets.CommandMessage;

/**
 * The /warn command
 * 
 * @author x4n4th
 */
public class WarnCommand extends LeetCommand{

  public WarnCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player p = (Player) sender;
    
    if (args.length != 1) {
      p.sendMessage(ChatColor.RED + "/warn <player>");
      return true;
    } else {
      CommandMessage message = new CommandMessage(sender.getName(), "warn", args[0]);
      PlayerMap.INSTANCE.waitingForReason.put(sender.getName(), message);
      p.sendMessage(ChatColor.RED + "Please enter a reason:");
      return true;
    }
  }


}
