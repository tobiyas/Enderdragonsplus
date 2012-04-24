/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus.listeners;


import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener; 
import org.bukkit.event.EventHandler; 

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;


public class Listener_Entity implements Listener {
	private EnderdragonsPlus plugin;

	public Listener_Entity(){
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent event){
		if(!event.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
		
		if(plugin.interactConfig().getconfig_active()) 
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void replaceDragon(CreatureSpawnEvent event){
		if(event.isCancelled()) return;
		if(!event.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
		if(!plugin.interactConfig().getconfig_replaceAllDragons()) return;
		
		if(plugin.interactConfig().getconfig_debugOutput())
			plugin.log("enderdragon id: " + event.getEntity().getEntityId());
		
		int id = event.getEntity().getEntityId();
		if(plugin.getContainer().containsID(id)) return;
		
		if(plugin.interactConfig().getconfig_debugOutput())
			plugin.log("id detection failed.");
		
		if(plugin.interactBridgeController().isSpecialDragon(event.getEntity())) return;
		
		spawnLimitedEnderDragon(event.getLocation());
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEnderDragonExlplode(EntityExplodeEvent event) {
		if(!plugin.interactConfig().getconfig_disableEnderdragonBlockDamage()) return;
		int id = event.getEntity().getEntityId();
		if (plugin.getContainer().containsID(id)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void event(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(!event.getDamager().getType().equals(EntityType.ENDER_DRAGON)) return;
		EntityEnderDragon dragon = ((CraftEnderDragon) event.getDamager()).getHandle();
		if(dragon instanceof LimitedEnderDragon){
			event.setDamage(plugin.interactConfig().getconfig_dragonDamage());
		}
	}
	
	private LimitedEnderDragon spawnLimitedEnderDragon(Location location){
		World world = ((CraftWorld)location.getWorld()).getHandle();
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world);
		dragon.spawn(false);
		dragon.setHealth(plugin.interactConfig().getconfig_dragonHealth());
		return dragon;
	}

}
