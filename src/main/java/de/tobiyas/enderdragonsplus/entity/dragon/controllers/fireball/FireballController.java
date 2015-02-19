package de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballGenerator;
import de.tobiyas.enderdragonsplus.entity.fireball.LimitedFireball;

public class FireballController implements IFireballController {

	protected ITargetController targetController;
	protected LimitedED dragon;
	protected int fireballTicks;
	protected EnderdragonsPlus plugin;
	
	public FireballController(LimitedED dragon, ITargetController targetController){
		this.targetController = targetController;
		this.dragon = dragon;
		this.fireballTicks = 0;
		this.plugin = EnderdragonsPlus.getPlugin();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController#forceSpitFireball()
	 */
	@Override
	public void forceSpitFireball(){
		fireballTicks = plugin.interactConfig().getConfig_dragonSpitFireballsEvery() * 21; //over border
		checkSpitFireBall();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController#checkSpitFireBall()
	 */
	@Override
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
			LinkedList<LivingEntity> entities = targetController.getTargetsInRange(maxFireballTargets, maxDistanceSquared);
			
			for(LivingEntity target : entities){
				if(target == null || !(target instanceof Player)) continue;
				if(!checkFiredirectionHeight(target.getLocation().getY(), targetController.getDragonLocation().getY())){
					fireballTicks = (fireEveryX * 20) / 2;
					continue;
				}
				
				Player player = (Player) target;
				Location playerLocation = player.getLocation();
				Location dragonLocation = targetController.getDragonLocation();
				
				if(playerLocation.distanceSquared(dragonLocation) > maxDistanceSquared)
					continue;
				fireFireball(target);
			}
		}
	}
	
	protected boolean checkFiredirectionHeight(double heightTarget, double heightDragon){
		double directionHeight = heightTarget - heightDragon;
		if(directionHeight >= 0)
			return false;
		
		return true;
	}
	
	protected boolean checkActive(){
		boolean fireFireBall = plugin.interactConfig()
				.getConfig_dragonsSpitFireballs();
		return fireFireBall;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController#fireFireball(org.bukkit.entity.LivingEntity)
	 */
	@Override
	public void fireFireball(LivingEntity target){
		Location locDragon = targetController.getDragonLocation();
		Location loc = new Location(locDragon.getWorld(), 
				target.getLocation().getX()	- locDragon.getBlockX(), 
				target.getLocation().getY() - locDragon.getBlockY(), 
				target.getLocation().getZ()	- locDragon.getBlockZ());
		fireFireballToDirection(loc);
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController#fireFireballToDirection(org.bukkit.Location)
	 */
	@Override
	public void fireFireballToDirection(Location direction){
		System.out.println("Fireing Fireball!");
		if (direction.getWorld() != targetController.getDragonLocation().getWorld())
			return;		
		
		World world = dragon.getBukkitWorld();
		LimitedFireball fireBall = FireballGenerator.generate(world, (LivingEntity) dragon.getBukkitEntity(), 
				direction.getBlockX(), 
				direction.getBlockY(), 
				direction.getBlockZ()
				);
		
		if(fireBall == null) return;
		
		fireBall.spawnIn(world);
		double fireBallSpeedup = plugin.interactConfig().getConfig_FireBallSpeedUp();
		fireBall.speedUp(fireBallSpeedup);
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController#fireFireballOnLocation(org.bukkit.Location)
	 */
	@Override
	public void fireFireballOnLocation(Location location){
		Location direction = location.clone();
		direction = direction.subtract(targetController.getDragonLocation().clone());
		
		if (direction.getWorld() != dragon.getBukkitWorld()){
			return;
		}
		
		World world = dragon.getBukkitWorld();
		LimitedFireball fireBall = FireballGenerator.generate(world, (LivingEntity) dragon.getBukkitEntity(), 
				direction.getBlockX(), 
				direction.getBlockY(), 
				direction.getBlockZ()
				);
		
		if(fireBall == null) return;
		
		fireBall.spawnIn(world);
		double fireBallSpeedup = plugin.interactConfig().getConfig_FireBallSpeedUp();
		fireBall.speedUp(fireBallSpeedup);
	}
}
