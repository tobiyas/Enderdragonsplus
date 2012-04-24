/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
 package de.tobiyas.enderdragonsplus.configuration;

 
 import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

 
 public class Config{
	private boolean config_fireBukkitEvents;

	private EnderdragonsPlus plugin;

	private boolean config_active;
	private boolean config_includeHeight;
	private int config_maxFollowDistance;
	private int config_maxHomeDistance;
	private int config_dropEXP;
	private boolean config_replaceAllDragons;
	private boolean config_debugOutput;
	private int config_dragonDamage;

	private int config_dragonHealtch;
	
	private boolean config_ignorePlayerGamemode1;

	private boolean config_disableEnderdragonBlockDamage;

	private boolean config_neverUnloadChunkWithED;


	public Config(EnderdragonsPlus plugin){
		this.plugin = plugin;
		setupConfiguration();
		reloadConfiguration();
	}

	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();
		config.options().header("active: true means it will not spawn.");

		config.addDefault("deactivateDragonTemples", true);
		config.addDefault("maxPlayerFollowDistance", 100);
		config.addDefault("maxHomeDisatance", 500);
		config.addDefault("includeHeight", false);
		config.addDefault("dropEXP", 200);
		config.addDefault("debugOutputs", false);
		config.addDefault("replaceAllDragons", true);
		config.addDefault("dragonHealth", 200);
		config.addDefault("dragonDamage", 10);
		config.addDefault("ignorePlayerGamemode1", true);
		config.addDefault("fireBukkitEvents", false);
		config.addDefault("disableEnderdragonBlockDamage", true);
		config.addDefault("neverUnloadChunkWithED", false);
		
		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_active = config.getBoolean("deactivateDragonTemples", true);
		config_maxFollowDistance = config.getInt("maxPlayerFollowDistance", 100);
		config_maxHomeDistance = config.getInt("maxHomeDisatance", 500);
		config_includeHeight = config.getBoolean("includeHeight", false);
		config_dropEXP = config.getInt("dropEXP", 2000);
		config_debugOutput = config.getBoolean("debugOutputs", false);
		config_replaceAllDragons = config.getBoolean("replaceAllDragons", true);
		config_dragonHealtch = config.getInt("dragonHealth", 200);
		config_dragonDamage = config.getInt("dragonDamage", 10);
		config_ignorePlayerGamemode1 = config.getBoolean("ignorePlayerGamemode1");
		config_fireBukkitEvents = config.getBoolean("fireBukkitEvents");
		config_disableEnderdragonBlockDamage = config.getBoolean("disableEnderdragonBlockDamage", true);
		config_neverUnloadChunkWithED = config.getBoolean("neverUnloadChunkWithED", false);
	}
	
	public void reload(){
		reloadConfiguration();
	}
	
	
	public boolean getconfig_active(){
		return config_active;
	}
	
	public int getconfig_maxFollowDistance(){
		return config_maxFollowDistance;
	}
	
	public int getconfig_maxHomeDistance(){
		return config_maxHomeDistance;
	}
	
	public boolean getconfig_includeHeight(){
		return config_includeHeight;
	}
	
	public int getconfig_dropEXP(){
		return config_dropEXP;
	}
	
	public boolean getconfig_debugOutput(){
		return config_debugOutput;
	}
	
	public boolean getconfig_replaceAllDragons(){
		return config_replaceAllDragons;
	}
	
	public int getconfig_dragonHealth(){
		return config_dragonHealtch;
	}

	public int getconfig_dragonDamage() {
		return config_dragonDamage;
	}
	
	public boolean getconfig_ignorePlayerGamemode1(){
		return config_ignorePlayerGamemode1;
	}

	public boolean getconfig_fireBukkitEvents() {
		return config_fireBukkitEvents;
	}

	public boolean getconfig_disableEnderdragonBlockDamage() {
		return config_disableEnderdragonBlockDamage;
	}
	
	public boolean getconfig_neverUnloadChunkWithED(){
		return config_neverUnloadChunkWithED;
	}

}
