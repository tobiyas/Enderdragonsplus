package de.tobiyas.enderdragonsplus.entity.dragon.controllers.health;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;

public class DragonHealthController implements IDragonHealthContainer {

	private EnderdragonsPlus plugin;
	private LimitedED dragon;
	private Random random;
	
	private HashMap<String, Float> damageDoneByPlayer;
	private String lastPlayerAttacked = "";
	
	/**
	 * The nearest Crystal to the Dragon
	 */
	private EnderCrystal nearestCrystal = null;
	
	
	public DragonHealthController(LimitedED dragon){
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
	public DragonHealthController(LimitedED dragon, Map<String,Float> playerMapCompound){
		this(dragon);
		
		for(Entry<String,Float> entry : playerMapCompound.entrySet()){
			try{
				String playerName = entry.getKey();
				float damage = entry.getValue();
				
				if(playerName != null && !"".equals(playerName)){
					if(this.damageDoneByPlayer.containsKey(playerName)){
						damage += damageDoneByPlayer.get(playerName);
					}
					
					this.damageDoneByPlayer.put(playerName, damage);					
				}
			}catch(Exception exp){}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#checkRegainHealth()
	 */
	@Override
	public void checkRegainHealth() {
		EnderDragon bukkitDragon = (EnderDragon) dragon.getBukkitEntity();
		
		if (nearestCrystal != null) {
			if (nearestCrystal.isDead()) {
				//dealing 10 dmg for the Crystal.
				bukkitDragon.setHealth(bukkitDragon.getHealth() - 10f);
				//Below old code
				//dragon.a(dragon.bq, DamageSource.explosion((Explosion) null), 10F);
				
				nearestCrystal = null;
			} else if (bukkitDragon.getTicksLived() % 10 == 0 && bukkitDragon.getHealth() < bukkitDragon.getMaxHealth()) {
				// CraftBukkit start
				EntityRegainHealthEvent event = new EntityRegainHealthEvent(
						dragon.getBukkitEntity(),
						1.0,
						EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
				Bukkit.getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					float newDragonHealth = (float) (bukkitDragon.getHealth() + event.getAmount());
					bukkitDragon.setHealth(newDragonHealth);
				}
				// CraftBukkit end
			}
		}

		if (random.nextInt(20) == 0) {
			float range = 32;
			float squaredRange = range * range;
			
			//search nearest enderCrystal
			double nearest = Double.MAX_VALUE;
			nearestCrystal = null;
			
			for(Entity entity : dragon.getBukkitEntity().getNearbyEntities(range, range, range)){
				if(entity instanceof EnderCrystal ){
					double distSquared = entity.getLocation().distanceSquared(dragon.getLocation());
					if(distSquared > squaredRange) continue;
					
					if(distSquared < nearest){
						nearestCrystal = (EnderCrystal) entity;
						nearest = distSquared;
					}
				}
			}

		}
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#damageEntities(java.util.List)
	 */
	@Override
	public List<LivingEntity> damageEntities(List<LivingEntity> list) {
		Iterator<LivingEntity> damageIt = list.iterator();
		while(damageIt.hasNext()){
			LivingEntity entity = damageIt.next();
			// CraftBukkit start - throw damage events when the dragon
			// attacks
			// The EntityHuman case is handled in EntityHuman, so don't
			// throw it here
			if (!(entity instanceof Player)) {
				EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
						dragon.getBukkitEntity(),
						entity,
						EntityDamageEvent.DamageCause.ENTITY_ATTACK,
						dragon.getMeeleDamage());

				Bukkit.getPluginManager().callEvent(damageEvent);

				if(damageEvent.isCancelled() || damageEvent.getDamage() <= 0){
					damageIt.remove();
					continue;
				}

				entity.setLastDamageCause(damageEvent);
				entity.damage(damageEvent.getDamage());
				// CraftBukkit end
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#mapHealth()
	 */
	@Override
	public int mapHealth(){
		EnderDragon bukkitDragon = (EnderDragon) dragon.getBukkitEntity();
		
		double actualHealth = bukkitDragon.getHealth();
		double maxHealth = bukkitDragon.getMaxHealth();
		
		double percentage = actualHealth / maxHealth;
		int mappedHealth = (int) Math.floor(percentage * 200);
		if(mappedHealth < 0)
			mappedHealth = 0;
	
		return mappedHealth;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#rememberDamage(org.bukkit.entity.LivingEntity, float)
	 */
	@Override
	public void rememberDamage(LivingEntity source, float damage){
		if(source instanceof Player){
			rememberDamage(((Player)source).getName(), damage);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#rememberDamage(java.lang.String, float)
	 */
	@Override
	public void rememberDamage(String player, float damage){
		float newDmg = damage;	
		if(damageDoneByPlayer.containsKey(player))
			newDmg += damageDoneByPlayer.get(player);
		
		damageDoneByPlayer.put(player, newDmg);
		lastPlayerAttacked = player;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#getPlayerDamage()
	 */
	@Override
	public Map<String, Float> getPlayerDamage(){
		return damageDoneByPlayer;
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#getLastPlayerAttacked()
	 */
	@Override
	public String getLastPlayerAttacked() {
		return lastPlayerAttacked;
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#getDamageByPlayer(java.lang.String)
	 */
	@Override
	public float getDamageByPlayer(String player) {
		if(damageDoneByPlayer.containsKey(player))
			return damageDoneByPlayer.get(player);
		
		return 0;
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#recheckHealthNotOvercaped()
	 */
	@Override
	public void recheckHealthNotOvercaped() {
		EnderDragon bukkitDragon = (EnderDragon) dragon.getBukkitEntity();
		
		double dragonMaxHealth = bukkitDragon.getMaxHealth();
		double dragonCurrentHealth = bukkitDragon.getHealth();
		
		if(dragonCurrentHealth > dragonMaxHealth){
			bukkitDragon.setHealth(dragonMaxHealth);
		}
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer#generatePlayerDamageMap()
	 */
	@Override
	public Map<String,Float> generatePlayerDamageMap() {
		return new HashMap<String, Float>(damageDoneByPlayer);
	}
	
}
