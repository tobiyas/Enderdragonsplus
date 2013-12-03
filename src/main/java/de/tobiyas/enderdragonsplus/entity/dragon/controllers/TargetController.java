package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;
import de.tobiyas.util.math.Bresenham;

public class TargetController {

	private LinkedList<EntityLiving> targets;
	private EntityLiving currentTarget;
	
	private Location targetLocation;
	private Location homeLocation;

	private boolean lockTarget;
	private boolean isFlyingHome;
	
	private boolean isHostile;
	
	private EnderdragonsPlus plugin;
	private Random random = new Random();
	private LimitedEnderDragon dragon;
	
	private int unTargetTicksMax;
	private int unTargetTick;
	private Location forceGoTo;
	
	
	public TargetController(Location homeLocation, LimitedEnderDragon dragon, boolean isHostile){
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
		this.isFlyingHome = false;
	}
	
	/**
	 * Sets up the Target management + adds the players located in the targetList as Targets.
	 * 
	 * @param homeLocation
	 * @param dragon
	 * @param isHostile
	 * @param targetList
	 */
	@SuppressWarnings("unchecked")
	public TargetController(Location homeLocation, LimitedEnderDragon dragon, boolean isHostile, NBTTagCompound targetList){
		this(homeLocation, dragon, isHostile);
		
		for(String key : (Set<String>) targetList.c()){
			try{
				String playerName = targetList.getString(key);
				Player player = Bukkit.getPlayer(playerName);
				if(player != null && player.isOnline()){
					targets.add(((CraftPlayer) player).getHandle());
				}
				
			}catch(Exception exp){}
		}
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
			if(targetLoc.getWorld() != currentLocation.getWorld()){
				continue;
			}
			
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
		if(isHostile && !isFlyingHome){
			rescanTargetsAggressive();
		}
		
		checkTargets();
		return switchTarget();
	}
	
	private void rescanTargetsAggressive(){
		Location currentLocation = getDragonLocation();
		List<Player> players = currentLocation.getWorld().getPlayers();
		targets.clear();
		for(Player player : players){
			targets.add( ((CraftPlayer)player).getHandle());
		}
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
			double distanceSquared = currentLocation.distanceSquared(targetLocation);
			
			if(distanceSquared < 900){
				lockTarget = false;
			}else{
				return false;
			}
		}
		
		boolean targetChanged = false;
		
		if(targets.size() == 0){
			targetChanged = currentTarget != null && targetLocation != homeLocation;
			newTarget = null;
		}else{
			int randomTarget = random.nextInt(targets.size());
			newTarget = targets.get(randomTarget);
			if(newTarget != null && newTarget.getHealth() > 0){
				targetChanged = currentTarget != newTarget;
			}
			
		}
		
		if(targetChanged){
			currentTarget = fireBukkitEvent(newTarget);
			targetLocation = currentTarget == null ? homeLocation.clone() : currentTarget.getBukkitEntity().getLocation();
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
	 * @param entity the location of the entity to check
	 * @return the new location to fly to
	 */
	private Location getLoc(EntityLiving entity){
		return entity == null ? homeLocation.clone() : entity.getBukkitEntity().getLocation();
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
	
	public LimitedEnderDragon getDragon(){
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
	//
	
	public void changeTarget() {
		try {
			dragon.bz = false;

			int homeRange = plugin.interactConfig().getConfig_maxHomeDistance();

			if (getVectorDistance(homeLocation) > homeRange){
				isFlyingHome = true;
				forceGoTo = homeLocation;
				
				currentTarget = null;
				lockTarget = true;
				
				targets.clear();
			}
			
			switchTargetsWithMode();
			Location newTarget = getTargetLocation();
			
			setNewTarget(newTarget, getLock());
		} catch (Exception e) {
			if (!plugin.interactConfig().getConfig_debugOutput())
				return;
			if (LimitedEnderDragon.broadcastedError != 10) {
				LimitedEnderDragon.broadcastedError++;
				return;
			}

			LimitedEnderDragon.broadcastedError = 0;
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
		if (lockTarget){
			forceGoTo = location;
		}
		

		if (forceGoTo != null){
			location = forceGoTo;
		}

		if(isFlyingHome){
			location = homeLocation.clone();
		}

		if (getVectorDistance(location) < 30) {
			if(isFlyingHome && location.equals(homeLocation)){
				isFlyingHome = false;
			}
			
			if (forceGoTo != null) {
				forceGoTo = null;
				location = homeLocation.clone();
				return;
			}
		}
		

		double vecDistance = 0;
		do {
			dragon.h = location.getX();
			dragon.i = (70.0F + this.random.nextFloat() * 50.0F);
			dragon.j = location.getZ();
			if (forceGoTo == null  && !isFlyingHome) {
				dragon.h += (this.random.nextFloat() * 120.0F - 60.0F);
				dragon.j += (this.random.nextFloat() * 120.0F - 60.0F);

				double distanceX = dragon.locX - dragon.h;
				double distanceY = dragon.locY - dragon.i;
				double distanceZ = dragon.locZ - dragon.j;

				vecDistance = distanceX * distanceX + distanceY * distanceY
						+ distanceZ * distanceZ;
				
				if(vecDistance > 100){
					int minHeight = 0;
					//recalculate Height.
					Location target = new Location(dragon.getLocation().getWorld(), dragon.h, dragon.i, dragon.j);
					Location currentLocation = dragon.getLocation();
					
					Queue<Location> line = Bresenham.line3D(currentLocation, target);
					Iterator<Location> lineIt = line.iterator();
					
					String calcString = "";
					
					while(lineIt.hasNext()){
						Location nextLocation = lineIt.next();
						int newMinHeight = nextLocation.getWorld().getHighestBlockYAt(nextLocation) + 7;
						if(newMinHeight > minHeight){
							minHeight = newMinHeight;

							calcString += " new Height: x " + nextLocation.getBlockX() + " z " + nextLocation.getBlockZ() + " y " + newMinHeight;
						}
						
					}
					
					plugin.getDebugLogger().log(calcString);
					
					if(minHeight < 10) minHeight = 20;
					dragon.i = minHeight;
				}
			} else {
				dragon.i = location.getY();
				vecDistance = 101;
			}

		} while (vecDistance < 100);
	}
	
	
	public boolean isFlyingHome(){
		return isFlyingHome;
	}

	public void forceFlyingHome(boolean flyingHome) {
		isFlyingHome = flyingHome;
	}

	public Location getHomeLocation() {
		return homeLocation.clone();
	}
	
	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation.clone();
	}
	
	
	/**
	 * Returns an NBTTagList containing the current targets.
	 * This is for storing / restoring the Entities.
	 * 
	 * @return
	 */
	public NBTTagCompound getCurrentTagetsAsNBTList(){
		NBTTagCompound list = new NBTTagCompound();
		
		int i = 0;
		for(EntityLiving target : targets){
			if(target instanceof EntityPlayer){
				String playerName = ((EntityPlayer) target).getName();
				list.setString("target" + i, playerName);
				i++;
			}
		}
		
		return list;
	}
	
	/**
	 * Returns a COPIED list of targets from the Dragon
	 * 
	 * @return
	 */
	public List<Entity> getAllCurrentTargets(){
		return new LinkedList<Entity>(targets);
	}
}
