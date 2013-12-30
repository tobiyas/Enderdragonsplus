package de.tobiyas.enderdragonsplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class Listener_DragonRiderTeleport implements Listener {

	/**
	 * The plugin
	 */
	private EnderdragonsPlus plugin;
	
	
	public Listener_DragonRiderTeleport() {
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void OnPlayerTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		
		Entity vehicle = player.getVehicle();
		if(vehicle != null && vehicle.getType() == EntityType.ENDER_DRAGON){
			CraftEnderDragon dragon = (CraftEnderDragon) vehicle;
			if(dragon.getHandle() instanceof LimitedEnderDragon){
				LimitedEnderDragon limitedDragon = (LimitedEnderDragon) dragon.getHandle();
				boolean portal = event.getFrom().getWorld() != event.getTo().getWorld();
				limitedDragon.teleportTo(event.getTo(), portal);
				
				scheduleReattatch(player, dragon);
			}
		}
	}


	/**
	 * Reattaches the Player to the dragon.
	 * 
	 * @param player to attach
	 * @param dragon to attach to
	 */
	private void scheduleReattatch(final Player player, final CraftEnderDragon dragon) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				dragon.setPassenger(player);
			}
		}, 0);
	}
}
