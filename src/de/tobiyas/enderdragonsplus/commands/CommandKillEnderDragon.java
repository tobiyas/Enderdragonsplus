package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandKillEnderDragon implements CommandExecutor {

	private EnderdragonsPlus plugin;
	
	public CommandKillEnderDragon(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("killenderdragons").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /killenderdragons");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "You are no player!");
			return true;
		}
		
		Player player = (Player) sender;
		if(!(plugin.getPermissionManager().checkPermissions(player, PermissionNode.killEnderDragons)))
			return true;
		
		int range = 100;
		
		if(args.length == 1){
			range = Integer.parseInt(args[0]);
			if(range < 0) range = 100;
		}
		
		int killed = plugin.getContainer().killEnderDragons(player.getLocation(), range);
		
		if(killed != 0)
			player.sendMessage(ChatColor.GREEN + "Killed " + killed + " dragon/s.");
		else
			player.sendMessage(ChatColor.RED + "No dragons found in range.");
		return true;
	}

}
