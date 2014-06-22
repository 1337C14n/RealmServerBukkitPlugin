package leetclan.plugins.Controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.chat.Chat;

public enum VaultWrapper {
	INSTANCE;
	
    public Permission perms = null;
    public Chat chat = null;
	
    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

	public Permission getPerms() {
		return perms;
	}

	public Chat getChat() {
		return chat;
	}
}
