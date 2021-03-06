package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public class Container {

	private HashMap<UUID, LimitedED> dragonList;
	private EnderdragonsPlus plugin;
	
	public Container(){
		plugin = EnderdragonsPlus.getPlugin();
		dragonList = new HashMap<UUID, LimitedED>();
		new CleanRunner(this);
	}
	
	public int cleanRun(){
		List<UUID> toDelete = new ArrayList<UUID>();
		
		for(UUID id : dragonList.keySet()){
			LimitedED dragon = dragonList.get(id);
			if(dragon == null || dragon.getBukkitEntity().isDead())
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
	
	public int killEnderDragons(Location location, int range, boolean instantRemove){
		ArrayList<UUID> toRemove = new ArrayList<UUID>();
		for(UUID dragonID : dragonList.keySet()){
			LimitedED dragon = dragonList.get(dragonID);
			if(dragon != null && (range == 0 || dragon.isInRange(location, range))){
				toRemove.add(dragonID);
				if(instantRemove){
					dragon.remove();
				}else{
					dragon.damage(DamageCause.MAGIC, 100000);
				}
			}
		}
		
		if(toRemove.size() == 0)
			return 0;
		
		for(UUID dragonID : toRemove)
			dragonList.remove(dragonID);
		
		return toRemove.size();
	}
	

	public int sendAllDragonsHome() {
		int i = 0;
		
		for(UUID dragonID : dragonList.keySet()){
			LimitedED dragon = dragonList.get(dragonID);
			if(dragon != null){
				dragon.forceFlyHome(true);
				i++;
			}
		}
		return i;
	}
	
	public boolean containsID(UUID id){
		return dragonList.containsKey(id);
	}
	
	public Set<UUID> getAllIDs(){
		return dragonList.keySet();
	}

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
	
	
	public LimitedED getDragonById(UUID id){
		return dragonList.get(id);
	}
	
	public Location getPositionByID(UUID id){
		LimitedED dragon = dragonList.get(id);
		if(dragon == null) return null;
		return dragon.getLocation();
	}

	public boolean isLoaded(UUID id) {
		LimitedED dragon = dragonList.get(id);
		
		boolean isNotDeleted = dragon != null;
		boolean isLoaded = false;
		try{
			isLoaded = dragon.getLocation().getChunk().isLoaded();
		}catch(Exception exp){}
		return isNotDeleted && isLoaded;
	}
	
	public void registerDragon(LimitedED dragon){
		dragonList.put(dragon.getUUID(), dragon);
	}
	
	public void unregisterDragon(UUID dragonId){
		dragonList.remove(dragonId);
	}

	public List<LimitedED> getAllDragons() {
		return new LinkedList<LimitedED>(dragonList.values());
	}
}
