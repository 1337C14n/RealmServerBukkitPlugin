package leetclan.plugins.realmServerBukkitPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import leetclan.plugins.realmServerBukkitPlugin.permissions.GetPermissionsTask;
import leetclan.plugins.realmServerBukkitPlugin.tasks.KickTask;
import leetclan.plugins.realmServerBukkitPlugin.tasks.SendPlayerMessageTask;

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
import packets.PlayerList;
import packets.PlayerMessage;
import packets.PlayerProxyPacket;
import packets.RedirectPacket;
import realmConnection.RealmServerConnector;

public class BukkitRealmServerConnector extends RealmServerConnector{
  RealmServerBukkitPlugin plugin;
  
  public BukkitRealmServerConnector(String address, int port, RealmServerBukkitPlugin plugin) {
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

        for(String player : messagePacket.getRecipients()){
          if(Bukkit.getPlayer(player) != null){
            if(messagePacket.isSenderIsModerator()){
              new SendPlayerMessageTask(player, appendMessage(messagePacket.getPlayerPrefix(), messagePacket.getSender(), messagePacket.getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1"), messagePacket.getChannelName(), messagePacket.isPrefixEnabled())).runTaskLater(this.plugin, 0);
            } else {
              new SendPlayerMessageTask(player, appendMessage(messagePacket.getPlayerPrefix(), messagePacket.getSender(), messagePacket.getMessage(), messagePacket.getChannelName(), messagePacket.isPrefixEnabled())).runTaskLater(this.plugin, 0);
            }                 
          }
        }
      } else if (packet instanceof PlayerMessage) {
        PlayerMessage messagePacket = (PlayerMessage) packet;
        new SendPlayerMessageTask(messagePacket.getName(), messagePacket.getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1")).runTaskLater(this.plugin, 0);
        
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

          new SendPlayerMessageTask(player.getName(), ((Broadcast) packet).getMessage().replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1")).runTaskLater(this.plugin, 0);
        }
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
