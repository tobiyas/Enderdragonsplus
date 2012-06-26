package de.tobiyas.enderdragonsplus.commands;

import java.util.ArrayList;

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
		
		if(cmd.equalsIgnoreCase("port")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.portRespawner)) return true;
			if(!(args.length == 2 || args.length == 3)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use /edprespawner port <spawnerName> [sub-number]");
				return true;
			}
				
				
			String respawner = args[1];
			
			ArrayList<Location> locs = plugin.getDragonSpawnerManager().getLocationOfRespawner(respawner);
			
			if(locs == null){
				player.sendMessage(ChatColor.RED + "Respawner: " + ChatColor.LIGHT_PURPLE + respawner + ChatColor.RED + " could not be found.");
				return true;
			}
			
			int number = 0;
			if(args.length == 3){
				try{
					number = Integer.parseInt(args[2]);
					if(number < 1 || number > locs.size())
						throw new NumberFormatException();
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "3rd. Argument must be a number and between 1 and " + locs.size());
					return true;
				}
			}
			
			player.teleport(locs.get(number - 1));
			player.sendMessage(ChatColor.GREEN + "You have been teleported to Respawner " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.GREEN+
								"[" + ChatColor.LIGHT_PURPLE + number + ChatColor.GREEN + "].");
			return true;
		}
		
		
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
			
			if(args.length == 0){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use the command like this: /epdrespawner <spawnerName> [respawntime] [maxDragons]");
				return true;
			}
			
			String respawnerName = args[1];
			int respawnTime = 60 * 60;
			int maxDragons = 1;
			
			
			if(args.length >= 3)
				try{
					respawnTime = Integer.valueOf(args[2]);
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "The respawnTime must be a number (in seconds)!");
					return true;
				}
			
			if(args.length == 4){
				try{
					maxDragons = Integer.valueOf(args[3]);
				}catch(NumberFormatException e){
					player.sendMessage(ChatColor.RED + "The maxDragons must be a number!");
					return true;
				}
			}
			
			Location loc = player.getLocation();
			if(plugin.getDragonSpawnerManager().addSpawner(loc, respawnTime, maxDragons, respawnerName))
				player.sendMessage(ChatColor.GREEN + "Successfully created a new Dragon-Respawner.");
			else
				player.sendMessage(ChatColor.RED + "Creation failed! Name already exists.");
			
			return true;
		}
		
		postHelp(player);
		return true;
	}
	
	private void postHelp(Player player){
		player.sendMessage(ChatColor.RED + "Command not recognized! Use /edprespawner <create,port,info,clear,debugsigns>");
	}

}
