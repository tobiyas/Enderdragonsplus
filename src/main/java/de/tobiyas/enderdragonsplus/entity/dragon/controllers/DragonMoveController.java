package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import net.minecraft.server.v1_6_R2.AxisAlignedBB;
import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.Explosion;
import net.minecraft.server.v1_6_R2.MathHelper;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonMoveController {

	private LimitedEnderDragon dragon;
	private Random random;
	private EnderdragonsPlus plugin;
	
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
	
	private Explosion explosionSource = new Explosion(null, dragon, Double.NaN, Double.NaN, Double.NaN, Float.NaN); // CraftBukkit - reusable source for CraftTNTPrimed.getSource()
	
	
	// Original: a(AxisAlignedBB axisalignedbb)
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
		org.bukkit.craftbukkit.v1_6_R2.CraftWorld craftWorld = dragon.world.getWorld();
		// CraftBukkit end

		for (int blockX = pos1X; blockX <= pos2X; ++blockX) {
			for (int blockY = pos1Y; blockY <= pos2Y; ++blockY) {
				for (int blockZ = pos1Z; blockZ <= pos2Z; ++blockZ) {
					int blockType = dragon.world
							.getTypeId(blockX, blockY, blockZ);

					if (blockType != 0) {
						if (blockType != Block.OBSIDIAN.id
								&& blockType != Block.WHITESTONE.id
								&& blockType != Block.BEDROCK.id) {
							hitSomething = true;
							// CraftBukkit start - add blocks to list rather
							// than destroying them
							// this.world.setTypeId(k1, l1, i2, 0);
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
			// this flag literally means 'Dragon hit something hard' (Obsidian,
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
				int blockId = block.getTypeId();

				if (block.getType() == Material.AIR) {
					continue;
				}

				int blockX = block.getX();
				int blockY = block.getY();
				int blockZ = block.getZ();

				if (Block.byId[blockId].a(explosionSource)) {
					Block.byId[blockId].dropNaturally(dragon.world, blockX, blockY, blockZ, block.getData(), event.getYield(), 0);
				}
				Block.byId[blockId].wasExploded(dragon.world, blockX, blockY, blockZ, explosionSource);

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
	
	public int getCurrentFlightHeight(){
		int posX = dragon.getLocation().getBlockX();
		int posZ = dragon.getLocation().getBlockZ();
		
		int posY = dragon.getLocation().getBlockY();
		
		//TODO search for possible flight height.
		return posX + posY + posZ == 0 ? 70 : 70;
	}
	
}
