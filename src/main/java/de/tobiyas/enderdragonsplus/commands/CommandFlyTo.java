package de.tobiyas.enderdragonsplus.commands;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController;
import de.tobiyas.enderdragonsplus.meshing.MeshFinderTask;
import de.tobiyas.enderdragonsplus.meshing.MeshPoint;
import de.tobiyas.enderdragonsplus.meshing.ToTargetController;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandFlyTo implements CommandExecutor {

	/**
	 * The Plugin to use.
	 */
	private final EnderdragonsPlus plugin;
	
	 
	public CommandFlyTo(EnderdragonsPlus plugin) {
		this.plugin = plugin;
		
		try{
			plugin.getCommand("edpflyto").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edpflyto");
		}
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		

		//Check for reload of the Mesh data:
		if(args.length == 1 && args[0].equalsIgnoreCase("reloadmesh") && sender.isOp()){
			plugin.getMeshManager().startComplexMeshGeneration();
			sender.sendMessage(ChatColor.GREEN + "Reloading Mesh! This may take time!");
			return true;
		}
		
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		
		final Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.flytoUse)){
			return true;
		}
		
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.flytoUse)){
			//check create
			if(args.length == 2 && args[0].equalsIgnoreCase("create")){
				String destinationName = args[1];
				Location loc = player.getLocation();
				
				plugin.getMeshManager().addDestination(destinationName, loc);
				player.sendMessage(ChatColor.YELLOW + "[EDP]: " + ChatColor.AQUA + destinationName 
						+ ChatColor.GREEN + " created.");
				return true;
			}
			
			//check delete
			if(args.length == 2 && args[0].equalsIgnoreCase("delete")){
				String destinationName = args[1];
				
				plugin.getMeshManager().removeDestination(destinationName);
				player.sendMessage(ChatColor.YELLOW + "[EDP]: " + ChatColor.AQUA + destinationName 
						+ ChatColor.GREEN + " deleted.");
				return true;
			}
			
			
			//list
			if(args.length == 1 && args[0].equalsIgnoreCase("list")){
				DecimalFormat formatter = new DecimalFormat("0.0");
				
				List<String> destinations = new LinkedList<String>(plugin.getMeshManager().getDestinations());
				Collections.sort(destinations);
				
				player.sendMessage(ChatColor.YELLOW + "{-_-}" + ChatColor.GREEN + " Destinations "
						+  ChatColor.YELLOW +"{-_-}");
				if(destinations.isEmpty()) player.sendMessage(ChatColor.RED + "No destinations available.");
				
				for(String name : destinations){
					Location destinationLoc = plugin.getMeshManager().getLocation(name);
					double distance = destinationLoc.getWorld() == player.getWorld() 
							? destinationLoc.distance(player.getLocation())
							: Double.MAX_VALUE;
					
					Vector vec = destinationLoc.toVector();
					player.sendMessage(ChatColor.AQUA + name + ":" 
						+ ChatColor.YELLOW + " (distance: " + (distance == Double.MAX_VALUE ? "Other World" : formatter.format(distance))+ ")"
						+ ChatColor.LIGHT_PURPLE + " Location:" 
						+ " X: " + formatter.format(vec.getX())
						+ " Y: " + formatter.format(vec.getY())
						+ " Z: " + formatter.format(vec.getZ())
					);
				}
				
				return true;
			}
		}
		
		
		Location target = null;
		String targetName = "";
		if(args.length == 3){
			try{
				Location newTarget = new Location(player.getWorld(),0,0,0);
				newTarget.setX(Double.parseDouble(args[0]));
				newTarget.setY(Double.parseDouble(args[1]));
				newTarget.setZ(Double.parseDouble(args[2]));
				
				target = newTarget;
				targetName = newTarget.getBlockX() + "," + newTarget.getBlockY() + "," + newTarget.getBlockZ();
			}catch(Throwable exp){}
		}
		
		if(args.length == 1){
			String destination = args[0];
			Location loc = plugin.getMeshManager().getLocation(destination);
			
			if(loc == null){
				player.sendMessage(ChatColor.RED + "This destination does not exist.");
				return true;
			}
			
			if(loc.getWorld() != player.getWorld()){
				player.sendMessage(ChatColor.RED + "Wrong world. :(");
				return true;
			}
			
			target = loc;
			targetName = destination;
		}
		
		if(target == null){
			player.sendMessage(ChatColor.RED + "Target not found.");
			return true;
		}
		
		
		final String finalLocationName = targetName;
		new MeshFinderTask(player.getLocation(), target, true) {
			@Override
			public void meshWayFound(List<MeshPoint> points) {
				LivingEntity entity = DragonAPI.spawnNewEnderdragon(player.getLocation(), false);
				LimitedED dragon = DragonAPI.getDragonByEntity(entity);
				dragon.setCollision(false);
				
				entity.setCustomName("§1§3§3§7" + ChatColor.AQUA + "Flug nach " + ChatColor.GREEN + finalLocationName);
				entity.setPassenger(player);
				
				dragon.getDragonHealthController().setInvincible(true);
				dragon.setDragonMoveController(new ToTargetController(player, dragon, points));
				dragon.setFireballController(new SillyFireballContoller(dragon, dragon.getTargetController()));
			}
		}.start();
		
		return true;
	}

	
	/**
	 * This will do NO fireballs at all.
	 * 
	 * @author Tobiyas
	 *
	 */
	public static class SillyFireballContoller extends FireballController{

		public SillyFireballContoller(LimitedED dragon, ITargetController targetController) {
			super(dragon, targetController);
		}
		
		
		@Override
		public void fireFireball(LivingEntity target) {
		}
		
		
		@Override
		public void fireFireballOnLocation(Location location) {
		}
		
		
		@Override
		public void fireFireballToDirection(Location direction) {
		}
		
	}
	
}
