/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus.listeners;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;


public class Listener_Entity implements Listener {
	private EnderdragonsPlus plugin;
	
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
				LivingEntity shooter = ((LivingEntity)arrow.getShooter());
				damager = shooter;
			}
			
			if(damager instanceof Player){
				Player player = (Player) damager;
				UUID uid = event.getEntity().getUniqueId();
				LimitedEnderDragon dragon = plugin.getContainer().getDragonById(uid);
				if(dragon == null)
					return;

				dragon.addEnemy(damager);
				plugin.getDamageWhisperController().dragonGotDamage((EnderDragon)dragon.getBukkitEntity(), player);
			}
		}
	}
	
	@EventHandler
	public void callDeath(EntityDeathEvent event){
		if(!plugin.interactConfig().getConfig_anounceDragonKill())
			return;

		if(!(((CraftEntity)event.getEntity()).getHandle() instanceof LimitedEnderDragon))
			return;
		
		LimitedEnderDragon dragon = (LimitedEnderDragon) ((CraftEnderDragon)event.getEntity()).getHandle();
		String lastPlayerAttacked = dragon.getLastPlayerAttacked();
		if(lastPlayerAttacked.equals("")){
			return;
		}			

		parseDragonDeath(dragon, event.getEntity().getWorld());
	}
	
	private void parseDragonDeath(LimitedEnderDragon dragon,org.bukkit.World dragonDeathWorld){
		String message = plugin.interactConfig().getConfig_dragonKillMessage();
		double damage = dragon.getDamageByPlayer(dragon.getLastPlayerAttacked());
		
		message =
		message.replaceAll(Pattern.quote("~player_kill~"), dragon.getLastPlayerAttacked())
				.replaceAll(Pattern.quote("{player_kill}"), dragon.getLastPlayerAttacked())
				
				.replaceAll(Pattern.quote("~player_kill_dmg~"), damage + "")
				.replaceAll(Pattern.quote("{player_kill_dmg}"), damage + "")
				
				.replaceAll(Pattern.quote("~age~"), dragon.getAgeName())
				.replaceAll(Pattern.quote("{age}"), dragon.getAgeName())
				
				.replaceAll("(&([a-f0-9]))", "§$2");
		
		
		List<org.bukkit.World> toWorlds = decodeWorlds(dragonDeathWorld);
		announceToWorlds(message, toWorlds);
	}
	
	private List<org.bukkit.World> decodeWorlds(org.bukkit.World dragonDeathWorld) {
		String worldString = plugin.interactConfig().getConfig_dragonKillMessageToWorlds();
		List<org.bukkit.World> worldList = new LinkedList<org.bukkit.World>();
		if(worldString.contains("~all~") || worldString.contains("{all}"))
			return Bukkit.getWorlds();
		
		worldString.replace("{current}", dragonDeathWorld.getName());
		worldString.replace("~current~", dragonDeathWorld.getName());
		
		String[] worlds = worldString.split(",");
		for(String world : worlds){
			org.bukkit.World bukkitWorld = Bukkit.getWorld(world);
			if(bukkitWorld != null && !worldList.contains(bukkitWorld))
				worldList.add(bukkitWorld);
		}
		
		return worldList;
	}

	private void announceToWorlds(String message, List<org.bukkit.World> toWorlds) {
		for(org.bukkit.World world : toWorlds){
			for(Player player : world.getPlayers())
				player.sendMessage(message);
		}
			
	}
}
