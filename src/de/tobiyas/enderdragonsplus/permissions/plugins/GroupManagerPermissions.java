package de.tobiyas.enderdragonsplus.permissions.plugins;

import java.util.ArrayList;
import java.util.Collection;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class GroupManagerPermissions implements PermissionPlugin {

	private EnderdragonsPlus plugin;
	private GroupManager groupManager;
	private boolean isActive;
	
	public GroupManagerPermissions(){
		plugin = EnderdragonsPlus.getPlugin();
		isActive = false;
		init();
	}
	
	
	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean getPermissions(CommandSender sender, String permissionNode) {
		if(!(sender instanceof Player)) return true;
		if(!isActive()) return false;
		
		Player player = (Player) sender;
		return hasPermissionGroupManager(player, permissionNode);
	}
	
	private boolean hasPermissionGroupManager(final Player base, final String node){
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return false;
		}
		return handler.has(base, node);
	}

	@Override
	public ArrayList<String> getGroups() {
		ArrayList<String> groups = new ArrayList<String>();
		if(!isActive()) return groups;
		
		Collection<Group> groupList = groupManager.getWorldsHolder().getDefaultWorld().getGroupList();
		
		for(Group group : groupList){
			groups.add(group.getName());
		}
		
		return groups;
	}

	@Override
	public void init() {
		isActive = initGM();
	}
	
	private boolean initGM(){
		try{
			GroupManager gm = (GroupManager) plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if(gm == null) return false;
			groupManager = gm;
		}catch(Exception e){
			return false;
		}
		
		return true;
	}


	@Override
	public String getGroupOfPlayer(Player player) {
		if(!isActive()) return "";
		
		Group group = groupManager.getWorldsHolder().getWorldData(player).getDefaultGroup();
		if(group == null) return "";
		
		return group.getName();
	}


	@Override
	public String getName() {
		return "GroupManager";
	}

}
