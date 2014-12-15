package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R1.BlockEnderPortal;
import net.minecraft.server.v1_8_R1.Blocks;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityExperienceOrb;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.MathHelper;

import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R1.util.BlockStateListPopulator;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.dragontemples.DragonTemple;
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

			dragon.world.addParticle(EnumParticle.EXPLOSION_HUGE, dragon.locX + offsetX, 
				dragon.locY + 2 + offsetY,
				dragon.locZ + offsetZ,
				0d, 0d, 0d,
				new int[0]);
		}

		boolean giveDirectlyToPlayer = EnderdragonsPlus.getPlugin().interactConfig().isConfig_dragonGiveXPOnlyToDamagers();
		if ((ticksToDespawn < 50) && (ticksToDespawn % 5 == 0) && !giveDirectlyToPlayer) {
			int exp = dragon.getExpReward() / 20;
			dropEXPOrbs(exp);
		}
	
		dragon.move(0, 0.1, 0);
		dragon.aH = (dragon.yaw += 20);
		if (ticksToDespawn <= 0) {
			if(giveDirectlyToPlayer){
				givePlayersDirectlyXP(dragon.getExpReward());
			}else{
				int exp = dragon.getExpReward() / 2;
				dropEXPOrbs(exp);
				
			}
			
			int dragonX = MathHelper.floor(dragon.getLocation().getBlockX());
			int dragonY = MathHelper.floor(dragon.getLocation().getBlockZ());
			
			spawnEnderPortal(dragonX, dragonY);
			//dragon.aH(); //TODO not sure what this does.
			dragon.die();
		}
	}

	/**
	 * Gives the EXP directly to the Players that hittet the dragon at least once.
	 * This is equaly spread among the players.
	 * 
	 * @param expReward
	 */
	private void givePlayersDirectlyXP(int expReward) {
		List<Entity> targets = dragon.getAllTargets();
		
		List<Player> playersToGiveXP = new LinkedList<Player>();
		for(Entity target : targets){
			if(target.getBukkitEntity() instanceof Player){
				playersToGiveXP.add((Player) target.getBukkitEntity());
			}
		}
		
		if(playersToGiveXP.size() == 0){
			dropEXPOrbs(expReward);
			return;
		}
		
		int expPerPlayer = expReward / playersToGiveXP.size();
		for(Player player : playersToGiveXP){
			for(int i = 0; i < expReward; i += 50){
				player.giveExp(i);			
			}
			
			int rest = expPerPlayer % 50;
			player.giveExp(rest);
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
		//BlockEnderPortal.a = true;
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
								world.setTypeUpdate(l, k, i1, Blocks.BEDROCK);
							}
						} else if (k > spawnHeight) {
							world.setTypeId(l, k, i1, 0);
						} else if (d2 > ((double) (b1 - 1) - 0.5D)
								* ((double) (b1 - 1) - 0.5D)) {
							world.setTypeUpdate(l, k, i1, Blocks.BEDROCK);
						} else {
							world.setTypeUpdate(l, k, i1, Blocks.END_PORTAL);
						}
					}
				}
			}
		}

		world.setTypeUpdate(posX, spawnHeight + 0, posZ, Blocks.BEDROCK);
		world.setTypeUpdate(posX, spawnHeight + 1, posZ, Blocks.BEDROCK);
		world.setTypeUpdate(posX, spawnHeight + 2, posZ, Blocks.BEDROCK);
		world.setTypeUpdate(posX - 1, spawnHeight + 2, posZ, Blocks.TORCH);
		world.setTypeUpdate(posX + 1, spawnHeight + 2, posZ, Blocks.TORCH);
		world.setTypeUpdate(posX, spawnHeight + 2, posZ - 1, Blocks.TORCH);
		world.setTypeUpdate(posX, spawnHeight + 2, posZ + 1, Blocks.TORCH);
		world.setTypeUpdate(posX, spawnHeight + 3, posZ, Blocks.BEDROCK);
		world.setTypeUpdate(posX, spawnHeight + 4, posZ, Blocks.DRAGON_EGG);
		
		for (BlockState state : world.getList()) {
            state.update(true);
        }

		//TODO check if needed.
		//BlockEnderPortal.a = false;
	}

	public List<ItemStack> getItemDrops(List<DropContainer> list) {
		List<ItemStack> stacks = new LinkedList<ItemStack>();
		for(DropContainer container : list){
			stacks.add(container.generateItem());
		}
		
		return stacks;		
	}
	
}
