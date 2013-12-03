package de.tobiyas.enderdragonsplus.listeners;

import static de.tobiyas.enderdragonsplus.util.MinecraftChatColorUtils.decodeColors;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.regex.Pattern;

import net.minecraft.server.World;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class Listener_Dragon_Spawn implements Listener {

	private EnderdragonsPlus plugin;
	public static int recDepth = 0;
	
	
	public Listener_Dragon_Spawn() {
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void replaceDragon(CreatureSpawnEvent event){
		//checking all criteria if replacing possible
		if(event.isCancelled()) return;
		if(!event.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
		if(!plugin.interactConfig().getConfig_replaceAllDragons()) return;
		
		if(plugin.interactConfig().getConfig_debugOutput())
			plugin.log("enderdragon id: " + event.getEntity().getEntityId());
		
		
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
			
		
		//replacing
		UUID id = event.getEntity().getUniqueId();
		
		String uidString = id.toString();
		event.getEntity().remove();
		
		Entity newDragon = spawnLimitedEnderDragon(event.getLocation(), uidString).getBukkitEntity();
		try{
			EntityEvent entityEvent = (EntityEvent) event;
			
			Field field = entityEvent.getClass().getSuperclass().getDeclaredField("entity");
			field.setAccessible(true);
			field.set(entityEvent, newDragon);
			
		}catch (Exception exp) {
			plugin.log("Something gone Wrong with Injecting!");
			plugin.getDebugLogger().logStackTrace(exp);
			//spawning went wrong. Returning...
			return;
		}
		
		//Anouncing
		if(plugin.getContainer().containsID(id)){
			if(plugin.interactConfig().getConfig_anounceDragonSpawning())
				announceDragon(event.getEntity());
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void listenDragonSpawn(CreatureSpawnEvent event){
		Entity entity = event.getEntity();
		UUID entityId = entity.getUniqueId();
		
		if(plugin.getContainer().containsID(entityId)){
			if(plugin.interactConfig().getConfig_anounceDragonSpawning()){
				announceDragon(entity);
			}
		}
		
	}
	
	private void announceDragon(Entity entity){
		
		LimitedEnderDragon dragon = null;
		try{
			dragon = (LimitedEnderDragon)((CraftEnderDragon) entity).getHandle();
		}catch(ClassCastException exp){return;}
		
		String ageName = dragon.getAgeName();
		
		Location loc = dragon.getLocation();
		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		
		String message = plugin.interactConfig().getConfig_dragonSpawnMessage();
		message = message.replaceAll(Pattern.quote("~x~"), ChatColor.LIGHT_PURPLE + "" + x + ChatColor.GREEN);
		message = message.replaceAll(Pattern.quote("~y~"), ChatColor.LIGHT_PURPLE + "" + y + ChatColor.GREEN);
		message = message.replaceAll(Pattern.quote("~z~"), ChatColor.LIGHT_PURPLE + "" + z + ChatColor.GREEN);
		message = message.replaceAll(Pattern.quote("~age~"), ChatColor.RED + ageName + ChatColor.GREEN);
		
		message = message.replaceAll(Pattern.quote("~world~"), ChatColor.LIGHT_PURPLE + world + ChatColor.GREEN);
		
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(decodeColors(message));
		}
	}
	
	private LimitedEnderDragon spawnLimitedEnderDragon(Location location, String uid){
		World world = ((CraftWorld)location.getWorld()).getHandle();
		
		UUID uuid = UUID.fromString(uid);
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world, uuid);
		dragon.spawn();
		return dragon;
	}

}
