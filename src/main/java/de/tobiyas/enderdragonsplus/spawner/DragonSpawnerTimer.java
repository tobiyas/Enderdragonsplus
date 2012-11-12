package de.tobiyas.enderdragonsplus.spawner;

import org.bukkit.Bukkit;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class DragonSpawnerTimer implements Runnable{

	private EnderdragonsPlus plugin;
	private DragonSpawnerManager dragonSpawnerManager;
	private boolean active;
	
	
	public DragonSpawnerTimer(DragonSpawnerManager dragonSpawnerManager){
		this.dragonSpawnerManager = dragonSpawnerManager;
		plugin = EnderdragonsPlus.getPlugin();
		active = true;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	public void stopTimer(){
		active = false;
	}
	
	public void continueTimer(){
		active = true;
	}
	
	@Override
	public void run() {
		if(active)
			dragonSpawnerManager.tick();
		
	}

}
