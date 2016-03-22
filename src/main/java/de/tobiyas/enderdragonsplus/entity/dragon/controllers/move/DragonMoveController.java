package de.tobiyas.enderdragonsplus.entity.dragon.controllers.move;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public class DragonMoveController implements IDragonMoveController {

	protected LimitedED dragon;
	protected Random random;
	protected EnderdragonsPlus plugin;
	protected boolean collision = true;
	
	
	protected final double DRAGON_MOVE_SPEED = 1;
	
	public DragonMoveController(LimitedED dragon){
		this.dragon = dragon;
		random = new Random();
		plugin = EnderdragonsPlus.getPlugin();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#checkDragonSits()
	 */
	@Override
	public boolean checkDragonSits(){
		org.bukkit.entity.Entity target = dragon.getTarget();
		
		return target == null;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#knockbackNearbyEntities(java.util.List)
	 */
	@Override
	public void knockbackNearbyEntities(List<LivingEntity> entities) {
		double pointX = (dragon.getMinBBLocation().getX() + dragon.getMaxBBLocation().getX()) / 2;
		double pointZ = (dragon.getMinBBLocation().getZ() + dragon.getMaxBBLocation().getZ()) / 2;

		for (LivingEntity entity : entities) {
			if (entity != null) {
				double motX = entity.getLocation().getX() - pointX;
				double motY = 0.2;
				double motZ = entity.getLocation().getZ() - pointZ;
				
				double normalizer = motX * motX + motZ * motZ;
				motX = motX /normalizer * 4;
				motZ = motZ / normalizer * 4;

				entity.setVelocity(new Vector(motX, motY, motZ));
			}
		}
	}
	
	
	/**
	 * Rounds an arc to -180° till 180°
	 * @param toRound
	 * @return
	 */
	protected float arc(float toRound){
		toRound %= 360.0F;
        if (toRound >= 180.0F) toRound -= 360.0F;

        if (toRound < -180.0F) toRound += 360.0F;

        return toRound;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#adjustMotAndLocToPlayerMovement(float, float)
	 */
	@Override
	public void adjustMotAndLocToPlayerMovement(float forMot, float sideMot) {
		if(sideMot == Float.NaN) sideMot = 0;
		if(forMot == Float.NaN) forMot = 0;

		//forMot = -forMot;
        //sideMot = -sideMot;
      
		//It seems like the method has changed directions for MC 1.7.5
		//This means arg1 + arg2 are exchanged.
        dragon.callSuperRiding(sideMot, forMot, 0.2F);
        

        //adjust dragon yaw + pitch
        dragon.setYaw((float) arc(dragon.getYaw() - 180));
        dragon.setPitch(dragon.getPassenger().getLocation().getPitch());
       
        
        //check for front/ back movement to get the sugar in the height
        if(forMot > 0.1 || forMot < -0.1){
        	float movementChange = (float) (dragon.getPassenger().getLocation().getPitch() * 0.001);
        	
        	if(forMot < 0){
        		Vector vec = dragon.getMotion();
        		vec.add(new Vector(0, movementChange, 0));
        		dragon.setMotion(vec);
        	}else{
        		Vector vec = dragon.getMotion();
        		vec.add(new Vector(0, -movementChange, 0));
        		dragon.setMotion(vec);
        	}
        }

        //simple move the dragon. Don't use the overloaded NMS method
        moveToDragonMotion();        
        
        final float movementSpeedSlowingMult = 0.91F;
        
        Vector vec = dragon.getMotion();
        vec.multiply(movementSpeedSlowingMult);
        dragon.setMotion(vec);
    }

	
	/**
	 * A simplification of the Dragon move method in {@link Entity#move(double, double, double)}
	 * <br>This also checks for collision!
	 * <br>If an collision is detected, the Motions are inverted.
	 */
	protected void moveToDragonMotion(){
		
		boolean useSoftCollison = dragon.getCollisionController().hasCollision() && plugin.interactConfig().isConfig_useSoftRidingCollision();
		boolean collisionDetected = dragon.getCollisionController().checkCollisionAndPortals();
		
		Location dragonLoc = dragon.getLocation();
		Vector motion = dragon.getMotion();
		
		if(useSoftCollison){
			Block block = dragon.getBukkitWorld().getBlockAt(
	        		(int) Math.floor(dragonLoc.getX() + motion.getX()), 
	        		(int) Math.floor(dragonLoc.getY() + motion.getY()), 
	        		(int) Math.floor(dragonLoc.getZ() + motion.getZ())
	        );

	        collisionDetected = block.getType() != Material.AIR;
        }
		
		if(collisionDetected){
			motion = motion.multiply(-1);
			dragon.setMotion(motion);
			
		}
		
		dragon.move(motion);
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#playerMovedEntity(float, float)
	 */
	@Override
	public boolean playerMovedEntity(float sideMot, float forMot){
		if (dragon.getPassenger() == null || !(dragon.getPassenger() instanceof Player)) {
	        return true;
	    }
	 
		double calculationYaw = arc(dragon.getPassenger().getLocation().getYaw() - 180);
		dragon.setLastYaw((float)calculationYaw);

	    dragon.setYaw(dragon.getPassenger().getLocation().getYaw() % 360.0F);
	    dragon.setPitch(dragon.getPassenger().getLocation().getPitch() % 360.0F);
	 
	    sideMot = dragon.getPassengerSideMot() * 0.5F;
	    forMot = dragon.getPassengerForMot();
	 
	    if (forMot <= 0.0F) {
	        forMot *= 0.25F;  
	    }
	    sideMot *= 0.75F;
	 
	    forMot *= 10; //speed up
	    sideMot *= 10;
	    
	    if(checkJump()){
    		Vector mot = dragon.getMotion();
    		dragon.setMotion(mot.setY(0.4));
	    }
	    
    	adjustMotAndLocToPlayerMovement(sideMot, forMot);
	    return false;
	}
	
	
	/**
	 * The Cached jump field.
	 */
	private Field jumpFieldCache;
	
	/**
	 * Checks if the Entity is jumping.
	 * If so, the MotY is increased by 0.5.
	 */
	protected boolean checkJump(){
		try {
			Field jump = null;
			if(jumpFieldCache != null) jump = jumpFieldCache;

			Object entity =  dragon.getPassenger();
			Method getHandle = entity.getClass().getDeclaredMethod("getHandle");
			Object mcEntity = getHandle.invoke(entity);
			
			Class<?> mcEntityClass = mcEntity.getClass();
			
		    int i = 5;
		    while (mcEntityClass != null && mcEntityClass != Object.class && i > 0){
		    	try{
		    		if(jump == null){
				    	jump = mcEntityClass.getDeclaredField(dragon.getPlayerIsJumpingFieldName());
				    	jump.setAccessible(true);
		    		}
		    		
			    	if (jump != null){
			    		return jump.getBoolean(mcEntity);
			    	}
			    	
		    	}catch(Throwable exp){
		    		//when it did not work, try to go to the super class.
		    		mcEntityClass = mcEntityClass.getSuperclass();
		    	}finally{
		    		i--;
		    	}
		    }
	    } catch (Throwable exp) {
	        exp.printStackTrace();
	    }
		
		return false;
	}
	
	
	
	//Logic vars.
	protected boolean doNothingLock;
	
	protected Vector oldSpeed;
	protected Vector oldTarget;
	
	/**
	 * No idea what this does!
	 */
	public float bf = 0f;
	
	
	/**
	 * The last angle of this location.
	 */
	private double lastAngle = 0;
	
	/**
	 * For how many ticks the entity slows down.
	 */
	private long slowForXTicks = 0;
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#moveDragon()
	 */
	@Override
	public void moveDragon(){
		EnderDragon bukkitDragon = (EnderDragon) dragon.getBukkitEntity();
		//reduce the no dmg ticks.
		int noDmgTicks = bukkitDragon.getNoDamageTicks();
		if(noDmgTicks != 0) bukkitDragon.setNoDamageTicks(noDmgTicks--);
		
		//dead entities need no ticks.
		if(bukkitDragon.isDead()) return;
		
		Location currentLoc = bukkitDragon.getLocation();
		Location targetLocation = dragon.getTargetLocation();
		boolean shouldSitDown = plugin.interactConfig().getConfig_dragonsSitDownIfInactive();
		
		//first we check for new Targets.
		if(targetLocation == null) dragon.getTargetController().changeTarget();
		targetLocation = dragon.getTargetLocation();
		
		
		//we need to change our target if we are too close or to far away.
		double currentDist = currentLoc.distanceSquared(targetLocation);
		if(currentDist < 100 || currentDist > 150 * 150){
			dragon.getTargetController().changeTarget();
			targetLocation = dragon.getTargetController().getTargetLocation();
		}
		
		
		//second we check for incoming damage.
		
		
		//third we check if we need to sit down.
		if(dragon.getTargetController().getCurrentTarget() == null 
				&& shouldSitDown) {
			//dragon is going down.
			Location nextlocation = currentLoc.subtract(0, 0.2, 0);
			if(nextlocation.getBlock().getType() != Material.AIR){
				dragon.move(0, -0.2, 0);
				return;
			}else{
				dragon.move(0, 0, 0);
				
			}
			
			return;
		}
		
		//4.1 we need to calc the next position.
		double vecX = targetLocation.getX() - currentLoc.getX();
		double vecY = targetLocation.getY() - currentLoc.getY();
		double vecZ = targetLocation.getZ() - currentLoc.getZ();
		
		//so we multiply this to get some speed.
		//The flight direction is also the motion of the dragon.
		Vector flightDirection = new Vector(vecX, vecY, vecZ);
		
		flightDirection = flightDirection.normalize().multiply(DRAGON_MOVE_SPEED);
		
		
		double currentAngle = flightDirection.angle(new Vector(0,0,1));
		double diff = lastAngle == 0 ? 0 : Math.abs(lastAngle - currentAngle);
		
		lastAngle = currentAngle;
		//we have a really rapid turn. Let's slow it down a bit.
		if(diff > 0.1){
			slowForXTicks = (int)(20D / DRAGON_MOVE_SPEED);
			flightDirection = flightDirection.normalize().multiply(0.001);
		}
		
		if(slowForXTicks > 0){
			slowForXTicks --;
			//slow down while turning.
			flightDirection = flightDirection.normalize().multiply(0.001);
		}
		
		
		//This is the next location the dragon wants to fly.
		Location dragonNextMovePostion = currentLoc.add(flightDirection);
		
		
		
		//4.2.we check for collision.
		if(collision){
			//we define first which material we call 'solid'
			List<Material> solidMaterial = Arrays.asList(new Material[]{
					Material.ENDER_STONE, Material.OBSIDIAN, Material.BEDROCK
			});
			
			if(solidMaterial.contains(dragonNextMovePostion.getBlock().getType())){
				//we have a collision.
				flightDirection = flightDirection.multiply(-1);
				dragon.move(flightDirection);
				return;
			}
		}
		
		
		//4.3 set pitch /yaw Correctly.
		float yaw = calcYawFromVec(flightDirection);
		float pitch = calcPitchFromVec(flightDirection);
		
		dragon.setYaw(yaw);
		dragon.setPitch(pitch);
		
		
		//4.4 we Move our dragon
		dragon.move(flightDirection);
		

		
		//6. we check for outgoing damage
		if (noDmgTicks == 0) {
			List<LivingEntity> entitiesInDragonRange = getEntitiesInRange();
			
			entitiesInDragonRange = dragon.getDragonHealthController().damageEntities(entitiesInDragonRange);
			//only knockoff those who got damage
			knockbackNearbyEntities(entitiesInDragonRange);
		}
		
		
		//7. we check for fireballs
		dragon.getFireballController().checkSpitFireBall();
	}
	
	/**
	 * Calculates the Pitch from the vector passed.
	 * 
	 * @param vec to calc.
	 * @return
	 */
	protected float calcPitchFromVec(Vector vec){
		float arc = vec.clone().setY(0).angle(new Vector(1,0,0));
		return arc;
	}

	/**
	 * Calculates the Yaw from the vector passed.
	 * 
	 * @param vec to calc.
	 * @return
	 */
	protected float calcYawFromVec(Vector vec){
		double dx = vec.getX();
        double dz = vec.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        
        return (float) (-yaw * 180 / Math.PI - 180);
	}
	
	private List<LivingEntity> getEntitiesInRange() {
		final double range = 5;
		final double rangeSquare = range * range;
		
		Location dragonLoc = dragon.getLocation();
		
		List<LivingEntity> inRange = new LinkedList<LivingEntity>();
		
		List<Entity> nearby = dragon.getBukkitEntity().getNearbyEntities(range, range, range);
		for(Entity entity : nearby){
			if(entity instanceof LivingEntity
					&& entity.getLocation().distanceSquared(dragonLoc) < rangeSquare){
				LivingEntity target = (LivingEntity) entity;
				
				//check if the dragon can already deal damage to those poor creatures.
				if(target.getNoDamageTicks() <= 0) inRange.add(target);
			}
		}
		
		return inRange;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController#restoreOldDataIfPossible()
	 */
	@Override
	public void restoreOldDataIfPossible() {
		doNothingLock = false;
		
		if(oldSpeed != null){
			dragon.setMotion(oldSpeed);
			oldSpeed = null;
		}
		
		if(oldTarget != null){
			dragon.getTargetController().setNewTarget(oldTarget.toLocation(dragon.getBukkitWorld()), false);
			oldTarget = null;
		}
	}

	@Override
	public boolean hasCollision() {
		return collision;
	}

	@Override
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
}
