/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus.listeners;


import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.World;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener; 
import org.bukkit.event.EventHandler; 

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;


public class Listener_Entity implements Listener {
	private EnderdragonsPlus plugin;
	public static int recDepth = 0;
	
	public Listener_Entity(){
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent event){
		if(!event.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
		
		if(plugin.interactConfig().getConfig_deactivateDragonTemples()) 
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void replaceDragon(CreatureSpawnEvent event){
		if(event.isCancelled()) return;
		if(!event.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
		if(!plugin.interactConfig().getConfig_replaceAllDragons()) return;
		
		if(plugin.interactConfig().getConfig_debugOutput())
			plugin.log("enderdragon id: " + event.getEntity().getEntityId());
		
		UUID id = event.getEntity().getUniqueId();
		if(plugin.getContainer().containsID(id)){
			if(plugin.interactConfig().getConfig_anounceDragonSpawning())
				announceDragon(event.getEntity());
			return;
		}
		
		if(plugin.interactConfig().getConfig_debugOutput())
			plugin.log("id detection failed.");
		
		if(plugin.interactBridgeController().isSpecialDragon(event.getEntity())) return;
		
		if(recDepth > 40){
			plugin.log("CRITICAL: Concurring plugins detected! Disable the concurring plugin!");
			return;
		}
		
		if(recDepth == 0)
			new RecursionEraser();
		
		recDepth ++;
			
		String uidString = id.toString();
		event.getEntity().remove();
		
		Entity newDragon = spawnLimitedEnderDragon(event.getLocation(), uidString).getBukkitEntity();
		try{
			EntityEvent entityEvent = (EntityEvent) event;			
			
			Field field = entityEvent.getClass().getSuperclass().getDeclaredField("entity");
			field.setAccessible(true);
			field.set(entityEvent, newDragon);
			
		}catch (IllegalArgumentException e) {
			plugin.log("Something gone Wrong with Injecting!");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			plugin.log("Something gone Wrong with Injecting!");
			e.printStackTrace();
		} catch (SecurityException e) {
			plugin.log("Something gone Wrong with Injecting!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			plugin.log("Something gone Wrong with Injecting!");
			e.printStackTrace();
		}	
	}
	
	private void announceDragon(Entity entity){
		Location loc = entity.getLocation();
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(ChatColor.GREEN + "A new Dragon has spawned at: " + ChatColor.LIGHT_PURPLE + loc.getBlockX() +
								ChatColor.GREEN + ", " + ChatColor.LIGHT_PURPLE + loc.getBlockZ() + ChatColor.GREEN + 
								" on world: " + ChatColor.LIGHT_PURPLE + loc.getWorld().getName());
		}
	}
	
	@EventHandler
	public void onEnderDragonExplode(EntityExplodeEvent event) {
		if(!plugin.interactConfig().getConfig_disableEnderdragonBlockDamage()) return;
		if(!(event.getEntity() instanceof EnderDragon)) return;
		UUID id = event.getEntity().getUniqueId();
		if (plugin.getContainer().containsID(id)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void entityDamageEvent(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		
		checkPlayerDamageDragon(event);
		if(!event.getDamager().getType().equals(EntityType.ENDER_DRAGON)) return;
		
		if(!(event.getEntity() instanceof Player)) return;
		if(plugin.interactConfig().getConfig_informPlayerDamageTaken()){
			Player player = (Player) event.getEntity();
			player.sendMessage(ChatColor.YELLOW + "The Dragon has done " + ChatColor.LIGHT_PURPLE + event.getDamage() + ChatColor.YELLOW + " damage to you.");
		}
	}
	
	private void checkPlayerDamageDragon(EntityDamageByEntityEvent event){
		if(!plugin.interactConfig().getconfig_informPlayerDamageDone()) return;
		
		if(event.getEntity().getType() == EntityType.ENDER_DRAGON){
			Entity damager = event.getDamager();
			
			if(damager.getType() == EntityType.ARROW){
				Arrow arrow = (Arrow) event.getDamager();
				LivingEntity shooter = arrow.getShooter();
				damager = shooter;
			}
			
			if(damager instanceof Player){
				Player player = (Player) damager;
				UUID uid = event.getEntity().getUniqueId();
				LimitedEnderDragon dragon = plugin.getContainer().getDragonById(uid);
				if(dragon == null)
					return;
				
				int actualLife = dragon.getHealth() - event.getDamage();
				int maxLife = dragon.getMaxHealth();
				
				String midLifeString = parsePersentageLife(actualLife, maxLife);
				player.sendMessage(ChatColor.YELLOW + "The Dragon has " + midLifeString + ChatColor.YELLOW + " health left. You did " + 
						ChatColor.LIGHT_PURPLE + event.getDamage() + ChatColor.YELLOW + " damage.");
			}
		}
	}
	
	private String parsePersentageLife(int actual, int max){
		float currentPercentage = actual / max;
		
		if(currentPercentage < 0.2)
			return ChatColor.RED + "" + actual + "/" + max;
		
		if(currentPercentage < 0.5)
			return ChatColor.YELLOW + "" + actual + "/" + max;
		
		return ChatColor.GREEN + "" + actual + "/" + max;
	}
	
	private LimitedEnderDragon spawnLimitedEnderDragon(Location location, String uid){
		World world = ((CraftWorld)location.getWorld()).getHandle();
		
		UUID uuid = UUID.fromString(uid);
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world, uuid);
		dragon.spawn(false);
		dragon.setHealth(plugin.interactConfig().getConfig_dragonHealth());
		return dragon;
	}

}
