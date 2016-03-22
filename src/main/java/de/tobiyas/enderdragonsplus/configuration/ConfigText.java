package de.tobiyas.enderdragonsplus.configuration;

import de.tobiyas.enderdragonsplus.util.Consts;

public class ConfigText {
	
	private static String PlayerInteraction(){
		return 
			  "#	REGION: PlayerInteraction\n"
			+ "#SUBREGION: EnderDragons.Targeting\n"
			+ "#This value defines every x seconds the dragon searches a new Target.\n"
			+ "#If set to a very low value (exp: 0-20) the Dragon will always be around the player and not fly off again.\n"
			+ "#default: 60\n"
			+ "dragonUntargeting: 60\n\n"
		
			+ "#At which distance the EnderDragon should not target a player any more\n"
			+ "#default: 100\n"
			+ "maxPlayerFollowDistance: 100\n\n"

			+ "#if a Dragon should ignore player in Creative mode\n"
			+ "#default: true\n"
			+ "ignorePlayerGamemode1: true\n\n"
		
			+ "#Disables targeting imunity through Permission: 'edplus.targeting.ignore'\n"
			+ "#default: false\n"
			+ "disableTargetImun: false\n\n"
		
			+ "#if a Dragon should ONLY attack Players attacking him\n"
			+ "#or he attacks everyone in range. (true = everyone, false = only attackers)\n"
			+ "#default: false\n"
			+ "dragonsAreHostile: false\n\n"
		
			+ "#   /SUBREGION: EnderDragons.Targeting\n\n\n"

			+ "#Informes the Player how much damage he has done to an EnderDragon\n"
			+ "#default: true\n"
			+ "informPlayerDamageDone: true\n\n"

			+ "#Informes the Player how much damage he has gotten from an EnderDragon\n"
			+ "#default: true\n"
			+ "informPlayerDamageTaken: true\n"
			+ "#	/REGION: PlayerInteraction\n\n\n";
	}
	
	private static String WorldHandling(){
		return 
			  "#	REGION: WorldHandling\n"
			+ "#replaces all other spawned Dragons with EnderDragons of this Plugin\n"
			+ "#NOTICE: It will never replace special Dragons (as in DragonTravel, RideThaDragon)\n"
			+ "#default: true\n"
			+ "replaceAllDragons: true\n\n"
		
			+ "#replaces all dragons On the fly when loaded.\n"
			+ "#This gets triggered for example when there are dragons active,\n"
			+ "#before the plugin is active\n"
			+ "replaceOnTheFly: true\n\n"

			+ "#if set to true the plugin will cancle all unloads of EnderDragons\n"
			+ "#default: false\n"
			+ "neverUnloadChunkWithED: false\n\n"

			+ "#The ticks an EnderDragon will do when out of player sight (0 = off)\n"
			+ "#default: 25\n"
			+ "ticksPerSecondWhenOutOfRange: 25\n\n"

			+ "#Tells the plugin to handle Dragon Loading and Unloading\n"
			+ "#NOTICE: If false, Dragons may disappear because of illigal Minecraft moveEvents\n"
			+ "#default: true\n"
			+ "pluginHandlesDragonLoads: true\n"
			+ "#	/REGION: WorldHandling\n\n\n";
	}
	
	private static String WorldDamage(){
		return 
			  "#REGION: WorldDamage\n"
			+ "#Disables the BlockDamage of an EnderDragon fly through them\n"
			+ "#default: true\n"
			+ "disableEnderdragonBlockDamage: true\n\n"
		
			+ "#Disables Explosion effect when dragon hits block\n"
			+ "#default: false \n"
			+ "deactivateBlockExplodeEffect: false\n\n"

			+ "#Disables the Temple when an EnderDragon dies\n"
			+ "#default: true\n"
			+ "deactivateDragonTemples: true\n\n"
		
			+ "#This option is to replace the Dragon temple spawned at death. \n"
			+ "#It only works with the Plugin: Worldedit. \n"
			+ "#The Scematic has to be in plugins/EnderdragonsPlus/temples/ \n"
			+ "# IMPORTANT!!!! The total String has to be in ' ' symbols or it will not load correct. (see default below)\n"
			+ "#default: 'STDTemple.schematic'\n"
			+ "dragonTempleFile: 'STDTemple.schematic'\n"
			+ "#	/REGION: WorldDamage\n\n\n";
	}
	
