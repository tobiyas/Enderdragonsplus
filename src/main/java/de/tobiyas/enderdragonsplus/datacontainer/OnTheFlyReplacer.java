package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;

public class OnTheFlyReplacer implements Runnable {

	private EnderdragonsPlus plugin;
	
	public OnTheFlyReplacer(){
		plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 10);
	}
	
	@Override
	public void run() {
		if(plugin.interactConfig().getConfig_replaceOnTheFly()){
			for(World world : Bukkit.getWorlds()){
				for(Entity entity : world.getEntities()){
					if(entity.getType() != EntityType.ENDER_DRAGON) continue;
					UUID entityID = entity.getUniqueId();
					if(plugin.interactBridgeController().isSpecialDragon((LivingEntity)entity)) continue;
					if(entityID == null) continue;
					boolean isEDPDragon = plugin.getContainer().getDragonById(entityID) != null;
					if(isEDPDragon) continue;
					
					Location loctaion = entity.getLocation();
					entity.remove();
					boolean worked = spawnLimitedEnderDragon(loctaion, entityID) != null;
					if(plugin.interactConfig().getConfig_debugOutput())
						plugin.log("Replaced dragon: " + entityID + " worked: " + worked);
				}
			}
		}
	}
	
	private LimitedEnderDragonV131 spawnLimitedEnderDragon(Location location, UUID uuid){
		net.minecraft.server.v1_4_6.World world = ((CraftWorld)location.getWorld()).getHandle();
		
		LimitedEnderDragonV131 dragon = new LimitedEnderDragonV131(location, world, uuid);
		dragon.spawn(false);
		dragon.setHealth(plugin.interactConfig().getConfig_dragonHealth());
		return dragon;
	}

}
