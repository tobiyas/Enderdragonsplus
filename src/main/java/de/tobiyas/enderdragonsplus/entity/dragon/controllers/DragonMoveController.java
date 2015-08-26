package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityComplexPart;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.Material;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Vec3D;

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
		double pointX = (dragon.bo.getBoundingBox().a + dragon.bo.getBoundingBox().d) / 2;
		double pointZ = (dragon.bo.getBoundingBox().c + dragon.bo.getBoundingBox().f) / 2;

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
			Block block = dragon.world.getType(new BlockPosition(
	        		(int) Math.floor(dragon.locX + dragon.motX), 
	        		(int) Math.floor(dragon.locY + dragon.motY), 
	        		(int) Math.floor(dragon.locZ + dragon.motZ))).getBlock();

	        collisionDetected = block.getMaterial() != Material.AIR;
        }
		
		if(collisionDetected){
			dragon.motX = -dragon.motX;
        	dragon.motY = -dragon.motY;
        	dragon.motZ = -dragon.motZ;
		}
		
		//dragon.a sets the bounding box. Since it seems to we can not move the Box... :(
		dragon.a(dragon.getBoundingBox().c(dragon.motX, dragon.motY, dragon.motZ)); //simply adds the speed to the AABB
		
        dragon.locX = (dragon.getBoundingBox().a + dragon.getBoundingBox().d) / 2.0D;
        dragon.locY = dragon.getBoundingBox().b + (double) dragon.getHeadHeight() - (double) dragon.S;
        dragon.locZ = (dragon.getBoundingBox().c + dragon.getBoundingBox().f) / 2.0D;
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
	    dragon.lastYaw = dragon.yaw = (float) MathHelper.g(dragon.passenger.yaw);
	    dragon.pitch = dragon.passenger.pitch * 0.5F;
	 
	    dragon.yaw = dragon.passenger.yaw % 360f;
	    dragon.pitch = dragon.passenger.pitch % 360f;
	    
	    dragon.aI = dragon.aG = dragon.yaw;
	 
	    dragon.S = 1.0F;  
	 
	    sideMot = ((EntityLiving) dragon.passenger).aZ * 0.5F;
	    forMot = ((EntityLiving) dragon.passenger).ba;
	 
	    if (forMot <= 0.0F) {
	        forMot *= 0.25F;  
	    }
	    sideMot *= 0.75F;
	 
	    forMot *= 10; //speed up
	    sideMot *= 10;
	    
	    float speed = 5.0F;
	    dragon.k(speed);
	    
	    checkJump();
    	adjustMotAndLocToPlayerMovement(sideMot, forMot);
	    return false;
	}
	
	
	/**
	 * Checks if the Entity is jumping.
	 * If so, the MotY is increased by 0.5.
	 */
	protected void checkJump(){
		if(dragon.passenger == null) return;
		
        try {
            Field jump = null;
            jump = EntityLiving.class.getDeclaredField("aY");
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
	public void moveDragon(){
		dragon.yaw = MathHelper.g(dragon.yaw);

		if (dragon.bl < 0) {
            for (int d05 = 0; d05 < dragon.bk.length; ++d05) {
                dragon.bk[d05][0] = (double) dragon.yaw;
                dragon.bk[d05][1] = dragon.locY;
            }
        }
		
		if (++dragon.bl == dragon.bk.length) {
            dragon.bl = 0;
        }

        dragon.bk[dragon.bl][0] = dragon.yaw;
        dragon.bk[dragon.bl][1] = dragon.locY;


		double oldTargetDistanceX = dragon.a - dragon.locX;
		double oldTargetDistanceY = dragon.b - dragon.locY;
		double oldTargetDistanceZ = dragon.c - dragon.locZ;
		double oldTargetDistancePythagoras = 
				oldTargetDistanceX * oldTargetDistanceX 
				+ oldTargetDistanceY * oldTargetDistanceY 
				+ oldTargetDistanceZ * oldTargetDistanceZ;
		
		Entity currentTarget = dragon.getTargetController().getCurrentTarget();
		boolean attackingMode = true;
		if (currentTarget != null) {
			dragon.a = currentTarget.locX;
			dragon.c = currentTarget.locZ;
			
			double newDragonDistanceX = dragon.a - dragon.locX;
			double newDragonDistanceY = dragon.c - dragon.locZ;
			double newDragonDistancePythagoras = Math.sqrt(newDragonDistanceX * newDragonDistanceX 
					+ newDragonDistanceY * newDragonDistanceY);
			double attackAngleAsHeight = 0.4 + ((newDragonDistancePythagoras / 80D) - 1); //this seams to be the attacking mode.

			if (attackAngleAsHeight > 10.0D) {
				attackAngleAsHeight = 10.0D;
			}

			//set the attacking angle via height
			dragon.b = currentTarget.getBoundingBox().b + attackAngleAsHeight;
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
							.setX(dragon.a)
							.setY(dragon.b)
							.setZ(dragon.c);
						
				dragon.a = dragon.locX;
				dragon.b = dragon.locY;
				dragon.c = dragon.locZ;
				dragon.yaw = 0;
				
				Location loc = dragon.getLocation().clone();
				loc.subtract(0, 1, 0);
				if(loc.getBlock().getType() == org.bukkit.Material.AIR){
					dragon.motY = -0.2;
					dragon.b = dragon.locY-0.2;
				}else{
					doNothingLock = true;
				}
			}else{
				dragon.a += random.nextGaussian() * 2D;
				dragon.c += random.nextGaussian() * 2D;
			}
			
		}

		if (dragon.bw || (oldTargetDistancePythagoras < 100.0D) || oldTargetDistancePythagoras > 22500D || dragon.positionChanged
				|| dragon.E) {
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

		Vec3D targetVector = new Vec3D(
				dragon.a - dragon.locX, 
				dragon.b - dragon.locY, 
				dragon.c - dragon.locZ
			).a();
		
		double directionDegree = dragon.yaw * Math.PI / 180.0F;
        Vec3D motionVector = new Vec3D(
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
			if (dragon.bx) {
				dragon.move(dragon.motX * 0.8, dragon.motY * 0.8, dragon.motZ * 0.8);
			} else {
				dragon.move(dragon.motX, dragon.motY, dragon.motZ);
			}
		}

		Vec3D currentMotionVector = new Vec3D(dragon.motX, dragon.motY, dragon.motZ).a();
		float scaledMotionLength = (float) (currentMotionVector.b(motionVector) + 1.0D) / 2.0F;

		scaledMotionLength *= 0.15F;
		scaledMotionLength += 0.8F;
		
		dragon.motX *= scaledMotionLength;
		dragon.motZ *= scaledMotionLength;
		dragon.motY *= 0.91;

		dragon.aG = dragon.yaw;
        
		//setting Complex parts to some fix values. O_o wtf...
		dragon.bn.width = dragon.bn.length = 3.0F;
        dragon.bp.width = dragon.bp.length = 2.0F;
        dragon.bq.width = dragon.bq.length = 2.0F;
        dragon.br.width = dragon.br.length = 2.0F;
        
        dragon.bo.length = 3.0F;
        dragon.bo.width = 5.0F;
        dragon.bs.length = 2.0F;
        dragon.bs.width = 4.0F;
        dragon.bt.length = 3.0F;
        dragon.bt.width = 4.0F;
        
        
		float f1 = (float) ((dragon.b(5, 1.0F)[1] - dragon.b(10, 1.0F)[1]) * 10.0F / 
				180.0F * Math.PI);
		
		float f2 = MathHelper.cos(f1);
		float f9 = -MathHelper.sin((float) f1);
		
		float f11 = MathHelper.sin((float) directionDegree);
		float f12 = MathHelper.cos((float) directionDegree);

		dragon.bo.t_();
		dragon.bo.setPositionRotation(dragon.locX + (f11 * 0.5F),
				dragon.locY, dragon.locZ - (f12 * 0.5F), 0.0F, 0.0F);
		dragon.bs.t_();
		dragon.bs.setPositionRotation(dragon.locX + (f12 * 4.5F),
				dragon.locY + 2.0D, dragon.locZ + (f11 * 4.5F), 0.0F, 0.0F);
		dragon.bt.t_();
		dragon.bt.setPositionRotation(dragon.locX - (f12 * 4.5F),
				dragon.locY + 2.0D, dragon.locZ - (f11 * 4.5F), 0.0F, 0.0F);

		if (dragon.hurtTicks == 0 && attackingMode) {
			dragon.getDragonMoveController().knockbackNearbyEntities(dragon.world.getEntities(dragon, dragon.bs.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0)));
			dragon.getDragonMoveController().knockbackNearbyEntities(dragon.world.getEntities(dragon, dragon.bt.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0)));
			dragon.getDragonHealthController().damageEntities(dragon.world.getEntities(dragon, dragon.bn.getBoundingBox().grow(1.0D, 1.0D, 1.0D)));
		}

		// LimitedEnderDragon - begin: Added FireBalls here!
		dragon.getFireballController().checkSpitFireBall();
		// LimitedEnderDragon - end

		double[] adouble = dragon.b(5, 1.0F);
		double[] adouble1 = dragon.b(0, 1.0F);

		float f19 = MathHelper.sin((float) (directionDegree - bg * 0.01F));
		float f13 = MathHelper.cos((float)directionDegree
				- bg * 0.01F);

		dragon.bn.t_();
		dragon.bn.setPositionRotation(dragon.locX + (f19 * 5.5F * f2),
				dragon.locY + (adouble1[1] - adouble[1])
						+ (f9 * 5.5F), dragon.locZ
						- (f13 * 5.5F * f2), 0, 0);

		for (int j = 0; j < 3; ++j) {
			EntityComplexPart entitycomplexpart = null;

			if (j == 0) {
				entitycomplexpart = dragon.bp;
			}

			if (j == 1) {
				entitycomplexpart = dragon.bq;
			}

			if (j == 2) {
				entitycomplexpart = dragon.br;
			}

			double[] adouble2 = dragon.b(12 + j * 2, 1F);
			float f14 = (float) (directionDegree + MathHelper.g(adouble2[0] - adouble[0]) * Math.PI / 180F);
			float f15 = MathHelper.sin(f14);
			float f16 = MathHelper.cos(f14);
			float f17 = 1.5F;
			float f18 = (j + 1) * 2.0F;

			entitycomplexpart.t_();
			entitycomplexpart.setPositionRotation(dragon.locX - ((f11 * f17 + f15 * f18) * f2), 
					dragon.locY + (adouble2[1] - adouble[1]) * 1.0D - ((f18 + f17) * f9) + 1.5D, 
					dragon.locZ + ((f12 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
		}

		dragon.bx = dragon.getCollisionController().checkHitBlocks(dragon.bn.getBoundingBox())
				| dragon.getCollisionController().checkHitBlocks(dragon.bo.getBoundingBox());
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
			dragon.a = oldTarget.getX();
			dragon.b = oldTarget.getY();
			dragon.c = oldTarget.getZ();
			
			oldTarget = null;
		}
	}
}
