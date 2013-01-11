package de.tobiyas.enderdragonsplus.datacontainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_4_6.DamageSource;

import org.bukkit.Chunk;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.DragonStore;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;

public class Container {

	private HashMap<UUID, DragonInfoContainer> homeList;
	private EnderdragonsPlus plugin;
	
	public Container(){
		plugin = EnderdragonsPlus.getPlugin();
		homeList = new HashMap<UUID, DragonInfoContainer>();
		new CleanRunner(this);
	}
	
	public int cleanRun(){
		List<UUID> toDelete = new ArrayList<UUID>();
		
		for(UUID id : homeList.keySet()){
			DragonInfoContainer dragonContainer = homeList.get(id);
			if(dragonContainer.dragon == null || !dragonContainer.dragon.isAlive())
				if(dragonContainer.isLoaded) toDelete.add(id);
		}
		
		if(toDelete.size() == 0) return 0;
		
		for(UUID dragon : toDelete){
			DragonInfoContainer container = homeList.get(dragon);
			if(!container.isLoaded)
				new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon." + dragon).delete();
			homeList.remove(dragon);
			if(plugin.interactConfig().getConfig_debugOutput())
				plugin.log("removed Dragon!");
		}
		
		return toDelete.size();
	}
	
	public int killEnderDragons(Location location, int range){
		ArrayList<UUID> toRemove = new ArrayList<UUID>();
		for(UUID dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(container.isLoaded){
				if(container.dragon != null && (range == 0 ||container.dragon.isInRange(location, range))){
					toRemove.add(dragonID);
					//container.dragon.remove();
					container.dragon.dealDamage(DamageSource.EXPLOSION, 1000);
				}
			}else{
				//TODO handle unloaded dragons
			}
		}
		
		if(toRemove.size() == 0)
			return 0;
		
		for(UUID dragonID : toRemove)
			homeList.remove(dragonID);
		
		return toRemove.size();
	}
	
	public void saveContainer(){
		File file = new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator);
		if(!file.exists())
			file.mkdirs();
		
		int i = 0;
		int j = 0;
		
		for(UUID dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(!DragonStore.saveToPath(container.dragon)) j++;
			container.isLoaded = false;
			container.dragon.remove();
			i++;
		}
		if(i != 0) plugin.log("Saved: " + (i-j) + " dragons of : " + i);
	}
	
	public int saveAllLoaded(){
		File file = new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator);
		if(!file.exists())
			file.mkdirs();
		
		int j = 0;
		
		for(UUID id : homeList.keySet()){
			DragonInfoContainer con = homeList.get(id);
			if(!con.isLoaded) continue;
			con.isLoaded = false;
			DragonStore.saveToPath(con.dragon);
			if(plugin.interactConfig().getConfig_pluginHandleLoads()) con.dragon.remove();
			j++;
		}
		
		return j;
	}
	
	public void loadContainer(){
		File dragonPath = new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator);
		File[] children = dragonPath.listFiles();
		if(children == null) return;
		
		int i = 0;
		for(File child : children){
			LimitedEnderDragonV131 dragon = DragonStore.loadFromFile(child.getPath());
			if(dragon != null){
				if(!dragon.spawn(false)){
					homeList.get(dragon.uniqueId).isLoaded = false;
					homeList.get(dragon.uniqueId).firstLoad = true;
					DragonStore.saveToPath(dragon);
				}
				i++;
			}
		}
		
		if(i != 0) plugin.log("Loaded " + i + " Dragon(s).");
	}
	
	public String loadDragonsInLoadedChunks(){
		int i = 0;
		String dragonPath = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
		
		List<UUID> toLoad = new LinkedList<UUID>();
		
		for(UUID id : homeList.keySet()){
			if(!homeList.get(id).isLoaded){
				Location location = homeList.get(id).location;
				if(location.getWorld().isChunkLoaded(getChunkX(location), getChunkY(location)))
					toLoad.add(id);
				i++;
			}
		}
		
		int j = 0;
		for(UUID id : toLoad){
			LimitedEnderDragonV131 dragon = DragonStore.loadFromFile(dragonPath + id);
			if(dragon == null) continue;
			dragon.spawn(false);
			j++;
		}
		
		return j + " of " + i;
	}

	public int sendAllDragonsHome() {
		int i = 0;
		
		for(UUID dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(container.dragon != null){
				container.flyingHome = true;
				i++;
			}
		}
		return i;
	}
	
	public boolean getFlyingHome(UUID id){
		DragonInfoContainer temp = homeList.get(id);
		return temp.flyingHome;
	}

	public Location getHomeByID(UUID id) {
		DragonInfoContainer temp = homeList.get(id);
		return temp.homeLocation;
	}

	public void setHomeID(UUID entityId, Location homelocation, Location location, boolean flyingHome, LimitedEnderDragonV131 dragon){
		if(plugin.interactConfig().getConfig_debugOutput()) 
			plugin.log("set ID: " + entityId);
		
		DragonInfoContainer temp = new DragonInfoContainer(entityId, location.clone(), homelocation.clone(), flyingHome, true, dragon);
		homeList.put(entityId, temp);
	}
	
	public void setFlyingHome(UUID uid, boolean value){
		DragonInfoContainer temp = homeList.get(uid);
		temp.flyingHome = value;
	}
	
	public boolean containsID(UUID id){
		return homeList.containsKey(id);
	}
	
	public Set<UUID> getAllIDs(){
		return homeList.keySet();
	}

	public void loadDragonsInChunk(Chunk chunk) {
		for(UUID id : homeList.keySet()){
			DragonInfoContainer con = homeList.get(id);
			if(con.isLoaded) continue;
			if(locationIsInChunk(con.location, chunk)){
				if(plugin.interactConfig().getConfig_debugOutput()) 
					plugin.log("loading: " + con.ID);
				
				String path = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
				LimitedEnderDragonV131 dragon = DragonStore.loadFromFile(path + con.ID);
				dragon.spawn(con.firstLoad);
			}
		}
	}

	public void saveDragon(UUID entityId) {
		if(plugin.interactConfig().getConfig_debugOutput())
			plugin.log("Saving Dragon: " + entityId);
		DragonInfoContainer con = homeList.get(entityId);
		DragonStore.saveToPath(con.dragon);
		con.location = con.dragon.getLocation();
		con.isLoaded = false;
	}
	
	public int loaded(){
		int i = 0;
		for(UUID id : homeList.keySet()){
			if(homeList.get(id).isLoaded) i++;
		}
		
		return i;
	}
	
	public int count(){
		return homeList.size();
	}
	
	public LimitedEnderDragonV131 getDragonById(UUID id){
		DragonInfoContainer con = homeList.get(id);
		if(con == null || !con.isLoaded) return null;
		return con.dragon;
	}
	
	
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
	}
	
	public Location getPositionByID(UUID id){
		DragonInfoContainer con = homeList.get(id);
		if(con == null) return null;
		return con.location;
	}

	public boolean isLoaded(UUID id) {
		DragonInfoContainer con = homeList.get(id);
		if(con == null) return false;
		return con.isLoaded;
	}

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
	}
}
