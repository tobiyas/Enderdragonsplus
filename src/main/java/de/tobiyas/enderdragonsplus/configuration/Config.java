package de.tobiyas.enderdragonsplus.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class Config
{
  private EnderdragonsPlus plugin;
 
  //Enderdragons general
  private double config_dragonMaxHealth;
  private double config_dragonHealtch;
  private int config_dropEXP;
  private double config_dragonDamage;
  private int config_maxHomeDistance;
  private int config_maxHomeDistanceSquared; //easier calculation
  @SuppressWarnings("unused")
  private boolean config_disableDragonHealthBar;
  private boolean config_dragonsSitDownIfInactive;
  
  //Fireballs
  private boolean config_dragonsSpitFireballs;
  private int config_dragonSpitFireballsEvery;
  private int config_dragonsSpitFireballsRange;
  private boolean config_disableFireballWorldDamage;
  private double config_fireballEntityDamage;
  private int config_fireballExplosionRadius;
  private int config_maxFireballTargets;
  private int config_fireballSetOnFireChance;
  private int config_fireballBurnTime;
  private boolean config_disableFireballRebounce;
  private double config_fireBallSpeedUp;
  
  //World damage
  private boolean config_disableEnderdragonBlockDamage;
  private boolean config_deactivateBlockExplosionEffect;
  private boolean config_deactivateDragonTemples;
  private String config_dragonTempleFile;
  
  //World handling
  private boolean config_replaceAllDragons;
  private boolean config_replaceOnTheFly;
  private boolean config_neverUnloadChunkWithED;
  private int config_ticksWhenOutOfRange;
  private boolean config_pluginHandleLoads;
  
  //Player interaction (Targeting)
  private int config_dragonUntargeting;
  private int config_maxFollowDistance;
  private int config_maxFollowDistanceSquared; //easier calculation
  private boolean config_ignorePlayerGamemode1;
  private boolean config_disableTargetImun;
  private boolean config_dragonsAreHostile;
  
  //Player information
  private boolean config_informPlayerDamageDone;
  private boolean config_informPlayerDamageTaken;
  
  //Anouncements
  private boolean config_anounceDragonKill;
  private String config_dragonKillMessage;
  private String config_dragonKillMessageToWorlds;
  private boolean config_anounceDragonSpawning;
  private String config_dragonSpawnMessage;
  
  //Debug + API layer
  private boolean config_debugOutput;
  private boolean config_fireBukkitEvents;
  private boolean config_uploadMetrics;
  private boolean config_uploadErrors;
  
 

  public Config(EnderdragonsPlus plugin)
  {
    this.plugin = plugin;
    reloadConfiguration();
  }

  private void setupConfiguration() {
	FileConfiguration config = this.plugin.getConfig();
	config.addDefault("deactivateDragonTemples", Boolean.valueOf(true));
	config.addDefault("maxPlayerFollowDistance", Integer.valueOf(100));
	config.addDefault("maxHomeDistance", Integer.valueOf(500));
	config.addDefault("includeHeight", Boolean.valueOf(false));
	config.addDefault("dropEXP", Integer.valueOf(200));
	config.addDefault("debugOutputs", Boolean.valueOf(false));
	config.addDefault("replaceAllDragons", Boolean.valueOf(true));
	config.addDefault("dragonSpawnHealth", Double.valueOf(200));
	config.addDefault("dragonDamage", Double.valueOf(10));
	config.addDefault("ignorePlayerGamemode1", Boolean.valueOf(true));
	config.addDefault("fireBukkitEvents", Boolean.valueOf(false));
	config.addDefault("disableEnderdragonBlockDamage", Boolean.valueOf(true));
	config.addDefault("neverUnloadChunkWithED", Boolean.valueOf(false));
	config.addDefault("ticksPerSecondWhenOutOfRange", Integer.valueOf(25));
	config.addDefault("pluginHandlesDragonLoads", Boolean.valueOf(true));
	config.addDefault("dragonMaxHealth", Double.valueOf(200));
	config.addDefault("informPlayerDamageDone", Boolean.valueOf(true));
	config.addDefault("informPlayerDamageTaken", Boolean.valueOf(true));
	config.addDefault("replaceOnTheFly", Boolean.valueOf(true));
	config.addDefault("dragonsSpitFireballs", Boolean.valueOf(true));
	config.addDefault("dragonsSpitFireballsEveryXSeconds", Integer.valueOf(7));
	config.addDefault("dragonsSpitFireballsRange", Integer.valueOf(100));
	config.addDefault("anounceDragonSpawning", Boolean.valueOf(true));
	config.addDefault("dragonSpawnMessage", "'&aA new Dragon has spawned at: x: ~x~ y: ~y~ z: ~z~ on world: ~world~.'");
	config.addDefault("dragonKillMessage", "'&aPlayer &b~player_kill~ &ahas killed a dragon! (done &1~player_kill_dmg~ &adamage)'");
	config.addDefault("anounceDragonKill", true);
	config.addDefault("dragonKillMessageToWorlds", "~all~");
	config.addDefault("deactivateBlockExplodeEffect", Boolean.valueOf(false));
	config.addDefault("disableFireballWorldDamage", Boolean.valueOf(true));
	config.addDefault("fireballEntityDamage", Double.valueOf(4));
	config.addDefault("fireballExplosionRadius", Integer.valueOf(5));
	config.addDefault("maxFireballTargets", Integer.valueOf(2));
	config.addDefault("fireballSetOnFireChance", Integer.valueOf(50));
	config.addDefault("fireballBurnTime", Integer.valueOf(5));
	config.addDefault("disableDragonHealthBar", Boolean.valueOf(false));
	config.addDefault("dragonUntargeting", Integer.valueOf(60));
	config.addDefault("dragonsAreHostile", Boolean.valueOf(false));
	config.addDefault("disableTargetImun", Boolean.valueOf(false));
	config.addDefault("disableFireballRebounce", Boolean.valueOf(false));
	config.addDefault("fireBallSpeedUp", 1D);
	config.addDefault("dragonTempleFile", "STDTemple.schematic");
	config.addDefault("dragonsSitDownIfInactive", false);
	config.addDefault("uploadMetrics", true);
	config.addDefault("uploadErrors", false);	

	config.options().copyDefaults(true);
  }

  private void reloadConfiguration()
  {
    this.plugin.reloadConfig();
    setupConfiguration();

    this.config_deactivateDragonTemples = this.plugin.getConfig().getBoolean("deactivateDragonTemples", true);
    this.config_maxFollowDistance = this.plugin.getConfig().getInt("maxPlayerFollowDistance", 100);
    this.config_maxHomeDistance = this.plugin.getConfig().getInt("maxHomeDistance", 500);
    this.config_dropEXP = this.plugin.getConfig().getInt("dropEXP", 2000);
    this.config_debugOutput = this.plugin.getConfig().getBoolean("debugOutputs", false);
    this.config_replaceAllDragons = this.plugin.getConfig().getBoolean("replaceAllDragons", true);
    this.config_dragonHealtch = this.plugin.getConfig().getDouble("dragonSpawnHealth", 200);
    this.config_dragonDamage = this.plugin.getConfig().getDouble("dragonDamage", 10);
    this.config_ignorePlayerGamemode1 = this.plugin.getConfig().getBoolean("ignorePlayerGamemode1");
    this.config_fireBukkitEvents = this.plugin.getConfig().getBoolean("fireBukkitEvents");
    this.config_disableEnderdragonBlockDamage = this.plugin.getConfig().getBoolean("disableEnderdragonBlockDamage", true);
    this.config_neverUnloadChunkWithED = this.plugin.getConfig().getBoolean("neverUnloadChunkWithED", false);
    this.config_ticksWhenOutOfRange = this.plugin.getConfig().getInt("ticksPerSecondWhenOutOfRange", 25);
    this.config_pluginHandleLoads = this.plugin.getConfig().getBoolean("pluginHandlesDragonLoads", true);
    this.config_dragonMaxHealth = this.plugin.getConfig().getDouble("dragonMaxHealth", 200);
    this.config_informPlayerDamageDone = this.plugin.getConfig().getBoolean("informPlayerDamageDone", true);
    this.config_informPlayerDamageTaken = this.plugin.getConfig().getBoolean("informPlayerDamageTaken", true);
    this.config_replaceOnTheFly = this.plugin.getConfig().getBoolean("replaceOnTheFly", true);
    this.config_dragonsSpitFireballs = this.plugin.getConfig().getBoolean("dragonsSpitFireballs", true);
    this.config_dragonSpitFireballsEvery = this.plugin.getConfig().getInt("dragonsSpitFireballsEveryXSeconds", 7);
    this.config_dragonsSpitFireballsRange = this.plugin.getConfig().getInt("dragonsSpitFireballsRange", 100);
    this.config_anounceDragonSpawning = this.plugin.getConfig().getBoolean("anounceDragonSpawning", true);
    this.config_deactivateBlockExplosionEffect = this.plugin.getConfig().getBoolean("deactivateBlockExplodeEffect", false);
    this.config_dragonSpawnMessage = this.plugin.getConfig().getString("dragonSpawnMessage", "'&aA new Dragon has spawned at: x: ~x~ y: ~y~ z: ~z~ on world: ~world~.'");
    this.config_dragonKillMessage = this.plugin.getConfig().getString("dragonKillMessage", "'&aPlayer &b~player_kill~ &ahas killed a dragon! (done &1~player_kill_dmg~ &adamage)'");   
    this.config_anounceDragonKill = this.plugin.getConfig().getBoolean("anounceDragonKill", true);
    this.config_dragonKillMessageToWorlds = this.plugin.getConfig().getString("dragonKillMessageToWorlds", "~all~");    
    this.config_disableFireballWorldDamage = this.plugin.getConfig().getBoolean("disableFireballWorldDamage", true);
    this.config_dragonTempleFile = this.plugin.getConfig().getString("dragonTempleFile", "'STDTemple.schematic'");

    this.config_fireballEntityDamage = this.plugin.getConfig().getDouble("fireballEntityDamage", 4);
    this.config_fireballExplosionRadius = this.plugin.getConfig().getInt("fireballExplosionRadius", 5);
    this.config_maxFireballTargets = this.plugin.getConfig().getInt("maxFireballTargets", 2);
    this.config_fireballSetOnFireChance = this.plugin.getConfig().getInt("fireballSetOnFireChance", 50);
    this.config_fireballBurnTime = this.plugin.getConfig().getInt("fireballBurnTime", 5);
    this.config_disableFireballRebounce = this.plugin.getConfig().getBoolean("disableFireballRebounce", false);
    this.config_fireBallSpeedUp = this.plugin.getConfig().getDouble("fireBallSpeedUp", 1.0D);

    this.config_disableDragonHealthBar = this.plugin.getConfig().getBoolean("disableDragonHealthBar", false);

    this.config_dragonUntargeting = this.plugin.getConfig().getInt("dragonUntargeting", 60);
    this.config_dragonsAreHostile = this.plugin.getConfig().getBoolean("dragonsAreHostile", false);
    this.config_disableTargetImun = this.plugin.getConfig().getBoolean("disableTargetImun", false);
    this.config_dragonsSitDownIfInactive = this.plugin.getConfig().getBoolean("dragonsSitDownIfInactive", false);

    if (this.plugin.getConfig().getInt("maxHomeDisatance", -1) != -1) {
      this.config_maxHomeDistance = this.plugin.getConfig().getInt("maxHomeDisatance", 500);
    }

    this.config_maxFollowDistanceSquared = (this.config_maxFollowDistance * this.config_maxFollowDistance);
    this.config_maxHomeDistanceSquared = (this.config_maxHomeDistance * this.config_maxHomeDistance);
    this.config_uploadMetrics = this.plugin.getConfig().getBoolean("uploadMetrics");
    this.config_uploadErrors = this.plugin.getConfig().getBoolean("uploadErrors");
  }

  public void reload() {
	  reloadConfiguration();
  }

  public boolean getConfig_deactivateDragonTemples() {
    return this.config_deactivateDragonTemples;
  }

  public int getConfig_maxFollowDistance() {
    return this.config_maxFollowDistance;
  }

  public int getConfig_maxFollowDistanceSquared() {
    return this.config_maxFollowDistanceSquared;
  }

  public int getConfig_maxHomeDistance() {
    return this.config_maxHomeDistance;
  }

  public int getConfig_maxHomeDistanceSquared() {
    return this.config_maxHomeDistanceSquared;
  }

  public int getConfig_dropEXP() {
    return this.config_dropEXP;
  }

  public boolean getConfig_debugOutput() {
    return this.config_debugOutput;
  }

  public boolean getConfig_replaceAllDragons() {
    return this.config_replaceAllDragons;
  }

  public double getConfig_dragonHealth() {
    return this.config_dragonHealtch;
  }

  public double getConfig_dragonDamage() {
    return this.config_dragonDamage;
  }

  public boolean getConfig_ignorePlayerGamemode1() {
    return this.config_ignorePlayerGamemode1;
  }

  public boolean getConfig_fireBukkitEvents() {
    return this.config_fireBukkitEvents;
  }

  public boolean getConfig_disableEnderdragonBlockDamage() {
    return this.config_disableEnderdragonBlockDamage;
  }

  public boolean getConfig_neverUnloadChunkWithED() {
    return this.config_neverUnloadChunkWithED;
  }

  public int getConfig_ticksPerSeconds() {
    return this.config_ticksWhenOutOfRange;
  }

  public boolean getConfig_pluginHandleLoads() {
    return this.config_pluginHandleLoads;
  }

  public double getConfig_dragonMaxHealth() {
    return this.config_dragonMaxHealth;
  }

  public boolean getconfig_informPlayerDamageDone() {
    return this.config_informPlayerDamageDone;
  }

  public boolean getConfig_informPlayerDamageTaken() {
    return this.config_informPlayerDamageTaken;
  }

  public boolean getConfig_replaceOnTheFly() {
    return this.config_replaceOnTheFly;
  }

  public boolean getConfig_dragonsSpitFireballs() {
    return this.config_dragonsSpitFireballs;
  }

  public boolean getConfig_anounceDragonSpawning() {
    return this.config_anounceDragonSpawning;
  }

  public int getConfig_dragonSpitFireballsEvery() {
    return this.config_dragonSpitFireballsEvery;
  }

  public int getConfig_dragonsSpitFireballsRange() {
    return this.config_dragonsSpitFireballsRange;
  }

  public boolean getConfig_deactivateBlockExplosionEffect() {
    return this.config_deactivateBlockExplosionEffect;
  }

  public String getConfig_dragonSpawnMessage() {
    return this.config_dragonSpawnMessage;
  }

  public boolean getConfig_disableFireballWorldDamage() {
    return this.config_disableFireballWorldDamage;
  }

  public double getConfig_fireballEntityDamage() {
    return this.config_fireballEntityDamage;
  }

  public int getConfig_fireballExplosionRadius() {
    return this.config_fireballExplosionRadius;
  }

  public int getConfig_fireballSetOnFire() {
    return this.config_fireballSetOnFireChance;
  }

  public int getConfig_fireballBurnTime() {
    return this.config_fireballBurnTime;
  }

  public int getConfig_maxFireballTargets() {
    return this.config_maxFireballTargets;
  }

  public boolean getConfig_disableDragonHealthBar() {
	//TODO think how to disable it.
    return false;
  }

  public int getConfig_dragonUntargeting() {
    return this.config_dragonUntargeting;
  }

  public boolean getConfig_dragonsAreHostile() {
    return this.config_dragonsAreHostile;
  }

  public boolean getConfig_disableTargetImun() {
    return this.config_disableTargetImun;
  }

  public boolean getConfig_disableFireballRebounce() {
    return this.config_disableFireballRebounce;
  }

  public double getConfig_FireBallSpeedUp() {
    return this.config_fireBallSpeedUp;
  }

	public String getConfig_dragonTempleFile() {
		return config_dragonTempleFile;
	}
	
	public boolean getConfig_dragonsSitDownIfInactive() {
		return config_dragonsSitDownIfInactive;
	}
	
	public boolean getConfig_anounceDragonKill() {
		return config_anounceDragonKill;
	}
	
	public String getConfig_dragonKillMessage(){
		return config_dragonKillMessage;
	}
	
	public String getConfig_dragonKillMessageToWorlds(){
		return config_dragonKillMessageToWorlds;
	}
	
	public boolean getConfig_uploadMetrics() {
		return config_uploadMetrics;
	}

	public boolean getConfig_uploadErrors() {
		return config_uploadErrors;
	}
}