package de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R1;

import net.minecraft.server.v1_7_R1.DamageSource;
import net.minecraft.server.v1_7_R1.EntityLargeFireball;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent;
import de.tobiyas.enderdragonsplus.entity.fireball.LimitedFireball;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent.RebounceReason;

public class LimitedFireball_1_7R1 extends EntityLargeFireball implements LimitedFireball {

	private double speedup = 1;
	private int maxSurvivalCounter = 20 * 5; // 20 ticks per sec * 5 seconds

	public LimitedFireball_1_7R1(World world) {
		super(world);
	}

	public LimitedFireball_1_7R1(World world, EntityLiving entityliving, double d0,
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

		Q();
		if (damageSource.getEntity() != null) {
			if (damageSource.getEntity() instanceof LimitedED)
				return false;
			Vec3D vec3d = damageSource.getEntity().ag();

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
	public void h() {
		speedUp();
		if (--maxSurvivalCounter < 0) {
			this.die();
			return;
		}
		super.h();
	}

	private void speedUp() {
		motX *= speedup;
		motY *= speedup;
		motZ *= speedup;

		P();
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

	@Override
	public void spawnIn(org.bukkit.World world) {
		this.spawnIn(((CraftWorld) world).getHandle());
	}

}
