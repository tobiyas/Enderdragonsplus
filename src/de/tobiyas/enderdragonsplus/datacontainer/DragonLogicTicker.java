package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;

public class DragonLogicTicker implements Runnable {

	private EnderdragonsPlus plugin;
	private HashMap<Integer, Location> locs;
	
	public DragonLogicTicker(){
		plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 5, 5);
		locs = new HashMap<Integer, Location>();
	}
	
	@Override
	public void run() {
		int limit = plugin.interactConfig().getconfig_ticksPerSeconds();
		limit /= 5;
		
		boolean debugOutputs = plugin.interactConfig().getconfig_debugOutput();
		
		if(limit != 0){
			Set<Integer> ids = plugin.getContainer().getAllIDs();
			for(int id : ids){
				//plugin.log("Handling Dragon id: " + id);
				LimitedEnderDragon dragon = plugin.getContainer().getDragonById(id);
				
				if(dragon == null || !plugin.getContainer().isLoaded(id)){
					locs.remove(id);
					continue;
				}
				//plugin.log("loaded and != null");
				
				Location lastDragonLoc = locs.get(id);
				if(lastDragonLoc == null){
					locs.put(id, dragon.getLocation().clone());
					continue;
				}
				
				if(lastDragonLoc.equals(dragon.getLocation())){
					//plugin.log("doing F_");
					//plugin.log(dragon.getLocation().toString());
					
					for(int i = 0; i < limit; i++){
						if(dragon == null) break;
						dragon.F_();
					}
					
					//plugin.log(dragon.getLocation().toString());
				}
				
				locs.put(id, dragon.getLocation());
				
				if(debugOutputs)
					plugin.log("dragon Logic Calls: " + dragon.getLogicCalls());
			}
		}
	}

}
