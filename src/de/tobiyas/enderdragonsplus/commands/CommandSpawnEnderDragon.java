package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;


public class CommandSpawnEnderDragon implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandSpawnEnderDragon(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("spawnenderdragon").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /spawnenderdragon");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!(plugin.getPermissionManager().checkPermissions(player, PermissionNode.createEnderDragon)))
			return true;
		
		LivingEntity entity = DragonAPI.spawnNewEnderdragon(player.getLocation());
		
		if(entity == null){
			player.sendMessage(ChatColor.RED + "ENTITY = NULL!!!");
			return true;
		}
		
		player.sendMessage(ChatColor.GREEN + "Dragon spawned.");
		return true;
	}
}
