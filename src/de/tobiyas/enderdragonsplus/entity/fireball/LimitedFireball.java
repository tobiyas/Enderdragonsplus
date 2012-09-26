package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Fireball;

import de.tobiyas.enderdragonsplus.entity.fireball.FireballRebounceEvent.RebounceReason;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class LimitedFireball extends EntityFireball {

	private double speedup = 1;
	
	public LimitedFireball(World world) {
		super(world);
	}
	
	 public LimitedFireball(World world, double d0, double d1, double d2, double d3, double d4, double d5){
		 super(world, d0, d1, d2, d3, d4, d5);
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
			 Vec3D vec3d = damageSource.getEntity().Z();

			 if (vec3d != null) {
				 this.motX = vec3d.a;
				 this.motY = vec3d.b;
				 this.motZ = vec3d.c;
				 this.dirX = (this.motX * 0.1D);
				 this.dirY = (this.motY * 0.1D);
				 this.dirZ = (this.motZ * 0.1D);
			 }

			 if ((damageSource.getEntity() instanceof EntityLiving)) {
				 this.shooter = ((EntityLiving)damageSource.getEntity());
			 }

			 return true;
		 }
		 return false;
	 }
	 
	 @Override
	 public void h_() {
		 speedUp();
		 super.h_();
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
}
