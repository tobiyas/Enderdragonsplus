package de.tobiyas.enderdragonsplus.API;

import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import net.minecraft.server.v1_6_R2.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeNotFoundException;

public class DragonAPI {

	
	/**
	 * @param dragon
	 * @param target
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, LivingEntity target){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		LimitedEnderDragon limitedDragon = getDragonByEntity(dragon);
		
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
		LimitedEnderDragon limitedDragon = getDragonByEntity(dragon);
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
		
		LimitedEnderDragon limitedDragon = getDragonByEntity(dragon);
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
		LimitedEnderDragon dragon = getDragonByEntity(bukkitDragon);
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
		return spawnNewEnderdragon(location, "Normal");
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
		
		World world = ((CraftWorld)location.getWorld()).getHandle();
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world, ageName);
		dragon.spawn();
		
		if(dragon.getBukkitEntity() == null){
			return null;
		}
			
		return (LivingEntity)dragon.getBukkitEntity();
	}
	
	/**
	 * @param dragon
	 * @param property
	 * @param value
	 * @return boolean if it worked
	 */
	public static boolean setPropertyToDragon(LivingEntity dragon, String property, Object value) throws OperationNotSupportedException{
		LimitedEnderDragon limitedDragon = getDragonByEntity(dragon);
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
		LimitedEnderDragon limitedDragon = getDragonByEntity(dragon);
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
	public static boolean spitFireballOnTarget(LivingEntity dragon, Entity target){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return false;
		
		return LEdragon.spitFireBallOnTarget((net.minecraft.server.v1_6_R2.Entity) target);
	}
	
	
	/**
	 * returns a dragon representation by bukkit entity
	 * @param entity
	 * @return dragon or null if not found
	 */
	private static LimitedEnderDragon getDragonByEntity(Entity entity){
		return getDragonById(entity.getUniqueId());
	}
	
	
	private static LimitedEnderDragon getDragonById(UUID id){
		LimitedEnderDragon dragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(id);
		return dragon;
	}
}
