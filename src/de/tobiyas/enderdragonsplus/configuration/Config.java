package de.tobiyas.enderdragonsplus.configuration;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class Config
{
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
  @SuppressWarnings("unused")
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
  private boolean config_disableFireballRebounce;
  private double config_fireBallSpeedUp;
  
  private boolean config_replaceOnTheFly;
  private boolean config_deactivateBlockExplosionEffect;
  private boolean config_informPlayerDamageDone;
  private boolean config_informPlayerDamageTaken;
  private boolean config_anounceDragonSpawning;
  private String config_dragonSpawnMessage;
  
  private int config_dragonUntargeting;
  private boolean config_dragonsAreHostile;
  private boolean config_disableTargetImun;
  
  private String config_dragonTempleFile;
		  

  public Config(EnderdragonsPlus plugin)
  {
    this.plugin = plugin;
    reloadConfiguration();
  }

  private void setupConfiguration() {
    this.plugin.getConfig().addDefault("deactivateDragonTemples", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("maxPlayerFollowDistance", Integer.valueOf(100));
    this.plugin.getConfig().addDefault("maxHomeDistance", Integer.valueOf(500));
    this.plugin.getConfig().addDefault("includeHeight", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("dropEXP", Integer.valueOf(200));
    this.plugin.getConfig().addDefault("debugOutputs", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("replaceAllDragons", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("dragonSpawnHealth", Integer.valueOf(200));
    this.plugin.getConfig().addDefault("dragonDamage", Integer.valueOf(10));
    this.plugin.getConfig().addDefault("ignorePlayerGamemode1", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("fireBukkitEvents", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("disableEnderdragonBlockDamage", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("neverUnloadChunkWithED", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("ticksPerSecondWhenOutOfRange", Integer.valueOf(25));
    this.plugin.getConfig().addDefault("pluginHandlesDragonLoads", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("dragonMaxHealth", Integer.valueOf(200));
    this.plugin.getConfig().addDefault("informPlayerDamageDone", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("informPlayerDamageTaken", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("replaceOnTheFly", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("dragonsSpitFireballs", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("dragonsSpitFireballsEveryXSeconds", Integer.valueOf(7));
    this.plugin.getConfig().addDefault("dragonsSpitFireballsRange", Integer.valueOf(100));
    this.plugin.getConfig().addDefault("anounceDragonSpawning", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("dragonSpawnMessage", "'&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.'");
    this.plugin.getConfig().addDefault("deactivateBlockExplodeEffect", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("disableFireballWorldDamage", Boolean.valueOf(true));
    this.plugin.getConfig().addDefault("fireballEntityDamage", Integer.valueOf(4));
    this.plugin.getConfig().addDefault("fireballExplosionRadius", Integer.valueOf(5));
    this.plugin.getConfig().addDefault("maxFireballTargets", Integer.valueOf(2));
    this.plugin.getConfig().addDefault("fireballSetOnFireChance", Integer.valueOf(50));
    this.plugin.getConfig().addDefault("fireballBurnTime", Integer.valueOf(5));
    this.plugin.getConfig().addDefault("disableDragonHealthBar", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("dragonUntargeting", Integer.valueOf(60));
    this.plugin.getConfig().addDefault("dragonsAreHostile", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("disableTargetImun", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("disableFireballRebounce", Boolean.valueOf(false));
    this.plugin.getConfig().addDefault("fireBallSpeedUp", Double.valueOf(1.0D));
    this.plugin.getConfig().addDefault("dragonTempleFile", "STDTemple.schematic");

    this.plugin.getConfig().options().copyDefaults(true);
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
    this.config_dragonHealtch = this.plugin.getConfig().getInt("dragonSpawnHealth", 200);
    this.config_dragonDamage = this.plugin.getConfig().getInt("dragonDamage", 10);
    this.config_ignorePlayerGamemode1 = this.plugin.getConfig().getBoolean("ignorePlayerGamemode1");
    this.config_fireBukkitEvents = this.plugin.getConfig().getBoolean("fireBukkitEvents");
    this.config_disableEnderdragonBlockDamage = this.plugin.getConfig().getBoolean("disableEnderdragonBlockDamage", true);
    this.config_neverUnloadChunkWithED = this.plugin.getConfig().getBoolean("neverUnloadChunkWithED", false);
    this.config_ticksWhenOutOfRange = this.plugin.getConfig().getInt("ticksPerSecondWhenOutOfRange", 25);
    this.config_pluginHandleLoads = this.plugin.getConfig().getBoolean("pluginHandlesDragonLoads", true);
    this.config_dragonMaxHealth = this.plugin.getConfig().getInt("dragonMaxHealth", 200);
    this.config_informPlayerDamageDone = this.plugin.getConfig().getBoolean("informPlayerDamageDone", true);
    this.config_informPlayerDamageTaken = this.plugin.getConfig().getBoolean("informPlayerDamageTaken", true);
    this.config_replaceOnTheFly = this.plugin.getConfig().getBoolean("replaceOnTheFly", true);
    this.config_dragonsSpitFireballs = this.plugin.getConfig().getBoolean("dragonsSpitFireballs", true);
    this.config_dragonSpitFireballsEvery = this.plugin.getConfig().getInt("dragonsSpitFireballsEveryXSeconds", 7);
    this.config_dragonsSpitFireballsRange = this.plugin.getConfig().getInt("dragonsSpitFireballsRange", 100);
    this.config_anounceDragonSpawning = this.plugin.getConfig().getBoolean("anounceDragonSpawning", true);
    this.config_deactivateBlockExplosionEffect = this.plugin.getConfig().getBoolean("deactivateBlockExplodeEffect", false);
    this.config_dragonSpawnMessage = this.plugin.getConfig().getString("dragonSpawnMessage", "'&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.'");
    this.config_disableFireballWorldDamage = this.plugin.getConfig().getBoolean("disableFireballWorldDamage", true);
    this.config_dragonTempleFile = this.plugin.getConfig().getString("dragonTempleFile", "STDTemple.schematic");

    this.config_fireballEntityDamage = this.plugin.getConfig().getInt("fireballEntityDamage", 4);
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

    if (this.plugin.getConfig().getInt("maxHomeDisatance", -1) != -1) {
      this.config_maxHomeDistance = this.plugin.getConfig().getInt("maxHomeDisatance", 500);
    }

    this.config_maxFollowDistanceSquared = (this.config_maxFollowDistance * this.config_maxFollowDistance);
    this.config_maxHomeDistanceSquared = (this.config_maxHomeDistance * this.config_maxHomeDistance);
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

  public int getConfig_dragonHealth() {
    return this.config_dragonHealtch;
  }

  public int getConfig_dragonDamage() {
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

  public int getConfig_dragonMaxHealth() {
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

  public int getConfig_fireballEntityDamage() {
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

  public boolean getConfig_disableDragonHealthBar()
  {
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
}