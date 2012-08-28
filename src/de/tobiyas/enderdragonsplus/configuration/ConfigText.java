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
		
		text += "#Defines if it is anounces publicaly if a dragon spawns\n";
		text += "#default: true\n";
		text += "anounceDragonSpawning: true\n\n";
		
		text += "#If announce is activated, this is the message.\n";
		text += "#{x} is the X position. {y} is the y position. {z} is the z position. {world} is the world\n";
		text += "#default: '&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.'\n";
		text += "dragonSpawnMessage: '&aA new Dragon has spawned at: x: {x} y: {y} z: {z} on world: {world}.'\n\n";
		
		text += "#If the dragon healthbar should be hidden (Not working Yet!!!)\n";
		text += "#default: false\n";
		text += "disableDragonHealthBar: false\n\n";
		
		
		text += "#SUBREGION: EnderDragons.Fireballs\n";
		text += "#Defines if the dragon can spit FireBalls\n";
		text += "#default: true\n";
		text += "dragonsSpitFireballs: true\n\n";
		
		text += "#The time the enderdragon can spit Fireballs (every X seconds)\n";
		text += "#default: 7\n";
		text += "dragonsSpitFireballsEveryXSeconds: true\n\n";
		
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
		
		text += "#  /SUBREGION: EnderDragons.Fireballs\n";
		text += "#	/REGION: EnderDragons\n\n\n";


		text += "#REGION: WorldDamage\n";
		text += "#Disables the BlockDamage of an EnderDragon fly through them\n";
		text += "#default: true\n";
		text += "disableEnderdragonBlockDamage: true\n\n";
		
		text += "#Disables Explosion effect when dragon hits block\n";
		text += "#default: false \n";
		text += "deactivateBlockExplodeEffect: false\n";

		text += "#Disables the Temple when an EnderDragon dies\n";
		text += "#default: true\n";
		text += "deactivateDragonTemples: true\n";
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
		text += "#If set to a very low value (exp: 0-20) the Dragon will always be around the player and not fly off again.";
		text += "#default: 60\n";
		text += "dragonUntargeting: 60\n\n";		
		
		text += "#At which distance the EnderDragon should not target a player any more\n";
		text += "#default: 100\n";
		text += "maxPlayerFollowDistance: 100\n\n";

		text += "#if a Dragon should ignore player in Creative mode\n";
		text += "#default: true\n";
		text += "ignorePlayerGamemode1: true\n\n";
		
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
		text += "fireBukkitEvents: false\n";
		text += "#	/REGION: Debug&API layer";
		
		return text;
	}
}
