package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_4_R1.DamageSource;

import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class Container {

	private HashMap<UUID, LimitedEnderDragon> dragonList;
	private EnderdragonsPlus plugin;
	
	public Container(){
		plugin = EnderdragonsPlus.getPlugin();
		dragonList = new HashMap<UUID, LimitedEnderDragon>();
		new CleanRunner(this);
	}
	
	public int cleanRun(){
		List<UUID> toDelete = new ArrayList<UUID>();
		
		for(UUID id : dragonList.keySet()){
			LimitedEnderDragon dragon = dragonList.get(id);
			if(dragon == null || !dragon.isAlive())
				toDelete.add(id);
		}
		
		if(toDelete.size() == 0) return 0;
		
		for(UUID dragon : toDelete){
			dragonList.remove(dragon);
			if(plugin.interactConfig().getConfig_debugOutput())
				plugin.log("removed Dragon!");
		}
		
		return toDelete.size();
	}
	
	public int killEnderDragons(Location location, int range){
		ArrayList<UUID> toRemove = new ArrayList<UUID>();
		for(UUID dragonID : dragonList.keySet()){
			LimitedEnderDragon dragon = dragonList.get(dragonID);
			if(dragon != null && (range == 0 || dragon.isInRange(location, range))){
				toRemove.add(dragonID);
				//container.dragon.remove();
				dragon.dealDamage(DamageSource.EXPLOSION, 1000);
			}
		}
		
		if(toRemove.size() == 0)
			return 0;
		
		for(UUID dragonID : toRemove)
			dragonList.remove(dragonID);
		
		return toRemove.size();
	}
	
	/*
	public String loadDragonsInLoadedChunks(){
		int i = 0;
		String dragonPath = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
		
		List<UUID> toLoad = new LinkedList<UUID>();
		
		for(UUID id : dragonList.keySet()){
			if(!dragonList.get(id).isLoaded){
				Location location = dragonList.get(id).location;
				if(location.getWorld().isChunkLoaded(getChunkX(location), getChunkY(location)))
					toLoad.add(id);
				i++;
			}
		}
		
		int j = 0;
		for(UUID id : toLoad){
			LimitedEnderDragon dragon = DragonStore.loadFromFile(dragonPath + id);
			if(dragon == null) continue;
			dragon.spawn(false);
			j++;
		}
		
		return j + " of " + i;
	}*/

	public int sendAllDragonsHome() {
		int i = 0;
		
		for(UUID dragonID : dragonList.keySet()){
			LimitedEnderDragon dragon = dragonList.get(dragonID);
			if(dragon != null){
				dragon.forceFlyHome(true);
				i++;
			}
		}
		return i;
	}
	
	/*
	public boolean getFlyingHome(UUID id){
		DragonInfoContainer temp = homeList.get(id);
		return temp.flyingHome;
	}

	public Location getHomeByID(UUID id) {
		DragonInfoContainer temp = homeList.get(id);
		return temp.homeLocation;
	}

	public void setHomeID(UUID entityId, Location homelocation, Location location, boolean flyingHome, LimitedEnderDragon dragon){
		if(plugin.interactConfig().getConfig_debugOutput()) 
			plugin.log("set ID: " + entityId);
		
		DragonInfoContainer temp = new DragonInfoContainer(entityId, location.clone(), homelocation.clone(), flyingHome, true, dragon);
		homeList.put(entityId, temp);
	}
	
	public void setFlyingHome(UUID uid, boolean value){
		DragonInfoContainer temp = homeList.get(uid);
		temp.flyingHome = value;
	}*/
	
	public boolean containsID(UUID id){
		return dragonList.containsKey(id);
	}
	
	public Set<UUID> getAllIDs(){
		return dragonList.keySet();
	}

	/*
	public void loadDragonsInChunk(Chunk chunk) {
		for(UUID id : dragonList.keySet()){
			DragonInfoContainer con = dragonList.get(id);
			if(con.isLoaded) continue;
			if(locationIsInChunk(con.location, chunk)){
				if(plugin.interactConfig().getConfig_debugOutput()) 
					plugin.log("loading: " + con.ID);
				
				String path = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
				LimitedEnderDragon dragon = DragonStore.loadFromFile(path + con.ID);
				dragon.spawn(con.firstLoad);
			}
		}
	}*/

	/*
	public void saveDragon(UUID entityId) {
		if(plugin.interactConfig().getConfig_debugOutput())
			plugin.log("Saving Dragon: " + entityId);
		DragonInfoContainer con = homeList.get(entityId);
		DragonStore.saveToPath(con.dragon);
		con.location = con.dragon.getLocation();
		con.isLoaded = false;
	}*/
	
	public int loaded(){
		int i = 0;
		for(UUID id : dragonList.keySet()){
			if(dragonList.get(id) != null) i++;
		}
		
		return i;
	}
	
	public int count(){
		return dragonList.size();
	}
	
	
	public LimitedEnderDragon getDragonById(UUID id){
		return dragonList.get(id);
	}
	
	/*
	private boolean locationIsInChunk(Location location, Chunk chunk){
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();

		int locX = getChunkX(location);
		int locZ = getChunkY(location);
		
		return chunkX == locX && chunkZ == locZ;
	}
	
	private int getChunkX(Location loc){
		int locX = loc.getBlockX();
		if(locX < 0)
			locX = (((locX-1) / 16)-1);
		else
			locX /= 16;
		
		return locX;
	}
	
	private int getChunkY(Location loc){
		int locZ = loc.getBlockZ();
				
		if(locZ < 0) 
			locZ = (((locZ-1) / 16)-1);
		else
			locZ /= 16;
		
		return locZ;
	}*/
	
	public Location getPositionByID(UUID id){
		LimitedEnderDragon dragon = dragonList.get(id);
		if(dragon == null) return null;
		return dragon.getLocation();
	}

	public boolean isLoaded(UUID id) {
		LimitedEnderDragon dragon = dragonList.get(id);
		return dragon != null;
	}
	
	public void registerDragon(UUID dragonId, LimitedEnderDragon dragon){
		dragonList.put(dragonId, dragon);
	}
	
	public void unregisterDragon(UUID dragonId){
		dragonList.remove(dragonId);
	}

	/*
	public boolean setHome(UUID id, Location location) {
		DragonInfoContainer con = homeList.get(id);
		if(con == null) return false;
		con.homeLocation = location;
		return true;
	}
	
	public void setProperty(UUID id, String property, Object value){
		DragonInfoContainer con = homeList.get(id);
		if(con == null) return;
		con.properties.put(property, value);
	}
	
	public Object getProperty(UUID id, String property){
		DragonInfoContainer con = homeList.get(id);
		if(con == null) return null;
		return con.properties.get(property);
	}*/
}
