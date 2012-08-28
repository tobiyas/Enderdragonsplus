package de.tobiyas.enderdragonsplus.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityLiving;

public class TargetController {

	private LinkedList<EntityLiving> targets;
	private EntityLiving currentTarget;
	
	private Location targetLocation;
	private Location homeLocation;
	
	private boolean lockTarget;
	
	private boolean isHostile;
	
	private EnderdragonsPlus plugin;
	private Random rand = new Random();
	private LimitedEnderDragonV131 thisDragon;
	
	private int unTargetTicksMax;
	private int unTargetTick;
	
	public TargetController(Location homeLocation, LimitedEnderDragonV131 dragon, boolean isHostile){
		targets = new LinkedList<EntityLiving>();
		currentTarget = null;
		this.thisDragon = dragon;
		
		this.homeLocation = homeLocation;
		this.targetLocation = homeLocation;
		this.isHostile = isHostile;
		
		this.lockTarget = false;
		this.plugin = EnderdragonsPlus.getPlugin();
		
		this.unTargetTicksMax = plugin.interactConfig().getConfig_dragonUntargeting();
		this.unTargetTick = unTargetTicksMax;
	}	
	
	public void addTarget(EntityLiving entity){
		targets.add(entity);
	}
	
	public void removeTarget(EntityLiving entity){
		targets.remove(entity);
	}
	
	public void clearTargets(){
		targets.clear();
	}
	
	private void checkTargets(Location currentLocation){			
		LinkedList<EntityLiving> newTargets = new LinkedList<EntityLiving>();
		
		for(EntityLiving entity : targets){
			if(entity == null) continue;
			LivingEntity bukkitEntity = (LivingEntity) entity.getBukkitEntity();
			Location targetLoc = bukkitEntity.getLocation();
			if(targetLoc.getWorld() != currentLocation.getWorld())
				continue;
			
			double distanceSquared = currentLocation.distanceSquared(targetLoc);
			double allowedDistance = plugin.interactConfig().getConfig_maxFollowDistanceSquared();
			if(distanceSquared > allowedDistance) continue;
			if(!isValidTarget(bukkitEntity)) continue;
			newTargets.add(entity);
		}
		
		clearTargets();
		targets = newTargets;
	}
	
	public boolean switchTargetsWithMode(Location currentLocation){
		if(isHostile)
			rescanTargetsAggressive(currentLocation);
		
		checkTargets(currentLocation);
		return switchTarget(currentLocation);
	}
	
	private void rescanTargetsAggressive(Location loc){
		List<Player> players = loc.getWorld().getPlayers();
		targets.clear();
		for(Player player : players)
			targets.add( ((CraftPlayer)player).getHandle());
	}
	
	private boolean switchTarget(Location currentLocation){
		EntityLiving newTarget = currentTarget;
		
		if(lockTarget){
			if(currentLocation.distanceSquared(targetLocation) < 900)
			lockTarget = false;
		}
		
		boolean targetChanged = false;
		
		if(targets.size() == 0){
			targetChanged = currentTarget != null && targetLocation != homeLocation;
			newTarget = null;
		}else{
			int randomTarget = rand.nextInt(targets.size());
			newTarget = targets.get(randomTarget);
			if(newTarget != null && newTarget.getHealth() > 0)	
				targetChanged = currentTarget != newTarget;
			
		}
		
		if(targetChanged){
			currentTarget = fireBukkitEvent(newTarget);
			targetLocation = currentTarget == null ? homeLocation : currentTarget.getBukkitEntity().getLocation();
			return true;
		}
		
		return false;
	}
	
	public LinkedList<Entity> getTargetsInRange(int number, double range){
		LinkedList<Entity> randomTargets = new LinkedList<Entity>();
		
		Collections.shuffle(targets);
		for(Entity entity : targets)
			if(isInRange(entity, range)){
				randomTargets.add(entity);
				if(randomTargets.size() >= number)
					break;
			}
		
		return randomTargets;
	}
	
	//Private Methods
	
	private boolean isInRange(Entity entity, double range){
		double doubleRange = range * range;
		Location entityLocation = entity.getBukkitEntity().getLocation();
		double distance = getDragonLocation().distanceSquared(entityLocation);
		
		return distance <= doubleRange;
	}
	
	private boolean isValidTarget(LivingEntity entity){
		if(entity == null || entity.isDead()) return false;
		if(!(entity instanceof Player)) return true;
		
		Player player = (Player) entity;
		if(player.getGameMode() == GameMode.CREATIVE && 
			plugin.interactConfig().getConfig_ignorePlayerGamemode1()) 
			return false;
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.getIgnored)) 
			return false;
		
		return true;
	}
	
	private EntityLiving fireBukkitEvent(EntityLiving nextTarget){
		// fire bukkit event: Target change
		if (plugin.interactConfig().getConfig_fireBukkitEvents()) {
			if (currentTarget != nextTarget) {
				EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(
						thisDragon.getBukkitEntity(),
						nextTarget == null ? null : (LivingEntity) nextTarget.getBukkitEntity(),
						TargetReason.RANDOM_TARGET);
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				if (event.isCancelled()) return currentTarget;
				return (EntityLiving) ((CraftEntity)event.getEntity()).getHandle();
			}
		}
		
		return nextTarget; 
	}
	
	private Location getLoc(EntityLiving entity){
		return entity == null ? homeLocation : entity.getBukkitEntity().getLocation();
	}
	
	//Getter - Setter
	//Targets
	public Location getTargetLocation(){
		return targetLocation;
	}
	
	public EntityLiving getCurrentTarget(){
		unTargetTick -= 1;
		if(unTargetTick <= 0){
			unTargetTick = unTargetTicksMax;
			return currentTarget;
		}
		return null;
	}
	
	public Location getDragonLocation(){
		return thisDragon.getLocation();
	}
	
	public LimitedEnderDragonV131 getDragon(){
		return thisDragon;
	}
	
	public boolean hasTargets(){
		return targets.size() != 0;
	}
	
	//Locking
	public void lockTarget(){
		this.lockTarget = true;
	}
	
	public void unlockTarget(){
		this.lockTarget = false;
	}
	
	public boolean getLock(){
		return this.lockTarget;
	}
	
	//Forcing
	public void forceTarget(Location loc){
		currentTarget = null;
		targetLocation = loc;
	}
	
	public void forceTarget(EntityLiving entity){
		if(entity == null) return;
		
		
		currentTarget = fireBukkitEvent(entity);
		targetLocation = getLoc(entity);
	}
	
	//Hostile
	public boolean isHostile(){
		return isHostile;
	}
	
	public void setHostile(boolean isHostile){
		this.isHostile = isHostile;
	}
}
