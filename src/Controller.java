/*
 * 1337Clan
 */
package leetclan.plugins.Controller;

import java.util.logging.Logger;

import leetclan.plugins.Controller.commands.ControllerCommandExecutor;

import org.bukkit.plugin.java.JavaPlugin;

public class Controller extends JavaPlugin {
  static final Logger log = Logger.getLogger("Minecraft");
  public boolean enabled = true;
  public boolean commandLogEnabled = false;
  
  @Override
  public void onDisable() {
    Controller.log.info("[1337Clan Controller] Disabled");
  }

  @Override
  public void onEnable() {

    Controller.log.info("[1337Clan Controller] Enabling");
    
    ControllerRealmConnector connectionHandler = new ControllerRealmConnector("localhost", 2000, this);
    Thread connectionThread = new Thread(connectionHandler);
    connectionThread.start();
    
    VaultWrapper.INSTANCE.setupChat();

    new ControllerListener(this);

    ControllerCommandExecutor myExecutor = new ControllerCommandExecutor(this);
    
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    getCommand("ch").setExecutor(myExecutor);
    getCommand("r").setExecutor(myExecutor);
    getCommand("msg").setExecutor(myExecutor);
    getCommand("ban").setExecutor(myExecutor);
    getCommand("unban").setExecutor(myExecutor);
    getCommand("mute").setExecutor(myExecutor);
    getCommand("warn").setExecutor(myExecutor);
    getCommand("hub").setExecutor(myExecutor);
    getCommand("ignore").setExecutor(myExecutor);
    getCommand("join").setExecutor(myExecutor);
    getCommand("ignored").setExecutor(myExecutor);
    getCommand("perm").setExecutor(myExecutor);
    
    Controller.log.info("[1337Clan Controller] Enabled");
  }
}
