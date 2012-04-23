package de.tobiyas.enderdragonsplus.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class LimitedEnderDragon extends EntityEnderDragon {

	private Entity u;
	private EnderdragonsPlus plugin;
	public static int broadcastedError = 0;
	
	public LimitedEnderDragon(Location location, World world){
		super(world);
		
		plugin = EnderdragonsPlus.getPlugin();
		plugin.getContainer().setHomeID(getBukkitEntity().getEntityId(), location, location, false, this);
		
		setPosition(location.getX(), location.getY(), location.getZ());
		this.yaw = (location.getYaw() + 180.0F);
	    while (this.yaw > 360.0F)
	      this.yaw -= 360.0F;
	    while (this.yaw < 0.0F)
	      this.yaw += 360.0F;
	    if ((this.yaw < 45.0F) || (this.yaw > 315.0F))
	      this.yaw = 0.0F;
	    else if (this.yaw < 135.0F)
	      this.yaw = 90.0F;
	    else if (this.yaw < 225.0F)
	      this.yaw = 180.0F;
	    else
	      this.yaw = 270.0F;
	}
	
	public LimitedEnderDragon(World world){
		super(world);
		plugin = EnderdragonsPlus.getPlugin();
	}
	
	private void checkRegainHealth() {
        if (this.s != null) {
            if (this.s.dead) {
                if (!this.world.isStatic) {
                    this.a(this.g, DamageSource.EXPLOSION, 10);
                }

                this.s = null;
            } else if (this.ticksLived % 10 == 0 && this.health < this.t) {
                // CraftBukkit start
                org.bukkit.event.entity.EntityRegainHealthEvent event = new org.bukkit.event.entity.EntityRegainHealthEvent(this.getBukkitEntity(), 1, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.health += event.getAmount();
                }
                // CraftBukkit end
            }
        }

        if (this.random.nextInt(10) == 0) {
            float f = 32.0F;
            List<?> list = this.world.a(EntityEnderCrystal.class, this.boundingBox.grow((double) f, (double) f, (double) f));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator<?> iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                double d1 = entity.j(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = (EntityEnderCrystal) entity;
                }
            }

            this.s = entityendercrystal;
        }
    }
	
	@Override
	public void e() {
        this.n = this.o;
        if (!this.world.isStatic) {
            this.datawatcher.watch(16, Integer.valueOf(this.health));
        }

        float f;
        float f1;
        float d05;

        if (this.health <= 0) {
            f = (this.random.nextFloat() - 0.5F) * 8.0F;
            d05 = (this.random.nextFloat() - 0.5F) * 4.0F;
            f1 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.a("largeexplode", this.locX + (double) f, this.locY + 2.0D + (double) d05, this.locZ + (double) f1, 0.0D, 0.0D, 0.0D);
        } else {
            this.checkRegainHealth();
            f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
            f *= (float) Math.pow(2.0D, this.motY);
            if (this.q) {
                this.o += f * 0.5F;
            } else {
                this.o += f;
            }

            while (this.yaw >= 180.0F) {
                this.yaw -= 360.0F;
            }

            while (this.yaw < -180.0F) {
                this.yaw += 360.0F;
            }

            if (this.e < 0) {
                for (int i = 0; i < this.d.length; ++i) {
                    this.d[i][0] = (double) this.yaw;
                    this.d[i][1] = this.locY;
                }
            }

            if (++this.e == this.d.length) {
                this.e = 0;
            }

            this.d[this.e][0] = (double) this.yaw;
            this.d[this.e][1] = this.locY;
            double d0;
            double d1;
            double d2;
            double d3;
            float f3;

            if (this.world.isStatic) {
                if (this.aN > 0) {
                    d0 = this.locX + (this.aO - this.locX) / (double) this.aN;
                    d1 = this.locY + (this.aP - this.locY) / (double) this.aN;
                    d2 = this.locZ + (this.aQ - this.locZ) / (double) this.aN;

                    for (d3 = this.aR - (double) this.yaw; d3 < -180.0D; d3 += 360.0D) {
                        ;
                    }

                    while (d3 >= 180.0D) {
                        d3 -= 360.0D;
                    }

                    this.yaw = (float) ((double) this.yaw + d3 / (double) this.aN);
                    this.pitch = (float) ((double) this.pitch + (this.aS - (double) this.pitch) / (double) this.aN);
                    --this.aN;
                    this.setPosition(d0, d1, d2);
                    this.c(this.yaw, this.pitch);
                }
            } else {
                d0 = this.a - this.locX;
                d1 = this.b - this.locY;
                d2 = this.c - this.locZ;
                d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (this.u != null) {
                    this.a = this.u.locX;
                    this.c = this.u.locZ;
                    double d4 = this.a - this.locX;
                    double d5 = this.c - this.locZ;
                    double d6 = Math.sqrt(d4 * d4 + d5 * d5);
                    double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

                    if (d7 > 10.0D) {
                        d7 = 10.0D;
                    }

                    this.b = this.u.boundingBox.b + d7;
                } else {
                    this.a += this.random.nextGaussian() * 2.0D;
                    this.c += this.random.nextGaussian() * 2.0D;
                }

                if (this.p || d3 < 100.0D || d3 > 22500.0D || this.positionChanged || this.bz) {
                    this.changeTarget(false);
                }

                d1 /= (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
                f3 = 0.6F;
                if (d1 < (double) (-f3)) {
                    d1 = (double) (-f3);
                }

                if (d1 > (double) f3) {
                    d1 = (double) f3;
                }

                for (this.motY += d1 * 0.10000000149011612D; this.yaw < -180.0F; this.yaw += 360.0F) {
                    ;
                }

                while (this.yaw >= 180.0F) {
                    this.yaw -= 360.0F;
                }

                double d8 = 180.0D - Math.atan2(d0, d2) * 180.0D / Math.PI;

                double d9;

                for (d9 = d8 - (double) this.yaw; d9 < -180.0D; d9 += 360.0D) {
                    ;
                }

                while (d9 >= 180.0D) {
                    d9 -= 360.0D;
                }

                if (d9 > 50.0D) {
                    d9 = 50.0D;
                }

                if (d9 < -50.0D) {
                    d9 = -50.0D;
                }

                Vec3D vec3d = Vec3D.create(this.a - this.locX, this.b - this.locY, this.c - this.locZ).b();
                Vec3D vec3d1 = Vec3D.create((double) MathHelper.sin(this.yaw * (float)Math.PI / 180.0F), this.motY, (double) (-MathHelper.cos(this.yaw * (float)Math.PI / 180.0F))).b();
                float f4 = (float) (vec3d1.a(vec3d) + 0.5D) / 1.5F;

                if (f4 < 0.0F) {
                    f4 = 0.0F;
                }

                this.aY *= 0.8F;
                float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0F + 1.0F;
                double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0D + 1.0D;

                if (d10 > 40.0D) {
                    d10 = 40.0D;
                }

                this.aY = (float) ((double) this.aY + d9 * (0.699999988079071D / d10 / (double) f5));
                this.yaw += this.aY * 0.1F;
                float f6 = (float) (2.0D / (d10 + 1.0D));
                float f7 = 0.06F;

                this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
                if (this.q) {
                    this.move(this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
                } else {
                    this.move(this.motX, this.motY, this.motZ);
                }

                Vec3D vec3d2 = Vec3D.create(this.motX, this.motY, this.motZ).b();
                float f8 = (float) (vec3d2.a(vec3d1) + 1.0D) / 2.0F;

                f8 = 0.8F + 0.15F * f8;
                this.motX *= (double) f8;
                this.motZ *= (double) f8;
                this.motY *= 0.9100000262260437D;
            }

            this.V = this.yaw;
            this.g.width = this.g.length = 3.0F;
            this.i.width = this.i.length = 2.0F;
            this.j.width = this.j.length = 2.0F;
            this.k.width = this.k.length = 2.0F;
            this.h.length = 3.0F;
            this.h.width = 5.0F;
            this.l.length = 2.0F;
            this.l.width = 4.0F;
            this.m.length = 3.0F;
            this.m.width = 4.0F;
            d05 = (float) (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
            f1 = MathHelper.cos(d05);
            float f9 = -MathHelper.sin(d05);
            float f10 = this.yaw * 3.1415927F / 180.0F;
            float f11 = MathHelper.sin(f10);
            float f12 = MathHelper.cos(f10);

            this.h.F_();
            this.h.setPositionRotation(this.locX + (double) (f11 * 0.5F), this.locY, this.locZ - (double) (f12 * 0.5F), 0.0F, 0.0F);
            this.l.F_();
            this.l.setPositionRotation(this.locX + (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ + (double) (f11 * 4.5F), 0.0F, 0.0F);
            this.m.F_();
            this.m.setPositionRotation(this.locX - (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ - (double) (f11 * 4.5F), 0.0F, 0.0F);
            if (!this.world.isStatic) {
                this.C();
            }

            if (!this.world.isStatic && this.at == 0) {
                this.a(this.world.getEntities(this, this.l.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.a(this.world.getEntities(this, this.m.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.b(this.world.getEntities(this, this.g.boundingBox.grow(1.0D, 1.0D, 1.0D)));
            }

            double[] adouble = this.a(5, 1.0F);
            double[] adouble1 = this.a(0, 1.0F);

            f3 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F - this.aY * 0.01F);
            float f13 = MathHelper.cos(this.yaw * (float)Math.PI / 180.0F - this.aY * 0.01F);

            this.g.F_();
            this.g.setPositionRotation(this.locX + (double) (f3 * 5.5F * f1), this.locY + (adouble1[1] - adouble[1]) * 1.0D + (double) (f9 * 5.5F), this.locZ - (double) (f13 * 5.5F * f1), 0.0F, 0.0F);

            for (int j = 0; j < 3; ++j) {
                EntityComplexPart entitycomplexpart = null;

                if (j == 0) {
                    entitycomplexpart = this.i;
                }

                if (j == 1) {
                    entitycomplexpart = this.j;
                }

                if (j == 2) {
                    entitycomplexpart = this.k;
                }

                double[] adouble2 = this.a(12 + j * 2, 1.0F);
                float f14 = this.yaw * (float)Math.PI / 180.0F + this.fixRotation(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
                float f15 = MathHelper.sin(f14);
                float f16 = MathHelper.cos(f14);
                float f17 = 1.5F;
                float f18 = (float) (j + 1) * 2.0F;

                entitycomplexpart.F_();
                entitycomplexpart.setPositionRotation(this.locX - (double) ((f11 * f17 + f15 * f18) * f1), this.locY + (adouble2[1] - adouble[1]) * 1.0D - (double) ((f18 + f17) * f9) + 1.5D, this.locZ + (double) ((f12 * f17 + f16 * f18) * f1), 0.0F, 0.0F);
            }

            if (!this.world.isStatic) {
                this.q = this.a(this.g.boundingBox) | this.a(this.h.boundingBox);
            }
        }
    }
	
	 private void C() {}
	
	 private void a(List<?> list) {
	        double d0 = (this.h.boundingBox.a + this.h.boundingBox.d) / 2.0D;
	        double d1 = (this.h.boundingBox.c + this.h.boundingBox.f) / 2.0D;
	        Iterator<?> iterator = list.iterator();

	        while (iterator.hasNext()) {
	            Entity entity = (Entity) iterator.next();

	            if (entity instanceof EntityLiving) {
	                double d2 = entity.locX - d0;
	                double d3 = entity.locZ - d1;
	                double d4 = d2 * d2 + d3 * d3;

	                entity.b_(d2 / d4 * 4.0D, 0.2D, d3 / d4 * 4.0D);
	            }
	        }
	    }

	    private void b(List<?> list) {
	        for (int i = 0; i < list.size(); ++i) {
	            Entity entity = (Entity) list.get(i);

	            if (entity instanceof EntityLiving) {
	                // CraftBukkit start - throw damage events when the dragon attacks
	                // The EntityHuman case is handled in EntityHuman, so don't throw it here
	                if (!(entity instanceof EntityHuman)) {
	                    org.bukkit.event.entity.EntityDamageByEntityEvent damageEvent = new org.bukkit.event.entity.EntityDamageByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK, 10);
	                    Bukkit.getPluginManager().callEvent(damageEvent);

	                    if (!damageEvent.isCancelled()) {
	                        entity.damageEntity(DamageSource.mobAttack(this), damageEvent.getDamage());
	                    }
	                } else {
	                    entity.damageEntity(DamageSource.mobAttack(this), 10);
	                }
	                // CraftBukkit end
	            }
	        }
	    }
	
	private float fixRotation(double d0) {
        while (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        while (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return (float) d0;
    }
	
	private boolean a(AxisAlignedBB axisalignedbb) {
	        int i = MathHelper.floor(axisalignedbb.a);
	        int j = MathHelper.floor(axisalignedbb.b);
	        int k = MathHelper.floor(axisalignedbb.c);
	        int l = MathHelper.floor(axisalignedbb.d);
	        int i1 = MathHelper.floor(axisalignedbb.e);
	        int j1 = MathHelper.floor(axisalignedbb.f);
	        boolean flag = false;
	        boolean flag1 = false;

	        // CraftBukkit start - create a list to hold all the destroyed blocks
	        List<org.bukkit.block.Block> destroyedBlocks = new ArrayList<org.bukkit.block.Block>();
	        org.bukkit.craftbukkit.CraftWorld craftWorld = this.world.getWorld();
	        // CraftBukkit end
	        for (int k1 = i; k1 <= l; ++k1) {
	            for (int l1 = j; l1 <= i1; ++l1) {
	                for (int i2 = k; i2 <= j1; ++i2) {
	                    int j2 = this.world.getTypeId(k1, l1, i2);

	                    if (j2 != 0) {
	                        if (j2 != Block.OBSIDIAN.id && j2 != Block.WHITESTONE.id && j2 != Block.BEDROCK.id) {
	                            flag1 = true;
	                            // CraftBukkit start - add blocks to list rather than destroying them
	                            //this.world.setTypeId(k1, l1, i2, 0);
	                            destroyedBlocks.add(craftWorld.getBlockAt(k1, l1, i2));
	                            // CraftBukkit end
	                        } else {
	                            flag = true;
	                        }
	                    }
	                }
	            }
	        }

	        if (flag1) {
	            // CraftBukkit start - set off an EntityExplodeEvent for the dragon exploding all these blocks
	            org.bukkit.entity.Entity bukkitEntity = this.getBukkitEntity();
	            org.bukkit.event.entity.EntityExplodeEvent event = new org.bukkit.event.entity.EntityExplodeEvent(bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0F);
	            Bukkit.getPluginManager().callEvent(event);
	            if (event.isCancelled()) {
	                // this flag literally means 'Dragon hit something hard' (Obsidian, White Stone or Bedrock) and will cause the dragon to slow down.
	                // We should consider adding an event extension for it, or perhaps returning true if the event is cancelled.
	                return flag;
	            } else {
	                for (org.bukkit.block.Block block : event.blockList()) {
	                    craftWorld.explodeBlock(block, event.getYield());
	                }
	            }
	            // CraftBukkit end
	            double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * (double) this.random.nextFloat();
	            double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * (double) this.random.nextFloat();
	            double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * (double) this.random.nextFloat();

	            this.world.a("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
	        }

	        return flag;
	    }
	
	@SuppressWarnings("unchecked")
	public void changeTarget(boolean force){
		try{
		this.p = false;
		boolean includeHeight = plugin.interactConfig().getconfig_includeHeight();
		
		int homeRange = plugin.interactConfig().getconfig_maxHomeDistance();
		Location homeLocation = getHomeLocation();
		
		if(getVectorDistance(homeLocation, includeHeight) > homeRange)
			plugin.getContainer().setFlyingHome(getID(), true);
			
		if(getFlyingHome()) force = true;
        if (!force && this.random.nextInt(2) == 0 && this.world.players.size() > 0) {
        	List<Entity> list = this.world.players;
        	List<Entity> targets = new LinkedList<Entity>();
        	
        	for(Entity player : list){
        		if(plugin.interactConfig().getconfig_ignorePlayerGamemode1()){
        			Player bukkitPlayer = (Player) player.getBukkitEntity();
        			plugin.log(bukkitPlayer.getName() + "  " + bukkitPlayer.getGameMode().getValue());
        			if(bukkitPlayer.getGameMode().getValue() == 1) continue;
        		}
        		
        		int maxRange = plugin.interactConfig().getconfig_maxFollowDistance();
        		if(getVectorDistance(player.locX, player.locY, player.locZ, includeHeight) < maxRange) targets.add(player);
        	}
        	if(targets.size() == 0){
        		changeTarget(true);
        		return;
        	}
        	
        	Entity nextTarget = (Entity) targets.get(this.random.nextInt(this.world.players.size()));
        	
        	//fire bukkit event: Target change
        	if(plugin.interactConfig().getconfig_fireBukkitEvents()){
        		if(!u.equals(nextTarget)){
        			EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), (LivingEntity)nextTarget, TargetReason.RANDOM_TARGET);
        			this.world.getServer().getPluginManager().callEvent(event);
        			if(!event.isCancelled()){
        				this.u = nextTarget;
        			}else
        				changeTarget(true);
        		}
        	}else
        		this.u = nextTarget;
           
        } else {
            boolean flag = false;
            
            if(getVectorDistance(homeLocation, includeHeight) < 100)
            	plugin.getContainer().setFlyingHome(getID(), false);
            
            do {
                this.a = homeLocation.getX();
                this.b = (double) (70.0F + this.random.nextFloat() * 50.0F);
                this.c = homeLocation.getZ();
                this.a += (double) (this.random.nextFloat() * 120.0F - 60.0F);
                this.c += (double) (this.random.nextFloat() * 120.0F - 60.0F);
                double d0 = this.locX - this.a;
                double d1 = this.locY - this.b;
                double d2 = this.locZ - this.c;

                flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
            } while (!flag);

            this.u = null;
        }
		}catch(Exception e){
			if(!plugin.interactConfig().getconfig_debugOutput()) return;
			if(LimitedEnderDragon.broadcastedError != 10){
				LimitedEnderDragon.broadcastedError ++;
				return;
			}
			
			LimitedEnderDragon.broadcastedError = 0;
			plugin.log("An Error has Accured. Tried to access to an illigel mob (function: changeTarget). Disabling ErrorMessage for massive Spaming!");
			plugin.log("ID: " + getID() + " IDs: " + plugin.getContainer().getAllIDs());
			e.printStackTrace();
			return;
		}
	}
	
	public boolean isInRange(Location location, int range, boolean includeHeight){
		if(range == 0) return true;
		double posX = location.getX();
		double posY = location.getY();
		double posZ = location.getZ();
		
		return (getVectorDistance(posX, posY, posZ, includeHeight) <= range);
	}
	
	private double getVectorDistance(double x, double y, double z, boolean includeHeight){
		double deltaX = locX - x;
		double deltaY = 0;
		double deltaZ = locZ - z;
		
		if(includeHeight) deltaY = locY - y;
		
		deltaX *= deltaX;
		deltaY *= deltaY;
		deltaZ *= deltaZ;
		
		return Math.sqrt(deltaX + deltaY + deltaZ);
	}
	
	private double getVectorDistance(Location location, boolean includeHeight){
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		return getVectorDistance(x, y, z, includeHeight);
	}
	
	public void remove(){
		getBukkitEntity().remove();
	}
	

	public String getName(){
		return "EnderDragon";
	}
	
	@Override
	public int getExpReward(){
		return plugin.interactConfig().getconfig_dropEXP();
	}

	public Location getLocation() {
		return getBukkitEntity().getLocation();
	}
	
	public void spawn(){
		spawnCraftBukkit();
	}
	
	private void spawnCraftBukkit(){
		World world = ((CraftWorld) getLocation().getWorld()).getHandle();
		world.addEntity(this);
		setPosition(locX, locY, locZ);
	}
	
	public boolean saveToPath(){
		String path = plugin.getDataFolder() + File.separator + "tempDragons" + File.separator + "dragon." + getID();
		File file = new File(path);
		if(file.exists())
			file.delete();
		
		Location homeLocation = plugin.getContainer().getHomeByID(getBukkitEntity().getEntityId());
		YamlConfiguration config = new YamlConfiguration();
		config.createSection("homeLocation");
		config.set("homeLocation.x", homeLocation.getX());
		config.set("homeLocation.y", homeLocation.getY());
		config.set("homeLocation.z", homeLocation.getZ());
		config.set("homeLocation.world", homeLocation.getWorld().getName());
		
		config.set("actualPosition.x", locX);
		config.set("actualPosition.y", locY);
		config.set("actualPosition.z", locZ);
		
		config.set("flyingHome", getFlyingHome());
		
		config.set("health", this.getHealth());
		try {
			config.save(path);
		} catch (IOException e) {
			plugin.log("Could not save Dragon.");
			return false;
		}
		
		return true;
	}
	
	public static LimitedEnderDragon loadFromFile(String path){
		File file = new File(path);
		if(!file.exists()) return null;
		
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(path);
		}catch(Exception e){
			EnderdragonsPlus.getPlugin().log("Loading Dragon failed.");
			return null;
		}
		
		double x = config.getDouble("homeLocation.x");
		double y = config.getDouble("homeLocation.y");
		double z = config.getDouble("homeLocation.z");
		String worldName = config.getString("homeLocation.world");
		
		double actX = config.getDouble("actualPosition.x");
		double actY = config.getDouble("actualPosition.y");
		double actZ = config.getDouble("actualPosition.z");
		World world = ((CraftWorld) Bukkit.getWorld(worldName)).getHandle();
		
		Location location = new Location(Bukkit.getWorld(worldName) , x, y, z);
		
		int health = config.getInt("health");
		boolean flyingHome = config.getBoolean("flyingHome");
		
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world);
		
		dragon.locX = actX;
		dragon.locY = actY;
		dragon.locZ = actZ;
		
		dragon.setHealth(health);
		EnderdragonsPlus.getPlugin().getContainer().setFlyingHome(dragon.getID(), flyingHome);

		file.delete();
		return dragon;
	}
	
	public Location getHomeLocation(){
		return plugin.getContainer().getHomeByID(getBukkitEntity().getEntityId());
	}
	
	public int getID(){
		return getBukkitEntity().getEntityId();
	}
	
	private boolean getFlyingHome(){
		return plugin.getContainer().getFlyingHome(getBukkitEntity().getEntityId());
	}	
}
