package de.tobiyas.enderdragonsplus.datacontainer;

import org.bukkit.Bukkit;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class CleanRunner implements Runnable {

	private Container container;
	
	public CleanRunner(Container container){
		this.container = container;
		EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, 10);
	}
	
	@Override
	public void run() {
		container.cleanRun();
	}

}
