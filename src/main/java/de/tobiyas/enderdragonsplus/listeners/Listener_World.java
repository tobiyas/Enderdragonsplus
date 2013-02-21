package de.tobiyas.enderdragonsplus.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class Listener_World implements Listener{

	private EnderdragonsPlus plugin;
	
	public Listener_World(){
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent event){
		Chunk chunk = event.getChunk();
		for(Entity entity : chunk.getEntities()){
			if(plugin.getContainer().containsID(entity.getUniqueId())){
				if(plugin.interactConfig().getConfig_neverUnloadChunkWithED()){
					event.setCancelled(true);
					return;
				}
				/*if(plugin.interactConfig().getConfig_pluginHandleLoads()){
					plugin.getContainer().saveDragon(entity.getUniqueId());
					entity.remove();
				}*/ //TODO check if works
			}
		}
	}
	
	//@EventHandler
	public void chunkLoad(ChunkLoadEvent event){
		if(!plugin.interactConfig().getConfig_pluginHandleLoads()) return;
		try{
			//Chunk chunk = event.getChunk();
			//plugin.getContainer().loadDragonsInChunk(chunk);
		}catch(Exception e){
			e.printStackTrace();
			if(plugin.interactConfig().getConfig_debugOutput()) plugin.log("DEBUG: Dragon in Chunk load error.");
		}
	}
}
