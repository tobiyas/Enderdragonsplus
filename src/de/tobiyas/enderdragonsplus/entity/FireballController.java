package de.tobiyas.enderdragonsplus.entity;

import java.util.LinkedList;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityFireball;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class FireballController {

	//private LimitedEnderDragonV131 dragon;
	private TargetController targetController;
	private int fireballTicks;
	private EnderdragonsPlus plugin;
	
	public FireballController(TargetController targetController){
		this.targetController = targetController;
		this.fireballTicks = 0;
		this.plugin = EnderdragonsPlus.getPlugin();
	}
	
	public void forceSpitFireball(){
		fireballTicks = plugin.interactConfig().getConfig_dragonSpitFireballsEvery() * 21; //over border
		checkSpitFireBall();
	}
	
	public void checkSpitFireBall(){
		if(!checkActive()) return;
		fireballTicks++;
		int fireEveryX = plugin.interactConfig()
				.getConfig_dragonSpitFireballsEvery();
		
		if (fireballTicks > (fireEveryX * 20)) {
			fireballTicks = 0;
			if (!targetController.hasTargets())
				return;
			
			int maxDistanceSquared = plugin.interactConfig().getConfig_dragonsSpitFireballsRange() ^ 2;
			maxDistanceSquared *= maxDistanceSquared;
			
			int maxFireballTargets = plugin.interactConfig().getConfig_maxFireballTargets();
			LinkedList<Entity> entities = targetController.getTargetsInRange(maxFireballTargets, maxDistanceSquared);
			
			for(Entity target : entities){
				if(target == null || !(target.getBukkitEntity() instanceof Player)) continue;
				Player player = (Player) target.getBukkitEntity();
				Location playerLocation = player.getLocation();
				Location dragonLocation = targetController.getDragonLocation();
				
				if(playerLocation.distanceSquared(dragonLocation) > maxDistanceSquared)
					continue;
				Location fireDirection = new Location(dragonLocation.getWorld(), 
						playerLocation.getBlockX()	- dragonLocation.getBlockX(), 
						playerLocation.getBlockY() - dragonLocation.getBlockY(), 
						playerLocation.getBlockZ()	- dragonLocation.getBlockZ());
	
				fireFireball(fireDirection);
			}
		}
	}
	
	private boolean checkActive(){
		boolean fireFireBall = plugin.interactConfig()
				.getConfig_dragonsSpitFireballs();
		return fireFireBall;
	}
	
	public void fireFireball(Entity entity){
		Location locDragon = targetController.getDragonLocation();
		Location loc = new Location(locDragon.getWorld(), 
				entity.locX	- locDragon.getBlockX(), 
				entity.locY - locDragon.getBlockY(), 
				entity.locZ	- locDragon.getBlockZ());
		fireFireball(loc);
	}
	
	private void fireFireball(Location loc){
		if (loc.getWorld() != targetController.getDragonLocation().getWorld())
			return;		
		
		EntityFireball fireBall = new EntityFireball(
				targetController.getDragon().world, 
				targetController.getDragon(),
				loc.getBlockX(), 
				loc.getBlockY(), 
				loc.getBlockZ());
		
		targetController.getDragon().world.addEntity(fireBall);
	}
}
