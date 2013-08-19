package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent.RebounceReason;
import net.minecraft.server.v1_6_R2.DamageSource;
import net.minecraft.server.v1_6_R2.EntityLargeFireball;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.MovingObjectPosition;
import net.minecraft.server.v1_6_R2.Vec3D;
import net.minecraft.server.v1_6_R2.World;

public class LimitedFireball extends EntityLargeFireball {

	private double speedup = 1;
	private int maxSurvivalCounter = 20 * 5; // 20 ticks per sec * 5 seconds

	public LimitedFireball(World world) {
		super(world);
	}

	public LimitedFireball(World world, EntityLiving entityliving, double d0,
			double d1, double d2) {
		super(world, entityliving, d0, d1, d2);
	}

	@Override
	public boolean damageEntity(DamageSource damageSource, float i) {
		if (!damageSource.translationIndex.equals("onFire")) {
			FireballRebounceEvent event = new FireballRebounceEvent(
					(Fireball) this.getBukkitEntity(), RebounceReason.ARROW,
					damageSource.getEntity() == null ? null : damageSource
							.getEntity().getBukkitEntity());

			event = CraftEventFactory.callEvent(event);
			if (event.isCancelled())
				return false;
		}

		K();
		if (damageSource.getEntity() != null) {
			if (damageSource.getEntity() instanceof LimitedEnderDragon)
				return false;
			Vec3D vec3d = damageSource.getEntity().Z();

			if (vec3d != null) {
				this.motX = vec3d.c;
				this.motY = vec3d.d;
				this.motZ = vec3d.e;
				this.dirX = (this.motX * 0.1D);
				this.dirY = (this.motY * 0.1D);
				this.dirZ = (this.motZ * 0.1D);
			}

			/*
			 * // not needed since Enderdragons must be identified
			 * 
			 * if ((damageSource.getEntity() instanceof EntityLiving)) {
			 * this.shooter = ((EntityLiving)damageSource.getEntity()); }
			 */

			return true;
		}
		return false;
	}

	@Override
	public void l_() {
		speedUp();
		if (--maxSurvivalCounter < 0) {
			this.die();
			return;
		}
		super.l_();
	}

	private void speedUp() {
		motX *= speedup;
		motY *= speedup;
		motZ *= speedup;

		K();
	}

	public void speedUp(double speed) {
		this.speedup = speed;
		speedUp();
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		float fireballDamage = (float) EnderdragonsPlus.getPlugin()
				.interactConfig().getConfig_fireballEntityDamage();

		if (movingobjectposition.entity != null) {
			movingobjectposition.entity.damageEntity(
					DamageSource.fireball(this, this.shooter), fireballDamage);
		}

		// CraftBukkit start
		ExplosionPrimeEvent event = new ExplosionPrimeEvent(
				(Explosive) CraftEntity.getEntity(this.world.getServer(), this));
		this.world.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			// give 'this' instead of (Entity) null so we know what causes the
			// damage
			this.world.createExplosion(this, this.locX, this.locY, this.locZ,
					event.getRadius(), event.getFire(), this.world
							.getGameRules().getBoolean("mobGriefing"));
		}
		// CraftBukkit end

		this.die();
	}

}
