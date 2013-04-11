package de.tobiyas.enderdragonsplus.configuration;

import de.tobiyas.enderdragonsplus.util.Consts;

public class ConfigText {
	
	public static String getConfig(){
		String text = "";
		
		text += "#Config for Enderdragons+\n";
		text += "#TemplateVersion " + Consts.ConfigVersion + "\n\n";

		text += "#	REGION: EnderDragons\n";
		text += "#The maximal Life an EnderDragon comes to Life\n";
		text += "#When set to -1, the Health will not be set (for compability to other plugins regulating health)\n";
		text += "#default: 200\n";
		text += "dragonMaxHealth: 200\n\n";

		text += "#The actual Life an EnderDragon comes to Life\n";
		text += "#default: 200\n";
		text += "dragonSpawnHealth: 200\n\n";

		text += "#Exp an EnderDragon will drop when he dies\n";
		text += "#default: 20000\n";
		text += "dropEXP: 20000\n\n";

		text += "#Damage an EnderDragon does when attacking\n";
		text += "#default: 10\n";
		text += "dragonDamage: 10\n\n";

		text += "#Tells the EnderDragon at what distance to return to his home\n";
		text += "#default: 500\n";
		text += "maxHomeDistance: 500\n\n";
		
		text += "#If the dragon healthbar should be hidden (Not working Yet!!!)\n";
		text += "#default: false\n";
		text += "disableDragonHealthBar: false\n\n";
		
		text += "#If dragons have no targets they will set down\n";
		text += "#default: false\n";
		text += "dragonsSitDownIfInactive: false\n\n";
		
		
		text += "#SUBREGION: EnderDragons.Fireballs\n";
		text += "#Defines if the dragon can spit FireBalls\n";
		text += "#default: true\n";
		text += "dragonsSpitFireballs: true\n\n";
		
		text += "#The time the enderdragon can spit Fireballs (every X seconds)\n";
		text += "#default: 7\n";
		text += "dragonsSpitFireballsEveryXSeconds: 7\n\n";
		
		text += "#The maximum distance the enderdragon can spit Fireballs\n";
		text += "#default: 100\n";
		text += "dragonsSpitFireballsRange: 100\n\n";
		
		text += "#when a Fireball explodes, damage to the World is prevented\n";
		text += "#default: true\n";
		text += "disableFireballWorldDamage: true\n\n";
		
		text += "#The damage a fireball does, when it hits an Entity\n";
		text += "#default: 4\n";
		text += "fireballEntityDamage: 4\n\n";
		
		text += "#The radius a Fireball will explode in\n";
		text += "#default: 8\n";
		text += "fireballExplosionRadius: 8\n\n";
		
		text += "#The maximum amount of Fireballs a Dragon can spit at once.\n";
		text += "#default: 2\n";
		text += "maxFireballTargets: 2\n\n";
		
		text += "#When a Fireball explodes, it sets all entities with a % chance on fire\n";
		text += "#default: 50\n";
		text += "fireballSetOnFireChance: 50\n\n";
		
		text += "#When a Fireball sets a Player on fire, it burns this time (in seconds)\n";
		text += "#default: 5\n";
		text += "fireballBurnTime: 5\n\n";
		
		text += "#Disables rebounce, when Fireball is hit by Something\n";
		text += "#default: false\n";
		text += "disableFireballRebounce: false\n\n";
		
		text += "#Varies the speedup of the fireballs. > 1 is faster, < 1 is slower\n";
		text += "#CAUTION!!! only vary by small values (like 1.05, 1.10, 0.95, ....)\n";
		text += "#If you vary to much it is impossible to evade!\n";
		text += "#default: 1.00\n";
		text += "fireBallSpeedUp: 1.00\n";
		
		text += "#  /SUBREGION: EnderDragons.Fireballs\n\n";
		
		text += "#SUBREGION: EnderDragons.Announcements\n";
				
		text += "#Defines if it is anounces publicaly if a dragon spawns\n";
		text += "#default: true\n";
		text += "anounceDragonSpawning: true\n\n";
		
		text += "#If Dragon Spawn announce is activated, this is the message.\n";
		text += "#Keywords: {x} is the X position. {y} is the y position.\n";
		text += "#{z} is the z position. {world} is the world\n";
		text += "#{age} is the age Name of the dragon";
		text += "#default: '&aA new {age} Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.'\n";
		text += "dragonSpawnMessage: &aA new {age} Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.\n\n";

		text += "#Defines if it is anounces publicaly if a dragon dies\n";
		text += "#default: true\n";
		text += "anounceDragonKill: true\n\n";
		
		text += "#If Dragon death announce is activated, this is the message.\n";
		text += "#Keywords: {player_kill} = name of player who killed the dragon\n";
		text += "# {player_kill_dmg} = total damage done by the player killing the dragon\n";
		text += "# {age} the age name of the Dragon\n";
		text += "#default: '&aPlayer &b{player_kill} &ahas killed a {age} dragon! (done &1{player_kill_dmg} &adamage).'\n";
		text += "dragonKillMessage: '&aPlayer &b{player_kill} &ahas killed a {age} dragon! (done &1{player_kill_dmg} &adamage).'\n\n";
		
		text += "#To which worlds the Dragon kill message should be announced.\n";
		text += "#Keywords: {current} = the current world of the dragon\n";
		text += "# {all} = all worlds\n";
		text += "# IMPORTANT!!!! The total String has to be in ' ' symbols or it will not load correct. (see example below)\n";
		text += "#example: 'world,{current},gmworld'\n";
		text += "#default: '{all}'\n";
		text += "dragonKillMessageToWorlds: '{all}'\n\n";
		
		text += "#	/SUBREGION: EnderDragons.Announcements\n";
		text += "#	/REGION: EnderDragons\n\n\n";


		text += "#REGION: WorldDamage\n";
		text += "#Disables the BlockDamage of an EnderDragon fly through them\n";
		text += "#default: true\n";
		text += "disableEnderdragonBlockDamage: true\n\n";
		
		text += "#Disables Explosion effect when dragon hits block\n";
		text += "#default: false \n";
		text += "deactivateBlockExplodeEffect: false\n\n";

		text += "#Disables the Temple when an EnderDragon dies\n";
		text += "#default: true\n";
		text += "deactivateDragonTemples: true\n\n";
		
		text += "#This option is to replace the Dragon temple spawned at death. \n";
		text += "#It only works with the Plugin: Worldedit. \n";
		text += "#The Scematic has to be in plugins/EnderdragonsPlus/temples/ \n";
		text += "# IMPORTANT!!!! The total String has to be in ' ' symbols or it will not load correct. (see default below)\n";
		text += "#default: 'STDTemple.schematic'\n";
		text += "dragonTempleFile: 'STDTemple.schematic'\n";
		text += "#	/REGION: WorldDamage\n\n\n";


		text += "#	REGION: WorldHandling\n";
		text += "#replaces all other spawned Dragons with EnderDragons of this Plugin\n";
		text += "#NOTICE: It will never replace special Dragons (as in DragonTravel, RideThaDragon)\n";
		text += "#default: true\n"; 
		text += "replaceAllDragons: true\n\n";
		
		text += "#replaces all dragons On the fly when loaded.\n";
		text += "#This gets triggered for example when there are dragons active,\n";
		text += "#before the plugin is active\n";
		text += "replaceOnTheFly: true\n\n";

		text += "#if set to true the plugin will cancle all unloads of EnderDragons\n";
		text += "#default: false\n";
		text += "neverUnloadChunkWithED: false\n\n";

		text += "#The ticks an EnderDragon will do when out of player sight (0 = off)\n";
		text += "#default: 25\n";
		text += "ticksPerSecondWhenOutOfRange: 25\n\n";

		text += "#Tells the plugin to handle Dragon Loading and Unloading\n";
		text += "#NOTICE: If false, Dragons may disappear because of illigal Minecraft moveEvents\n";
		text += "#default: true\n";
		text += "pluginHandlesDragonLoads: true\n";
		text += "#	/REGION: WorldHandling\n\n\n";


		text += "#	REGION: PlayerInteraction\n";
		
		text += "#SUBREGION: EnderDragons.Targeting\n";
		text += "#This value defines every x seconds the dragon searches a new Target.\n";
		text += "#If set to a very low value (exp: 0-20) the Dragon will always be around the player and not fly off again.\n";
		text += "#default: 60\n";
		text += "dragonUntargeting: 60\n\n";		
		
		text += "#At which distance the EnderDragon should not target a player any more\n";
		text += "#default: 100\n";
		text += "maxPlayerFollowDistance: 100\n\n";

		text += "#if a Dragon should ignore player in Creative mode\n";
		text += "#default: true\n";
		text += "ignorePlayerGamemode1: true\n\n";
		
		text += "#Disables targeting imunity through Permission: 'edplus.targeting.ignore'\n";
		text += "#default: false\n";
		text += "disableTargetImun: false\n\n";
		
		text += "#if a Dragon should ONLY attack Players attacking him\n";
		text += "#or he attacks everyone in range. (true = everyone, false = only attackers)\n";
		text += "#default: false\n";
		text += "dragonsAreHostile: false\n\n";
		
		text += "#   /SUBREGION: EnderDragons.Targeting\n\n\n";

		text += "#Informes the Player how much damage he has done to an EnderDragon\n";
		text += "#default: true\n";
		text += "informPlayerDamageDone: true\n\n";

		text += "#Informes the Player how much damage he has gotten from an EnderDragon\n";
		text += "#default: true\n";
		text += "informPlayerDamageTaken: true\n";
		text += "#	/REGION: PlayerInteraction\n\n\n";

		text += "#	REGION: Debug&API layer\n";
		text += "#Gives Debug informations for Developers\n";
		text += "#NOTICE: If true, this will MASSIVLY spam your Minecraft Log!\n";
		text += "#default: false\n";
		text += "debugOutputs: false\n\n";

		text += "#If true it will fire Bukkit Events (as EntityChangeTarget)\n";
		text += "#default: false\n";
		text += "fireBukkitEvents: false\n\n";
		
		text += "#If true, the plugin will upload Stacktraces of errors to a database for easier debuging\n";
		text += "#default: false\n";
		text += "uploadErrors: false\n\n";
		
		text += "#If true, the plugin will upload Metrics to 'http://mcstats.org'\n";
		text += "#default: true\n";
		text += "uploadMetrics: true\n";
		text += "#	/REGION: Debug&API layer";
		
		return text;
	}
}
