package de.tobiyas.enderdragonsplus.permissions;

public class PermissionNode {
	private final static String pluginPre = "edplus.";
	
	//Command Permissions
	public final static String createEnderDragon = pluginPre + "create";
	public final static String killEnderDragons = pluginPre + "kill";
	public final static String commandBack = pluginPre + "commandback";
	public final static String reloadConfig = pluginPre + "reloadconfig";
	public final static String unloadAll = pluginPre + "unloadall";
	public final static String loadAll = pluginPre + "loadall";
	public final static String info = pluginPre + "info";
	
	//Respawner Permissions
	public final static String createRespawner = pluginPre + "respawner.create";
	public final static String removeRespawner = pluginPre + "respawner.remove";
	public final static String clearRespawners = pluginPre + "respawner.clear";
	public final static String infoRespawners = pluginPre + "respawner.info";
	public final static String debugRespawners = pluginPre + "respawner.debug";
	public final static String seeRespawners = pluginPre + "respawner.see";
	public final static String portRespawner = pluginPre + "respawner.port";
	
	//RidingPermissions
	public final static String ride = pluginPre + "riding.ride";
	public final static String shootFireballs = pluginPre + "riding.fireball";

	//Targeting Permissions
	public final static String getIgnored = pluginPre + "targeting.ignore";
}
