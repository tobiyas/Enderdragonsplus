package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandLoadAll implements CommandExecutor{

private EnderdragonsPlus plugin;
	
	public CommandLoadAll(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getCommand("edpload").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.loadAll))
			return true;
		
		String count = plugin.getContainer().loadDragonsInLoadedChunks();
		sender.sendMessage(ChatColor.GREEN + "" + count + " dragons loaded.");
		return true;
	}	
}
