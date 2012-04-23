/**
 * @author Toby
 *
 */
package de.tobiyas.enderdragonsplus.permissions;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.plugins.BukkitPermissionsPermissions;
import de.tobiyas.enderdragonsplus.permissions.plugins.GroupManagerPermissions;
import de.tobiyas.enderdragonsplus.permissions.plugins.OpPermissions;
import de.tobiyas.enderdragonsplus.permissions.plugins.PEXPermissions;
import de.tobiyas.enderdragonsplus.permissions.plugins.PermissionPlugin;
import de.tobiyas.enderdragonsplus.permissions.plugins.VaultPermissions;

public class PermissionManager{

	private PermissionPlugin permPlugin;
	private EnderdragonsPlus plugin;
	
	
	public PermissionManager(){
		plugin = EnderdragonsPlus.getPlugin();
		checkForPermissionsPlugin();
	}
	
	private void checkForPermissionsPlugin(){
		PermissionPlugin tempPlugin;
		try{
			tempPlugin = new VaultPermissions();
			if(tempPlugin.isActive()){
				permPlugin = tempPlugin;
				return;
			}
		}catch(NoClassDefFoundError e){}
		
		try{
			tempPlugin = new PEXPermissions();
			if(tempPlugin.isActive()){
				permPlugin = tempPlugin;
				return;
			}
		}catch(NoClassDefFoundError e){}
		
		try{
			tempPlugin = new GroupManagerPermissions();
			if(tempPlugin.isActive()){
				permPlugin = tempPlugin;
				return;
			}
		}catch(NoClassDefFoundError e){}
		
		try{
			tempPlugin = new BukkitPermissionsPermissions();
			if(tempPlugin.isActive()){
				permPlugin = tempPlugin;
				return;
			}
		}catch(NoClassDefFoundError e){}
		
		permPlugin = new OpPermissions();
		plugin.log("CRITICAL: No Permission-System hooked. Plugin will not work properly. " + 
					"Use one of the following Systems: Vault, PermissionsEx, GroupManager, BukkitPermissions. " + 
					"Using Op-Status as Permission.");	
	}

	
	/**
	 * The Check of Permissions on the inited Permission-System
	 * 
	 * @param player the Player to check
	 * @param permissionNode the String to check
	 * @return if the Player has Permissions
	 */
	private boolean checkPermissionsIntern(CommandSender sender, String permissionNode){
		if(sender == null) return false;
		return permPlugin.getPermissions(sender, permissionNode);
	}
	
	public boolean checkPermissions(CommandSender sender, String permissionNode){
		boolean perm = checkPermissionsIntern(sender, permissionNode);
		if(!perm)
			sender.sendMessage(ChatColor.RED + "You don't have Permissions!");
		
		return perm;
	}
	
	public boolean checkPermissionsSilent(CommandSender sender, String permissionNode){
		return checkPermissionsIntern(sender, permissionNode);
	}
	
	public ArrayList<String> getAllGroups(){
		return permPlugin.getGroups();
	}
	
	public String getGroupOfPlayer(Player player){
		return permPlugin.getGroupOfPlayer(player);
	}
	
	public String getPermissionsName(){
		return permPlugin.getName();
	}
	
}
