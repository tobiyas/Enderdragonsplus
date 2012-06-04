package de.tobiyas.enderdragonsplus.configuration;

public class ConfigText {

	public static String getConfig(){
		String text = "";
		
		text += "#Config for Enderdragons+\n";
		text += "#TemplateVersion 1.1\n\n";

		text += "#	REGION: EnderDragons\n";
		text += "#The maximal Life an EnderDragon comes to Life\n";
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
		text += "maxHomeDisatance: 500\n";
		text += "#	/REGION: EnderDragons\n\n\n";


		text += "#REGION: WorldDamage\n";
		text += "#Disables the BlockDamage of an EnderDragon fly through them\n";
		text += "#default: true\n";
		text += "disableEnderdragonBlockDamage: true\n\n";

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
		text += "#At which distance the EnderDragon should not target a player any more\n";
		text += "#default: 100\n";
		text += "maxPlayerFollowDistance: 100\n\n";

		text += "#if a Dragon should ignore player in Creative mode\n";
		text += "#default: true\n";
		text += "ignorePlayerGamemode1: true\n\n";

		text += "#Informes the Player how much damage he has done to an EnderDragon\n";
		text += "#default: true\n";
		text += "informPlayerDamageDone: true\n\n";

		text += "#Informes the Player how much damage he has gotten from an EnderDragon\n";
		text += "#default: true\n";
		text += "informPlayerDamageTaken: true\n";
		text += "#	/REGION: PlayerInteraction\n\n\n";

		text += "#	REGION: Calculation\n";
		text += "#Includes the height at calculations\n";
		text += "#default: false\n";
		text += "includeHeight: false\n";
		text += "#	/REGION: Calculation\n\n\n";

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
