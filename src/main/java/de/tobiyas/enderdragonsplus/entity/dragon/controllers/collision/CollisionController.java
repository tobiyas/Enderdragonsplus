package de.tobiyas.enderdragonsplus.entity.dragon.controllers.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public class CollisionController implements ICollisionController {

	/**
	 * The dragon to call stuff on.
	 */
	protected final LimitedED dragon;
	
	/**
	 * The Plugin to get settings and stuff from
	 */
	protected final EnderdragonsPlus plugin;
	
	/**
	 * if the dragon does not have collision, skip!
	 */
	protected boolean hasCollision;
	
	
	public CollisionController(LimitedED dragon) {
		this.dragon = dragon;
		this.plugin = EnderdragonsPlus.getPlugin();
	}
	

	@Override
	public boolean checkCollisionAndPortals(){
		if(!hasCollision) return false;
		
		boolean collisionDetected = false;
		
		Location min = dragon.getMinBBLocation();
		Location max = dragon.getMaxBBLocation();
		
		try{
			int minX = convert(min.getX());
			int minY = convert(min.getY());
			int minZ = convert(min.getZ());
			
			int maxX = convert(max.getX());
			int maxY = convert(max.getY());
			int maxZ = convert(max.getZ());
			
			//TODO this should check if the chunks here are loaded, If i'm correct.
			//if (dragon.world.b(minX, minY, minZ, maxX, maxY, maxZ)) {
				for (int loopX = minX; loopX <= maxX; ++loopX) {
					for (int loopY = minY; loopY <= maxY; ++loopY) {
						for (int loopZ = minZ; loopZ <= maxZ; ++loopZ) {
							Block block = min.getWorld().getBlockAt(loopX, loopY, loopZ);
							if(block.getType().isSolid()){
								collisionDetected = true;
							}
						}
					}
				}
			//}
		} catch (Throwable throwable) {
           return true;
        }
		
		return collisionDetected;
	}

	
	
	/**
	 * Random to use
	 */
	protected final Random random = new Random();
	
	
	/**
	 * This convers a double to a Int.
	 * 
	 * @param toConvert the value to convert.
	 * 
	 * @return the converted value.
	 */
	private int convert(double toConvert){
		return (int) Math.floor(toConvert);
	}
	
	
	@Override
	public boolean checkHitBlocks(Location min, Location max) {
		int pos1X = convert(min.getX());
		int pos1Y = convert(min.getY());
		int pos1Z = convert(min.getZ());

		int pos2X = convert(max.getX());
		int pos2Y = convert(max.getY());
		int pos2Z = convert(max.getZ());

		boolean hitSomethingHard = false;
		boolean hitSomething = false;

		// CraftBukkit start - create a list to hold all the destroyed blocks
		List<Block> destroyedBlocks = new ArrayList<Block>();
		World world = dragon.getLocation().getWorld();
		// CraftBukkit end

		for (int blockX = pos1X; blockX <= pos2X; ++blockX) {
			for (int blockY = pos1Y; blockY <= pos2Y; ++blockY) {
				for (int blockZ = pos1Z; blockZ <= pos2Z; ++blockZ) {
					Block block = min.getWorld().getBlockAt(blockX, blockY, blockZ);

					Material blockMat = block.getType(); 
					if (blockMat != Material.AIR) {
						if (blockMat != Material.OBSIDIAN
								&& blockMat != Material.ENDER_STONE //Also WHITESTONE in NMS source.
								&& blockMat != Material.BEDROCK) {

							hitSomething = true;
							// CraftBukkit start - add blocks to list rather
							// than destroying them
							// dragon.world.setTypeId(k1, l1, i2, 0);
							destroyedBlocks.add(world.getBlockAt(blockX,
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
		Entity bukkitEntity = dragon.getBukkitEntity();
		EntityExplodeEvent event = new EntityExplodeEvent(
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
			for (Block block : event.blockList()) {
				block.setType(Material.AIR); //We use the world method no notify something I think.
				//TODO check if this is correct.
				//dragon.world.setAir(block.getX(), block.getY(), block.getZ());
			}
		} else {
			for (Block block : event.blockList()) {
				Material blockId = block.getType();
				if (blockId == org.bukkit.Material.AIR) {
					continue;
				}

				int blockX = block.getX();
				int blockY = block.getY();
				int blockZ = block.getZ();

				block.setType(Material.AIR);
				world.createExplosion(blockX, blockY, blockZ, 0);
				
				//TODO check if this does the same. Below is the old NMS stuff.
				/*Block nmsBlock = org.bukkit.craftbukkit.util.CraftMagicNumbers
						.getBlock(blockId);
				if (nmsBlock.a(getExplosion())) {
					nmsBlock.dropNaturally(dragon.world, blockX, blockY,
							blockZ, block.getData(), event.getYield(), 0);
				}

				nmsBlock.wasExploded(dragon.world, blockX, blockY, blockZ,
						getExplosion());*/
			}
		}
		// CraftBukkit end

		if (!plugin.interactConfig().getConfig_deactivateBlockExplosionEffect()) {
			double posX = pos1X + (pos2X - pos1X)
					* this.random.nextFloat();
			double posY = pos1Y + (pos2Y - pos1Y)
					* this.random.nextFloat();
			double posZ = pos1Z + (pos2Z - pos1Z)
					* this.random.nextFloat();

			
			dragon.getBukkitWorld().createExplosion(new Location(dragon.getBukkitWorld(), posX, posY, posZ), 0);
		}

		return hitSomethingHard;
	}
	
	
	public void setCollision(boolean collision){
		this.hasCollision = collision;
	}
	
	public boolean hasCollision(){
		return hasCollision;
	}
}
