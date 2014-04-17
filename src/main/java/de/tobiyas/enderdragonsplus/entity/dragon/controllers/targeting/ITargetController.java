package de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public interface ITargetController {

	public abstract void addTarget(LivingEntity entity);

	public abstract void removeTarget(LivingEntity entity);

	public abstract void clearTargets();

	public abstract boolean switchTargetsWithMode();

	/**
	 * Gets X (number) targets in range (range)
	 * 
	 * @param number, the number of targets to search
	 * @param range, the range to check
	 * @return a List<Entity> with the given specs
	 */
	public abstract LinkedList<LivingEntity> getTargetsInRange(int number,
			double range);

	/**
	 * Checks if a Entity is in Range range of the dragon
	 * 
	 * @param entity, the Entity to check the range
	 * @param range, the range to check
	 * @return if the Entity is in range of the Dragon
	 */
	public abstract boolean isInRange(Location entityLocation, double range);

	//Getter - Setter
	//Targets
	public abstract Location getTargetLocation();

	public abstract Location getForceGoTo();

	public abstract LivingEntity getCurrentTarget();

	public abstract Location getDragonLocation();

	public abstract LimitedED getDragon();

	public abstract boolean hasTargets();

	//Locking
	public abstract void lockTarget();

	public abstract void unlockTarget();

	public abstract boolean getLock();

	//Forcing
	public abstract void forceTarget(Location loc);

	public abstract void forceTarget(LivingEntity entity);

	//Hostile
	public abstract boolean isHostile();

	public abstract void setHostile(boolean isHostile);

	public abstract void changeTarget();

	public abstract void setNewTarget(Location location, boolean lockTarget);

	public abstract boolean isFlyingHome();

	public abstract void forceFlyingHome(boolean flyingHome);

	public abstract Location getHomeLocation();

	public abstract void setHomeLocation(Location homeLocation);

	/**
	 * Returns an NBTTagList containing the current targets.
	 * This is for storing / restoring the Entities.
	 * 
	 * @return
	 */
	public abstract List<String> getCurrentTagetsAsStringList();

	/**
	 * Returns a COPIED list of targets from the Dragon
	 * 
	 * @return
	 */
	public abstract List<LivingEntity> getAllCurrentTargets();

}