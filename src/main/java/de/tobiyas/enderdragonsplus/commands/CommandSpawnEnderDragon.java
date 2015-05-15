package de.tobiyas.enderdragonsplus.commands;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;
import de.tobiyas.util.autocomplete.AutoCompleteUtils;


public class CommandSpawnEnderDragon implements CommandExecutor, TabCompleter {

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
		
		@SuppressWarnings("deprecation")
		List<Block> sightList = player.getLineOfSight((HashSet<Byte>)null, 100);
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

				Set<String> correctAges = plugin.getAgeContainerManager().getAllAgeNames();
				String ageNames = "";
				for(String correctAge : correctAges){
					ageNames += " " + ChatColor.AQUA + correctAge + ChatColor.RED + ",";
				}
				
				if(correctAges.size() > 0){
					ageNames = ageNames.substring(0, ageNames.length() - 3);
				}
				
				player.sendMessage(ChatColor.RED + "Valid ages are:" + ageNames);
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

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		
		Set<String> ages = plugin.getAgeContainerManager().getAllAgeNames();
		if(args.length == 0) return new LinkedList<String>(ages);
		if(args.length == 1) return AutoCompleteUtils.getAllNamesWith(ages, args[0]);
		
		return new LinkedList<String>();
	}
	
	
}
