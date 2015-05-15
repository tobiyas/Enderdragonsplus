package de.tobiyas.enderdragonsplus.entity.dragon.controllers.health;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;

public interface IDragonHealthContainer {

	/**
	 * Checks if the Dragon is near a EnderDragonCrystal to regain health
	 */
	public abstract void checkRegainHealth();

	/**
	 * Damages an list of entities
	 * @param list of Entities to damage
	 * @return 
	 */
	public abstract List<LivingEntity> damageEntities(List<LivingEntity> list);

	/**
	 * Returns the mapped health to 200
	 * @return
	 */
	public abstract int mapHealth();

	public abstract void rememberDamage(LivingEntity source, float damage);

	public abstract void rememberDamage(String player, float damage);

	public abstract Map<String, Float> getPlayerDamage();

	public abstract String getLastPlayerAttacked();

	public abstract float getDamageByPlayer(String player);

	public abstract void recheckHealthNotOvercaped();

	public abstract Map<String, Float> generatePlayerDamageMap();

	public abstract void setInvincible(boolean invincible);
	
	public abstract boolean isInvincible();

}