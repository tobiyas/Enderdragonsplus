package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.LinkedList;

import net.minecraft.server.v1_4_R1.Entity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.fireball.LimitedFireball;

public class FireballController {

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
				if(!checkFiredirectionHeight(target.locY, targetController.getDragonLocation().getY())){
					fireballTicks = (fireEveryX * 20) / 2;
					continue;
				}
				
				Player player = (Player) target.getBukkitEntity();
				Location playerLocation = player.getLocation();
				Location dragonLocation = targetController.getDragonLocation();
				
				if(playerLocation.distanceSquared(dragonLocation) > maxDistanceSquared)
					continue;
				fireFireball(target);
			}
		}
	}
	
	private boolean checkFiredirectionHeight(double heightTarget, double heightDragon){
		double directionHeight = heightTarget - heightDragon;
		if(directionHeight >= 0)
			return false;
		
		return true;
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
	
	private void fireFireball(Location direction){
		if (direction.getWorld() != targetController.getDragonLocation().getWorld())
			return;		
		
		LimitedFireball fireBall = new LimitedFireball(
				targetController.getDragon().world, 
				targetController.getDragon(),
				direction.getBlockX(), 
				direction.getBlockY(), 
				direction.getBlockZ());
		
		targetController.getDragon().world.addEntity(fireBall);
		double fireBallSpeedup = plugin.interactConfig().getConfig_FireBallSpeedUp();
		fireBall.speedUp(fireBallSpeedup);
	}
}
