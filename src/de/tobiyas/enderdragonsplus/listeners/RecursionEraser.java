package de.tobiyas.enderdragonsplus.listeners;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class RecursionEraser implements Runnable {
	
	private EnderdragonsPlus plugin;
	
	public RecursionEraser(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 1);
	}

	@Override
	public void run() {
		Listener_Entity.recDepth = 0;
	}

}
