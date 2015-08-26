package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.CrashReport;
import net.minecraft.server.v1_8_R3.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Explosion;
import net.minecraft.server.v1_8_R3.Material;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.ReportedException;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class CollisionController {

	/**
	 * The dragon to call stuff on.
	 */
	protected final LimitedEnderDragon dragon;
	
	/**
	 * The Plugin to get settings and stuff from
	 */
	protected final EnderdragonsPlus plugin;
	
	
	public CollisionController(LimitedEnderDragon dragon) {
		this.dragon = dragon;
		this.plugin = EnderdragonsPlus.getPlugin();
	}
	
	
	/**
	 * Checks if the Dragon collides with anything.
	 * <br>Return true if collision detected.
	 * 
	 * @return true if colliding.
	 * 
	 * @throws ReportedException on crash.
	 */
	public boolean checkCollisionAndPortals(){
		boolean collisionDetected = false;
		
		try{
			int i = MathHelper.floor(dragon.getBoundingBox().a + 0.001D);
			int j = MathHelper.floor(dragon.getBoundingBox().b + 0.001D);
			int k = MathHelper.floor(dragon.getBoundingBox().c + 0.001D);
			int l = MathHelper.floor(dragon.getBoundingBox().d - 0.001D);
			int i1 = MathHelper.floor(dragon.getBoundingBox().e - 0.001D);
			int j1 = MathHelper.floor(dragon.getBoundingBox().f - 0.001D);
	
			//if (dragon.world.b(i, j, k, l, i1, j1)) {
			for (int k1 = i; k1 <= l; ++k1) {
				for (int l1 = j; l1 <= i1; ++l1) {
					for (int i2 = k; i2 <= j1; ++i2) {
						Block block = dragon.world.getType(new BlockPosition(k1, l1, i2)).getBlock();
						if(block.getMaterial().isSolid()){
							collisionDetected = true;
						}

						try {
							block.a(dragon.world, new BlockPosition(k1, l1, i2) , dragon);
						} catch (Throwable throwable) {
							CrashReport crashreport = CrashReport.a(throwable,
									"Colliding entity with block");
							CrashReportSystemDetails crashreportsystemdetails = crashreport
									.a("Block being collided with");

							/*CrashReportSystemDetails.a(
									crashreportsystemdetails,
									block, dragon.world.getType(new BlockPosition(k1, l1, i2)));*/
							throw new ReportedException(crashreport);
						}
					}
				}
			}
			//}
		} catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

            dragon.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
		
		return collisionDetected;
	}

	
	
	/**
	 * Random to use
	 */
	protected final Random random = new SecureRandom();
	
	/**
	 * The Explosion source as Const.
	 * Lazy init. use getter!!!!
	 */
	private Explosion explosionSource;
	protected Explosion getExplosion(){
		if(explosionSource == null){
			explosionSource = new Explosion(null, dragon, Double.NaN, Double.NaN, Double.NaN, Float.NaN, true, true);
		}
		
		return explosionSource;
	}
	
	
	
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
		CraftWorld craftWorld = dragon.world.getWorld();
		// CraftBukkit end

		for (int blockX = pos1X; blockX <= pos2X; ++blockX) {
			for (int blockY = pos1Y; blockY <= pos2Y; ++blockY) {
				for (int blockZ = pos1Z; blockZ <= pos2Z; ++blockZ) {
					Block block = dragon.world.getType(new BlockPosition(blockX, blockY, blockZ)).getBlock();

					if (block.getMaterial() != Material.AIR) {
						if (block != Blocks.OBSIDIAN
								&& block != Blocks.END_STONE
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
			// dragon flag literally means 'Dragon hit something hard'
			// (Obsidian,
			// White Stone or Bedrock) and will cause the dragon to slow down.
			// We should consider adding an event extension for it, or perhaps
			// returning true if the event is cancelled.
			return hitSomethingHard;
		} else if (event.getYield() == 0F) {
			// Yield zero ==> no drops
			for (org.bukkit.block.Block block : event.blockList()) {
				BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
				dragon.world.setAir(pos);
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

				 Block nmsBlock = org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers.getBlock(blockId);
                 if (nmsBlock.a(explosionSource)) {
                     nmsBlock.dropNaturally(dragon.world, 
                    		 new BlockPosition(blockX, blockY, blockZ), 
                    		 nmsBlock.fromLegacyData(block.getData()), event.getYield(), 0);
                 }
                 
                 nmsBlock.wasExploded(dragon.world, new BlockPosition(blockX, blockY, blockZ), explosionSource);
                 dragon.world.setAir(new BlockPosition(blockX, blockY, blockZ));
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

			dragon.world.addParticle(EnumParticle.EXPLOSION_LARGE, posX, posY, posZ, 0, 0, 0, new int[0]);
		}

		return hitSomethingHard;
	}
}
