package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonLogicTicker implements Runnable {

	private EnderdragonsPlus plugin;
	private HashMap<UUID, Location> locs;
	
	public DragonLogicTicker(){
		plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 5, 5);
		locs = new HashMap<UUID, Location>();
	}
	
	@Override
	public void run() {
		int limit = plugin.interactConfig().getConfig_ticksPerSeconds();
		limit /= 5;
		
		boolean debugOutputs = plugin.interactConfig().getConfig_debugOutput();
		
		if(limit > 0){
			Set<UUID> ids = plugin.getContainer().getAllIDs();
			for(UUID id : ids){
				LimitedEnderDragon dragon = plugin.getContainer().getDragonById(id);
				
				if(dragon == null || !dragon.isAlive()){
					locs.remove(id);
					continue;
				}
				
				Location lastDragonLoc = locs.get(id);
				if(lastDragonLoc == null){
					locs.put(id, dragon.getLocation().clone());
					continue;
				}
				
				double flyDistance = lastDragonLoc.distance(dragon.getLocation());					
				if(flyDistance < 0.01){
					for(int i = 0; i < limit; i++){
						try{
							if(dragon == null) break;
							dragon.l_();
						}catch(Exception exp)
						{}//prevent Craftbukkit from crashing
					}
					
				}
				
				locs.put(id, dragon.getLocation());
				
				if(debugOutputs){
					plugin.log("dragon Logic Calls: " + dragon.getLogicCalls());
				}
			}
		}

	}

}
