package leetclan.plugins.Controller.commands;

import leetclan.plugins.Controller.RealmServerBukkitPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import packets.CommandMessage;
import realmConnection.RealmConnector;

/**
 * Handles all /ch commands
 * @author x4n4th
 *
 */
public class ChannelCommand extends LeetCommand{

  public ChannelCommand(RealmServerBukkitPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
    super(plugin, sender, command, label, args);
  }

  @Override
  public boolean onCommand() {
    
    //This command will only be usable by players
    if (!(sender instanceof Player)) {
      return true;
    }
    
    Player p = (Player) sender;
    
    //Command requires at least one argument from the player
    //TODO Return possible commands
    if (args.length == 0) {
      p.sendMessage(ChatColor.RED + "What did you want to do?");
      return true;
    }
    
    //First can be a secondary command or a channel name
    String firstArg = args[0];
    
    switch(firstArg.toUpperCase()){
    
      case "LIST": //Commands lists all channels to the player 
        RealmConnector.write(new CommandMessage(sender.getName(), "ch", "list"));
        break;
        
      case "CREATE": //Create a new Channel
        if (args.length <= 1) {
          p.sendMessage(ChatColor.RED + "/ch create <channel> <optional password>");
          return true;
          
        } else if(args.length == 2){ // /ch create <channel>       
          RealmConnector.write(new CommandMessage(sender.getName(), "ch", "create", args[1]));
          
        } else { // /ch create <channel> <password>
          
          String channelName = args[1];
          String password = args[2];
          
          //Limiting channel length to 10 characters
          if(channelName.length() >  10){
            sender.sendMessage("&7[&4*&7] &7Channel name too long");
            return true;
          }
          //Limiting channel password length to 8 characters
          if(password.length() > 8) {
            sender.sendMessage("&7[&4*&7] &7Password is too long");
            return true;
          }
          
          RealmConnector.write(new CommandMessage(sender.getName(), "ch", "create", channelName, password));
        }
        break;
        
      case "DELETE": //delete a channel
        if (args.length <= 1) {
          p.sendMessage(ChatColor.RED + "/ch delete <channel>");
          return true;
        }
        
        String channelName = args[1];     
        RealmConnector.write(new CommandMessage(sender.getName(), "ch", "delete", channelName));
        break;
        
      case "ACTIVE":
        RealmConnector.write(new CommandMessage(sender.getName(), "ch", "active"));
        break;
        
      case "LEAVE":
        if(args.length > 1){
          RealmConnector.write(new CommandMessage(sender.getName(), "ch", "leave", args[1]));
          return true;
        }
        
        RealmConnector.write(new CommandMessage(sender.getName(), "ch", "leave"));
        break;
      
      case "JOIN":
        if(args.length == 3) { // /ch join <channel> <password>
          RealmConnector.write(new CommandMessage(sender.getName(), "ch", "join", args[1], args[2]));
        } else if(args.length == 2){
          RealmConnector.write(new CommandMessage(sender.getName(), "ch", "join", args[1]));
        } else {
          p.sendMessage("&7[&4*&7] &7/ch join <channel>");
          p.sendMessage("&7[&4*&7] &7/ch join <channel> <password>");
        }
        break;
        
      case "RELOAD":
        if(p.hasPermission("chat.admin")){
          //TODO Re-implement
        }
        break;
        
      default:
        RealmConnector.write(new CommandMessage(sender.getName(), "ch", args[0]));  
        break;
    }

    return true;
  }

}
