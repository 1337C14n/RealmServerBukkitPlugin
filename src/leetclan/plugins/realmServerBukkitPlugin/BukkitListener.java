/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leetclan.plugins.realmServerBukkitPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import leetclan.plugins.realmServerBukkitPlugin.permissions.GetPermissionsTask;
import leetclan.plugins.realmServerBukkitPlugin.tasks.LoginTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;

import packets.ChatMessage;
import packets.CommandMessage;
import packets.PlayerLogin;
import packets.PlayerProxyPacket;
import packets.RequestServer;
import realmConnection.RealmServerConnector;

/**
 * 
 * @author x4n4th
 */
public class BukkitListener implements Listener {
  RealmServerBukkitPlugin plugin;

  public BukkitListener(RealmServerBukkitPlugin instance) {
    plugin = instance;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onAsycChatEvent(AsyncPlayerChatEvent event) {
    String name = event.getPlayer().getName();
    String message = event.getMessage();

    if (PlayerMap.INSTANCE.waitingForReason.containsKey(name)) {
      CommandMessage commandMessage = PlayerMap.INSTANCE.waitingForReason.get(name);

      String[] args = commandMessage.getArgs();
      String time;

      String playerName = args[0];

      if (args.length > 1) {
        time = args[1];
      } else {
        time = null;
      }

      CommandMessage newMessage = new CommandMessage(commandMessage.getSender(), commandMessage.getCommand(), playerName, time, message);

      RealmServerConnector.write(newMessage);

      PlayerMap.INSTANCE.waitingForReason.remove(name);
    } else {
      if (RealmServerConnector.isConnected()) {
        ChatMessage playerMessage = new ChatMessage(name, VaultWrapper.INSTANCE.getChat().getPlayerPrefix(Bukkit.getPlayer(name)), message);

        if (event.getPlayer().hasPermission("chat.mod")) {
          playerMessage.setPlayerIsMod(true);
        }
        RealmServerConnector.write(playerMessage);
      } else {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
          p.sendMessage(appendMessage(name, message));
        }
      }
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.setJoinMessage(null);

    new LoginTask(event).runTaskLater(this.plugin, 20);

    System.out.println("Server Type: " + Game.getGameType());

    GetPermissionsTask permissionTask = new GetPermissionsTask(this.plugin, event.getPlayer().getName());
    Thread playerPermThread = new Thread(permissionTask);
    playerPermThread.start();

    if (Bukkit.getWorld("hub") != null) {
      Player player = event.getPlayer();
      player.teleport(Bukkit.getWorld("hub").getSpawnLocation());

      player.getInventory().clear();

      ArrayList<String> ls = new ArrayList<String>();
      ls.add("Click to open world menu");
      ItemStack item = createItem(Material.NETHER_STAR, "Port Key", ls);

      ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
      BookMeta bm = (BookMeta) book.getItemMeta();
      bm.setAuthor("1337 Network");
      bm.setTitle("Guide Book");
      ArrayList<String> pages = new ArrayList<String>();
      pages.add("Using Port Keys" + "\nThe port key gives you the ability to teleport to another server!" + "\nLeft click or attack with the key.");
      /*
       * pages.add("Using the chat system" + "/ch <channel>" +
       * "/ch create <channel>" + "/ch delete <channel>" + "/ch <channel> " +
       * "/ch leave or /ch leave <channel>");
       */
      bm.setPages(pages);

      book.setItemMeta(bm);

      if (!player.getInventory().contains(item)) {
        player.getInventory().addItem(item);
        // player.getInventory().addItem(book);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerKick(PlayerKickEvent event) {
    String name = event.getPlayer().getName();

    PlayerLogin player = new PlayerLogin(name, false);

    RealmServerConnector.write(player);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);
    String name = event.getPlayer().getName();

    PlayerLogin player = new PlayerLogin(name, false);

    RealmServerConnector.write(player);
  }
  
  @EventHandler
  public void onplayerPreLogin(AsyncPlayerPreLoginEvent e) {
    String playerName = e.getName();
    
    JSONArray response = getRequest(playerName);
    
    if(this.isPlayerBanned(response) == true){
      e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "You were banned? Please go to 1337clan.com");
    }
  }
  
  /**
   * Request the JSON payload need for a player to log in
   * 
   * @param player players name
   * @return JSON containing name of server and if the player is banned
   */
  private JSONArray getRequest(String player){
    String absoluteURI = "http://localhost/network/player.php?query=server&name=" + player;
    System.out.println("Getting JSON");
    try {
      URLConnection connection = new URL(absoluteURI).openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      connection.connect();
  
      String line;
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      while((line = reader.readLine()) != null) {
       builder.append(line);
      }
      System.out.println("Recieved JSON: " + builder.toString());
      return new JSONArray(builder.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  private boolean isPlayerBanned(JSONArray json){
    if(json != null){
      return ((String) json.getJSONObject(0).get("banned")).equals("0") ? false : true;
    }
      
    return false;
  }
  @EventHandler
  public void onplayerinteract(PlayerInteractEvent e) {
    // We only want this to happen on the hub or we will be deleting peoples
    // inventories
    if (Bukkit.getWorld("hub") == null) {
      return;
    }
      Player player = e.getPlayer();
    
    try{
      if (e.hasBlock() == true || e.hasItem() == true) {
        if (e.getItem().getType() == Material.NETHER_STAR) {
          player.openInventory(getHubInventory());
        }
      }
    }catch (NullPointerException ex){
      //This should not happen.
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {

    HumanEntity entity = event.getWhoClicked();

    if (entity instanceof Player) {

      Player player = (Player) entity;
      ItemStack clicked = event.getCurrentItem();
      Inventory inventory = event.getInventory();
      if (inventory.getName().equals("Server Item Menu")) {

        if (clicked.getType() == Material.OBSIDIAN) {
          event.setCancelled(true);
          player.closeInventory();

          PlayerProxyPacket message = new PlayerProxyPacket(player.getName());
          RealmServerConnector.write(message);

          RequestServer request = new RequestServer(player.getName(), "creative");
          RealmServerConnector.write(request);
        } else if (clicked.getType() == Material.DIAMOND_SWORD) {
          event.setCancelled(true);
          player.closeInventory();

          CommandMessage message = new CommandMessage(player.getName(), "join", "sg");
          RealmServerConnector.write(message);
        } else if (clicked.getType() == Material.DIAMOND_PICKAXE) {
          event.setCancelled(true);
          player.closeInventory();

          PlayerProxyPacket message = new PlayerProxyPacket(player.getName());
          RealmServerConnector.write(message);

          RequestServer request = new RequestServer(player.getName(), "survival");
          RealmServerConnector.write(request);
        } else if (clicked.getType() == Material.DIAMOND_ORE) {
          event.setCancelled(true);
          player.closeInventory();

          PlayerProxyPacket message = new PlayerProxyPacket(player.getName());
          RealmServerConnector.write(message);

          RequestServer request = new RequestServer(player.getName(), "SkyGrid");
          RealmServerConnector.write(request);
        } else if (clicked.getType() == Material.FIREBALL) {
          event.setCancelled(true);
          player.closeInventory();

          PlayerProxyPacket message = new PlayerProxyPacket(player.getName());
          RealmServerConnector.write(message);

          RequestServer request = new RequestServer(player.getName(), "Factions");
          RealmServerConnector.write(request);
        }
      }
    }
  }

  private Inventory getHubInventory() {
    Inventory inventory = Bukkit.createInventory(null, 9, "Server Item Menu");
    ArrayList<String> ls = new ArrayList<String>();

    ItemStack item;

    ls.add("This item takes you to Creative");
    item = createItem(Material.OBSIDIAN, "creative", ls);
    inventory.addItem(item);

    ls.clear();
    ls.add("This item takes you to survival");
    item = createItem(Material.DIAMOND_PICKAXE, "survival", ls);
    inventory.addItem(item);

    ls.clear();
    ls.add("Join the Survival Games");
    item = createItem(Material.DIAMOND_SWORD, "survivalGames", ls);
    inventory.addItem(item);

    ls.clear();
    ls.add("Join the SkyGrid Server");
    item = createItem(Material.DIAMOND_ORE, "SkyGrid", ls);
    inventory.addItem(item);

    ls.clear();
    ls.add("Factions!");
    item = createItem(Material.FIREBALL, "Factions", ls);
    inventory.addItem(item);

    return inventory;
  }

  private String appendMessage(String sender, String message) {
    return VaultWrapper.INSTANCE.getChat().getPlayerPrefix(Bukkit.getPlayer(sender)).replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1") + ChatColor.WHITE + sender + ": " + message;
  }

  private ItemStack createItem(Material item, String name, List<String> Lore) {
    ItemStack stack = new ItemStack(item);
    ItemMeta im = stack.getItemMeta();
    im.setDisplayName(name);
    im.setLore(Lore);
    stack.setItemMeta(im);
    return stack;

  }
}
