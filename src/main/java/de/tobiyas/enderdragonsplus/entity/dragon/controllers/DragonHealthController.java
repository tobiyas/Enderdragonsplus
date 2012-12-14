package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;
//import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;

import net.minecraft.server.v1_4_5.DamageSource;
import net.minecraft.server.v1_4_5.Entity;
import net.minecraft.server.v1_4_5.EntityEnderCrystal;
import net.minecraft.server.v1_4_5.EntityHuman;
import net.minecraft.server.v1_4_5.EntityLiving;

public class DragonHealthController {

	private EnderdragonsPlus plugin;
	private LimitedEnderDragonV131 dragon;
	private Random random;
	
	private int maxHealth;
	
	//private AgeContainer ageContainer;
	
	public DragonHealthController(LimitedEnderDragonV131 dragon){
		plugin = EnderdragonsPlus.getPlugin();
		this.dragon = dragon;
		random = new Random();
		
		this.maxHealth = EnderdragonsPlus.getPlugin().interactConfig().getConfig_dragonMaxHealth();
	}
	
	
	/**
	 * Checks if the Dragon is near a EnderDragonCrystal to regain health
	 */
	public void checkRegainHealth() {
		if (dragon.bQ != null) {
			if (dragon.bQ.dead) {
				dragon.a(dragon.g, DamageSource.EXPLOSION, 10);
				dragon.bQ = null;
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

			dragon.bQ = entityendercrystal;
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
							plugin.interactConfig().getConfig_dragonDamage());

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
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
}
