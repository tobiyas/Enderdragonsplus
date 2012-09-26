package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

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
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;
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
	private Random random = new Random();
	private LimitedEnderDragonV131 dragon;
	
	private int unTargetTicksMax;
	private int unTargetTick;
	private Location forceGoTo;
	
	public TargetController(Location homeLocation, LimitedEnderDragonV131 dragon, boolean isHostile){
		targets = new LinkedList<EntityLiving>();
		currentTarget = null;
		this.dragon = dragon;
		
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
	
	private void checkTargets(){
		Location currentLocation = getDragonLocation();
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
	
	public boolean switchTargetsWithMode(){
		if(isHostile)
			rescanTargetsAggressive();
		
		checkTargets();
		return switchTarget();
	}
	
	private void rescanTargetsAggressive(){
		Location currentLocation = getDragonLocation();
		List<Player> players = currentLocation.getWorld().getPlayers();
		targets.clear();
		for(Player player : players)
			targets.add( ((CraftPlayer)player).getHandle());
	}
	
	/**
	 * Switches target of the Dragon
	 * 
	 * @param currentLocation
	 * @return
	 */
	private boolean switchTarget(){
		Location currentLocation = getDragonLocation();
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
			int randomTarget = random.nextInt(targets.size());
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
	
	/**
	 * Gets X (number) targets in range (range)
	 * 
	 * @param number, the number of targets to search
	 * @param range, the range to check
	 * @return a List<Entity> with the given specs
	 */
	public LinkedList<Entity> getTargetsInRange(int number, double range){
		LinkedList<Entity> randomTargets = new LinkedList<Entity>();
		
		Collections.shuffle(targets);
		for(Entity entity : targets)
			if(isInRange(entity.getBukkitEntity().getLocation(), range)){
				randomTargets.add(entity);
				if(randomTargets.size() >= number)
					break;
			}
		
		return randomTargets;
	}
	
	//Private Methods
	
	/**
	 * Checks if a Entity is in Range range of the dragon
	 * 
	 * @param entity, the Entity to check the range
	 * @param range, the range to check
	 * @return if the Entity is in range of the Dragon
	 */
	public boolean isInRange(Location entityLocation, double range){
		if(entityLocation.getWorld() != dragon.getLocation().getWorld()) return false;
		double doubleRange = range * range;
		double distance = getDragonLocation().distanceSquared(entityLocation);
		
		return distance <= doubleRange;
	}
	
	/**
	 * Checks if the target (@param: entity) is valid to the config 
	 * 
	 * @param entity, the Entity to check
	 * @return true, if the target is a valid target
	 */
	private boolean isValidTarget(LivingEntity entity){
		if(entity == null || entity.isDead()) return false;
		if(!(entity instanceof Player)) return true;
		
		Player player = (Player) entity;
		if(player.getGameMode() == GameMode.CREATIVE && 
			plugin.interactConfig().getConfig_ignorePlayerGamemode1()) 
			return false;
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.getIgnored) &&
			!plugin.interactConfig().getConfig_disableTargetImun()) 
			return false;
		
		return true;
	}
	
	/**
	 * Fires the Bukkit event when a dragon changes his target
	 * 
	 * @param nextTarget the next target puposed by the Controller
	 * @return the next Target purposed after event
	 */
	private EntityLiving fireBukkitEvent(EntityLiving nextTarget){
		// fire bukkit event: Target change
		if (plugin.interactConfig().getConfig_fireBukkitEvents()) {
			if (currentTarget != nextTarget) {
				EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(
						dragon.getBukkitEntity(),
						nextTarget == null ? null : (LivingEntity) nextTarget.getBukkitEntity(),
						TargetReason.RANDOM_TARGET);
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				if (event.isCancelled()) return currentTarget;
				return (EntityLiving) ((CraftEntity)event.getEntity()).getHandle();
			}
		}
		
		return nextTarget; 
	}
	
	/**
	 * returns the Location of the given Entity
	 * Only used for API calls.
	 * 
	 * @param entity the new target (or null to tell him to fly home)
	 * @return the new location to fly to
	 */
	private Location getLoc(EntityLiving entity){
		return entity == null ? homeLocation : entity.getBukkitEntity().getLocation();
	}
	
	//Getter - Setter
	//Targets
	public Location getTargetLocation(){
		return targetLocation;
	}
	
	public Location getForceGoTo(){
		return forceGoTo;
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
		return dragon.getLocation();
	}
	
	public LimitedEnderDragonV131 getDragon(){
		return dragon;
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
	
	//****//
	//Original EnderDragon functions!
	//TODO: Needs cleanup!
	
	public void changeTarget(boolean force) {
		try {
			dragon.bE = false;

			int homeRange = plugin.interactConfig().getConfig_maxHomeDistance();
			Location homeLocation = dragon.getHomeLocation();

			if (getVectorDistance(homeLocation) > homeRange)
				plugin.getContainer().setFlyingHome(dragon.getUUID(), true);

			if (dragon.isFlyingHome() || dragon.getForceLocation() != null)
				force = true;
			
			switchTargetsWithMode();
			Location newTarget = getTargetLocation();
			
			setNewTarget(newTarget,getLock());
		} catch (Exception e) {
			if (!plugin.interactConfig().getConfig_debugOutput())
				return;
			if (LimitedEnderDragonV131.broadcastedError != 10) {
				LimitedEnderDragonV131.broadcastedError++;
				return;
			}

			LimitedEnderDragonV131.broadcastedError = 0;
			plugin.log("An Error has Accured. Tried to access to an illigel mob (function: changeTarget). Disabling ErrorMessage for massive Spaming!");
			e.printStackTrace();
			return;
		}
	}
	
	private double getVectorDistance(Location location) {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		return getVectorDistance(x, y, z);
	}
	
	private double getVectorDistance(double x, double y, double z) {
		double deltaX = dragon.locX - x;
		double deltaY = dragon.locY - y;
		double deltaZ = dragon.locZ - z;

		deltaX *= deltaX;
		deltaY *= deltaY;
		deltaZ *= deltaZ;

		return Math.sqrt(deltaX + deltaY + deltaZ);
	}
	
	public void setNewTarget(Location location, boolean lockTarget) {
		if (lockTarget)
			forceGoTo = location;

		if (forceGoTo != null)
			location = forceGoTo;

		if (getVectorDistance(location) < 30) {
			plugin.getContainer().setFlyingHome(dragon.getUUID(), false);
			if (forceGoTo != null) {
				forceGoTo = null;
				location = plugin.getContainer().getHomeByID(dragon.getUUID());
				return;
			}
		}

		double vecDistance = 0;
		do {
			dragon.b = location.getX();
			dragon.c = (70.0F + this.random.nextFloat() * 50.0F);
			dragon.d = location.getZ();
			if (forceGoTo == null) {
				dragon.b += (this.random.nextFloat() * 120.0F - 60.0F);
				dragon.d += (this.random.nextFloat() * 120.0F - 60.0F);

				double distanceX = dragon.locX - dragon.b;
				double distanceY = dragon.locY - dragon.c;
				double distanceZ = dragon.locZ - dragon.d;

				vecDistance = distanceX * distanceX + distanceY * distanceY
						+ distanceZ * distanceZ;
			} else {
				dragon.c = location.getY();
				vecDistance = 101;
			}

		} while (vecDistance < 100);
	}
}
