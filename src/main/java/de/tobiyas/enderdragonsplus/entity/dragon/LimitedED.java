package de.tobiyas.enderdragonsplus.entity.dragon;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.collision.ICollisionController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot.IItemLootController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.PropertyController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController;

public interface LimitedED {

	/**
	 * This checks if the Dragon CAN be supported on the Current Server.
	 * @return
	 */
	public abstract boolean isSupportedOnCurrentServer();
	
	
	public abstract String getName();

	/**
	 * This deals damage to the Dragon.
	 * 
	 * @param damagesource from where it happens.
	 * @param amount the amount
	 * 
	 * @return true if worked.
	 */
	public abstract boolean dealDamage(DamageCause damagesource, float amount);

	
	/**
	 * This is the internal logic tick doing everything.
	 */
	public abstract void internalLogicTick();

	
	/**
	 * This is for riding dragons.
	 * The player moved the entity.
	 * 
	 * @param motX x-direction
	 * @param motY y-direction
	 * 
	 * @return if true, super is called.
	 */
	public boolean playerMovedEntity(float motX, float motY);

	/**
	 * Damges the dragon.
	 * 
	 * @param cause
	 * @param value
	 * @return
	 */
	public boolean damage(DamageCause cause, double value);
	
	
	public abstract boolean spitFireBallOnTarget(LivingEntity target);

	/**
	 * Fires a Fireball to a location.
	 * 
	 * @param location to fire to.
	 * @return true if worked.
	 */
	public abstract boolean spitFireBallOnTarget(Location location);

	/**
	 * This is called when the NBT stuff should populate.
	 * They have to be casted the the NMS class of the Version of course.
	 * 
	 * @param compound to load.
	 */
	public abstract void loadAdditionalNBTStuffAndCreateControllers(Object compound);

	/**
	 * Saving additional dragon data to NBT Compound
	 * They have to be casted the the NMS class of the Version of course.
	 */
	public abstract void saveAdditionalNBTStuff(Object compound);

	public abstract void remove();

	public abstract int getExpReward();

	public abstract Location getLocation();
	
	/**
	 * This ONLY sets X,Y,Z NO pitch or YAW.
	 * @param loc to set.
	 */
	public abstract void setNativeLocation(Location loc);

	
	public abstract boolean spawn();

	public abstract Location getHomeLocation();

	public abstract int getID();

	public abstract boolean isFlyingHome();

	public abstract void setTarget(LivingEntity entity);

	public abstract LivingEntity getTarget();

	public abstract int getLogicCalls();

	public abstract void goToLocation(Location location);

	public abstract void changeUUID(UUID uID);

	public abstract UUID getUUID();

	public abstract Location getForceLocation();

	public abstract void addEnemy(LivingEntity entity);

	public abstract boolean isInRange(Location loc, double range);

	public abstract Map<String, Float> getPlayerDamageDone();

	public abstract String getLastPlayerAttacked();

	public abstract float getDamageByPlayer(String player);

	public abstract double getMeeleDamage();

	public abstract List<ItemStack> generateLoot();

	public abstract String getAgeName();

	public abstract boolean isHostile();

	public abstract void forceFlyHome(boolean flyingHome);

	public abstract void setNewHome(Location newHomeLocation);

	public abstract void setProperty(String property, Object value);

	public abstract Object getProperty(String property);

	public abstract AgeContainer getAgeContainer();

	public abstract List<LivingEntity> getAllTargets();

	public abstract Location getTargetLocation();

	public abstract IFireballController getFireballController();

	public abstract void setFireballController(
			IFireballController fireballController);

	public abstract ITargetController getTargetController();

	public abstract void setTargetController(ITargetController targetController);

	public abstract IItemLootController getItemController();

	public abstract void setItemController(IItemLootController itemController);

	public abstract IDragonHealthContainer getDragonHealthController();

	public abstract void setDragonHealthController(
			IDragonHealthContainer dragonHealthController);

	public abstract IDragonMoveController getDragonMoveController();

	public abstract void setDragonMoveController(
			IDragonMoveController dragonMoveController);

	public abstract PropertyController getPropertyController();

	public abstract void setPropertyController(
			PropertyController propertyController);

	public abstract ICollisionController getCollisionController();

	public abstract void setCollisionController(
			ICollisionController collisionController);
	
	/**
	 * Returns the Bukkit entity this dragon belongs to
	 * @return
	 */
	public abstract Entity getBukkitEntity();
	
	/**
	 * Returns the current world of the Dragon
	 * @return
	 */
	public abstract World getBukkitWorld();
	
	
	/**
	 * This returns the minimum Location of the Bounding Box of the Dragon.
	 * 
	 * @return the loc of the Min bb.
	 */
	public abstract Location getMinBBLocation();
	
	
	/**
	 * This returns the maximum Location of the Bounding Box of the Dragon.
	 * 
	 * @return the loc of the Max bb.
	 */
	public abstract Location getMaxBBLocation();
	
	
	//handles pitching
	public abstract float getPitch();
	public abstract void setPitch(float newPitch);
	
	//handles lastYaw
	public abstract float getLastYaw();
	public abstract void setLastYaw(float newLastYaw);
	
	//handles yawing
	public abstract float getYaw();
	public abstract void setYaw(float newYaw);
	
	
	/**
	 * This gets the passenger of the Entity.
	 * 
	 * @return the passenger.
	 */
	public Entity getPassenger();
	
	/**
	 * This sets the Passenger of the Entity.
	 */
	public void setPassenger(Entity passenger);

	public float getPassengerSideMot();
	public float getPassengerForMot();

	public void setPassengerSideMot(float newValue);
	public void setPassengerForMot(float newValue);
	

	//This will be set natively.
	public abstract Vector getMotion();
	public abstract void setMotion(Vector vec);
	
	public abstract void move(double x, double y, double z);
	public abstract void move(Vector directionToMove);
	
	
	/**
	 * Sets the Health of the dragon.
	 * 
	 * @param newHealth to set
	 */
	public void sHealth(double newHealth);
	
	/**
	 * Sets the Max health of the Dragon.
	 * 
	 * @param maxHealth to set.
	 */
	public void sMaxHealth(double maxHealth);
	
	/**
	 * gets the Health of the dragon.
	 * 
	 * @return the current Health
	 */
	public double gHealth();
	
	/**
	 * gets the Max health of the Dragon.
	 * 
	 * @return maxHealth.
	 */
	public double gMaxHealth();
	
	
	/**
	 * This calls the super method for riding.
	 * 
	 * @param sideMot
	 * @param forMot
	 */
	public void callSuperRiding(float sideMot, float forMot, float speed);
	
	/**
	 * Returns the field name for the rider.
	 * The Field determins if the player is jumping.
	 * 
	 * @return
	 */
	public String getPlayerIsJumpingFieldName();
	
	
	public boolean hasCollision();
	
	
	public void setCollision(boolean collison);


}