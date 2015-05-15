package de.tobiyas.enderdragonsplus.API;

import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonVersionManager;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeNotFoundException;
import de.tobiyas.enderdragonsplus.listeners.Listener_Dragon_Spawn;

public class DragonAPI {

	
	/**
	 * @param dragon
	 * @param target
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, LivingEntity target){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		LimitedED limitedDragon = getDragonByEntity(dragon);
		
		if(limitedDragon != null) 
			limitedDragon.setTarget(target);
		
		return limitedDragon != null;
	}
	
	/**
	 * @param dragon
	 * @param player
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, Player player){
		return setTarget(dragon, (LivingEntity) player);
	}
	
	/**
	 * @param dragon
	 * @param location
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, Location location){
		LimitedED limitedDragon = getDragonByEntity(dragon);
		if(limitedDragon != null)
			limitedDragon.goToLocation(location);
		
		return limitedDragon != null;
	}
	
	/**
	 * @param dragon
	 * @return boolean if it worked
	 */
	public static boolean sendHome(LivingEntity dragon){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		LimitedED limitedDragon = getDragonByEntity(dragon);
		if(limitedDragon != null)
			limitedDragon.goToLocation(limitedDragon.getHomeLocation());
		
		return limitedDragon != null;
	}
	
	/**
	 * @param bukkitDragon
	 * @param location
	 * @return
	 */
	public static boolean setNewHome(LivingEntity bukkitDragon, Location location){
		if(!(bukkitDragon.getType() == EntityType.ENDER_DRAGON)) return false;
		LimitedED dragon = getDragonByEntity(bukkitDragon);
		if(dragon != null){
			dragon.setNewHome(location);
		}
		
		return dragon != null;
	}
	
	/**
	 * @param dragon
	 * @param location
	 * @return boolean if it worked
	 */
	public static boolean setNewHomeAndGoTo(LivingEntity dragon, Location location){
		if(!setNewHome(dragon, location)) return false;
		return sendHome(dragon);
	}
	
	/**
	 * @param location
	 * @return
	 */
	public static LivingEntity spawnNewEnderdragon(Location location){
		return spawnNewEnderdragon(location, true);
	}
	
	
	/**
	 * @param location to spawn at
	 * @param forceGlobalSpawnNotice if a global notice should be sent.
	 * 
	 * @return the spawned dragon.
	 */
	public static LivingEntity spawnNewEnderdragon(Location location, boolean forceGlobalSpawnNotice){
		boolean before = Listener_Dragon_Spawn.SUPPRESS_DRAGON_MESSAGE;
		if(!forceGlobalSpawnNotice) Listener_Dragon_Spawn.SUPPRESS_DRAGON_MESSAGE = true;
		
		LivingEntity spawned = spawnNewEnderdragon(location, "Normal");
		if(!forceGlobalSpawnNotice) Listener_Dragon_Spawn.SUPPRESS_DRAGON_MESSAGE = before;
		return spawned;
	}
	
	/**
	 * @param ageName
	 * @param location
	 * @return
	 */
	public static LivingEntity spawnNewEnderdragon(Location location, String ageName){
		try{
			EnderdragonsPlus.getPlugin().getAgeContainerManager().getAgeContainer(ageName);
		}catch(AgeNotFoundException exp){
			return null;
		}
		
		LimitedED dragon = LimitedEnderDragonVersionManager.generate(location, ageName);
		dragon.spawn();
		
		if(dragon.getBukkitEntity() == null){
			return null;
		}
			
		return (LivingEntity) dragon.getBukkitEntity();
	}
	
	/**
	 * @param dragon
	 * @param property
	 * @param value
	 * @return boolean if it worked
	 */
	public static boolean setPropertyToDragon(LivingEntity dragon, String property, Object value) throws OperationNotSupportedException{
		LimitedED limitedDragon = getDragonByEntity(dragon);
		if(limitedDragon == null)
			return false;
		
		limitedDragon.setProperty(property, value);
		return true;
	}
	
	/**
	 * @param dragon
	 * @param property
	 * @return Object found by property
	 */
	public static Object getPropertyToDragon(LivingEntity dragon, String property){
		LimitedED limitedDragon = getDragonByEntity(dragon);
		if(limitedDragon == null)
			return null;
		
		return limitedDragon.getProperty(property);
	}
	
	/**
	 * Spits a fireball on the target Entity
	 * 
	 * @param dragon
	 * @param target
	 * @return if it worked
	 */
	public static boolean spitFireballOnTarget(LivingEntity dragon, LivingEntity target){
		LimitedED LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return false;
		
		return LEdragon.spitFireBallOnTarget(target);
	}
	
	
	/**
	 * returns a dragon representation by bukkit entity
	 * @param entity
	 * @return dragon or null if not found
	 */
	public static LimitedED getDragonByEntity(Entity entity){
		return getDragonById(entity.getUniqueId());
	}
	
	
	private static LimitedED getDragonById(UUID id){
		LimitedED dragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(id);
		return dragon;
	}
}
