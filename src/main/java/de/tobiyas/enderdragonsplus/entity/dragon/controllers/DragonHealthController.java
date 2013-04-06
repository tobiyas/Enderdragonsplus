package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
//import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;

import net.minecraft.server.v1_5_R2.DamageSource;
import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityEnderCrystal;
import net.minecraft.server.v1_5_R2.EntityHuman;
import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.Explosion;

public class DragonHealthController {

	private EnderdragonsPlus plugin;
	private LimitedEnderDragon dragon;
	private Random random;
	
	private HashMap<String, Integer> damageDoneByPlayer;
	private String lastPlayerAttacked = "";
	
	//private AgeContainer ageContainer;
	
	public DragonHealthController(LimitedEnderDragon dragon){
		plugin = EnderdragonsPlus.getPlugin();
		this.dragon = dragon;
		random = new Random();
		
		damageDoneByPlayer = new HashMap<String, Integer>();
	}
	
	
	/**
	 * Checks if the Dragon is near a EnderDragonCrystal to regain health
	 */
	public void checkRegainHealth() {
		if (dragon.bS != null) {
			if (dragon.bS.dead) {
				dragon.a(dragon.g, DamageSource.explosion((Explosion) null), 10);
				dragon.bS = null;
			} else if (dragon.ticksLived % 10 == 0 && dragon.getHealth() < dragon.getMaxHealth()) {
				// CraftBukkit start
				org.bukkit.event.entity.EntityRegainHealthEvent event = new org.bukkit.event.entity.EntityRegainHealthEvent(
						dragon.getBukkitEntity(),
						1,
						org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
				dragon.world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					int newDragonHealth = dragon.getHealth() + event.getAmount();
					dragon.setHealth(newDragonHealth);
				}
				// CraftBukkit end
			}
		}

		if (random.nextInt(10) == 0) {
			float range = 32;
			@SuppressWarnings("unchecked")
			List<Entity> list = dragon.world.a(EntityEnderCrystal.class,
					dragon.boundingBox.grow(range, range, range));
			
			EntityEnderCrystal entityendercrystal = null;
			double nearestDistance = Double.MAX_VALUE;

			for(Entity entity : list){
				double currentDistance = entity.e(dragon);

				if (currentDistance < nearestDistance) {
					nearestDistance = currentDistance;
					entityendercrystal = (EntityEnderCrystal) entity;
				}
			}

			dragon.bS = entityendercrystal;
		}
	}
	
	/**
	 * Damages an list of entities
	 * @param list of Entities to damage
	 */
	public void damageEntities(List<Entity> list) {
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);

			if (entity instanceof EntityLiving) {
				// CraftBukkit start - throw damage events when the dragon
				// attacks
				// The EntityHuman case is handled in EntityHuman, so don't
				// throw it here
				if (!(entity instanceof EntityHuman)) {
					org.bukkit.event.entity.EntityDamageByEntityEvent damageEvent = new org.bukkit.event.entity.EntityDamageByEntityEvent(
							dragon.getBukkitEntity(),
							entity.getBukkitEntity(),
							org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK,
							dragon.getMeeleDamage());

					Bukkit.getPluginManager().callEvent(damageEvent);

					if (!damageEvent.isCancelled()) {
						 entity.getBukkitEntity().setLastDamageCause(damageEvent);
						entity.damageEntity(DamageSource.mobAttack(dragon),
								damageEvent.getDamage());
					}
				} else {
					int damageDone = plugin.interactConfig().getConfig_dragonDamage();
					entity.damageEntity(DamageSource.mobAttack(dragon), damageDone);
				}
				// CraftBukkit end
			}
		}
	}
	
	/**
	 * Returns the mapped health to 200
	 * @return
	 */
	public int mapHealth(){
		double actualHealth = dragon.getHealth();
		double maxHealth = dragon.getMaxHealth();
		
		double percentage = actualHealth / maxHealth;
		int mappedHealth = (int) Math.floor(percentage * 200);
		if(mappedHealth < 0)
			mappedHealth = 0;
	
		return mappedHealth;
	}
	
	public void rememberDamage(DamageSource source, int damage){
		if(source.getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) source.getEntity();
			rememberDamage(player.getName(), damage);
		}
	}
	
	public void rememberDamage(String player, int damage){
		int newDmg = damage;	
		if(damageDoneByPlayer.containsKey(player))
			newDmg += damageDoneByPlayer.get(player);
		
		damageDoneByPlayer.put(player, newDmg);
		lastPlayerAttacked = player;
	}
	
	public Map<String, Integer> getPlayerDamage(){
		return damageDoneByPlayer;
	}


	public String getLastPlayerAttacked() {
		return lastPlayerAttacked;
	}


	public int getDamageByPlayer(String player) {
		if(damageDoneByPlayer.containsKey(player))
			return damageDoneByPlayer.get(player);
		
		return 0;
	}
	
}
