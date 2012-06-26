package de.tobiyas.enderdragonsplus.spawner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnerManager {

	private EnderdragonsPlus plugin;
	private HashMap<String, DragonSpawnerContainer> spawners;
	private DragonSpawnerTimer ticker;
	
	private YAMLConfigExtended config;
	
	
	public DragonSpawnerManager(){
		this.plugin = EnderdragonsPlus.getPlugin();
		spawners = new HashMap<String, DragonSpawnerContainer>();
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
	
	public boolean addSpawner(Location loc, int respawnTime, int maxDragons, String spawnerName){
		if(containerContains(spawnerName)) return false;
		DragonSpawnerContainer container = DragonSpawnerContainer.createContainer(loc.getBlock().getLocation(), maxDragons, respawnTime, spawnerName);
		if(container == null)
			return false;
		spawners.put(spawnerName, container);
		return true;
	}
	
	private boolean containerContains(String name){
		for(String sName : spawners.keySet())
			if(sName.equalsIgnoreCase(name))
				return true;
		return false;
	}

	public boolean hasRespawnerOn(Location loc){
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			if(spawner.isNear(loc))
				return true;
		}
		return false;
	}
	
	public boolean deleteSpawner(Location loc){
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			if(spawner.isNear(loc)){
				if(spawner.remove(config, loc, false))
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
			
			spawners.put(container.getSpawnerID(), container);
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
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.saveContainer(config);
		}
		if(!config.save())
			plugin.log("ERROR on saving Spawners.");
	}
	
	public void tick() {
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.tick();
		}
	}
	
	public int resetSigns(){
		int i = 0;
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			i+= spawner.createSigns();
		}
		return i;
	}

	public int clearAll() {
		int cleared = spawners.size();
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.remove(config, new Location(Bukkit.getWorlds().get(0), 0, 250, 0), true);
		}
		
		spawners.clear();
		
		return cleared;
	}

	public void sendInfoToPlayer(Player player) {
		if(spawners.size() == 0)
			player.sendMessage(ChatColor.RED + "No Spawners registered.");
		
		for(String id : spawners.keySet()){
			DragonSpawnerContainer spawner = spawners.get(id);
			spawner.sendInfo(player);
		}
	}

	public ArrayList<Location> getLocationOfRespawner(String respawner) {
		DragonSpawnerContainer spawner =  spawners.get(respawner);
		if(spawner == null)
			return null;
		
		return spawner.getLocation();
	}

	public boolean linkSpawner(String respawnerName, Location placeLocation) {
		for(String id : spawners.keySet()){
			DragonSpawnerContainer container = spawners.get(id);
			if(container.getSpawnerID().equalsIgnoreCase(respawnerName)){
				container.linkPosition(placeLocation);
				return true;
			}
		}
		
		return false;
	}

}
