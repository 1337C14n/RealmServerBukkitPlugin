package leetclan.plugins.Controller;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import packets.Broadcast;
import packets.CanPlayerJoin;
import packets.ChannelMessage;
import packets.ChatMessage;
import packets.ClientLogin;
import packets.Kick;
import packets.Packet;
import packets.PermissionChange;
import packets.Permissions;
import packets.PlayerList;
import packets.PlayerMessage;
import packets.PlayerProxyPacket;
import packets.RedirectPacket;
import realmConnection.RealmConnector;

public class ControllerRealmConnector extends RealmConnector{
  Controller plugin;
  
  public ControllerRealmConnector(String address, int port, Controller plugin) {
    super(address, port);
    this.plugin = plugin;
  }

  @Override
  public void run() {
    while(true){
      Packet packet = connection.read();
      
      if(packet instanceof ChatMessage){
        System.out.println("Recieved: " + packet);
      } else if (packet instanceof ChannelMessage){
        ChannelMessage messagePacket = (ChannelMessage) packet;
        
        /*System.out.println("\nChannel Name: " + messagePacket.getChannelName());
        System.out.println("Message: " + messagePacket.getMessage());
        System.out.println("Sender: " + messagePacket.getSender());
        System.out.println("Prefix: " + messagePacket.getPlayerPrefix());
        System.out.println("Recipients: " + messagePacket.getRecipients() + "\n");*/
        
        for(String player : messagePacket.getRecipients()){
          if(Bukkit.getPlayer(player) != null){
            if(messagePacket.getChannelName().contains("_private_")){         
              //Private Message do not add channelName
                       
              ArrayList<String> playerNames = new ArrayList<String>();
              for(Player bukkitPlayer : Bukkit.getOnlinePlayers()){
                playerNames.add(bukkitPlayer.getName());
              }
              
              if(player.equalsIgnoreCase(messagePacket.getSender())){
                String to = "The Abyss";
                for(String goingTo: messagePacket.getRecipients()){
                  if(!goingTo.equalsIgnoreCase(player)){
                    to = goingTo;
                  }
                }
           
                if(playerNames.contains(player)){
                  Bukkit.getPlayer(player).sendMessage(ChatColor.GRAY + "To " + to +  ChatColor.GREEN + ": " + messagePacket.getMessage());
                }
              } else {
                if(playerNames.contains(player)){
                  Bukkit.getPlayer(player).sendMessage(ChatColor.GRAY + "From " + messagePacket.getSender() + ChatColor.GREEN + ": " + messagePacket.getMessage());
                }
              }
            } else {
              if(messagePacket.isSenderIsModerator()){
                Bukkit.getPlayer(player).sendMessage(appendMessage(messagePacket.getPlayerPrefix(), messagePacket.getSender(), messagePacket.getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1"), messagePacket.getChannelName(), messagePacket.isPrefixEnabled()));
              } else {
                Bukkit.getPlayer(player).sendMessage(appendMessage(messagePacket.getPlayerPrefix(), messagePacket.getSender(), messagePacket.getMessage(), messagePacket.getChannelName(), messagePacket.isPrefixEnabled()));
              }       
            }
          }
        }
      } else if (packet instanceof PlayerMessage) {
        PlayerMessage messagePacket = (PlayerMessage) packet;
        Player bukkitPlayer = Bukkit.getPlayer(messagePacket.getName());
        
        if(bukkitPlayer != null){
          bukkitPlayer.sendMessage(messagePacket.getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1"));
        }
        
      } else if (packet instanceof PlayerList){
        if(((PlayerList) packet).getPlayers() == null){
          ArrayList<String> players = new ArrayList<String>();
          for(Player player : Bukkit.getOnlinePlayers()){
            players.add(player.getName());
          }
          write(new PlayerList(players));
        }
      } else if (packet instanceof Kick){
        String player = ((Kick) packet).getPlayerName();
        if(PlayerLoginMap.INSTANCE.playerSynchro.containsKey(player)){
          synchronized(PlayerLoginMap.INSTANCE.playerSynchro.get(player)){
            PlayerLoginMap.INSTANCE.playerSynchro.get(player).notifyAll();
            PlayerLoginMap.INSTANCE.playerKick.put(player, (Kick)packet);
            
          }
        } else {    
          System.out.println("The player should be kicked");
          @SuppressWarnings("unused")
          BukkitTask task = new KickTask((Kick) packet).runTaskLater(this.plugin, 20);
        }
      } else if (packet instanceof Broadcast){
        for(Player player : Bukkit.getOnlinePlayers()) {
          player.sendMessage(((Broadcast) packet).getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1"));
        }
      } else if (packet instanceof ClientLogin){
        
        /*On login to the master server will send a client login packet with the type type of server.
        if(((ClientLogin) packet).getType() == null){
          
          boolean isMaster = false;
          for(World world : Bukkit.getWorlds()){
            if(world.getName().equalsIgnoreCase("hub")){
              isMaster = true;
            }
          }
          
          if(isMaster){
            write(new ClientLogin("master", "Master Hub"));
            setType("master");
          } else if(Game.getGameType() != null){
            write(new ClientLogin(Game.getGameType(), Game.getName()));
            setType(Game.getGameType());
          } else {
            write(new ClientLogin("node", "Other"));
            setType("node");
          }
        }*/
        
      } else if (packet instanceof Permissions){

      } else if (packet instanceof RedirectPacket){

        PlayerProxyPacket message = new PlayerProxyPacket(((RedirectPacket) packet).getPlayer());  
        
        write(message);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
          dos.writeUTF("Connect");
          dos.writeUTF(((RedirectPacket) packet).getServerName());
          Player p = Bukkit.getPlayer(((RedirectPacket) packet).getPlayer());
          p.sendPluginMessage(plugin, "BungeeCord", baos.toByteArray());
          baos.close();
          dos.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } else if (packet instanceof CanPlayerJoin){
        System.out.println("Lets just let them in the server for now");
        ((CanPlayerJoin) packet).setCanJoin(true);
        write(packet);
      } else if (packet instanceof PermissionChange){
        ArrayList<String> changedPlayers = ((PermissionChange) packet).getPlayers();
        
        for(String player : changedPlayers){
          GetPermissionsTask permissionTask = new GetPermissionsTask(this.plugin, player);
          Thread playerPermThread = new Thread(permissionTask);
          playerPermThread.start();
        }
      }
    }
    
  }
  
  @Override
  public void onReconnect() {
    write(new ClientLogin(Game.getGameType(), Game.getName()));
  }
  
  private String appendMessage(String playerPrefix, String sender, String message, String channelName, boolean prefixEnabled){
    String prefix;
    try{
      prefix = VaultWrapper.INSTANCE.getChat().getPlayerPrefix(Bukkit.getPlayer(sender));
    } catch (NullPointerException e) {
      prefix = null;
    }
    
    if(prefix != null){
      if(prefixEnabled){
        return "<" + ChatColor.AQUA + channelName + ChatColor.WHITE + "> " + prefix.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1") + 
            sender + ChatColor.WHITE + ": " + message;
      } else {
        return  prefix.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1") + sender + ChatColor.WHITE + ": " + message;
      }
    } else if (playerPrefix != null){
      if(prefixEnabled){
        return  "<" + ChatColor.AQUA + channelName + ChatColor.WHITE + "> " + playerPrefix.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1") +
            sender +  ChatColor.WHITE +  ": " + message;
      } else {
        return playerPrefix.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1") + sender + ChatColor.WHITE + ": " + message;
      }
    } else {
      if(prefixEnabled){
        return  "<" + ChatColor.AQUA + channelName + ChatColor.WHITE + "> " +
            sender +  ChatColor.WHITE +  ": " + message;
      } else {
        return  sender + ChatColor.WHITE + ": " + message;
      }
    }

  }
}
