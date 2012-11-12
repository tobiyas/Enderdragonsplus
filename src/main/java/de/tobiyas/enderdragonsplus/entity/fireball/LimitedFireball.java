package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonV131;
import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent.RebounceReason;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLargeFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class LimitedFireball extends EntityLargeFireball {

	private double speedup = 1;
	private int maxSurvivalCounter = 20 * 3; //20 ticks per sec * 3 seconds
	
	public LimitedFireball(World world) {
		super(world);
	}
	 
	 public LimitedFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
		 super(world, entityliving, d0, d1, d2);
	 }
	 
	 @Override
	 public boolean damageEntity(DamageSource damageSource, int i) {
		 if(!damageSource.translationIndex.equals("onFire")){
			 FireballRebounceEvent event = 
					 new FireballRebounceEvent((Fireball)this.getBukkitEntity(), 
					 RebounceReason.ARROW, 
					 damageSource.getEntity() == null ? null : damageSource.getEntity().getBukkitEntity());
			 
			 event = CraftEventFactory.callEvent(event);
			 if(event.isCancelled())
				 return false;
		 }
		 
		 K();
		 if (damageSource.getEntity() != null) {
			 if(damageSource.getEntity() instanceof LimitedEnderDragonV131) return false;
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
			  // not needed since Enderdragons must be identified
			  *
			 if ((damageSource.getEntity() instanceof EntityLiving)) {
				 this.shooter = ((EntityLiving)damageSource.getEntity());
			 }*/

			 return true;
		 }
		 return false;
	 }
	 
	 @Override
	 public void j_() {
		 speedUp();
		 if(--maxSurvivalCounter < 0){
			 this.die();
			 return;
		 }
		 super.j_();
	 }
	 
	 private void speedUp(){
		 motX *= speedup;
		 motY *= speedup;
		 motZ *= speedup;
		 
		 K();
	 }
	 
	 public void speedUp(double speed){
		 this.speedup = speed;
		 speedUp();
	 }
	 
	 @Override
	 protected void a(MovingObjectPosition movingobjectposition) {
		 int fireballDamage = EnderdragonsPlus.getPlugin().interactConfig().getConfig_fireballEntityDamage();
		 
		 if (movingobjectposition.entity != null) {
			 movingobjectposition.entity.damageEntity(DamageSource.fireball(this, this.shooter), fireballDamage);
		 }

		 // CraftBukkit start
		 ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(this.world.getServer(), this));
		 this.world.getServer().getPluginManager().callEvent(event);
	
		 if (!event.isCancelled()) {
			 // give 'this' instead of (Entity) null so we know what causes the damage
			 this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), this.world.getGameRules().getBoolean("mobGriefing"));
		 }
		 // CraftBukkit end
	
		 this.die();
	 }

}
