package leetclan.plugins.realmServerBukkitPlugin.commands;

import leetclan.plugins.realmServerBukkitPlugin.RealmServerBukkitPlugin;
import leetclan.plugins.realmServerBukkitPlugin.PlayerMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import packets.CommandMessage;

/**
 * Handles /mute command
 * @author x4n4th
 *
 */
public class MuteCommand extends LeetCommand{

  public MuteCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player p = (Player) sender;
    
    if (args.length < 1) {
      p.sendMessage(ChatColor.RED + "/mute <player>");
      return true;
    } else if (args.length >= 2) {
      // At this point I am going to assume that the args 2 through
      // args.length - 1 are time params
      int time = 0;

      for (int i = 1; i < args.length; i++) {
        String timeParam = args[i].toLowerCase();
        String type = null;
        String tempNumber = "";

        for (char chars : timeParam.toCharArray()) {
          if (Character.isDigit(chars)) {
            tempNumber += chars;
          } else if (chars == 'd') {
            type = "d";
          } else if (chars == 'h') {
            type = "h";
          } else if (chars == 'm') {
            type = "m";
          } else if (chars == 's') {
            type = "s";
          }
        }
        if (type == null || tempNumber == "") {
          p.sendMessage(ChatColor.RED + "/mute <player> 1d 1h 1m 1s");
          return true;
        }

        int number = Integer.parseInt(tempNumber);
        if (number > 60) {
          number = 60;
        }

        if (type == "d") {
          time += number * 86400;
        } else if (type == "h") {
          time += number * 3600;
        } else if (type == "m") {
          time += number * 60;
        } else if (type == "s") {
          time += number;
        }
      }
      String secondArg = args[0];
      System.out.println("Player to be muted: " + secondArg);
      CommandMessage message = new CommandMessage(sender.getName(), "mute", args[0], Integer.toString(time));
      
      PlayerMap.INSTANCE.waitingForReason.put(sender.getName(), message);
      p.sendMessage(ChatColor.RED + "Please enter a reason:");
      return true;
    }
    String secondArg = args[0];
    System.out.println("Player to be muted: " + secondArg);
    CommandMessage message = new CommandMessage(sender.getName(), "mute", args[0], null);
    
    PlayerMap.INSTANCE.waitingForReason.put(sender.getName(), message);
    p.sendMessage(ChatColor.RED + "Please enter a reason:");
    
    return true;
  }


}
