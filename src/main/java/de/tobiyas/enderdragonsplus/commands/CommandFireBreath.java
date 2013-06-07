package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.firebreath.FireBreath;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandFireBreath implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	
	public CommandFireBreath() {
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edpbreath").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edpbreath");
		}	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] arguments) {
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.createEnderDragon))
			return true;
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		
		if(arguments.length < 1){
			sender.sendMessage(ChatColor.RED + "One argument needed! Time to cast.");
			return true;
		}
		
		int time = 100;
		try{
			time = Integer.parseInt(arguments[0]);
		}catch(NumberFormatException exp){
			sender.sendMessage(ChatColor.RED + "The Argument must be a number.");
			return true;
		}
		
		Player player = (Player) sender;
		
		Vector direction = player.getEyeLocation().getDirection();
		final FireBreath testBreath = new FireBreath(player.getLocation(), direction, null);
		
		Runnable breathTicker = new Runnable() {
			
			@Override
			public void run() {
				while(testBreath.tick());
			}
		};
		
		Bukkit.getScheduler().runTaskTimer(plugin, breathTicker, 5, time);
		
		
		sender.sendMessage(ChatColor.GREEN + "On it's way...");
		return true;
	}

}
