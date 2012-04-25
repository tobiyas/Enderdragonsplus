package de.tobiyas.enderdragonsplus.API;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;

public class DragonAPI {

	
	public static boolean setTarget(LivingEntity dragon, LivingEntity target){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		LimitedEnderDragon lDragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getEntityId());
		if(lDragon == null) return false;
		lDragon.setTarget(target);
		
		return true;
	}
	
	public static boolean setTarget(LivingEntity dragon, Player player){
		return setTarget(dragon, (LivingEntity) player);
	}
	
	public static boolean setTarget(LivingEntity dragon, Location location){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getEntityId());
		if(LEdragon == null)
			return false;
		
		LEdragon.goToLocation(location);
		return true;
	}
	
	public static boolean sendHome(LivingEntity dragon){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		try{
			int id = dragon.getEntityId();
			EnderdragonsPlus.getPlugin().getContainer().setFlyingHome(id, true);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
}
