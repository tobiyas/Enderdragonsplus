package de.tobiyas.enderdragonsplus.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class Listener_Sign implements Listener {

	private EnderdragonsPlus plugin;
	
	public Listener_Sign(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Location blockLocation = event.getBlock().getLocation();
		Player player = event.getPlayer();
		
		if(block.getType() != Material.SIGN_POST) return;
		if(!plugin.getDragonSpawnerManager().hasRespawnerOn(blockLocation)) return;
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.removeRespawner)){
			event.setCancelled(true);
			return;
		}

		plugin.getDragonSpawnerManager().deleteSpawner(blockLocation);
		player.sendMessage(ChatColor.GREEN + "DragonRespawner has been removed.");
		block.setType(Material.AIR);
		event.setCancelled(true);
	}
}
