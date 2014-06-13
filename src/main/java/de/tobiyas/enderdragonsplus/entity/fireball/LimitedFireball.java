package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.World;



public interface LimitedFireball {

	
	/**
	 * Speeds the Fireball up.
	 * 
	 * @param speed to speed up.
	 */
	public void speedUp(double speed);

	/**
	 * Spawns the fireball in the World.
	 * 
	 * @param world to spawn in.
	 */
	public void spawnIn(World world);
	

}
