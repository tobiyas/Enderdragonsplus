package de.tobiyas.enderdragonsplus.spawner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnerManager {

	private EnderdragonsPlus plugin;
	private HashMap<Integer, DragonSpawnerContainer> spawners;
	private DragonSpawnerTimer ticker;
	
	private YAMLConfigExtended config;
	
	
	public DragonSpawnerManager(){
		this.plugin = EnderdragonsPlus.getPlugin();
		spawners = new HashMap<Integer, DragonSpawnerContainer>();
	}
	
	public void init(){
		loadList();
		ticker = new DragonSpawnerTimer(this);
	}
	
	public void stopTicker(){
		ticker.stopTimer();
	}
	
	public void continueTicker(){
		ticker.continueTimer();
	}
	
	public boolean addSpawner(Location loc, int respawnTime, int maxDragons){
		int freeID = getNextFreeID();
		String spawnerID = "spawner" + freeID;
		DragonSpawnerContainer container = DragonSpawnerContainer.createContainer(loc.getBlock().getLocation(), maxDragons, respawnTime, spawnerID);
		if(container == null)
			return false;
		spawners.put(freeID, container);
		return true;
	}
	
	private int getNextFreeID() {
		for(int i = 0; i < Integer.MAX_VALUE; i++){
			DragonSpawnerContainer container = spawners.get(i);
			if(container == null)
				return i;
		}
		return -1;
	}

	public boolean hasRespawnerOn(Location loc){
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			if(spawner.isNear(loc))
				return true;
		}
		return false;
	}
	
	public boolean deleteSpawner(Location loc){
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			if(spawner.isNear(loc)){
				spawner.remove(config);
				spawners.remove(id);
				return true;
			}
		}
		return false;
	}
	
	private void loadList(){
		checkExist();
		String path = plugin.getDataFolder() + File.separator + "spawner.yml";
		config = new YAMLConfigExtended(path).load();
		
		if(!config.getValidLoad()){
			plugin.log("Error loading Spawner config.");
			return;
		}
		
		Set<String> spawnerStrings = config.getYAMLChildren("spawners");
		for(String spawner : spawnerStrings){
			DragonSpawnerContainer container = DragonSpawnerContainer.createContainer(config, spawner);
			if(container == null)
				continue;
			int freeID = getNextFreeID();
			spawners.put(freeID, container);
		}
	}
	
	private void checkExist(){
		File file = new File(plugin.getDataFolder() + File.separator + "spawner.yml");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create 'spawner.yml'. Reason: " + e.getLocalizedMessage());
			}
	}
	
	public void saveList(){
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.saveContainer(config);
		}
		if(!config.save())
			plugin.log("ERROR on saving Spawners.");
	}
	
	public void tick() {
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.tick();
		}
	}
	
	public int resetSigns(){
		int i = 0;
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			if(spawner.resetSign())
				i++;
		}
		return i;
	}

	public int clearAll() {
		int cleared = spawners.size();
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.remove(config);
		}
		
		spawners.clear();
		
		return cleared;
	}

	public void sendInfoToPlayer(Player player) {
		if(spawners.size() == 0)
			player.sendMessage(ChatColor.RED + "No Spawners registered.");
		
		for(int id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.sendInfo(player);
		}
	}

	public Location getLocationOfRespawner(int respawner) {
		DragonSpawnerContainer spawner =  spawners.get(respawner);
		if(spawner == null)
			return null;
		
		return spawner.getLocation();
	}

}
