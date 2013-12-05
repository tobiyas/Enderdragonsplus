package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonMoveController {

	protected LimitedEnderDragon dragon;
	protected Random random;
	protected EnderdragonsPlus plugin;
	
	public DragonMoveController(LimitedEnderDragon dragon){
		this.dragon = dragon;
		random = new Random();
		plugin = EnderdragonsPlus.getPlugin();
	}
	
	public boolean checkDragonSits(){
		org.bukkit.entity.Entity target = dragon.getTarget();
		
		return target == null;
	}
	
	/**
	 * Knocks back all entites from the list
	 * 
	 * @param entities
	 */
	public void knockbackNearbyEntities(List<Entity> entities) {
		double pointX = (dragon.br.boundingBox.a + dragon.br.boundingBox.d) / 2;
		double pointZ = (dragon.br.boundingBox.c + dragon.br.boundingBox.f) / 2;

		for (Entity entity : entities) {
			if (entity instanceof EntityLiving) {
				double motX = entity.locX - pointX;
				double motY = 0.2;
				double motZ = entity.locZ - pointZ;
				
				double normalizer = motX * motX + motZ * motZ;
				motX = motX /normalizer * 4;
				motZ = motZ / normalizer * 4;

				entity.g(motX, motY, motZ);
			}
		}
	}
	
	/**
	 * Adjusts the Dragon Movement after a player moved.
	 * 
	 * @param sideMot to move for
	 * @param forMot to move for
	 */
	public void adjustMotAndLocToPlayerMovement(float sideMot, float forMot) {
        //forMot = -forMot;
        //sideMot = -sideMot;
      
        dragon.a(sideMot, forMot, 0.02F);

        //adjust dragon yaw + pitch
        dragon.yaw = (float) MathHelper.g(dragon.passenger.yaw - 180);
        dragon.pitch = dragon.passenger.pitch;
       
        
        //simple move the dragon. Don't use the overloaded NMS method
        moveToDragonMotion();        

        //check for front/ back movement to get the sugar in the height
        if(forMot > 0.1 || forMot < -0.1){
        	float movementChange = (float) (dragon.passenger.pitch * 0.0001);
        	
        	if(forMot < 0){
        		dragon.motY += movementChange;        		
        	}else{
        		dragon.motY -= movementChange;
        	}
        }

        
        final float movementSpeedSlowingMult = 0.91F;
        
        dragon.motY *= 0.9800000190734863D;
        dragon.motX *= (double) movementSpeedSlowingMult;
        dragon.motZ *= (double) movementSpeedSlowingMult;
    }

	
	/**
	 * A simplification of the Dragon move method in {@link Entity#move(double, double, double)}
	 * <br>This also checks for collision!
	 * <br>If an collision is detected, the Motions are inverted.
	 */
	protected void moveToDragonMotion(){
		boolean useSoftCollison = plugin.interactConfig().isConfig_useSoftRidingCollision();
		boolean collisionDetected = dragon.getCollisionController().checkCollisionAndPortals();
		
		if(useSoftCollison){
			Block block = dragon.world.getType(
	        		(int) Math.floor(dragon.locX + dragon.motX), 
	        		(int) Math.floor(dragon.locY + dragon.motY), 
	        		(int) Math.floor(dragon.locZ + dragon.motZ));

	        collisionDetected = block.getMaterial() != Material.AIR;
        }
		
		if(collisionDetected){
			dragon.motX = -dragon.motX;
        	dragon.motY = -dragon.motY;
        	dragon.motZ = -dragon.motZ;
		}
		
		dragon.boundingBox.d(dragon.motX, dragon.motY, dragon.motZ); //simply adds the speed to the AABB
		
        dragon.locX = (dragon.boundingBox.a + dragon.boundingBox.d) / 2.0D;
        dragon.locY = dragon.boundingBox.b + (double) dragon.height - (double) dragon.W;
        dragon.locZ = (dragon.boundingBox.c + dragon.boundingBox.f) / 2.0D;
	}
	
	
	/**
	 * Notify the Player moved the Entity (as Passager).
	 * 
	 * @param motX x direction
	 * @param motY y direction
	 * 
	 * @return true then call super.
	 */
	public boolean playerMovedEntity(float sideMot, float forMot){
		if (dragon.passenger == null || !(dragon.passenger instanceof EntityHuman)) {
	        return true;
	    }
	 
	    dragon.lastYaw = dragon.yaw = (float) MathHelper.g(dragon.passenger.yaw - 180);
	    dragon.pitch = dragon.passenger.pitch * 0.5F;
	 
	    dragon.b(dragon.passenger.yaw, dragon.passenger.pitch); //[url]https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/Entity.java#L155-L158[/url]
	    dragon.aP = dragon.aN = dragon.yaw;
	 
	    dragon.X = 1.0F;  
	 
	    sideMot = ((EntityLiving) dragon.passenger).be * 0.5F;
	    forMot = ((EntityLiving) dragon.passenger).bf;
	 
	    if (forMot <= 0.0F) {
	        forMot *= 0.25F;  
	    }
	    sideMot *= 0.75F;
	 
	    forMot *= 10; //speed up
	    sideMot *= 10;
	    
	    float speed = 5.0F;
	    dragon.i(speed);
	    
	    checkJump();
    	adjustMotAndLocToPlayerMovement(sideMot, forMot);
	    return false;
	}
	
	
	/**
	 * Checks if the Entity is jumping.
	 * If so, the MotY is increased by 0.5.
	 */
	protected void checkJump(){
		try {
		    Field jump = null;
		    jump = EntityLiving.class.getDeclaredField("bd");
		    jump.setAccessible(true);
	 
	        if (jump.getBoolean(dragon.passenger)) {
	            dragon.motY = 0.2;
	        }
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//Logic vars.
	protected boolean doNothingLock;
	
	protected Vector oldSpeed;
	protected Vector oldTarget;
	
	/**
	 * No idea what this does!
	 */
	public float bg = 0f;
	
	
	/**
	 * The Move logic.
	 */
	@SuppressWarnings("unchecked")
	public void moveDragon(){
		dragon.yaw = MathHelper.g(dragon.yaw);

		if (dragon.bo < 0) {
            for (int d05 = 0; d05 < dragon.bn.length; ++d05) {
                dragon.bn[d05][0] = (double) dragon.yaw;
                dragon.bn[d05][1] = dragon.locY;
            }
        }
		
		if (++dragon.bo == dragon.bn.length) {
            dragon.bo = 0;
        }

        dragon.bn[dragon.bo][0] = dragon.yaw;
        dragon.bn[dragon.bo][1] = dragon.locY;


		double oldTargetDistanceX = dragon.h - dragon.locX;
		double oldTargetDistanceY = dragon.i - dragon.locY;
		double oldTargetDistanceZ = dragon.j - dragon.locZ;
		double oldTargetDistancePythagoras = 
				oldTargetDistanceX * oldTargetDistanceX 
				+ oldTargetDistanceY * oldTargetDistanceY 
				+ oldTargetDistanceZ * oldTargetDistanceZ;
		
		Entity currentTarget = dragon.getTargetController().getCurrentTarget();
		boolean attackingMode = true;
		if (currentTarget != null) {
			dragon.h = currentTarget.locX;
			dragon.j = currentTarget.locZ;
			
			double newDragonDistanceX = dragon.h - dragon.locX;
			double newDragonDistanceY = dragon.j - dragon.locZ;
			double newDragonDistancePythagoras = Math.sqrt(newDragonDistanceX * newDragonDistanceX 
					+ newDragonDistanceY * newDragonDistanceY);
			double attackAngleAsHeight = 0.4 + ((newDragonDistancePythagoras / 80D) - 1); //this seams to be the attacking mode.

			if (attackAngleAsHeight > 10.0D) {
				attackAngleAsHeight = 10.0D;
			}

			//set the attacking angle via height
			dragon.i = currentTarget.boundingBox.b + attackAngleAsHeight;
		} else {
			boolean shouldSitDown = plugin.interactConfig().getConfig_dragonsSitDownIfInactive();
			if(!dragon.getTargetController().hasTargets() && !dragon.getTargetController().isFlyingHome() && shouldSitDown){
				attackingMode = false;
				oldSpeed = new Vector()
							.setX(dragon.motX)
							.setY(dragon.motY)
							.setZ(dragon.motZ);
				
				dragon.motX = 0;
				dragon.motY = 0;
				dragon.motZ = 0;
				
				oldTarget = new Vector()
							.setX(dragon.h)
							.setY(dragon.i)
							.setZ(dragon.j);
						
				dragon.h = dragon.locX;
				dragon.i = dragon.locY;
				dragon.j = dragon.locZ;
				dragon.yaw = 0;
				
				Location loc = dragon.getLocation().clone();
				loc.subtract(0, 1, 0);
				if(loc.getBlock().getType() == org.bukkit.Material.AIR){
					dragon.motY = -0.2;
					dragon.i = dragon.locY-0.2;
				}else{
					doNothingLock = true;
				}
			}else{
				dragon.h += random.nextGaussian() * 2D;
				dragon.j += random.nextGaussian() * 2D;
			}
			
		}

		if (dragon.bz || (oldTargetDistancePythagoras < 100.0D) || oldTargetDistancePythagoras > 22500D || dragon.positionChanged
				|| dragon.G) {
			dragon.getTargetController().changeTarget();
		}

		oldTargetDistanceY /= MathHelper.sqrt(oldTargetDistanceX * oldTargetDistanceX + oldTargetDistanceZ * oldTargetDistanceZ);
		float angleAbs = 0.6F; //abs when height - change is too high or too low.
		if (oldTargetDistanceY < -angleAbs) {
			oldTargetDistanceY = -angleAbs;
		}

		if (oldTargetDistanceY > angleAbs) {
			oldTargetDistanceY = angleAbs;
		}
		
		dragon.motY += oldTargetDistanceY * 0.1;
        dragon.yaw = MathHelper.g(dragon.yaw);
        
        
		double toTargetAngle = 180.0D - Math.atan2(oldTargetDistanceX, oldTargetDistanceZ) * 180.0D / Math.PI;
		double toTurnAngle = MathHelper.g(toTargetAngle - (double) dragon.yaw);

		if (toTurnAngle > 50.0D) {
			toTurnAngle = 50.0D;
		}

		if (toTurnAngle < -50.0D) {
			toTurnAngle = -50.0D;
		}

		Vec3D targetVector = dragon.world.getVec3DPool().create(
				dragon.h - dragon.locX, 
				dragon.i - dragon.locY, 
				dragon.j - dragon.locZ
			).a();
		
		double directionDegree = dragon.yaw * Math.PI / 180.0F;
        Vec3D motionVector = dragon.world.getVec3DPool().create(
        		MathHelper.sin((float) directionDegree), 
        		dragon.motY, 
        		-MathHelper.cos((float) directionDegree)
        	).a();
	
		float scaledTargetLength = (float) (motionVector.b(targetVector) + 0.5D) / 1.5F;

		if (scaledTargetLength < 0.0F) {
			scaledTargetLength = 0.0F;
		}

		bg *= 0.8F;
		float motionPythagoras = MathHelper.sqrt(dragon.motX * dragon.motX + dragon.motZ
				* dragon.motZ) + 1;

		if (motionPythagoras > 40.0F) {
			motionPythagoras = 40.0F;
		}

		bg += toTurnAngle * ((0.7 / motionPythagoras) / motionPythagoras);
		dragon.yaw += bg * 0.1;
		directionDegree = dragon.yaw * Math.PI / 180.0F; //recalculation
		float f6 = (float) (2.0D / (motionPythagoras + 1.0D));

		dragon.a(0, -1.0F, 0.06F * (scaledTargetLength * f6 + (1.0F - f6)));
		
		//From tobiyas stop moving when not needed to
		if(!doNothingLock){
			if (dragon.bA) {
				dragon.move(dragon.motX * 0.8, dragon.motY * 0.8, dragon.motZ * 0.8);
			} else {
				dragon.move(dragon.motX, dragon.motY, dragon.motZ);
			}
		}

		Vec3D currentMotionVector = dragon.world.getVec3DPool().create(dragon.motX, dragon.motY, dragon.motZ).a();
		float scaledMotionLength = (float) (currentMotionVector.b(motionVector) + 1.0D) / 2.0F;

		scaledMotionLength *= 0.15F;
		scaledMotionLength += 0.8F;
		
		dragon.motX *= scaledMotionLength;
		dragon.motZ *= scaledMotionLength;
		dragon.motY *= 0.91;

		dragon.aN = dragon.yaw;
        
		//setting Complex parts to some fix values. O_o wtf...
		dragon.bq.width = dragon.bq.length = 3.0F;
        dragon.bs.width = dragon.bs.length = 2.0F;
        dragon.bt.width = dragon.bt.length = 2.0F;
        dragon.bu.width = dragon.bu.length = 2.0F;
        
        dragon.br.length = 3.0F;
        dragon.br.width = 5.0F;
        dragon.bv.length = 2.0F;
        dragon.bv.width = 4.0F;
        dragon.bw.length = 3.0F;
        dragon.bw.width = 4.0F;
        
        
		float f1 = (float) ((dragon.b(5, 1.0F)[1] - dragon.b(10, 1.0F)[1]) * 10.0F / 
				180.0F * Math.PI);
		
		float f2 = MathHelper.cos(f1);
		float f9 = -MathHelper.sin((float) f1);
		
		float f11 = MathHelper.sin((float) directionDegree);
		float f12 = MathHelper.cos((float) directionDegree);

		dragon.br.h();
		dragon.br.setPositionRotation(dragon.locX + (f11 * 0.5F),
				dragon.locY, dragon.locZ - (f12 * 0.5F), 0.0F, 0.0F);
		dragon.bv.h();
		dragon.bv.setPositionRotation(dragon.locX + (f12 * 4.5F),
				dragon.locY + 2.0D, dragon.locZ + (f11 * 4.5F), 0.0F, 0.0F);
		dragon.bw.h();
		dragon.bw.setPositionRotation(dragon.locX - (f12 * 4.5F),
				dragon.locY + 2.0D, dragon.locZ - (f11 * 4.5F), 0.0F, 0.0F);

		if (dragon.hurtTicks == 0 && attackingMode) {
			dragon.getDragonMoveController().knockbackNearbyEntities(dragon.world.getEntities(dragon, dragon.bv.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			dragon.getDragonMoveController().knockbackNearbyEntities(dragon.world.getEntities(dragon, dragon.bw.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			dragon.getDragonHealthController().damageEntities(dragon.world.getEntities(dragon, dragon.bq.boundingBox.grow(1.0D, 1.0D, 1.0D)));
		}

		// LimitedEnderDragon - begin: Added FireBalls here!
		dragon.getFireballController().checkSpitFireBall();
		// LimitedEnderDragon - end

		double[] adouble = dragon.b(5, 1.0F);
		double[] adouble1 = dragon.b(0, 1.0F);

		float f19 = MathHelper.sin((float) (directionDegree - bg * 0.01F));
		float f13 = MathHelper.cos((float)directionDegree
				- bg * 0.01F);

		dragon.bq.h();
		dragon.bq.setPositionRotation(dragon.locX + (f19 * 5.5F * f2),
				dragon.locY + (adouble1[1] - adouble[1])
						+ (f9 * 5.5F), dragon.locZ
						- (f13 * 5.5F * f2), 0, 0);

		for (int j = 0; j < 3; ++j) {
			EntityComplexPart entitycomplexpart = null;

			if (j == 0) {
				entitycomplexpart = dragon.bs;
			}

			if (j == 1) {
				entitycomplexpart = dragon.bt;
			}

			if (j == 2) {
				entitycomplexpart = dragon.bu;
			}

			double[] adouble2 = dragon.b(12 + j * 2, 1F);
			float f14 = (float) (directionDegree + MathHelper.g(adouble2[0] - adouble[0]) * Math.PI / 180F);
			float f15 = MathHelper.sin(f14);
			float f16 = MathHelper.cos(f14);
			float f17 = 1.5F;
			float f18 = (j + 1) * 2.0F;

			entitycomplexpart.h();
			entitycomplexpart.setPositionRotation(dragon.locX - ((f11 * f17 + f15 * f18) * f2), 
					dragon.locY + (adouble2[1] - adouble[1]) * 1.0D - ((f18 + f17) * f9) + 1.5D, 
					dragon.locZ + ((f12 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
		}

		dragon.bA = dragon.getCollisionController().checkHitBlocks(dragon.bq.boundingBox)
				| dragon.getCollisionController().checkHitBlocks(dragon.br.boundingBox);
	}
	
	/**
	 * Restores all Old data
	 */
	public void restoreOldDataIfPossible() {
		doNothingLock = false;
		
		if(oldSpeed != null){
			dragon.motX = oldSpeed.getX();
			dragon.motY = oldSpeed.getY();
			dragon.motZ = oldSpeed.getZ();
			
			oldSpeed = null;
		}
		
		if(oldTarget != null){
			dragon.h = oldTarget.getX();
			dragon.i = oldTarget.getY();
			dragon.j = oldTarget.getZ();
			
			oldTarget = null;
		}
	}
}
