package de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragontemples.DragonTemple;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class ItemLootController implements IItemLootController {

	private LimitedED dragon;
	private Random random;
	
	private int ticksToDespawn = 200;
	
	/**
	 * Creates a new Loot controller for this dragon
	 * 
	 * @param dragon
	 */
	public ItemLootController(LimitedED dragon){
		random = new Random();
		this.dragon = dragon;
	}	
	
	
	
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
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot.IItemLootController#deathTick()
	 */
	@Override
	public void deathTick(){
		ticksToDespawn -= 1;
		if ((ticksToDespawn >= 0) && (ticksToDespawn <= 20)) {
			float offsetX = (this.random.nextFloat() - 0.5F) * 8.0F;
			float offsetY = (this.random.nextFloat() - 0.5F) * 4.0F;
			float offsetZ = (this.random.nextFloat() - 0.5F) * 8.0F;

			dragon.getWorld().createExplosion(dragon.getLocation().add(offsetX, offsetY, offsetZ), 0);
		}

		boolean giveDirectlyToPlayer = EnderdragonsPlus.getPlugin().interactConfig().isConfig_dragonGiveXPOnlyToDamagers();
		if ((ticksToDespawn < 50) && (ticksToDespawn % 5 == 0) && !giveDirectlyToPlayer) {
			int exp = dragon.getExpReward() / 20;
			dropEXPOrbs(exp, 5);
		}
	
		dragon.move(0, 0.1, 0);
		//dragon.aN = (dragon.yaw += 20);
		if (ticksToDespawn <= 0) {
			if(giveDirectlyToPlayer){
				givePlayersDirectlyXP(dragon.getExpReward());
			}else{
				int exp = dragon.getExpReward() / 2;
				dropEXPOrbs(exp, 5);
				
			}
			
			int dragonX = convert(dragon.getLocation().getBlockX());
			int dragonY = convert(dragon.getLocation().getBlockZ());
			
			spawnEnderPortal(dragonX, dragonY);
			//dragon.aH(); //TODO not sure what this does.
			dragon.remove();
		}
	}

	/**
	 * Gives the EXP directly to the Players that hittet the dragon at least once.
	 * This is equaly spread among the players.
	 * 
	 * @param expReward
	 */
	private void givePlayersDirectlyXP(int expReward) {
		List<LivingEntity> targets = dragon.getAllTargets();
		
		List<Player> playersToGiveXP = new LinkedList<Player>();
		for(LivingEntity target : targets){
			if(target instanceof Player){
				playersToGiveXP.add((Player) target);
			}
		}
		
		if(playersToGiveXP.size() == 0){
			dropEXPOrbs(expReward, 5);
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

	private void dropEXPOrbs(int totalAmount, int amountOfOrbs){
		World world = dragon.getWorld();
		Location dropLoc = dragon.getLocation();
		int toDrop = totalAmount / amountOfOrbs;
		
		while (totalAmount > 0) {			
			for(int i = 0; i < amountOfOrbs; i++){
				ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(dropLoc, EntityType.EXPERIENCE_ORB);
				if(orb == null) continue; // bukkit does not want us to spawn orbs.
				
				orb.setExperience(toDrop);
				totalAmount -= toDrop;
			}
			
			if(totalAmount > 0){
				ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(dropLoc, EntityType.EXPERIENCE_ORB);
				orb.setExperience(totalAmount);
			}
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
		Bukkit.getPluginManager().callEvent(event);
		
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
		//TODO check if this is really needed.
		//BlockEnderPortal.a = true;
		
		Map<Location,Material> updateMap = new HashMap<Location, Material>();
		World world = dragon.getWorld();
		
		int b1 = 4;

		for (int loopY = spawnHeight - 1; loopY <= spawnHeight + 32; ++loopY) {
			for (int loopX = posX - b1; loopX <= posX + b1; ++loopX) {
				for (int loopZ = posZ - b1; loopZ <= posZ + b1; ++loopZ) {
					double d0 = (double) (loopX - posX);
					double d1 = (double) (loopZ - posZ);
					double d2 = d0 * d0 + d1 * d1;

					if (d2 <= ((double) b1 - 0.5D) * ((double) b1 - 0.5D)) {
						if (loopY < spawnHeight) {
							if (d2 <= ((double) (b1 - 1) - 0.5D)
									* ((double) (b1 - 1) - 0.5D)) {
								updateMap.put(new Location(world, loopX,loopY,loopZ), Material.BEDROCK);
								//world.setTypeUpdate(loopX, loopY, loopZ, Blocks.BEDROCK);
							}
						} else if (loopY > spawnHeight) {
							updateMap.put(new Location(world, loopX,loopY,loopZ), Material.AIR);
							//world.setTypeId(loopX, loopY, loopZ, 0);
						} else if (d2 > ((double) (b1 - 1) - 0.5D)
								* ((double) (b1 - 1) - 0.5D)) {
							updateMap.put(new Location(world, loopX,loopY,loopZ), Material.BEDROCK);
							//world.setTypeUpdate(loopX, loopY, loopZ, Blocks.BEDROCK);
						} else {
							updateMap.put(new Location(world, loopX,loopY,loopZ), Material.ENDER_PORTAL);
							//world.setTypeUpdate(loopX, loopY, loopZ, Blocks.ENDER_PORTAL);
						}
					}
				}
			}
		}

//		world.setTypeUpdate(posX, spawnHeight + 0, posZ, Blocks.BEDROCK);
//		world.setTypeUpdate(posX, spawnHeight + 1, posZ, Blocks.BEDROCK);
//		world.setTypeUpdate(posX, spawnHeight + 2, posZ, Blocks.BEDROCK);
//		world.setTypeUpdate(posX - 1, spawnHeight + 2, posZ, Blocks.TORCH);
//		world.setTypeUpdate(posX + 1, spawnHeight + 2, posZ, Blocks.TORCH);
//		world.setTypeUpdate(posX, spawnHeight + 2, posZ - 1, Blocks.TORCH);
//		world.setTypeUpdate(posX, spawnHeight + 2, posZ + 1, Blocks.TORCH);
//		world.setTypeUpdate(posX, spawnHeight + 3, posZ, Blocks.BEDROCK);
//		world.setTypeUpdate(posX, spawnHeight + 4, posZ, Blocks.DRAGON_EGG);
		
		updateMap.put(new Location(world, posX, spawnHeight + 0, posZ), Material.BEDROCK);
		updateMap.put(new Location(world, posX, spawnHeight + 1, posZ), Material.BEDROCK);
		updateMap.put(new Location(world, posX, spawnHeight + 2, posZ), Material.BEDROCK);
		updateMap.put(new Location(world, posX - 1, spawnHeight + 2, posZ), Material.TORCH);
		updateMap.put(new Location(world, posX + 1, spawnHeight + 2, posZ), Material.TORCH);
		updateMap.put(new Location(world,posX, spawnHeight + 2, posZ - 1), Material.TORCH);
		updateMap.put(new Location(world, posX, spawnHeight + 2, posZ + 1), Material.TORCH);
		updateMap.put(new Location(world, posX, spawnHeight + 3, posZ), Material.BEDROCK);
		updateMap.put(new Location(world, posX, spawnHeight + 4, posZ), Material.DRAGON_EGG);
		
		for (Entry<Location,Material> entry : updateMap.entrySet()) {
			entry.getKey().getBlock().setType(entry.getValue());
        }

		//TODO check if this is really needed.
		//BlockEnderPortal.a = false;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot.IItemLootController#getItemDrops(java.util.List)
	 */
	@Override
	public List<ItemStack> getItemDrops(List<DropContainer> list) {
		List<ItemStack> stacks = new LinkedList<ItemStack>();
		for(DropContainer container : list){
			stacks.add(container.generateItem());
		}
		
		return stacks;		
	}
	
}
