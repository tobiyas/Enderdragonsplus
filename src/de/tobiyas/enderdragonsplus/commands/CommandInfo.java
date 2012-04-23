package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandInfo implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandInfo(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getCommand("edpinfo").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.info))
			return true;
		
		sender.sendMessage(ChatColor.GREEN + "registered: " + plugin.getContainer().count() + " loaded: " + plugin.getContainer().loaded());
		return true;
	}

}
