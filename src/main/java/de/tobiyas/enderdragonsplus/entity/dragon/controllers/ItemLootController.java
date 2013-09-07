package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.BlockEnderPortal;
import net.minecraft.server.v1_6_R2.EntityExperienceOrb;
import net.minecraft.server.v1_6_R2.MathHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R2.util.BlockStateListPopulator;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.dragontemples.DragonTemple;
import de.tobiyas.enderdragonsplus.entity.dragontemples.DragonTempleStore;
import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.config.returncontainer.DropContainer;

@SuppressWarnings("unused")
public class ItemLootController {

	private LimitedEnderDragon dragon;
	private Random random;
	
	private int ticksToDespawn = 200;
	
	/**
	 * Creates a new Loot controller for this dragon
	 * 
	 * @param dragon
	 */
	public ItemLootController(LimitedEnderDragon dragon){
		random = new Random();
		this.dragon = dragon;
	}	
	
	/**
	 * Ticks the Death of the dragon 1 tick further.
	 * This means giving exp and loot to the world.
	 */
	public void deathTick(){
		ticksToDespawn -= 1;
		if ((ticksToDespawn >= 0) && (ticksToDespawn <= 20)) {
			float offsetX = (this.random.nextFloat() - 0.5F) * 8.0F;
			float offsetY = (this.random.nextFloat() - 0.5F) * 4.0F;
			float offsetZ = (this.random.nextFloat() - 0.5F) * 8.0F;

			dragon.world.addParticle("hugeexplosion", dragon.locX + offsetX, 
				dragon.locY + 2 + offsetY,
				dragon.locZ + offsetZ,
				0, 0, 0);
		}

		if ((ticksToDespawn < 50) && (ticksToDespawn % 5 == 0)) {
			int exp = dragon.getExpReward() / 20;
			dropEXPOrbs(exp);
		}
	
		dragon.move(0, 0.1, 0);
		dragon.aN = (dragon.yaw += 20);
		if (ticksToDespawn <= 0) {
			int exp = dragon.getExpReward() - dragon.getExpReward() / 2;
			dropEXPOrbs(exp);

			int dragonX = MathHelper.floor(dragon.getLocation().getBlockX());
			int dragonY = MathHelper.floor(dragon.getLocation().getBlockZ());
			
			spawnEnderPortal(dragonX, dragonY);
			//dragon.aH(); //TODO not sure what this does.
			dragon.die();
		}
	}

	private void dropEXPOrbs(int totalAmount){
		while (totalAmount > 0) {
			int toSubtract = EntityExperienceOrb.getOrbValue(totalAmount);
			totalAmount -= toSubtract;
			dragon.world.addEntity(new EntityExperienceOrb(dragon.world,
					dragon.locX, dragon.locY, dragon.locZ, toSubtract));
		}
	}
	
	private void spawnEnderPortal(int posX, int posZ) {
		if(checkCancle())
			return;
		
		int spawnHeight = getNextRealBlockBelowLocation(dragon.getLocation().clone());
		String prePath = EnderdragonsPlus.getPlugin().getDataFolder() + File.separator + "temples" + File.separator;
		String fileName = EnderdragonsPlus.getPlugin().interactConfig().getConfig_dragonTempleFile();
		
		File completeFile = new File(prePath, fileName);
		
		DragonTemple temple = new DragonTemple(completeFile);
		if(!temple.buildAt(dragon.getLocation().getWorld(), posX, spawnHeight, posZ))
			buildNormalWay(posX, spawnHeight, posZ);
	}
	
	private boolean checkCancle(){
		EntityCreatePortalEvent event = new EntityCreatePortalEvent(
				(org.bukkit.entity.LivingEntity) dragon.getBukkitEntity(),
				new LinkedList<BlockState>(), PortalType.ENDER);
		dragon.world.getServer().getPluginManager().callEvent(event);
		
		return event.isCancelled();
	}
	
	private int getNextRealBlockBelowLocation(Location location){
		
		for(int i = location.getBlockY(); i > 1; i--){
			if(location.getBlock().getType() != org.bukkit.Material.AIR)
				return location.add(0, 1, 0).getBlockY();
			else
				location.subtract(0, 1, 0);
		}
		
		return 80;
	}
	
	private void buildNormalWay(int posX, int spawnHeight, int posZ){
		BlockEnderPortal.a = true;
		int b1 = 4;
		BlockStateListPopulator world = new BlockStateListPopulator(
				dragon.world.getWorld());

		for (int k = spawnHeight - 1; k <= spawnHeight + 32; ++k) {
			for (int l = posX - b1; l <= posX + b1; ++l) {
				for (int i1 = posZ - b1; i1 <= posZ + b1; ++i1) {
					double d0 = (double) (l - posX);
					double d1 = (double) (i1 - posZ);
					double d2 = d0 * d0 + d1 * d1;

					if (d2 <= ((double) b1 - 0.5D) * ((double) b1 - 0.5D)) {
						if (k < spawnHeight) {
							if (d2 <= ((double) (b1 - 1) - 0.5D)
									* ((double) (b1 - 1) - 0.5D)) {
								world.setTypeId(l, k, i1, Block.BEDROCK.id);
							}
						} else if (k > spawnHeight) {
							world.setTypeId(l, k, i1, 0);
						} else if (d2 > ((double) (b1 - 1) - 0.5D)
								* ((double) (b1 - 1) - 0.5D)) {
							world.setTypeId(l, k, i1, Block.BEDROCK.id);
						} else {
							world.setTypeId(l, k, i1, Block.ENDER_PORTAL.id);
						}
					}
				}
			}
		}

		world.setTypeId(posX, spawnHeight + 0, posZ, Block.BEDROCK.id);
		world.setTypeId(posX, spawnHeight + 1, posZ, Block.BEDROCK.id);
		world.setTypeId(posX, spawnHeight + 2, posZ, Block.BEDROCK.id);
		world.setTypeId(posX - 1, spawnHeight + 2, posZ, Block.TORCH.id);
		world.setTypeId(posX + 1, spawnHeight + 2, posZ, Block.TORCH.id);
		world.setTypeId(posX, spawnHeight + 2, posZ - 1, Block.TORCH.id);
		world.setTypeId(posX, spawnHeight + 2, posZ + 1, Block.TORCH.id);
		world.setTypeId(posX, spawnHeight + 3, posZ, Block.BEDROCK.id);
		world.setTypeId(posX, spawnHeight + 4, posZ, Block.DRAGON_EGG.id);
		
		for (BlockState state : world.getList()) {
            state.update(true);
        }

		BlockEnderPortal.a = false;
	}

	public List<ItemStack> getItemDrops(List<DropContainer> list) {
		List<ItemStack> stacks = new LinkedList<ItemStack>();
		for(DropContainer container : list){
			stacks.add(container.generateItem());
		}
		
		return stacks;		
	}
	
}
