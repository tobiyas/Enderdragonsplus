package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandGoHome implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandGoHome(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edpgohome").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edpgohome");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.commandBack))
			return true;
		
		int i = plugin.getContainer().sendAllDragonsHome();
		sender.sendMessage(ChatColor.GREEN + "All Dragons (" + i + ") were sent to their homes.");
		return true;
	}

}
