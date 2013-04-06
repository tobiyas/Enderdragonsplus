package de.tobiyas.enderdragonsplus.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.event.CraftEventFactory;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent;
import de.tobiyas.enderdragonsplus.entity.fireball.LimitedFireball;

public class Listener_Fireball implements Listener {

	private EnderdragonsPlus plugin;
	private Random rand;
	private boolean isInExplosion = false;
	
	public Listener_Fireball(){
		this.plugin = EnderdragonsPlus.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		rand = new Random();
	}
	
	@EventHandler
	public void HandleFireballHit(ExplosionPrimeEvent event){
		if(!(event.getEntity() instanceof LimitedFireball)) return; //No fireball -> not interesting for us.
		Projectile fireball = (Projectile) event.getEntity();
		if(fireball.getShooter() == null || !(fireball.getShooter() instanceof EnderDragon))
			return;
		
		if(fireball.getTicksLived() < 30){
			event.setCancelled(true);
			return;
		}
		
		if(fireball.getShooter().getType() == EntityType.ENDER_DRAGON){
			if(plugin.interactConfig().getConfig_disableFireballWorldDamage()){
				handleFireballWithoutExplosion((Fireball) fireball);
				event.setCancelled(true);
			}else
				handleFireballWithExplosion(event);
		}
	}
	
	private void handleFireballWithoutExplosion(Fireball fireball){
		double distance = plugin.interactConfig().getConfig_fireballExplosionRadius();
		List<Entity> nearby = fireball.getNearbyEntities(distance, distance, distance);
		fireball.getWorld().createExplosion(fireball.getLocation(), 0);
		
		int dmg = plugin.interactConfig().getConfig_fireballEntityDamage();
		for(Entity entity : nearby){
			if(!entity.isDead() && entity instanceof LivingEntity){
				if(entity instanceof EnderDragon) continue;
				
				EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(fireball, entity, DamageCause.ENTITY_EXPLOSION, dmg);
				event = CraftEventFactory.callEvent(event);
				if(!event.isCancelled())
					setOnFire((LivingEntity) entity);
			}
		}
	}
	
	private void handleFireballWithExplosion(ExplosionPrimeEvent event){
		handleFireballWithoutExplosion((Fireball) event.getEntity());
		Location loc = event.getEntity().getLocation();
		float radius = plugin.interactConfig().getConfig_fireballExplosionRadius();
		isInExplosion = true;
		loc.getWorld().createExplosion(loc, radius);
		isInExplosion = false;
	}
	
	private void setOnFire(LivingEntity entity){
		int randValue = rand.nextInt(100);
		if(randValue <= plugin.interactConfig().getConfig_fireballSetOnFire())
			return;
		
		int burnTime = plugin.interactConfig().getConfig_fireballBurnTime() * 20;
		burnTime += entity.getFireTicks();
		entity.setFireTicks(burnTime);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event){
		if(event.getEntity() == null || !(event.getEntity() instanceof LargeFireball)) return;
		if(!(event.getEntity().getShooter() instanceof EnderDragon)) return;
		
		if(plugin.interactConfig().getConfig_disableFireballWorldDamage())
			handleFireballWithoutExplosion((Fireball)event.getEntity());
		else{
			int radius = plugin.interactConfig().getConfig_fireballExplosionRadius();
			Location loc = event.getEntity().getLocation();
			loc.getWorld().createExplosion(loc, radius);
		}
	}
	
	@EventHandler
	public void onExplosionDamage(EntityDamageByBlockEvent event){
		if(isInExplosion && event.getCause() == DamageCause.BLOCK_EXPLOSION){
			event.setDamage(0);
		}
	}
	
	@EventHandler
	public void OnFireballBounce(FireballRebounceEvent event){
		boolean disableRebounce = plugin.interactConfig().getConfig_disableFireballRebounce();
		if(disableRebounce)
			event.setCancelled(true);
	}
}
