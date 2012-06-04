package de.tobiyas.enderdragonsplus.commands;

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
		
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.createRespawner)) return true;
		
		if(args.length == 1 && args[0].equalsIgnoreCase("clear")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.clearRespawners)) return true;
			int amount = plugin.getDragonSpawnerManager().clearAll();
			player.sendMessage(ChatColor.GREEN + "Cleared ALL spawners (" + ChatColor.LIGHT_PURPLE + amount + ChatColor.GREEN + ").");
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("info")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.infoRespawners)) return true;
			player.sendMessage(ChatColor.YELLOW + "====Respawner Info====");
			plugin.getDragonSpawnerManager().sendInfoToPlayer(player);			
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("debugsigns")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.debugRespawners)) return true;
			int amount = plugin.getDragonSpawnerManager().resetSigns();
			player.sendMessage(ChatColor.GREEN + "Resetted " + ChatColor.LIGHT_PURPLE + amount + ChatColor.GREEN + " Signs.");
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("port")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.portRespawner)) return true;
			int respawner;
			try{
				respawner = Integer.valueOf(args[1]);
			}catch(NumberFormatException e){
				player.sendMessage(ChatColor.RED + "The argument must be a number.");
				return true;
			}
			
			Location loc = plugin.getDragonSpawnerManager().getLocationOfRespawner(respawner);
			
			if(loc == null){
				player.sendMessage(ChatColor.RED + "Respawner: " + ChatColor.LIGHT_PURPLE + respawner + ChatColor.RED + " could not be found.");
				return true;
			}
			
			player.teleport(loc);
			player.sendMessage(ChatColor.GREEN + "You have been teleported to Respawner " + ChatColor.LIGHT_PURPLE + args[1]);
			return true;
		}
		
		if(args.length != 2){
			player.sendMessage(ChatColor.RED + "Wrong usage. Use the command like this: /epdrespawner [respawntime] [maxDragons]");
			return true;
		}
		
		int respawnTime;
		int maxDragons;
		
		try{
			respawnTime = Integer.valueOf(args[0]);
			maxDragons = Integer.valueOf(args[1]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED + "The two parametes both must be a number!");
			return true;
		}
		
		Location loc = player.getLocation();
		plugin.getDragonSpawnerManager().addSpawner(loc, respawnTime, maxDragons);
		player.sendMessage(ChatColor.GREEN + "Successfully created a new Dragon-Respawner.");
		return true;
	}

}
