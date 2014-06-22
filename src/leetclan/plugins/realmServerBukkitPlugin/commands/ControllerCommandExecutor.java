/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leetclan.plugins.Controller.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Logger;

import leetclan.plugins.Controller.RealmServerBukkitPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


/**
 * 
 * @author x4n4th
 */
public class ControllerCommandExecutor implements CommandExecutor {
  static final Logger log = Logger.getLogger("Minecraft");
  RealmServerBukkitPlugin plugin;
  
  @SuppressWarnings("serial")
  public static final HashMap<String, Class<? extends LeetCommand>> commandTypes = new HashMap<String, Class<? extends LeetCommand>>() {
    {
      put("ban", BanCommand.class);
      put("b", BanCommand.class);
      put("ch", ChannelCommand.class);
      put("hub", HubCommand.class);
      put("ignore", IgnoreCommand.class);
      put("ignored", IgnoredCommand.class);
      put("join", JoinCommand.class);
      put("msg", MessageCommand.class);
      put("mute", MuteCommand.class);
      put("r", RespondCommand.class);
      put("unban", UnBanCommand.class);
      put("warn", WarnCommand.class);
      put("perm", PermCommand.class);
    }
  };

  public ControllerCommandExecutor(RealmServerBukkitPlugin instance) {
    plugin = instance;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    String commandName = command.getName().toLowerCase();
    
    try {
      classForType(commandName).getConstructor(RealmServerBukkitPlugin.class, CommandSender.class, Command.class , String.class , String[].class).newInstance(plugin, sender, command, label, args).onCommand();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      log.warning("Controller recived an invalid command!");
      e.printStackTrace();
    }
    
    return true;
  }
  
  public static Class<? extends LeetCommand> classForType(String type) {
    return (Class<? extends LeetCommand>) (commandTypes.containsKey(type) ? commandTypes.get(type) : null);
  }
}
