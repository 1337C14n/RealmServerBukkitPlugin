package leetclan.plugins.realmServerBukkitPlugin;

import java.util.concurrent.ConcurrentHashMap;

import packets.Kick;

public enum PlayerLoginMap {
  INSTANCE;
  
  ConcurrentHashMap<String, Object> playerSynchro = new ConcurrentHashMap<String, Object>();
  ConcurrentHashMap<String, Kick> playerKick = new ConcurrentHashMap<String, Kick>();

}
