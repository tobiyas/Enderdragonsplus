package de.tobiyas.enderdragonsplus.meshing;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MeshManager {

	/**
	 * The Destinations to use.
	 */
	private Map<String,Location> destinations = new HashMap<String, Location>();
	
	
	public MeshManager() {
	}
	
	/**
	 * inits the Manager and loads up all Destinations.
	 */
	public void init(){
		this.destinations.clear();
		
		File file = new File(EnderdragonsPlus.getPlugin().getDataFolder(), "destinations.yml");
		if(!file.exists()) return;
		
		YAMLConfigExtended config = new YAMLConfigExtended(file).load();
		for(String key : config.getRootChildren()){
			Location loc = config.getLocation(key);
			if(loc != null) this.destinations.put(key, loc);
		}
	}
	
	/**
	 * Saves the Destinations.
	 */
	public void save(){
		File file = new File(EnderdragonsPlus.getPlugin().getDataFolder(), "destinations.yml");
		if(!file.exists()) try{ file.createNewFile(); }catch(Throwable exp){}
		
		YAMLConfigExtended config = new YAMLConfigExtended(file);
		for(Entry<String, Location> entry : destinations.entrySet()){
			config.set(entry.getKey(), entry.getValue());
		}
		
		config.save();
	}
	
	
	/**
	 * Adds a Destination to the Map.
	 * 
	 * @param destination to set
	 * @param location to set
	 */
	public void addDestination(String destination, Location location){
		destinations.put(destination, location);
		save();
	}
	
	/**
	 * Removes a Destination from the Map.
	 * 
	 * @param destination to remove
	 */
	public void removeDestination(String destination){
		if(destinations.remove(destination) != null) save();
	}
	
	/**
	 * Returns a Set of Destinations available.
	 * 
	 * @return a set of available destiantions.
	 */
	public Set<String> getDestinations(){
		return destinations.keySet();
	}
	
	/**
	 * Returns a Set of Destinations available.
	 * 
	 * @return a set of available destiantions.
	 */
	public Location getLocation(String destination){
		Location loc = destinations.get(destination);
		return loc == null ? null : loc.clone();
	}
	
	
	/**
	 * Generates a List of Vectors from the Start to the end.
	 * 
	 * @param start to generate from
	 * @param end to generate to.
	 * 
	 * @return the Way for the Way (pun intended)
	 */
	public List<Vector> getWay(Vector start, Vector end){
		List<Vector> way = new LinkedList<Vector>();
		way.add(start.clone());
		way.addAll(generateWayUp(start, 150));
		
		Vector up = start.clone().setY(150);
		Vector last = end.clone().setY(150);
		
		double dist = up.distance(last);
		Vector direction = last.clone().subtract(up.clone()).normalize().clone();
		while(dist > 50){
			way.add(up.add(direction.clone().multiply(25)).clone());
			dist -= 25;
		}
		
		way.add(last);
		way.add(end);
		return way;
	}
	
	
	
	private List<Vector> generateWayUp(Vector start, int height){
		List<Vector> way = new LinkedList<Vector>();
		
		return way;
	}
	

}