	private static String Riding(){
		return 
			  "#  SUBREGION: EnderDragons.Riding\n\n"
			+ "#If this is set to true, the Collision of a dragon (while riding) is only checked for 1 block where the dragon is moving to.\n"
			+ "#If set to false, the collosion is checked against the BoundingBox around the Dragon.\n"
			+ "#default: true'\n"
			+ "useSoftRidingCollision: true\n\n"
		
			+ "#This is the maximal Riding speed that can be passed to /edpRide <speed>\n"
			+ "#default: 4'\n"
			+ "maxRidingSpeed: 4\n\n"
			+ "#  /SUBREGION: EnderDragons.Riding\n\n";
	}
	
	
	private static String Waypoints(){
		return
			  "#  REGION: Waypoints\n\n"
				
			+ "#This is from where to where the mesh is generated.\n"
			+ "#The generation is for each world on it's own. When a world is not present, it has no mesh.\n"
			+ "#The first argument is the World, the rest is from where to where.\n"
			+ "#World#FromX#ToX#FromZ#ToZ.\n"
			+ "#default: -'world#-2000#2000#-2000#2000''\n"
			+ "meshGeneration:\n\n"
			+ "-'world#-2000#2000#-2000#2000'\n\n"
			+ "#  /REGION: Waypoints\n\n";
	}
	
	private static String Announcements(){
		return
			  "#SUBREGION: EnderDragons.Announcements\n"
			+ "#Defines if it is anounces publicaly if a dragon spawns\n"
			+ "#default: true\n"
			+ "anounceDragonSpawning: true\n\n"
		
			+ "#If Dragon Spawn announce is activated, this is the message.\n"
			+ "#Keywords: ~x~ is the X position. ~y~ is the y position.\n"
			+ "#~z~ is the z position. ~world~ is the world\n"
			+ "#~age~ is the age Name of the dragon"
			+ "#default: '&aA new ~age~ Dragon has spawned at: x: ~x~ y: ~y~ z: ~z~ on world: ~world~.'\n"
			+ "dragonSpawnMessage: '&aA new ~age~ Dragon has spawned at: x: ~x~ y: ~y~ z: ~z~ on world: ~world~.'\n\n"
			+ "#Defines if it is anounces publicaly if a dragon dies\n"
			+ "#default: true\n"
			+ "anounceDragonKill: true\n\n"
		
			+ "#If Dragon death announce is activated, this is the message.\n"
			+ "#Keywords: ~player_kill~ = name of player who killed the dragon\n"
			+ "# ~player_kill_dmg~ = total damage done by the player killing the dragon\n"
			+ "# ~age~ the age name of the Dragon\n"
			+ "#default: '&aPlayer &b~player_kill~ &ahas killed a ~age~ dragon! (done &1~player_kill_dmg~ &adamage).'\n"
			+ "dragonKillMessage: '&aPlayer &b~player_kill~ &ahas killed a ~age~ dragon! (done &1~player_kill_dmg~ &adamage).'\n\n"
		
			+ "#To which worlds the Dragon kill message should be announced.\n"
			+ "#Keywords: ~current~ = the current world of the dragon\n"
			+ "# ~all~ = all worlds\n"
			+ "# IMPORTANT!!!! The total String has to be in ' ' symbols or it will not load correct. (see example below)\n"
			+ "#example: 'world,~current~,gmworld'\n"
			+ "#default: '~all~'\n"
			+ "dragonKillMessageToWorlds: '~all~'\n\n"
			+ "#	/SUBREGION: EnderDragons.Announcements\n";
	}
	
