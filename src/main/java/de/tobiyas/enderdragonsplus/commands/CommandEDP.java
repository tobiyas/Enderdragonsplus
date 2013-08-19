package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandEDP implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandEDP() {
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("enderdragonsplus").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /enderdragonsplus");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		int page = 1;
		
		if(args.length > 0){
			try{
				page = Integer.parseInt(args[0]);
			}catch (Exception exp){}
		}
		
		sender.sendMessage(ChatColor.RED + "({-_-}) ENDERDRAGONS PLUS ({-_-})");
		sender.sendMessage(ChatColor.RED + "-------------" + ChatColor.YELLOW + "HELP" 
							+ ChatColor.RED + "----------------");
		
		sender.sendMessage(ChatColor.YELLOW + "Here are all commands listed you can " 
							+ ChatColor.BLUE + "USE" + ChatColor.YELLOW + ":");
		sender.sendMessage(ChatColor.YELLOW + "-[PAGE: " + ChatColor.RED + page + ChatColor.YELLOW + "]-");

		boolean usedAny = false;
		
		switch(page){
			case 1: usedAny = postPage1(sender); break;
			
			case 2: usedAny = postPage2(sender); break;
			
			default: postPageOverview(sender, page); usedAny = true; break;	
		}
		
		if(!usedAny){
			sender.sendMessage(ChatColor.RED + "You have no Permissions to use any Command on this page.");
		}

		return true;
	}
	
	
	
	private boolean postPage1(CommandSender sender){
		boolean usedAny = false;
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "--" + ChatColor.YELLOW + "Enderdragons " + 
				ChatColor.RED + "General" +
				ChatColor.LIGHT_PURPLE + "--");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.commandBack)){
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edpgohome" + ChatColor.YELLOW + ": sends all Enderdragons back home.");
		usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.createEnderDragon)){
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sedp" + ChatColor.YELLOW 
				+ ": spawns an Enderdragon at the location you are looking.");
		usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.info)){
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edpinfo" + ChatColor.YELLOW
				+ ": gives an info how many Dragons are loaded.");
		usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.killEnderDragons)){
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/kedp [range]" + ChatColor.YELLOW
				+ ": kills all Enderdragons in the given range (0 = all).");
		usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.reloadConfig)){
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edpreload" + ChatColor.YELLOW
				+ ": reloads the Config of the plugin.");
		usedAny = true;
		}
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edp [PageNr]" + ChatColor.YELLOW 
			+ ": shows the help.");
		
		return usedAny;
	}
	
	private boolean postPage2(CommandSender sender){
		boolean usedAny = false;
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "--" + ChatColor.YELLOW + "Enderdragons " + 
				ChatColor.RED + "Spawners" +
				ChatColor.LIGHT_PURPLE + "--");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.createRespawner)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/epdrespawner create <spawnerName> [dragonAgeName] [respawntime] [maxDragons]" +
					ChatColor.YELLOW + ": creates an Enderdragon spawner sign.");
			usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.createRespawner)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/respawner link <respawnername>" +
					ChatColor.YELLOW + ": links the sign to another Respawner.");
			usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.debugRespawners)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edprespawner debugsigns" + ChatColor.YELLOW +
					": recreates all broken signs");
			usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.infoRespawners)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edprespawner info" + ChatColor.YELLOW +
					": gives infos to all available Respawners");
			usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.portRespawner)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edprespawner port <spawnerName> [sub-number]" + ChatColor.YELLOW +
					": ports to a specific sign");
			usedAny = true;
		}
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.clearRespawners)){
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/edprespawner clear" + ChatColor.YELLOW +
					": clears ALL spawn Signs from ALL maps");
			usedAny = true;
		}
		
		return usedAny;
	}
	
	private void postPageOverview(CommandSender sender, int page){
		sender.sendMessage(ChatColor.RED + "Page : " + page + " not found.");
		sender.sendMessage(ChatColor.RED + "Page 1: " + ChatColor.LIGHT_PURPLE + "General");
		sender.sendMessage(ChatColor.RED + "Page 2: " + ChatColor.LIGHT_PURPLE + "Spawner");

		sender.sendMessage(ChatColor.YELLOW + "To see another page, use: " + ChatColor.LIGHT_PURPLE + "/edp [pagenumber]");
	}
}
