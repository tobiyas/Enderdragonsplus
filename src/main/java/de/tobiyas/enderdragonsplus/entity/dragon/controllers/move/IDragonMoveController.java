package de.tobiyas.enderdragonsplus.entity.dragon.controllers.move;

import java.util.List;

import org.bukkit.entity.LivingEntity;

public interface IDragonMoveController {

	public abstract boolean checkDragonSits();

	/**
	 * Knocks back all entites from the list
	 * 
	 * @param entities
	 */
	public abstract void knockbackNearbyEntities(List<LivingEntity> entities);

	/**
	 * Adjusts the Dragon Movement after a player moved.
	 * 
	 * @param sideMot to move for
	 * @param forMot to move for
	 */
	public abstract void adjustMotAndLocToPlayerMovement(float forMot,
			float sideMot);

	/**
	 * Notify the Player moved the Entity (as Passager).
	 * 
	 * @param motX x direction
	 * @param motY y direction
	 * 
	 * @return true then call super.
	 */
	public abstract boolean playerMovedEntity(float sideMot, float forMot);

	/**
	 * This is the new Rewritten dragon move logic.
	 */
	public abstract void moveDragon();

	/**
	 * Restores all Old data
	 */
	public abstract void restoreOldDataIfPossible();

}