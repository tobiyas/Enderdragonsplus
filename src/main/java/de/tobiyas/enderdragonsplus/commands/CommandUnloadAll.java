package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandUnloadAll implements CommandExecutor{

	private EnderdragonsPlus plugin;
	
	public CommandUnloadAll(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getCommand("edpunload").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.unloadAll))
			return true;
		
		//int count = plugin.getContainer().saveAllLoaded();
		sender.sendMessage(ChatColor.RED + "This command does not exist any more.");//ChatColor.GREEN + "" + count + " dragons unloaded!");
		return true;
	}

}
