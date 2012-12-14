/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus.listeners;


import java.lang.reflect.Field;
import java.util.UUID;
import java.util.regex.Pattern;

import net.minecraft.server.v1_4_5.World;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;
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
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;


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
		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		
		String message = plugin.interactConfig().getConfig_dragonSpawnMessage();
		message = message.replaceAll(Pattern.quote("{x}"), ChatColor.LIGHT_PURPLE + "" + x + ChatColor.GREEN);
		message = message.replaceAll(Pattern.quote("{y}"), ChatColor.LIGHT_PURPLE + "" + y + ChatColor.GREEN);
		message = message.replaceAll(Pattern.quote("{z}"), ChatColor.LIGHT_PURPLE + "" + z + ChatColor.GREEN);
		
		message = message.replaceAll(Pattern.quote("{world}"), ChatColor.LIGHT_PURPLE + world + ChatColor.GREEN);
		
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(decodeColor(message));
		}
	}
	
	private String decodeColor(String message){
		return message.replaceAll("(&([a-f0-9]))", "§2");
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
			plugin.getDamageWhisperController().playerGotDamage(player);
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
				LimitedEnderDragonV131 dragon = plugin.getContainer().getDragonById(uid);
				if(dragon == null)
					return;

				dragon.addEnemy(damager);
				plugin.getDamageWhisperController().dragonGotDamage((EnderDragon)dragon.getBukkitEntity(), player);
			}
		}
	}
	
	
	
	private LimitedEnderDragonV131 spawnLimitedEnderDragon(Location location, String uid){
		World world = ((CraftWorld)location.getWorld()).getHandle();
		
		UUID uuid = UUID.fromString(uid);
		LimitedEnderDragonV131 dragon = new LimitedEnderDragonV131(location, world, uuid);
		dragon.spawn(false);
		dragon.setHealth(plugin.interactConfig().getConfig_dragonHealth());
		return dragon;
	}

}
