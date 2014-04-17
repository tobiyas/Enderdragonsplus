package de.tobiyas.enderdragonsplus.spawner;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnPlace {

	//private Location loc;
	private DragonSpawnerContainer dsContainer;
	private int respawnTime;
	private UUID dragonID;
	private int newRespawnIn;
	private String dragonAgeName;
	
	private EnderdragonsPlus plugin;
	
	public DragonSpawnPlace(DragonSpawnerContainer dsContainer, int respawnTime, String dragonAgeName){
		plugin = EnderdragonsPlus.getPlugin();
		this.dsContainer = dsContainer;
		this.respawnTime = respawnTime;
		this.newRespawnIn = respawnTime;
		this.dragonAgeName = dragonAgeName;
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
		config.load();
		if(dragonID != null) 
			config.set("spawners." + id + ".places." + nr + ".id", dragonID.toString());
		config.set("spawners." + id + ".places." + nr + ".respawnIn", newRespawnIn);
		config.save();
	}
	
	public void tick(){
		boolean dragonExists = plugin.getContainer().containsID(dragonID);
		if(!dragonExists){
			dragonID = null;
			newRespawnIn --;
			if(newRespawnIn < 0){
				Location loc = getRandomLoc();
				LivingEntity newDragon = DragonAPI.spawnNewEnderdragon(loc, dragonAgeName);
				if(newDragon != null){
					newRespawnIn = respawnTime;
					dragonID = newDragon.getUniqueId();
				}
			}
		}
	}
	
	private Location getRandomLoc(){
		ArrayList<Location> locs = dsContainer.getLocation();
		if(locs.size() == 1)
			return locs.get(0);
			
		Random rand = new Random();
		int randInt = rand.nextInt(locs.size());
		return locs.get(randInt);
	}

	public void setRemainingRespawntime(int timeLeft) {
		this.newRespawnIn = timeLeft;
	}

	public void remove() {
		if(dragonID != null){
			LimitedED dragon = plugin.getContainer().getDragonById(dragonID);
			dragon.remove();
		}
	}
}
