package leetclan.plugins.realmServerBukkitPlugin.permissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import leetclan.plugins.realmServerBukkitPlugin.Game;

import org.bukkit.plugin.Plugin;
import org.json.JSONArray;

public class GetPermissionsTask implements Runnable{

  String playerName;
  Plugin plugin;
  
  public GetPermissionsTask(Plugin plugin, String playerName){
   this.plugin = plugin;
   this.playerName = playerName; 
  }
  
  @Override
  public void run() {
    JSONArray jsonData = getPermissionData(playerName, Game.getName());
    System.out.println("getServer test: " + jsonData.toString());
    
    List<String> permissionNodes = getPermNodes(jsonData);
  
    String prefix = getPrefix(jsonData);
    
    new SetPermissionTask(plugin, this.playerName, prefix, permissionNodes).runTaskLater(this.plugin, 20);
  }
  /**
   * Players permission nodes
   * 
   * @param json
   * @return permission nodes
   */
  private List<String> getPermNodes(JSONArray json){
    List<String> nodes = new ArrayList<String>();
    //return json != null ? (String) json.getJSONObject(1).get("name") : "hub";'
    
    if(json != null){
      for(int i = 1; i < json.length(); i++){
        try{
          nodes.add((String)json.getJSONObject(i).get("node"));
        } catch (Exception e){
          e.printStackTrace();
        }
      }
    }
    
    return nodes;
  }
  
  private String getPrefix(JSONArray json){
    return json != null ? (String) json.getJSONObject(0).get("prefix") : "";
  }
  
  /**
   * Request the JSON payload for all permission data
   * 
   * @param player players name
   * @return JSON containing name of server and if the player is banned
   */
  private JSONArray getPermissionData(String player, String server){
    String absoluteURI = "http://dev.1337clan.com/network/permission.php?name=" + player + "&server=" + server;
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
}