	private static String Fireball(){
		return 
			  "#SUBREGION: EnderDragons.Fireballs\n"
			+ "#Defines if the dragon can spit FireBalls\n"
			+ "#default: true\n"
			+ "dragonsSpitFireballs: true\n\n"
		
			+ "#The time the enderdragon can spit Fireballs (every X seconds)\n"
			+ "#default: 7\n"
			+ "dragonsSpitFireballsEveryXSeconds: 7\n\n"
		
			+ "#The maximum distance the enderdragon can spit Fireballs\n"
			+ "#default: 100\n"
			+ "dragonsSpitFireballsRange: 100\n\n"
		
			+ "#when a Fireball explodes, damage to the World is prevented\n"
			+ "#default: true\n"
			+ "disableFireballWorldDamage: true\n\n"
		
			+ "#The damage a fireball does, when it hits an Entity\n"
			+ "#This can be a double (number with comma)\n"
			+ "#default: 4\n"
			+ "fireballEntityDamage: 4\n\n"
		
			+ "#The radius a Fireball will explode in\n"
			+ "#default: 8\n"
			+ "fireballExplosionRadius: 8\n\n"
		
			+ "#The maximum amount of Fireballs a Dragon can spit at once.\n"
			+ "#default: 2\n"
			+ "maxFireballTargets: 2\n\n"
		
			+ "#When a Fireball explodes, it sets all entities with a % chance on fire\n"
			+ "#default: 50\n"
			+ "fireballSetOnFireChance: 50\n\n"
		
			+ "#When a Fireball sets a Player on fire, it burns this time (in seconds)\n"
			+ "#default: 5\n"
			+ "fireballBurnTime: 5\n\n"
		
			+ "#Disables rebounce, when Fireball is hit by Something\n"
			+ "#default: false\n"
			+ "disableFireballRebounce: false\n\n"
		
			+ "#Varies the speedup of the fireballs. > 1 is faster, < 1 is slower\n"
			+ "#CAUTION!!! only vary by small values (like 1.05, 1.10, 0.95, ....)\n"
			+ "#If you vary to much it is impossible to evade!\n"
			+ "#default: 1.00\n"
			+ "fireBallSpeedUp: 1.00\n"
		
			+ "#  /SUBREGION: EnderDragons.Fireballs\n\n";
	}
	
	private static String Debug(){
		return 
			  "#	REGION: Debug&API layer\n"
			+ "#Gives Debug informations for Developers\n"
			+ "#NOTICE: If true, this will MASSIVLY spam your Minecraft Log!\n"
			+ "#default: false\n"
			+ "debugOutputs: false\n\n"

			+ "#If true it will fire Bukkit Events (as EntityChangeTarget)\n"
			+ "#default: false\n"
			+ "fireBukkitEvents: false\n\n"
		
			+ "#If true, the plugin will upload Stacktraces of errors to a database for easier debuging\n"
			+ "#default: false\n"
			+ "uploadErrors: false\n\n"
		
			+ "#If true, the plugin will upload Metrics to 'http://mcstats.org'\n"
			+ "#default: true\n"
			+ "uploadMetrics: true\n"
			+ "#	/REGION: Debug&API layer";
	}
	
	
	private static String Enderdragons(){
		return 
			  "#	REGION: EnderDragons\n"
			+ "#The maximal Life an EnderDragon comes to Life\n"
			+ "#When set to -1, the Health will not be set (for compability to other plugins regulating health)\n"
			+ "#This can be a double (number with comma)\n"
			+ "#default: 200\n"
			+ "dragonMaxHealth: 200\n\n"

			+ "#The actual Life an EnderDragon comes to Life\n"
			+ "#This can be a double (number with comma)\n"
			+ "#default: 200\n"
			+ "dragonSpawnHealth: 200\n\n"

			+ "#Exp an EnderDragon will drop when he dies\n"
			+ "#default: 20000\n"
			+ "dropEXP: 20000\n\n"

			+ "#Damage an EnderDragon does when attacking\n"
			+ "#This can be a double (number with comma)\n"
			+ "#default: 10\n"
			+ "dragonDamage: 10\n\n"

			+ "#Tells the EnderDragon at what distance to return to his home\n"
			+ "#default: 500\n"
			+ "maxHomeDistance: 500\n\n"
		
			+ "#If the dragon healthbar should be hidden (Not working Yet!!!)\n"
			+ "#default: false\n"
			+ "disableDragonHealthBar: false\n\n"
			
			+ "#If dragons have no targets they will sit down\n"
			+ "#default: false\n"
			+ "dragonsSitDownIfInactive: false\n\n"
		
		
			+ Fireball()
			+ Announcements()
			+ Riding()
			+ "#	/REGION: EnderDragons\n\n\n";
	}
	
	public static String getConfig() {
		return
			  "#Config for Enderdragons+\n"
			+ "#TemplateVersion " + Consts.ConfigVersion + "\n\n"

			+ Enderdragons()
			+ WorldDamage()
			+ WorldHandling()
			+ Waypoints()
			+ PlayerInteraction()
			+ Debug();
	}
}
