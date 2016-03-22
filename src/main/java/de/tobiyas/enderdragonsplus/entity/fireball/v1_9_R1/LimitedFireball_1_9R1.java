package de.tobiyas.enderdragonsplus.entity.fireball.v1_9_R1;

import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.EntityLargeFireball;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.MovingObjectPosition;
import net.minecraft.server.v1_9_R1.Vec3D;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent.RebounceReason;
import de.tobiyas.enderdragonsplus.entity.fireball.LimitedFireball;

public class LimitedFireball_1_9R1 extends EntityLargeFireball implements LimitedFireball {

	private double speedup = 1;
	private int maxSurvivalCounter = 20 * 5; // 20 ticks per sec * 5 seconds

	public LimitedFireball_1_9R1(World world) {
		super(world);
	}

	public LimitedFireball_1_9R1(World world, EntityLiving entityliving, double d0,
			double d1, double d2) {
		super(world, entityliving, d0, d1, d2);
	}
	
	public LimitedFireball_1_9R1(org.bukkit.World world, org.bukkit.entity.LivingEntity entityliving, double d0,
			double d1, double d2) {
		
		super(((CraftWorld)world).getHandle(), ((CraftLivingEntity)entityliving).getHandle(), d0, d1, d2);
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

		this.ao();
		if (damageSource.getEntity() != null) {
			if (damageSource.getEntity() instanceof LimitedED)
				return false;
			Vec3D vec3d = damageSource.getEntity().aB();

			if (vec3d != null) {
				this.motX = vec3d.x;
				this.motY = vec3d.y;
				this.motZ = vec3d.z;
				this.dirX = (this.motX * 0.1D);
				this.dirY = (this.motY * 0.1D);
				this.dirZ = (this.motZ * 0.1D);
			}

			return true;
		}
		return false;
	}

	@Override
	public void U() {
		speedUp();
		if (--maxSurvivalCounter < 0) {
			this.die();
			return;
		}
		super.U();
	}

	private void speedUp() {
		motX *= speedup;
		motY *= speedup;
		motZ *= speedup;
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
			
			this.die();
		}
		// CraftBukkit end

	}

	@Override
	public void spawnIn(org.bukkit.World world) {
		((CraftWorld) world).getHandle().addEntity(this);
		//this.spawnIn(((CraftWorld) world).getHandle());
	}

}
