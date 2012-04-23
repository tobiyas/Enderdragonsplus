package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandReloadConfig implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandReloadConfig(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edpreload").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edpreload");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.reloadConfig))
			return true;
		
		plugin.interactConfig().reload();
		sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
		return true;
	}

}
