package de.tobiyas.enderdragonsplus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class Listener_Plugins implements Listener{
	
	private EnderdragonsPlus plugin;
	
	public Listener_Plugins(){
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event){
		String pluginName = event.getPlugin().getName();
		plugin.interactBridgeController().setDTActive(pluginName, false);
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event){
		String pluginName = event.getPlugin().getName();
		plugin.interactBridgeController().setDTActive(pluginName, true);
	}

}
