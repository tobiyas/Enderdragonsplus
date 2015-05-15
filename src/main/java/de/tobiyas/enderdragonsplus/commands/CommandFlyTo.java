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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.DragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController;
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
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		Player player = (Player) sender;
		
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
		
		
		Vector target = null;
		if(args.length == 3){
			try{
				Vector newTarget = new Vector();
				newTarget.setX(Double.parseDouble(args[0]));
				newTarget.setY(Double.parseDouble(args[1]));
				newTarget.setZ(Double.parseDouble(args[2]));
				
				target = newTarget;
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
			
			target = loc.toVector();
		}
		
		if(target == null){
			player.sendMessage(ChatColor.RED + "Target not found.");
			return true;
		}
		
		LivingEntity entity = DragonAPI.spawnNewEnderdragon(player.getLocation(), false);
		LimitedED dragon = DragonAPI.getDragonByEntity(entity);
		
		entity.setCustomName("§1§3§3§7" + ChatColor.AQUA + "Flug nach " + ChatColor.GREEN + args[0]);
		entity.setPassenger(player);
		
		dragon.getDragonHealthController().setInvincible(true);
		dragon.setDragonMoveController(new ToSpawnController(player, dragon, target));
		dragon.setFireballController(new SillyFireballContoller(dragon, dragon.getTargetController()));
		
		//player.sendMessage(ChatColor.GREEN + "Flying to target.");
		
		return true;
	}

	
	
	
	public class ToSpawnController extends DragonMoveController{
		
		/**
		 * The Way to use.
		 */
		private List<Vector> way;
		
		/**
		 * The Current Target.
		 */
		private Vector currentTarget = null;
		
		/**
		 * The current index.
		 */
		private int index = 0;
		
		/**
		 * The Player to use.
		 */
		private final Player player;
		
		/**
		 * The will be true when the travel is FINALLY done!
		 */
		private boolean done = false;
		
		
		public ToSpawnController(Player player, LimitedED dragon, Vector target) {
			super(dragon);
			
			this.player = player;
			this.way = plugin.getMeshManager().getWay(dragon.getLocation().toVector().clone(), target.clone());
			player.sendMessage(ChatColor.GREEN + "Die benötigte Zeit ist etwa " + (way.size() * 3) + " Sekunden.");
		}
		
		
		@Override
		public void moveDragon() {
			//remove dragon when no passenger.
			if(dragon.getPassenger() == null){
				if(done) return;
				
				if(!player.isValid() || !player.isOnline()){
					dragon.getBukkitEntity().remove();
					return;
				}
				
				//YOU SHALL NOT DISMOUNT!
				dragon.setPassenger(player);
			}
			
			if(currentTarget == null){
				currentTarget = way.get(index);
				index++;
			}
			
			if(dragon.getLocation().toVector().distanceSquared(currentTarget) < 4){
				if(index >= way.size()) {
					//first remove the passenger from to the destination.
					Entity passenger = dragon.getPassenger();
					if(passenger != null){
						//teleport to the destination.
						passenger.teleport(
								new Location(
										passenger.getWorld(), 
										currentTarget.getX(), 
										currentTarget.getY(), 
										currentTarget.getZ()
								)
						);
					}
					
					dragon.remove();
				}
				
				currentTarget = null;
				return;
			}
			
			Vector vec = currentTarget.clone().subtract(dragon.getLocation().toVector().clone()).normalize();
			dragon.setLastYaw(dragon.getYaw());
			dragon.setYaw(calcYawFromVec(vec));
			
			//MOVE BITCH! GET OUT OF THE WAY.
			dragon.setMotion(vec);
			this.moveToDragonMotion();
		}
		
	}
	
	
	/**
	 * This will do NO fireballs at all.
	 * 
	 * @author Tobiyas
	 *
	 */
	public class SillyFireballContoller extends FireballController{

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
