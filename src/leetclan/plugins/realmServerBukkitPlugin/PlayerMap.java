package leetclan.plugins.realmServerBukkitPlugin;

import java.util.concurrent.ConcurrentHashMap;

import packets.CommandMessage;

public enum PlayerMap {
  INSTANCE;
  
  public ConcurrentHashMap<String, CommandMessage> waitingForReason = new ConcurrentHashMap<>();
}
