package de.tobiyas.enderdragonsplus.commands;

import net.minecraft.server.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;
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
		
		spawnCraftEnderDragonBukkit(player);
		
		player.sendMessage(ChatColor.GREEN + "Dragon spawned.");
		return true;
	}
	
	private LimitedEnderDragon spawnCraftEnderDragonBukkit(Player player){
		Location location = player.getLocation();
		
		World world = ((CraftWorld)player.getWorld()).getHandle();
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world);
		dragon.spawn(false);
		dragon.setHealth(plugin.interactConfig().getconfig_dragonHealth());
		return dragon;
	}

}
