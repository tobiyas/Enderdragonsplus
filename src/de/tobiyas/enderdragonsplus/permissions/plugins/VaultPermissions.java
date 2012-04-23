package de.tobiyas.enderdragonsplus.permissions.plugins;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class VaultPermissions implements PermissionPlugin {
	
	private EnderdragonsPlus plugin;
	private Permission vaultPermission;
	private boolean isActive;
	
	public VaultPermissions(){
		plugin = EnderdragonsPlus.getPlugin();
		init();
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean getPermissions(CommandSender sender, String permissionNode) {
		if(sender == null) return false;
		if(permissionNode == null || permissionNode == "") return false;
		if(!isActive()) return false;
		return vaultPermission.has(sender, permissionNode);
	}

	@Override
	public ArrayList<String> getGroups() {
		ArrayList<String> groups = new ArrayList<String>();
		if(!isActive()) return groups;
		
		for(String group : vaultPermission.getGroups()){
			groups.add(group);
		}
		return groups;
	}

	@Override
	public void init() {
		isActive = initVault();
	}
	

	/**
	 * Sets up vault
	 * 
	 * @return if it worked 
	 */
	private boolean initVault(){
		try{
			RegisteredServiceProvider<Permission> permissionProvider = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null)
				vaultPermission = permissionProvider.getProvider();
			}catch(Exception e){}
		
		return (vaultPermission != null);
	}

	@Override
	public String getGroupOfPlayer(Player player) {
		if(!isActive()) return "";
		return vaultPermission.getPrimaryGroup(player);		
	}

	@Override
	public String getName() {
		return "Vault";
	}

}
