package de.tobiyas.enderdragonsplus.commands;

import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class CommandDEBUGGOTO implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandDEBUGGOTO(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getCommand("gethere").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			return true;
		}
		
		Player player = (Player) sender;
		
		Set<UUID> ids = plugin.getContainer().getAllIDs();
		
		int i = 0;
		for(UUID id : ids){
			if(!plugin.getContainer().isLoaded(id)) continue;
			
			plugin.getContainer().getDragonById(id).goToLocation(player.getLocation());
			i++;
		}
		
		
		player.sendMessage(ChatColor.GREEN + "calling " + i + " dragons to you.");
		return true;
	}

}
