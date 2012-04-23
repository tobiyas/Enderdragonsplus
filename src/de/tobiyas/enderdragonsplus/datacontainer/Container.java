package de.tobiyas.enderdragonsplus.datacontainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;

public class Container {

	private HashMap<Integer, DragonInfoContainer> homeList;
	private EnderdragonsPlus plugin;
	
	public Container(){
		plugin = EnderdragonsPlus.getPlugin();
		homeList = new HashMap<Integer, DragonInfoContainer>();
		new CleanRunner(this);
	}
	
	public int cleanRun(){
		List<Integer> toDelete = new ArrayList<Integer>();
		
		for(int id : homeList.keySet()){
			DragonInfoContainer dragonContainer = homeList.get(id);
			if(dragonContainer.dragon == null || !dragonContainer.dragon.isAlive())
				if(dragonContainer.isLoaded) toDelete.add(id);
		}
		
		if(toDelete.size() == 0) return 0;
		
		for(int dragon : toDelete){
			DragonInfoContainer container = homeList.get(dragon);
			if(!container.isLoaded)
				new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon." + dragon).delete();
			homeList.remove(dragon);
			if(plugin.interactConfig().getconfig_debugOutput())
				plugin.log("removed Dragon!");
		}
		
		return toDelete.size();
	}
	
	public int killEnderDragons(Location location, int range){
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		boolean considerRange = plugin.interactConfig().getconfig_includeHeight();
		
		for(int dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(container.isLoaded){
				if(container.dragon != null && container.dragon.isInRange(location, range, considerRange)){
					toRemove.add(dragonID);
					container.dragon.remove();
				}
			}else{
				
			}
		}
		
		if(toRemove.size() == 0)
			return 0;
		
		for(int dragonID : toRemove)
			homeList.remove(dragonID);
		
		return toRemove.size();
	}
	
	public void saveContainer(){
		File file = new File(plugin.getDataFolder() + File.separator + "tempDragons" + File.separator);
		if(!file.exists())
			file.mkdirs();
		
		int i = 0;
		int j = 0;
		
		for(int dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(!container.dragon.saveToPath()) j++;
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
		
		for(int id : homeList.keySet()){
			DragonInfoContainer con = homeList.get(id);
			if(!con.isLoaded) continue;
			con.isLoaded = false;
			con.dragon.saveToPath();
			con.dragon.remove();
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
			LimitedEnderDragon dragon = LimitedEnderDragon.loadFromFile(child.getPath());
			if(dragon != null){
				dragon.spawn();
				i++;
			}
		}
		
		if(i != 0) plugin.log("Loaded " + i + " Dragons.");
	}
	
	public String loadDragonsInLoadedChunks(){
		int i = 0;
		String dragonPath = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
		
		List<Integer> toDelete = new LinkedList<Integer>();
		
		for(int id : homeList.keySet()){
			if(!homeList.get(id).isLoaded){
				if(homeList.get(id).location.getChunk().isLoaded()){
					toDelete.add(id);
					i++;
				}
			}
		}
		
		int j = 0;
		for(int id : toDelete){
			LimitedEnderDragon dragon = LimitedEnderDragon.loadFromFile(dragonPath + id);
			if(dragon == null) continue;
			dragon.spawn();
			homeList.remove(id);
			j++;
		}
		
		return j + " of " + i;
	}

	public int sendAllDragonsHome() {
		int i = 0;
		
		for(int dragonID : homeList.keySet()){
			DragonInfoContainer container = homeList.get(dragonID);
			if(container.dragon != null){
				container.flyingHome = true;
				i++;
			}
		}
		return i;
	}
	
	public boolean getFlyingHome(int id){
		DragonInfoContainer temp = homeList.get(id);
		return temp.flyingHome;
	}

	public Location getHomeByID(int id) {
		DragonInfoContainer temp = homeList.get(id);
		return temp.homeLocation;
	}

	public void setHomeID(int entityId, Location homelocation, Location location, boolean flyingHome, LimitedEnderDragon dragon){
		if(plugin.interactConfig().getconfig_debugOutput()) 
			plugin.log("set ID: " + entityId);
		
		DragonInfoContainer temp = new DragonInfoContainer(entityId, location.clone(), homelocation.clone(), flyingHome, true, dragon);
		homeList.put(entityId, temp);
	}
	
	public void setFlyingHome(int id, boolean value){
		DragonInfoContainer temp = homeList.get(id);
		temp.flyingHome = value;
	}
	
	public boolean containsID(int id){
		return homeList.containsKey(id);
	}
	
	public Set<Integer> getAllIDs(){
		return homeList.keySet();
	}

	public void loadDragonsInChunk(Chunk chunk) {
		for(int id : homeList.keySet()){
			DragonInfoContainer con = homeList.get(id);
			if(con.isLoaded) continue;
			if(con.location.getChunk().equals(chunk)){
				if(plugin.interactConfig().getconfig_debugOutput()) 
					plugin.log("loading: " + con.ID);
				
				String path = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon.";
				LimitedEnderDragon dragon = LimitedEnderDragon.loadFromFile(path + con.ID);
				dragon.spawn();
				homeList.remove(con.ID);
			}
		}
	}

	public void saveDragon(int entityId) {
		DragonInfoContainer con = homeList.get(entityId);
		con.dragon.saveToPath();
		con.location = con.dragon.getLocation();
		con.dragon.remove();
		con.isLoaded = false;
		
		if(plugin.interactConfig().getconfig_debugOutput()) 
			plugin.log("saving: " + con.ID);
	}
	
	public int loaded(){
		int i = 0;
		for(int id : homeList.keySet()){
			if(homeList.get(id).isLoaded) i++;
		}
		
		return i;
	}
	
	public int count(){
		return homeList.size();
	}
}
