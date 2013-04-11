package de.tobiyas.enderdragonsplus.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
		
		List<Block> sightList = player.getLineOfSight(null, 100);
		Location spawnLocation = player.getLocation();
		
		for(Block block : sightList){
			if(block.getType() != Material.AIR){
				spawnLocation = block.getLocation();
				break;
			}
		}
		
		LivingEntity entity = null;
		if(args.length >= 1){
			entity = DragonAPI.spawnNewEnderdragon(spawnLocation, args[0]);
			
			if(entity == null){
				player.sendMessage(ChatColor.RED + "Could not spawn " + args[0] + " dragon. It does not exist.");
				return true;
			}
		}
		
		if(args.length == 0){
			entity = DragonAPI.spawnNewEnderdragon(spawnLocation);
			if(entity == null){
				player.sendMessage(ChatColor.RED + "Could not spawn dragon. Something gone wrong...");
				return true;
			}
		}
			
		
		
		player.sendMessage(ChatColor.GREEN + "Dragon spawned.");
		return true;
	}
}
