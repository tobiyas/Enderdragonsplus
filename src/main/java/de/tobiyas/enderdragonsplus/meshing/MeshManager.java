package de.tobiyas.enderdragonsplus.meshing;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.meshing.MeshGeneratorTask.MeshGenerationDoneCallback;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MeshManager implements MeshGenerationDoneCallback {

	public static final int FLIGHT_HEIGHT = 140;
	
	private static final int MAP_SIZE = 8200;
	
	
	/**
	 * The Destinations to use.
	 */
	private Map<String,Location> destinations = new HashMap<String, Location>();
	
	/**
	 * All MeshPoints present.
	 */
	private final List<MeshPoint> simpleAllPoints = new LinkedList<MeshPoint>();
	
	/**
	 * All MeshPoints present.
	 */
	private final List<MeshPoint> complexAllPoints = new LinkedList<MeshPoint>();
	
	
	public MeshManager() {
		generateConstantMesh();
		loadOrGenerateComplexMesh();
		
	}
	
	
	private void loadOrGenerateComplexMesh() {
		File file = new File(EnderdragonsPlus.getPlugin().getDataFolder(), "world_mesh");
		
		//Start generator!
		if(!file.exists()) {
			startComplexMeshGeneration();
			return;
		}
		
		//Start Loading:
		try{
			List<String> lines = FileUtils.readLines(file);
			
			for(String line : lines){
				String[] split = line.split(Pattern.quote("#"));
				if(split.length != 3){
					System.out.println("Line: " + line + " is broken in Mesh!");
					continue;
				}
				
				try{
					int x = Integer.parseInt(split[0]);
					int y = Integer.parseInt(split[1]);
					int z = Integer.parseInt(split[2]);
					
					complexAllPoints.add(new MeshPoint(x, y, z));
				}catch(NumberFormatException exp){}
			}
		}catch(Throwable exp){}
		
	}
	
	/**
	 * Starts a complete Mesh generation.
	 */
	public void startComplexMeshGeneration(){
		EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
		int everyTicks = 3;
		World world = Bukkit.getWorld("world");
		
		new MeshGeneratorTask(world, 0, 0, 8200, 8200, this)
			.runTaskTimer(plugin, everyTicks, everyTicks);
	}
	


	/**
	 * Generates the Mesh:
	 */
	private void generateConstantMesh(){
		simpleAllPoints.clear();
		
		for(int x = 0; x < MAP_SIZE; x+= 25){
			for(int z = 0; z < MAP_SIZE; z+= 25){
				simpleAllPoints.add(new MeshPoint(x,FLIGHT_HEIGHT,z));
			}			
		}
	}
	
	
	@Override
	public void meshGenerationDone(List<MeshPoint> points) {
		System.out.println("Mesh Generation Done!");
		long start = System.currentTimeMillis();
		
		this.complexAllPoints.clear();
		this.complexAllPoints.addAll(points);
		
		//Now save the mesh.
		File file = new File(EnderdragonsPlus.getPlugin().getDataFolder(), "world_mesh");
		try{
			List<String> toSave = new LinkedList<String>();
			for(MeshPoint point : points){
				toSave.add(point.getBlockX() + "#" + point.getBlockY() + "#" + point.getBlockZ());
			}
			
			FileUtils.writeLines(file, toSave);
		}catch(Throwable exp){}
		long took = System.currentTimeMillis() - start;
		System.out.println("Saving mesh took: " + took + "ms");
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
	 * Returns a copy of the Mesh.
	 * @return a copy of the mesh.
	 */
	public List<MeshPoint> getMeshCopy(boolean complex) {
		return new LinkedList<MeshPoint>(complex ? complexAllPoints : simpleAllPoints);
	}
	

}
