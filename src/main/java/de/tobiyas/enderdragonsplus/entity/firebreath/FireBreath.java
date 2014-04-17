package de.tobiyas.enderdragonsplus.entity.firebreath;


import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public class FireBreath {

	private Location location;
	private Vector direction;
	private LimitedED shooter;
	
	private double speed = 1;
	private boolean alive = true;
	private boolean alreadyHit = false;
	
	private BurningBlocksContainer blockContainer;
	
	
	public FireBreath(Location location, Vector direction, LimitedED shooter) {
		this.location = location;
		this.shooter = shooter;

		this.direction = direction.normalize();
		
		blockContainer = new BurningBlocksContainer();
	}
	
	/**
	 * Ticks this pseudo Entity
	 * Returns true if the entity is still alive.
	 * Returns false if the entity is dead and will not be called.
	 * 
	 * @return
	 */
	public boolean tick(){
		if(!alive){
			return false;
		}
		
		checkCollision();
		
		if(alreadyHit){
			spreadFire();
		}else{
			tickLocation();
		}
		
		return alive;
	}
	
	private void checkCollision(){
		Material mat = location.getBlock().getType();
		if(mat != Material.AIR){
			alreadyHit = true;
			blockContainer.addBlock(location);
		}
	}
	
	//spreads the fire one block in all directions
	private void spreadFire(){
		blockContainer.tick();
		if(blockContainer.areAllTicksDone()){
			this.alive = false;
		}
	}
	
	private void tickLocation(){
		this.location = location.add(direction);
		
		location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
	}
	
	/**
	 * Updates speed + Vector of speed
	 * @param newSpeed
	 */
	public void setSpeed(double newSpeed){
		this.speed = newSpeed;
		
		this.direction = direction.normalize().multiply(newSpeed);
	}
	
	public double getSpeed(){
		return speed;
	}

	public LimitedED getShooter() {
		return shooter;
	}
}
