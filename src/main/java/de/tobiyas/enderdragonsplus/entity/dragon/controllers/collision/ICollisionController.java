package de.tobiyas.enderdragonsplus.entity.dragon.controllers.collision;

import org.bukkit.Location;

public interface ICollisionController {

	/**
	 * Checks if the Dragon collides with anything.
	 * <br>Return true if collision detected.
	 * 
	 * @return true if colliding.
	 */
	public abstract boolean checkCollisionAndPortals();

	/**
	 * Checks if the Dragon hits a block.
	 * 
	 * @param axisalignedbb to check against.
	 * 
	 * @return true if hit a block, false if not.
	 */
	public abstract boolean checkHitBlocks(Location min, Location max);

}