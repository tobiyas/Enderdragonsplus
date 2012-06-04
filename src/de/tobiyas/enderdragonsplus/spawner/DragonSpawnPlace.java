package de.tobiyas.enderdragonsplus.spawner;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnPlace {

	private Location loc;
	private int respawnTime;
	private UUID dragonID;
	private int newRespawnIn;
	
	private EnderdragonsPlus plugin;
	
	public DragonSpawnPlace(Location loc, int respawnTime){
		plugin = EnderdragonsPlus.getPlugin();
		this.loc = loc;
		this.respawnTime = respawnTime;
		this.newRespawnIn = respawnTime;
	}
	
	public void setDelay(int delay){
		newRespawnIn += delay;
	}
	
	public boolean isFree(){
		return dragonID == null;
	}
	
	public void linkDragon(UUID dragonID){
		if(!isFree()) return;
		this.dragonID = dragonID;
		this.newRespawnIn = respawnTime;
	}
	
	public void saveSelf(YAMLConfigExtended config, String id, int nr){
		if(dragonID != null) 
			config.set("spawners." + id + ".places." + nr + ".id", dragonID.toString());
		config.set("spawners." + id + ".places." + nr + ".respawnIn", newRespawnIn);
	}
	
	public void tick(){
		boolean dragonExists = plugin.getContainer().containsID(dragonID);
		if(!dragonExists){
			dragonID = null;
			newRespawnIn --;
			if(newRespawnIn < 0){
				LivingEntity newDragon = DragonAPI.spawnNewEnderdragon(loc);
				if(newDragon != null){
					newRespawnIn = respawnTime;
					dragonID = newDragon.getUniqueId();
				}
			}
		}
	}

	public void setRemainingRespawntime(int timeLeft) {
		this.newRespawnIn = timeLeft;
	}

	public void remove() {
		if(dragonID != null){
			LimitedEnderDragon dragon = plugin.getContainer().getDragonById(dragonID);
			dragon.remove();
		}
	}
}
