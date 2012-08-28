/*
 * EnderdragonsPlus - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
 package de.tobiyas.enderdragonsplus.configuration;

 
 import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

 
 public class Config{
	private boolean config_fireBukkitEvents;

	private EnderdragonsPlus plugin;

	private boolean config_deactivateDragonTemples;
	private int config_maxFollowDistance;
	private int config_maxFollowDistanceSquared;	
	private int config_maxHomeDistance;
	private int config_maxHomeDistanceSquared;
	private int config_dropEXP;
	private boolean config_replaceAllDragons;
	private boolean config_debugOutput;
	private int config_dragonDamage;
	private int config_dragonHealtch;
	private boolean config_ignorePlayerGamemode1;
	private boolean config_disableEnderdragonBlockDamage;
	private boolean config_neverUnloadChunkWithED;
	private int config_ticksWhenOutOfRange;
	private boolean config_pluginHandleLoads;
	private int config_dragonMaxHealth;
	@SuppressWarnings("unused") //TODO remove when found solution
	private boolean config_disableDragonHealthBar;
	
	private boolean config_dragonsSpitFireballs;
	private int config_dragonSpitFireballsEvery;
	private int config_dragonsSpitFireballsRange;
	private boolean config_disableFireballWorldDamage;
	
	private int config_fireballEntityDamage;
	private int config_fireballExplosionRadius;
	private int config_maxFireballTargets;
	private int config_fireballSetOnFireChance;
	private int config_fireballBurnTime;
	
	
	private boolean config_replaceOnTheFly;
	private boolean config_deactivateBlockExplosionEffect;

	private boolean config_informPlayerDamageDone;
	private boolean config_informPlayerDamageTaken;
	private boolean config_anounceDragonSpawning;
	private String config_dragonSpawnMessage;
	
	private int config_dragonUntargeting;
	private boolean config_dragonsAreHostile;

	public Config(EnderdragonsPlus plugin){
		this.plugin = plugin;
		reloadConfiguration();
	}

	private void setupConfiguration(){
		plugin.getConfig().addDefault("deactivateDragonTemples", true);
		plugin.getConfig().addDefault("maxPlayerFollowDistance", 100);
		plugin.getConfig().addDefault("maxHomeDistance", 500);
		plugin.getConfig().addDefault("includeHeight", false);
		plugin.getConfig().addDefault("dropEXP", 200);
		plugin.getConfig().addDefault("debugOutputs", false);
		plugin.getConfig().addDefault("replaceAllDragons", true);
		plugin.getConfig().addDefault("dragonSpawnHealth", 200);
		plugin.getConfig().addDefault("dragonDamage", 10);
		plugin.getConfig().addDefault("ignorePlayerGamemode1", true);
		plugin.getConfig().addDefault("fireBukkitEvents", false);
		plugin.getConfig().addDefault("disableEnderdragonBlockDamage", true);
		plugin.getConfig().addDefault("neverUnloadChunkWithED", false);
		plugin.getConfig().addDefault("ticksPerSecondWhenOutOfRange", 25);
		plugin.getConfig().addDefault("pluginHandlesDragonLoads", true);
		plugin.getConfig().addDefault("dragonMaxHealth", 200);
		plugin.getConfig().addDefault("informPlayerDamageDone", true);
		plugin.getConfig().addDefault("informPlayerDamageTaken", true);
		plugin.getConfig().addDefault("replaceOnTheFly", true);
		plugin.getConfig().addDefault("dragonsSpitFireballs", true);
		plugin.getConfig().addDefault("dragonsSpitFireballsEveryXSeconds", 7);
		plugin.getConfig().addDefault("dragonsSpitFireballsRange", 100);
		plugin.getConfig().addDefault("anounceDragonSpawning", true);
		plugin.getConfig().addDefault("dragonSpawnMessage", "&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.");
		plugin.getConfig().addDefault("deactivateBlockExplodeEffect", false);
		plugin.getConfig().addDefault("disableFireballWorldDamage", true);
		plugin.getConfig().addDefault("fireballEntityDamage", 4);
		plugin.getConfig().addDefault("fireballExplosionRadius", 5);
		plugin.getConfig().addDefault("maxFireballTargets", 2);
		plugin.getConfig().addDefault("fireballSetOnFireChance", 50);
		plugin.getConfig().addDefault("fireballBurnTime", 5);
		plugin.getConfig().addDefault("disableDragonHealthBar", false);
		plugin.getConfig().addDefault("dragonUntargeting", 60);
		plugin.getConfig().addDefault("dragonsAreHostile", "false");
		
		plugin.getConfig().options().copyDefaults(true);
	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		setupConfiguration();

		config_deactivateDragonTemples = plugin.getConfig().getBoolean("deactivateDragonTemples", true);
		config_maxFollowDistance = plugin.getConfig().getInt("maxPlayerFollowDistance", 100);
		config_maxHomeDistance = plugin.getConfig().getInt("maxHomeDistance", 500);
		config_dropEXP = plugin.getConfig().getInt("dropEXP", 2000);
		config_debugOutput = plugin.getConfig().getBoolean("debugOutputs", false);
		config_replaceAllDragons = plugin.getConfig().getBoolean("replaceAllDragons", true);
		config_dragonHealtch = plugin.getConfig().getInt("dragonSpawnHealth", 200);
		config_dragonDamage = plugin.getConfig().getInt("dragonDamage", 10);
		config_ignorePlayerGamemode1 = plugin.getConfig().getBoolean("ignorePlayerGamemode1");
		config_fireBukkitEvents = plugin.getConfig().getBoolean("fireBukkitEvents");
		config_disableEnderdragonBlockDamage = plugin.getConfig().getBoolean("disableEnderdragonBlockDamage", true);
		config_neverUnloadChunkWithED = plugin.getConfig().getBoolean("neverUnloadChunkWithED", false);
		config_ticksWhenOutOfRange = plugin.getConfig().getInt("ticksPerSecondWhenOutOfRange", 25);
		config_pluginHandleLoads = plugin.getConfig().getBoolean("pluginHandlesDragonLoads", true);
		config_dragonMaxHealth = plugin.getConfig().getInt("dragonMaxHealth", 200);
		config_informPlayerDamageDone = plugin.getConfig().getBoolean("informPlayerDamageDone", true);
		config_informPlayerDamageTaken = plugin.getConfig().getBoolean("informPlayerDamageTaken", true);
		config_replaceOnTheFly = plugin.getConfig().getBoolean("replaceOnTheFly", true);
		config_dragonsSpitFireballs = plugin.getConfig().getBoolean("dragonsSpitFireballs", true);
		config_dragonSpitFireballsEvery = plugin.getConfig().getInt("dragonsSpitFireballsEveryXSeconds", 7);
		config_dragonsSpitFireballsRange = plugin.getConfig().getInt("dragonsSpitFireballsRange", 100);
		config_anounceDragonSpawning = plugin.getConfig().getBoolean("anounceDragonSpawning", true);
		config_deactivateBlockExplosionEffect = plugin.getConfig().getBoolean("deactivateBlockExplodeEffect", false);
		config_dragonSpawnMessage = plugin.getConfig().getString("dragonSpawnMessage", "&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.");
		config_disableFireballWorldDamage = plugin.getConfig().getBoolean("disableFireballWorldDamage", true);
		
		config_fireballEntityDamage = plugin.getConfig().getInt("fireballEntityDamage", 4);
		config_fireballExplosionRadius = plugin.getConfig().getInt("fireballExplosionRadius", 5);
		config_maxFireballTargets = plugin.getConfig().getInt("maxFireballTargets", 2);
		config_fireballSetOnFireChance = plugin.getConfig().getInt("fireballSetOnFireChance", 50);
		config_fireballBurnTime = plugin.getConfig().getInt("fireballBurnTime", 5);
		
		config_disableDragonHealthBar = plugin.getConfig().getBoolean("disableDragonHealthBar", false);
		
		config_dragonUntargeting = plugin.getConfig().getInt("dragonUntargeting", 60);
		config_dragonsAreHostile = plugin.getConfig().getBoolean("dragonsAreHostile", false);
		
		//stay compatible to old versions
		if(plugin.getConfig().getInt("maxHomeDisatance", -1) != -1)
			config_maxHomeDistance = plugin.getConfig().getInt("maxHomeDisatance", 500);
	
		//Square Cals
		config_maxFollowDistanceSquared = config_maxFollowDistance * config_maxFollowDistance;
		config_maxHomeDistanceSquared = config_maxHomeDistance * config_maxHomeDistance;
	}
	
	public void reload(){
		reloadConfiguration();
	}
	
	public boolean getConfig_deactivateDragonTemples(){
		return config_deactivateDragonTemples;
	}
	
	public int getConfig_maxFollowDistance(){
		return config_maxFollowDistance;
	}
	
	public int getConfig_maxFollowDistanceSquared(){
		return config_maxFollowDistanceSquared;
	}
	
	public int getConfig_maxHomeDistance(){
		return config_maxHomeDistance;
	}
	
	public int getConfig_maxHomeDistanceSquared(){
		return config_maxHomeDistanceSquared;
	}
	
	public int getConfig_dropEXP(){
		return config_dropEXP;
	}
	
	public boolean getConfig_debugOutput(){
		return config_debugOutput;
	}
	
	public boolean getConfig_replaceAllDragons(){
		return config_replaceAllDragons;
	}
	
	public int getConfig_dragonHealth(){
		return config_dragonHealtch;
	}

	public int getConfig_dragonDamage() {
		return config_dragonDamage;
	}
	
	public boolean getConfig_ignorePlayerGamemode1(){
		return config_ignorePlayerGamemode1;
	}

	public boolean getConfig_fireBukkitEvents() {
		return config_fireBukkitEvents;
	}

	public boolean getConfig_disableEnderdragonBlockDamage() {
		return config_disableEnderdragonBlockDamage;
	}
	
	public boolean getConfig_neverUnloadChunkWithED(){
		return config_neverUnloadChunkWithED;
	}
	
	public int getConfig_ticksPerSeconds(){
		return config_ticksWhenOutOfRange;
	}
	
	public boolean getConfig_pluginHandleLoads(){
		return config_pluginHandleLoads;
	}

	public int getConfig_dragonMaxHealth() {
		return config_dragonMaxHealth;
	}
	
	public boolean getconfig_informPlayerDamageDone(){
		return config_informPlayerDamageDone;
	}
	
	public boolean getConfig_informPlayerDamageTaken(){
		return config_informPlayerDamageTaken;
	}
	
	public boolean getConfig_replaceOnTheFly(){
		return config_replaceOnTheFly;
	}
	
	public boolean getConfig_dragonsSpitFireballs(){
		return config_dragonsSpitFireballs;
	}
	
	public boolean getConfig_anounceDragonSpawning(){
		return config_anounceDragonSpawning;
	}
	
	public int getConfig_dragonSpitFireballsEvery(){
		return config_dragonSpitFireballsEvery;
	}
	
	public int getConfig_dragonsSpitFireballsRange(){
		return config_dragonsSpitFireballsRange;
	}

	public boolean getConfig_deactivateBlockExplosionEffect() {
		return config_deactivateBlockExplosionEffect;
	}
	
	public String getConfig_dragonSpawnMessage(){
		return config_dragonSpawnMessage;
	}
	
	public boolean getConfig_disableFireballWorldDamage(){
		return config_disableFireballWorldDamage;
	}
	
	public int getConfig_fireballEntityDamage(){
		return config_fireballEntityDamage;
	}
	
	public int getConfig_fireballExplosionRadius(){
		return config_fireballExplosionRadius;
	}
	
	public int getConfig_fireballSetOnFire(){
		return config_fireballSetOnFireChance;
	}
	
	public int getConfig_fireballBurnTime(){
		return config_fireballBurnTime;
	}
	
	public int getConfig_maxFireballTargets(){
		return config_maxFireballTargets;
	}

	public boolean getConfig_disableDragonHealthBar() {
		//return config_disableDragonHealthBar;
		return false; //TODO try implementing!
	}
	
	public int getConfig_dragonUntargeting(){
		return config_dragonUntargeting;
	}
	
	public boolean getConfig_dragonsAreHostile(){
		return config_dragonsAreHostile;
	}
}
