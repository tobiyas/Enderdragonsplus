package de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;
import de.tobiyas.util.math.Bresenham;

public class TargetController implements ITargetController {

	protected LinkedList<LivingEntity> targets;
	protected LivingEntity currentTarget;
	
	protected Location targetLocation;
	protected Location homeLocation;

	protected boolean lockTarget;
	protected boolean isFlyingHome;
	
	protected boolean isHostile;
	
	protected EnderdragonsPlus plugin;
	protected Random random = new Random();
	protected LimitedED dragon;
	
	protected int unTargetTicksMax;
	protected int unTargetTick;
	protected Location forceGoTo;
	
	
	/**
	 * This targetChain is for trying to pass objects in the way.
	 */
	protected Queue<Location> targetChain = new LinkedList<Location>();
	
	
	public TargetController(Location homeLocation, LimitedED dragon, boolean isHostile){
		targets = new LinkedList<LivingEntity>();
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
	public TargetController(Location homeLocation, LimitedED dragon, boolean isHostile, List<String> targetList){
		this(homeLocation, dragon, isHostile);
		
		for(String value : targetList){
			try{
				Player player = Bukkit.getPlayer(value);
				if(player != null && player.isOnline()){
					targets.add(player);
				}
				
			}catch(Exception exp){}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#addTarget(org.bukkit.entity.LivingEntity)
	 */
	@Override
	public void addTarget(LivingEntity entity){
		targets.add(entity);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#removeTarget(org.bukkit.entity.LivingEntity)
	 */
	@Override
	public void removeTarget(LivingEntity entity){
		targets.remove(entity);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#clearTargets()
	 */
	@Override
	public void clearTargets(){
		targets.clear();
	}
	
	protected void checkTargets(){
		Location currentLocation = getDragonLocation();
		LinkedList<LivingEntity> newTargets = new LinkedList<LivingEntity>();
		
		for(LivingEntity entity : targets){
			if(entity == null) continue;
			Location targetLoc = entity.getLocation();
			if(targetLoc.getWorld() != currentLocation.getWorld()){
				continue;
			}
			
			double distanceSquared = currentLocation.distanceSquared(targetLoc);
			double allowedDistance = plugin.interactConfig().getConfig_maxFollowDistanceSquared();
			
			if(distanceSquared > allowedDistance) continue;
			if(!isValidTarget(entity)) continue;
			newTargets.add(entity);
		}
		
		clearTargets();
		targets = newTargets;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#switchTargetsWithMode()
	 */
	@Override
	public boolean switchTargetsWithMode(){
		if(isHostile && !isFlyingHome){
			rescanTargetsAggressive();
		}
		
		checkTargets();
		return switchTarget();
	}
	
	protected void rescanTargetsAggressive(){
		Location currentLocation = getDragonLocation();
		List<Player> players = currentLocation.getWorld().getPlayers();
		targets.clear();
		for(Player player : players){
			targets.add(player);
		}
	}
	
	/**
	 * Switches target of the Dragon
	 * 
	 * @param currentLocation
	 * @return
	 */
	protected boolean switchTarget(){
		Location currentLocation = getDragonLocation();
		LivingEntity newTarget = currentTarget;
		
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
			targetLocation = currentTarget == null ? homeLocation.clone() : currentTarget.getLocation();
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getTargetsInRange(int, double)
	 */
	@Override
	public LinkedList<LivingEntity> getTargetsInRange(int number, double range){
		LinkedList<LivingEntity> randomTargets = new LinkedList<LivingEntity>();
		
		Collections.shuffle(targets);
		for(LivingEntity entity : targets)
			if(isInRange(entity.getLocation(), range)){
				randomTargets.add(entity);
				if(randomTargets.size() >= number)
					break;
			}
		
		return randomTargets;
	}
	
	//protected Methods
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#isInRange(org.bukkit.Location, double)
	 */
	@Override
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
	protected boolean isValidTarget(LivingEntity entity){
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
	protected LivingEntity fireBukkitEvent(LivingEntity nextTarget){
		// fire bukkit event: Target change
		if (plugin.interactConfig().getConfig_fireBukkitEvents()) {
			if (currentTarget != nextTarget) {
				EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(
						dragon.getBukkitEntity(),
						nextTarget == null ? null : nextTarget,
						TargetReason.RANDOM_TARGET);
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				if (event.isCancelled()) return currentTarget;
				if (event.getEntity() instanceof LivingEntity)	return (LivingEntity) event.getEntity();
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
	protected Location getLoc(LivingEntity entity){
		return entity == null ? homeLocation.clone() : entity.getLocation();
	}
	
	//Getter - Setter
	//Targets
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getTargetLocation()
	 */
	@Override
	public Location getTargetLocation(){
		return targetLocation;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getForceGoTo()
	 */
	@Override
	public Location getForceGoTo(){
		return forceGoTo;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getCurrentTarget()
	 */
	@Override
	public LivingEntity getCurrentTarget(){
		unTargetTick -= 1;
		if(unTargetTick <= 0){
			unTargetTick = unTargetTicksMax;
			return currentTarget;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getDragonLocation()
	 */
	@Override
	public Location getDragonLocation(){
		return dragon.getLocation();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getDragon()
	 */
	@Override
	public LimitedED getDragon(){
		return dragon;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#hasTargets()
	 */
	@Override
	public boolean hasTargets(){
		return targets.size() != 0;
	}
	
	//Locking
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#lockTarget()
	 */
	@Override
	public void lockTarget(){
		this.lockTarget = true;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#unlockTarget()
	 */
	@Override
	public void unlockTarget(){
		this.lockTarget = false;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getLock()
	 */
	@Override
	public boolean getLock(){
		return this.lockTarget;
	}
	
	//Forcing
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#forceTarget(org.bukkit.Location)
	 */
	@Override
	public void forceTarget(Location loc){
		currentTarget = null;
		targetLocation = loc;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#forceTarget(org.bukkit.entity.LivingEntity)
	 */
	@Override
	public void forceTarget(LivingEntity entity){
		if(entity == null) return;
		
		
		currentTarget = fireBukkitEvent(entity);
		targetLocation = getLoc(entity);
	}
	
	//Hostile
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#isHostile()
	 */
	@Override
	public boolean isHostile(){
		return isHostile;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#setHostile(boolean)
	 */
	@Override
	public void setHostile(boolean isHostile){
		this.isHostile = isHostile;
	}
	
	//****//
	//Original EnderDragon functions!
	//
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#changeTarget()
	 */
	@Override
	public void changeTarget() {
		try {
			//TODO check if this is needed.
			//dragon.bz = false;

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
	
	protected double getVectorDistance(Location location) {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		return getVectorDistance(x, y, z);
	}
	
	protected double getVectorDistance(double x, double y, double z) {
		double deltaX = dragon.getLocation().getX() - x;
		double deltaY = dragon.getLocation().getY() - y;
		double deltaZ = dragon.getLocation().getZ() - z;

		deltaX *= deltaX;
		deltaY *= deltaY;
		deltaZ *= deltaZ;

		return Math.sqrt(deltaX + deltaY + deltaZ);
	}
	

	@Override
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
		
		double maxHomeDist = plugin.interactConfig().getConfig_maxHomeDistanceSquared();
		if(location.distanceSquared(homeLocation) > maxHomeDist){
			location = homeLocation.clone();
			targetLocation = homeLocation;
			isFlyingHome = true;
			return;
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
		

		if(!targetChain.isEmpty()){
			targetLocation = targetChain.poll();
			return;
		}
		
		
		//max 20 tries to find a target.
		int MAX_TRIES = 20;
		double vecDistance = 0;
		do {
			MAX_TRIES --;
			if(MAX_TRIES < 0){
				targetLocation = homeLocation;
				isFlyingHome = true;
				return;
			}
			
			//TODO assuming this are the locations of the Dragon.
			double locationX = location.getX();
			double locationY = (70.0F + this.random.nextFloat() * 50.0F);
			double locationZ = location.getZ();
			
			if (forceGoTo == null  && !isFlyingHome) {
				locationX += (this.random.nextFloat() * 120.0F - 60.0F);
				locationZ += (this.random.nextFloat() * 120.0F - 60.0F);

				double distanceX = dragon.getLocation().getX() - locationX;
				double distanceY = dragon.getLocation().getY() - locationY;
				double distanceZ = dragon.getLocation().getZ() - locationZ;

				vecDistance = distanceX * distanceX + distanceY * distanceY
						+ distanceZ * distanceZ;
				
				
				//check if the target is too far away from the home location. if yes, let's revise it.
				Location tempTargetLocation = new Location(dragon.getBukkitWorld(), locationX, locationY, locationZ);
				if(tempTargetLocation.distanceSquared(homeLocation) > maxHomeDist) continue;
				
				
				if(vecDistance > 100){
					//recalculate Height.
					Location target = new Location(dragon.getLocation().getWorld(), locationX, locationY, locationZ);
					Location currentLocation = dragon.getLocation();
					World world = dragon.getBukkitWorld();
					
					target = world.getHighestBlockAt(target).getLocation();
					target = target.add(0, 10 + random.nextInt(10), 0);
					
					//clear the chain befor repopulating
					targetChain.clear();
					
					boolean stillNotValidPath = true;
					Location toCheckFromStart = currentLocation;
					while(stillNotValidPath){
						Queue<Location> line = Bresenham.line3D(toCheckFromStart, target);
						Iterator<Location> lineIt = line.iterator();
						
						
						while(lineIt.hasNext()){
							Location nextLocation = lineIt.next();
							if(nextLocation.getBlock().getType() != Material.AIR){
								nextLocation = world.getHighestBlockAt(nextLocation).getLocation();
								nextLocation = nextLocation.add(0, 7, 0);
								
								toCheckFromStart = nextLocation;
								targetChain.add(toCheckFromStart);
								break;
							}
							
							if(!lineIt.hasNext()) stillNotValidPath = false;
						}
						
					}
					
					targetChain.add(target);
				}
			} else {
				locationY = location.getY();
				vecDistance = 101;
			}
			
			if(!targetChain.isEmpty()) {
				targetLocation = targetChain.poll();
			}else{
				targetLocation.setX( locationX );
				targetLocation.setY( locationY );
				targetLocation.setZ( locationZ );
			}
		} while (vecDistance < 100);
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#isFlyingHome()
	 */
	@Override
	public boolean isFlyingHome(){
		return isFlyingHome;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#forceFlyingHome(boolean)
	 */
	@Override
	public void forceFlyingHome(boolean flyingHome) {
		isFlyingHome = flyingHome;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getHomeLocation()
	 */
	@Override
	public Location getHomeLocation() {
		return homeLocation.clone();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#setHomeLocation(org.bukkit.Location)
	 */
	@Override
	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation.clone();
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getCurrentTagetsAsStringList()
	 */
	@Override
	public List<String> getCurrentTagetsAsStringList(){
		List<String> list = new LinkedList<String>();
		
		for(LivingEntity target : targets){
			if(target instanceof Player){
				String playerName = ((Player) target).getName();
				list.add(playerName);
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController#getAllCurrentTargets()
	 */
	@Override
	public List<LivingEntity> getAllCurrentTargets(){
		return new LinkedList<LivingEntity>(targets);
	}
}
