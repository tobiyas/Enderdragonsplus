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
		
		boolean instantRemove = false;
		for(String arg : args){
			if(arg.contains("-r")){
				instantRemove = true;
			}
		}
		
		Player player = (Player) sender;
		if(!(plugin.getPermissionManager().checkPermissions(player, PermissionNode.killEnderDragons)))
			return true;
		
		if(args.length == 0){
			args = new String[]{"100"};
		}
		
		if(args.length > 1){
			player.sendMessage(ChatColor.RED + "The command only supports 1 argument.");
			return true;
		}
		
		int range = 100;
		
		try{
			if(args.length == 1){
				range = Integer.parseInt(args[0]);
				if(range < 0) range = 100;
			}
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.LIGHT_PURPLE + args[0] + " " + ChatColor.RED + "is not a legal number.");
			return true;
		}
		
		int killed = plugin.getContainer().killEnderDragons(player.getLocation(), range, instantRemove);
		
		if(killed != 0)
			player.sendMessage(ChatColor.GREEN + "Killed " + killed + " dragon/s.");
		else
			player.sendMessage(ChatColor.RED + "No dragons found in range.");
		return true;
	}

}
