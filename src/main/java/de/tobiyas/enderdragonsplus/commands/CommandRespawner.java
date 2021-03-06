package de.tobiyas.enderdragonsplus.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandRespawner implements CommandExecutor {
	
	private EnderdragonsPlus plugin;
	
	public CommandRespawner(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edprespawner").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edprespawner");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be used by a Player.");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0){
			postHelp(player);
			return true;
		}
		
		String cmd = args[0];
		
		if(cmd.equalsIgnoreCase("clear")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.clearRespawners)) return true;
			int amount = plugin.getDragonSpawnerManager().clearAll();
			player.sendMessage(ChatColor.GREEN + "Cleared ALL spawners (" + ChatColor.LIGHT_PURPLE + amount + ChatColor.GREEN + ").");
			return true;
		}
		
		if(cmd.equalsIgnoreCase("info")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.infoRespawners)) return true;
			player.sendMessage(ChatColor.YELLOW + "====Respawner Info====");
			plugin.getDragonSpawnerManager().sendInfoToPlayer(player);			
			return true;
		}
		
		if(cmd.equalsIgnoreCase("debugsigns")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.debugRespawners)) return true;
			int amount = plugin.getDragonSpawnerManager().resetSigns();
			player.sendMessage(ChatColor.GREEN + "Resetted " + ChatColor.LIGHT_PURPLE + amount + ChatColor.GREEN + " Signs.");
			return true;
		}
		
		//porting to respawners
		if(cmd.equalsIgnoreCase("port")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.portRespawner)) return true;
			if(!(args.length == 2 || args.length == 3)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use /edprespawner port <spawnerName> [sub-number]");
				return true;
			}
				
			String respawner = args[1];
			ArrayList<Location> locs = plugin.getDragonSpawnerManager().getLocationsOfRespawner(respawner);
			if(locs == null){
				player.sendMessage(ChatColor.RED + "Respawner: " + ChatColor.LIGHT_PURPLE + respawner + ChatColor.RED + " could not be found.");
				return true;
			}
			
			int number = 1;
			if(args.length == 3){
				try{
					number = Integer.parseInt(args[2]);
					if(number < 1 || number > locs.size())
						throw new NumberFormatException();
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "3rd. Argument must be a number and between 1 and " + locs.size());
					return true;
				}catch(ArrayIndexOutOfBoundsException e){
					number = 1;
				}
			}
			
			player.teleport(locs.get(number - 1));
			player.sendMessage(ChatColor.GREEN + "You have been teleported to Respawner " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.GREEN+
								"[" + ChatColor.LIGHT_PURPLE + number + ChatColor.GREEN + "].");
			return true;
		}
		
		
		//Linking respawners
		if(cmd.equalsIgnoreCase("link")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.createRespawner)) return true;
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /respawner link <respawnername>");
				return true;
			}
			
			String respawnerName = args[1];
			Location placeLocation = player.getLocation().getBlock().getLocation();
			if(plugin.getDragonSpawnerManager().linkSpawner(respawnerName, placeLocation))
				player.sendMessage(ChatColor.GREEN + "Respawner linked successfully.");
			else
				player.sendMessage(ChatColor.RED + "This respawner-Name (" + ChatColor.LIGHT_PURPLE + respawnerName +
									ChatColor.RED + ") could not be found.");
			return true;
		}
		
		//Start of creating Respawner
		if(cmd.equalsIgnoreCase("create")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.createRespawner)) return true;
			
			if(args.length <= 2){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use the command like this: /epdrespawner create <spawnerName> [dragonAgeName] [respawntime] [maxDragons]");
				return true;
			}
			
			String respawnerName = args[1];
			int respawnTime = 60 * 60;
			int maxDragons = 1;
			String dragonAgeName = "Normal";
			
			//checks if a respawner with this name already exists
			List<Location> respawners = plugin.getDragonSpawnerManager().getLocationsOfRespawner(respawnerName);
			if(respawners != null){
				sender.sendMessage(ChatColor.RED + "The name " + ChatColor.LIGHT_PURPLE + respawnerName +
						ChatColor.RED + " is already taken.");
				return true;
			}
			
			if(args.length >= 3){
				try{
					dragonAgeName = args[2];
					if(plugin.getAgeContainerManager().getAgeContainer(dragonAgeName) == null){
						player.sendMessage(ChatColor.RED + "The age name: " + ChatColor.LIGHT_PURPLE
											+ dragonAgeName + ChatColor.RED + " does not exist.");
						return true;
					}
					
				}catch(Exception exp){
					player.sendMessage(ChatColor.RED + "Error during age evaluation.");
					return true;
				}
			}
			
			if(args.length >= 4)
				try{
					respawnTime = Integer.valueOf(args[3]);
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "The respawnTime must be a number (in seconds)!");
					return true;
				}
			
			if(args.length >= 5){
				try{
					maxDragons = Integer.valueOf(args[4]);
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "The maxDragons must be a number!");
					return true;
				}
			}
			
			Location loc = player.getLocation();
			if(plugin.getDragonSpawnerManager().addSpawner(loc, respawnTime, maxDragons, respawnerName, dragonAgeName))
				player.sendMessage(ChatColor.GREEN + "Successfully created a new Dragon-Respawner.");
			else
				player.sendMessage(ChatColor.RED + "Creation failed! Name already exists.");
			
			return true;
		}
		
		postHelp(player);
		return true;
	}
	
	private void postHelp(Player player){
		player.sendMessage(ChatColor.RED + "Command not recognized! Use " + ChatColor.GREEN + "/edprespawner" + ChatColor.YELLOW + 
							" <create,port,info,clear,debugsigns>");
	}

}
