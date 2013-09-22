package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.server.v1_6_R3.DamageSource;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityEnderCrystal;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.Explosion;
import net.minecraft.server.v1_6_R3.NBTTagFloat;
import net.minecraft.server.v1_6_R3.NBTTagList;

import org.bukkit.Bukkit;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonHealthController {

	private EnderdragonsPlus plugin;
	private LimitedEnderDragon dragon;
	private Random random;
	
	private HashMap<String, Float> damageDoneByPlayer;
	private String lastPlayerAttacked = "";
	
	
	public DragonHealthController(LimitedEnderDragon dragon){
		plugin = EnderdragonsPlus.getPlugin();
		this.dragon = dragon;
		random = new Random();
		
		damageDoneByPlayer = new HashMap<String, Float>();
	}
	
	/**
	 * This constructor also restores the Damage done.
	 * 
	 * @param dragon
	 * @param playerMapCompound
	 */
	public DragonHealthController(LimitedEnderDragon dragon, NBTTagList playerMapCompound){
		this(dragon);
		
		for(int i = 0; i < playerMapCompound.size(); i++){
			try{
				NBTTagFloat playerTag = (NBTTagFloat) playerMapCompound.get(i);
				String playerName = playerTag.getName();
				float damage = playerTag.data;
				
				if(playerName != null && !"".equals(playerName)){
					if(this.damageDoneByPlayer.containsKey(playerName)){
						damage += damageDoneByPlayer.get(playerName);
					}
					
					this.damageDoneByPlayer.put(playerName, damage);					
				}
			}catch(Exception exp){}
		}
	}
	
	
	/**
	 * Checks if the Dragon is near a EnderDragonCrystal to regain health
	 */
	public void checkRegainHealth() {
		if (dragon.bC != null) {
			if (dragon.bC.dead) {
				dragon.a(dragon.bq, DamageSource.explosion((Explosion) null), 10F);
				
				dragon.bC = null;
			} else if (dragon.ticksLived % 10 == 0 && dragon.getHealth() < dragon.getMaxHealth()) {
				// CraftBukkit start
				org.bukkit.event.entity.EntityRegainHealthEvent event = new org.bukkit.event.entity.EntityRegainHealthEvent(
						dragon.getBukkitEntity(),
						1.0,
						org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
				dragon.world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					float newDragonHealth = (float) (dragon.getHealth() + event.getAmount());
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

			dragon.bC = entityendercrystal;
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
								(float) damageEvent.getDamage());
					}
				} else {
					double damageDone = plugin.interactConfig().getConfig_dragonDamage();
					entity.damageEntity(DamageSource.mobAttack(dragon), (float) damageDone);
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
	
	public void rememberDamage(DamageSource source, float damage){
		if(source.getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) source.getEntity();
			rememberDamage(player.getName(), damage);
		}
	}
	
	public void rememberDamage(String player, float damage){
		float newDmg = damage;	
		if(damageDoneByPlayer.containsKey(player))
			newDmg += damageDoneByPlayer.get(player);
		
		damageDoneByPlayer.put(player, newDmg);
		lastPlayerAttacked = player;
	}
	
	public Map<String, Float> getPlayerDamage(){
		return damageDoneByPlayer;
	}


	public String getLastPlayerAttacked() {
		return lastPlayerAttacked;
	}


	public float getDamageByPlayer(String player) {
		if(damageDoneByPlayer.containsKey(player))
			return damageDoneByPlayer.get(player);
		
		return 0;
	}


	public void recheckHealthNotOvercaped() {
		float dragonMaxHealth = dragon.getMaxHealth();
		float dragonCurrentHealth = dragon.getHealth();
		
		if(dragonCurrentHealth > dragonMaxHealth){
			dragon.setHealth(dragonMaxHealth);
		}
	}


	public NBTTagList generatePlayerDamageMapAsNBT() {
		NBTTagList compound = new NBTTagList();
		
		for(Entry<String, Float> entry : damageDoneByPlayer.entrySet()){
			String playerName = entry.getKey();
			float dmg = entry.getValue();
			
			compound.add(new NBTTagFloat(playerName, dmg));
		}
		
		return compound;
	}
	
}
