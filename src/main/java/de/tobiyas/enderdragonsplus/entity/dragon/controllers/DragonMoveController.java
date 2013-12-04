package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.Explosion;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;

import org.bukkit.Bukkit;
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
	
	
	public void e(float sideMot, float forMot) {
        double d0;

        forMot = -forMot;
        sideMot = -sideMot;
       
        float f2 = 0.91F;

        if (dragon.onGround) {
            f2 = dragon.world.getType(MathHelper.floor(dragon.locX), MathHelper.floor(dragon.boundingBox.b) - 1, MathHelper.floor(dragon.locZ)).frictionFactor * 0.91F;
        }

        float f3 = 0.16277136F / (f2 * f2 * f2);
        float f4;

        if (dragon.onGround) {
            f4 = dragon.bl() * f3;
        } else {
            f4 = dragon.aR;
        }

        dragon.a(sideMot, forMot, f4);
        f2 = 0.91F;
        if (dragon.onGround) {
            f2 = dragon.world.getType(MathHelper.floor(dragon.locX), MathHelper.floor(dragon.boundingBox.b) - 1, MathHelper.floor(dragon.locZ)).frictionFactor * 0.91F;
        }

        if (dragon.h_()) {
            float f5 = 5.50F;

            if (dragon.motX < (double) (-f5)) {
                dragon.motX = (double) (-f5);
            }

            if (dragon.motX > (double) f5) {
                dragon.motX = (double) f5;
            }

            if (dragon.motZ < (double) (-f5)) {
                dragon.motZ = (double) (-f5);
            }

            if (dragon.motZ > (double) f5) {
                dragon.motZ = (double) f5;
            }

            dragon.fallDistance = 0.0F;
            if (dragon.motY < -f5) {
                dragon.motY = -f5;

            }
            if (dragon.motY > f5) {
            	dragon.motY = f5;
            }
        }

        //adjust dragon yaw + pitch
        dragon.yaw = (float) MathHelper.g(dragon.passenger.yaw - 180);
        dragon.pitch = dragon.passenger.pitch;

        dragon.move(dragon.motX, dragon.motY, dragon.motZ);
        if (dragon.positionChanged && dragon.h_()) {
            dragon.motY = 0.2D;
        }
        

        if(forMot > 0.1 || forMot < -0.1){
        	float movementChange = (float) (dragon.passenger.pitch * 0.0001);
        	
        	if(forMot < 0){
        		dragon.motY -= movementChange;        		
        	}else{
        		dragon.motY += movementChange;
        	}
        }
        
        dragon.motY *= 0.9800000190734863D;
        dragon.motX *= (double) f2;
        dragon.motZ *= (double) f2;

        dragon.aF = dragon.aG;
        d0 = dragon.locX - dragon.lastX;
        double d1 = dragon.locZ - dragon.lastZ;
        float f6 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

        if (f6 > 1.0F) {
            f6 = 1.0F;
        }

        dragon.aG += (f6 - dragon.aG) * 0.4F;
        dragon.aH += dragon.aG;
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
	 
	    // Set the entity's pitch, yaw, head rotation etc.
	    dragon.b(dragon.yaw, dragon.pitch); //[url]https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/Entity.java#L155-L158[/url]
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
	    
    	e(sideMot, forMot);
	    return false;
	}
	
	/**
	 * The Explosion source as Const
	 */
	protected final Explosion explosionSource = new Explosion(null, dragon, Double.NaN, Double.NaN, Double.NaN, Float.NaN); // CraftBukkit - reusable source for CraftTNTPrimed.getSource()
	
	
	// Original: a(AxisAlignedBB axisalignedbb)
	@SuppressWarnings("deprecation")
	public boolean checkHitBlocks(AxisAlignedBB axisalignedbb) {
		int pos1X = MathHelper.floor(axisalignedbb.a);
		int pos1Y = MathHelper.floor(axisalignedbb.b);
		int pos1Z = MathHelper.floor(axisalignedbb.c);

		int pos2X = MathHelper.floor(axisalignedbb.d);
		int pos2Y = MathHelper.floor(axisalignedbb.e);
		int pos2Z = MathHelper.floor(axisalignedbb.f);

		boolean hitSomethingHard = false;
		boolean hitSomething = false;

		// CraftBukkit start - create a list to hold all the destroyed blocks
		List<org.bukkit.block.Block> destroyedBlocks = new ArrayList<org.bukkit.block.Block>();
		org.bukkit.craftbukkit.CraftWorld craftWorld = dragon.world.getWorld();
		// CraftBukkit end

		for (int blockX = pos1X; blockX <= pos2X; ++blockX) {
			for (int blockY = pos1Y; blockY <= pos2Y; ++blockY) {
				for (int blockZ = pos1Z; blockZ <= pos2Z; ++blockZ) {
					Block block = dragon.world
							.getType(blockX, blockY, blockZ);

					if (block.getMaterial() != Material.AIR) {
						if (block != Blocks.OBSIDIAN
								&& block != Blocks.WHITESTONE
								&& block != Blocks.BEDROCK) {
							
							hitSomething = true;
							// CraftBukkit start - add blocks to list rather
							// than destroying them
							// dragon.world.setTypeId(k1, l1, i2, 0);
							destroyedBlocks.add(craftWorld.getBlockAt(blockX,
									blockY, blockZ));
							// CraftBukkit end
						} else {
							hitSomethingHard = true;
						}
					}
				}
			}
		}

		if (!hitSomething)
			return hitSomethingHard;

		// CraftBukkit start - set off an EntityExplodeEvent for the dragon
		// exploding all these blocks
		org.bukkit.entity.Entity bukkitEntity = dragon.getBukkitEntity();
		org.bukkit.event.entity.EntityExplodeEvent event = new org.bukkit.event.entity.EntityExplodeEvent(
				bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0F);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			// dragon flag literally means 'Dragon hit something hard' (Obsidian,
			// White Stone or Bedrock) and will cause the dragon to slow down.
			// We should consider adding an event extension for it, or perhaps
			// returning true if the event is cancelled.
			return hitSomethingHard;
		} else if (event.getYield() == 0F) {
			// Yield zero ==> no drops
			for (org.bukkit.block.Block block : event.blockList()) {
				dragon.world.setAir(block.getX(), block.getY(), block.getZ());
			}
		} else {
			for (org.bukkit.block.Block block : event.blockList()) {
				 org.bukkit.Material blockId = block.getType();
				 if (blockId == org.bukkit.Material.AIR) {
					continue;
				 }

				int blockX = block.getX();
				int blockY = block.getY();
				int blockZ = block.getZ();

				Block nmsBlock = org.bukkit.craftbukkit.util.CraftMagicNumbers.getBlock(blockId);
				if (nmsBlock.a(explosionSource)) {
					nmsBlock.dropNaturally(dragon.world, blockX, blockY, blockZ, block.getData(), event.getYield(), 0);
				}

				nmsBlock.wasExploded(dragon.world, blockX, blockY, blockZ, explosionSource);

				dragon.world.setAir(blockX, blockY, blockZ);
			}
		}
		// CraftBukkit end

		if (!plugin.interactConfig().getConfig_deactivateBlockExplosionEffect()) {
			double posX = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a)
					* this.random.nextFloat();
			double posY = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b)
					* this.random.nextFloat();
			double posZ = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c)
					* this.random.nextFloat();
			
			dragon.world.addParticle("largeexplode", posX, posY, posZ, 0, 0, 0);
		}

		return hitSomethingHard;
	}
	
	
	//Logic vars.
	protected boolean doNothingLock;
	
	protected Vector oldSpeed;
	protected Vector oldTarget;
	
	
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

		dragon.bg *= 0.8F;
		float motionPythagoras = MathHelper.sqrt(dragon.motX * dragon.motX + dragon.motZ
				* dragon.motZ) + 1;

		if (motionPythagoras > 40.0F) {
			motionPythagoras = 40.0F;
		}

		dragon.bg += toTurnAngle * ((0.7 / motionPythagoras) / motionPythagoras);
		dragon.yaw += dragon.bg * 0.1;
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

		float f19 = MathHelper.sin((float) (directionDegree - dragon.bg * 0.01F));
		float f13 = MathHelper.cos((float)directionDegree
				- dragon.bg * 0.01F);

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

		dragon.bA = dragon.getDragonMoveController().checkHitBlocks(dragon.bq.boundingBox)
				| dragon.getDragonMoveController().checkHitBlocks(dragon.br.boundingBox);
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
