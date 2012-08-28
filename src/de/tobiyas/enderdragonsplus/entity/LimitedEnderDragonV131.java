package de.tobiyas.enderdragonsplus.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.LocaleI18n;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class LimitedEnderDragonV131 extends EntityEnderDragon {

	//private Entity currentTarget;
	private int ticksToDespawn = 200;
	
	private EnderdragonsPlus plugin;
	public static int broadcastedError = 0;

	private Location forceGoTo;

	private int logicCall = 0;
	private FireballController fireballController;
	private TargetController targetController;

	public LimitedEnderDragonV131(Location location, World world) {
		super(world);

		plugin = EnderdragonsPlus.getPlugin();
		plugin.getContainer().setHomeID(getUUID(), location, location, false,
				this);

		int maxHealth = plugin.interactConfig().getConfig_dragonMaxHealth();
		if (maxHealth > 0)
			this.a = maxHealth;
		
		setPosition(location.getX(), location.getY(), location.getZ());
		this.expToDrop = plugin.interactConfig().getConfig_dropEXP();
		
		Location homeLocation = getHomeLocation();
		boolean hostile = plugin.interactConfig().getConfig_dragonsAreHostile();
		targetController = new TargetController(homeLocation, this, hostile);
		fireballController = new FireballController(targetController);
	}

	public LimitedEnderDragonV131(Location location, World world, UUID uid) {
		super(world);

		plugin = EnderdragonsPlus.getPlugin();
		changeUUID(uid);
		plugin.getContainer().setHomeID(getUUID(), location, location, false,
				this);

		setPosition(location.getX(), location.getY(), location.getZ());
		int maxHealth = plugin.interactConfig().getConfig_dragonMaxHealth();
		if (maxHealth > 0)
			this.a = maxHealth;
		this.expToDrop = plugin.interactConfig().getConfig_dropEXP();
		
		Location homeLocation = getHomeLocation();
		boolean hostile = plugin.interactConfig().getConfig_dragonsAreHostile();
		targetController = new TargetController(homeLocation, this, hostile);
		fireballController = new FireballController(targetController);
	}

	public LimitedEnderDragonV131(World world) {
		super(world);

		plugin = EnderdragonsPlus.getPlugin();
		if (plugin.interactConfig().getConfig_pluginHandleLoads()) {
			remove();
			return;
		} else
			this.expToDrop = plugin.interactConfig().getConfig_dropEXP();

		if (!plugin.getContainer().containsID(this.getUUID()))
			remove();
	}
	
	@Override
	protected void a() {
		this.datawatcher.a(8, Integer.valueOf(0));
		plugin = EnderdragonsPlus.getPlugin();
		if(!plugin.interactConfig().getConfig_disableDragonHealthBar())
			this.datawatcher.a(16, new Integer(this.a));
	}

	private void checkRegainHealth() {
		if (this.bH != null) {
			if (this.bH.dead) {
				this.a(this.h, DamageSource.EXPLOSION, 10);
				this.bH = null;
			} else if (this.ticksLived % 10 == 0 && this.health < this.a) {
				// CraftBukkit start
				org.bukkit.event.entity.EntityRegainHealthEvent event = new org.bukkit.event.entity.EntityRegainHealthEvent(
						this.getBukkitEntity(),
						1,
						org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
				this.world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					this.health += event.getAmount();
				}
				// CraftBukkit end
			}
		}

		if (this.random.nextInt(10) == 0) {
			float range = 32;
			@SuppressWarnings("unchecked")
			List<Entity> list = this.world.a(EntityEnderCrystal.class,
					this.boundingBox.grow(range, range, range));
			
			EntityEnderCrystal entityendercrystal = null;
			double nearestDistance = Double.MAX_VALUE;

			for(Entity entity : list){
				double currentDistance = entity.e(this);

				if (currentDistance < nearestDistance) {
					nearestDistance = currentDistance;
					entityendercrystal = (EntityEnderCrystal) entity;
				}
			}

			this.bH = entityendercrystal;
		}
	}

	@Override
	public String getLocalizedName() {
		return LocaleI18n.get("entity.EnderDragon.name");
	}

	@Override
	public boolean damageEntity(DamageSource source, int amount) {
		if (health <= 0)
			return false;

		if (this.noDamageTicks > 0)
			return false;

		if (source != DamageSource.GENERIC)
			return false;

		if (source.k())
			return false;

		this.setHealth(health - amount);
		if (this.health <= 0) {
			die(source);
		}				
			
		return true;
	}

	// Logic call. All Dragon logic on tick
	@Override
	public void d(){
		try{
			internalLogicTick();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void internalLogicTick(){
		logicCall++;
		this.bC = this.bD;

		double actualHealth = this.health;
		double maxHealth = this.a;
		
		double percentage = actualHealth / maxHealth;
		int mappedHealth = (int) Math.floor(percentage * 200);
		if(mappedHealth < 0)
			mappedHealth = 0;
		
		if(!plugin.interactConfig().getConfig_disableDragonHealthBar())
			this.datawatcher.watch(16, Integer.valueOf(mappedHealth));

		float f;
		float f1;
		double d05;

		if (this.health <= 0)
			return;

		checkRegainHealth();
		
		f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ
				* this.motZ) * 10.0F + 1.0F);
		f *= (float) Math.pow(2.0D, this.motY);
		
		if (this.bF) {
			this.bD += f * 0.5F;
		} else {
			this.bD += f;
		}

		while (this.yaw >= 180.0F) {
			this.yaw -= 360.0F;
		}

		while (this.yaw < -180.0F) {
			this.yaw += 360.0F;
		}

		if (this.f < 0) {
			for (int i = 0; i < this.e.length; ++i) {
				this.e[i][0] = this.yaw;
				this.e[i][1] = this.locY;
			}
		}

		if (++this.f == this.e.length) {
			this.f = 0;
		}

		this.e[this.f][0] = this.yaw;
		this.e[this.f][1] = this.locY;

		double d0 = this.b - this.locX;
		double d1 = this.c - this.locY;
		double d2 = this.d - this.locZ;
		double d3 = d0 * d0 + d1 * d1 + d2 * d2;
		double f3 = 0.6;
		
		Entity currentTarget = targetController.getCurrentTarget();
		if (currentTarget != null) {
			this.b = currentTarget.locX;
			this.d = currentTarget.locZ;
			
			double d4 = this.b - this.locX;
			double d5 = this.d - this.locZ;
			double d6 = Math.sqrt(d4 * d4 + d5 * d5);
			double d7 = 0.4 + d6 / 80D - 1D;

			if (d7 > 10.0D) {
				d7 = 10.0D;
			}

			this.c = currentTarget.boundingBox.b + d7;
		} else {
			this.b += this.random.nextGaussian() * 2D;
			this.d += this.random.nextGaussian() * 2D;
		}

		if (this.bE || d3 < 100.0D || d3 > 22500.0D || this.positionChanged
				|| this.G) {
			this.changeTarget(false);
		}

		d1 /= MathHelper.sqrt(d0 * d0 + d2 * d2);
		f3 = 0.6F;
		if (d1 < -f3) {
			d1 = -f3;
		}

		if (d1 > f3) {
			d1 = f3;
		}

		/*for (this.motY += d1 * 0.1; this.yaw < -180.0F; this.yaw += 360.0F) {
			;
		}

		while (this.yaw >= 180.0F) {
			this.yaw -= 360.0F;
		}*/
		
		this.motY += d1 * 0.1;
        this.yaw = MathHelper.g(this.yaw);
		double d8 = 180.0D - Math.atan2(d0, d2) * 180.0D / Math.PI;
		double d9 = MathHelper.g(d8 - (double) this.yaw);

		if (d9 > 50.0D) {
			d9 = 50.0D;
		}

		if (d9 < -50.0D) {
			d9 = -50.0D;
		}

		Vec3D vec3d = Vec3D.a().create(this.b - this.locX, this.c - this.locY,
				this.d - this.locZ).b();
		Vec3D vec3d1 = Vec3D.a().create(MathHelper.sin(this.yaw * (float) Math.PI
						/ 180.0F),
						this.motY,
						(-MathHelper.cos(this.yaw * (float) Math.PI
								/ 180.0F))).b();
		float f4 = (float) (vec3d1.b(vec3d) + 0.5D) / 1.5F;

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		this.bt *= 0.8F;
		float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ
				* this.motZ) + 1F;
		double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) + 1.0D;

		if (d10 > 40.0D) {
			d10 = 40.0D;
		}

		this.bt = (float) (this.bt + d9 * (0.7 / d10 / f5));
		this.yaw += this.bt * 0.1F;
		float f6 = (float) (2.0D / (d10 + 1.0D));
		float f7 = 0.06F;

		this.a(0, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
		if (this.bF) {
			this.move(this.motX * 0.8, this.motY * 0.8, this.motZ * 0.8);
		} else {
			this.move(this.motX, this.motY, this.motZ);
		}

		Vec3D vec3d2 = Vec3D.a().create(this.motX, this.motY, this.motZ).b();
		float f8 = (float) (vec3d2.b(vec3d1) + 1.0D) / 2.0F;

		f8 = 0.8F + 0.15F * f8;
		this.motX *= f8;
		this.motZ *= f8;
		this.motY *= 0.91;

		this.aq = this.yaw;
		this.h.width = this.h.length = 3.0F;
		this.j.width = this.j.length = 2.0F;
		this.by.width = this.by.length = 2.0F;
		this.bz.width = this.bz.length = 2.0F;
		this.i.length = 3.0F;
		this.i.width = 5.0F;
		this.bA.length = 2.0F;
		this.bA.width = 4.0F;
		this.bB.length = 3.0F;
		this.bB.width = 4.0F;
		d05 = (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F / 180.0F
				* Math.PI;
		f1 = MathHelper.cos((float) d05);
		float f9 = -MathHelper.sin((float) d05);
		float f10 = (float)(this.yaw * Math.PI / 180D);
		float f11 = MathHelper.sin(f10);
		float f12 = MathHelper.cos(f10);

		this.i.h_();
		this.i.setPositionRotation(this.locX + (f11 * 0.5F),
				this.locY, this.locZ - (f12 * 0.5F), 0.0F, 0.0F);
		this.bA.h_();
		this.bA.setPositionRotation(this.locX + (f12 * 4.5F),
				this.locY + 2.0D, this.locZ + (f11 * 4.5F), 0.0F, 0.0F);
		this.bB.h_();
		this.bB.setPositionRotation(this.locX - (f12 * 4.5F),
				this.locY + 2.0D, this.locZ - (f11 * 4.5F), 0.0F, 0.0F);

		if (this.hurtTicks == 0) {
			this.knockbackNearbyEntities(this.world.getEntities(this, this.bA.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			this.knockbackNearbyEntities(this.world.getEntities(this, this.bB.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			this.damageEntities(this.world.getEntities(this, this.h.boundingBox.grow(1.0D, 1.0D, 1.0D)));
		}

		// LimitedEnderDragon - begin: Added FireBalls here!
		fireballController.checkSpitFireBall();
		// LimitedEnderDragon - end

		double[] adouble = this.a(5, 1.0F);
		double[] adouble1 = this.a(0, 1.0F);

		f3 = MathHelper.sin((float) (this.yaw * Math.PI / 180D - this.bt * 0.01F));
		float f13 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F
				- this.aY * 0.01F);

		this.h.h_();
		this.h.setPositionRotation(this.locX + (f3 * 5.5F * f1),
				this.locY + (adouble1[1] - adouble[1])
						+ (f9 * 5.5F), this.locZ
						- (f13 * 5.5F * f1), 0, 0);

		for (int j = 0; j < 3; ++j) {
			EntityComplexPart entitycomplexpart = null;

			if (j == 0) {
				entitycomplexpart = this.j;
			}

			if (j == 1) {
				entitycomplexpart = this.by;
			}

			if (j == 2) {
				entitycomplexpart = this.bz;
			}

			double[] adouble2 = this.a(12 + j * 2, 1);
			float f14 = (this.yaw + (float) MathHelper.g(adouble2[0] - adouble[0]))
					* (float) Math.PI / 180.0F;
			float f15 = MathHelper.sin(f14);
			float f16 = MathHelper.cos(f14);
			float f17 = 1.5F;
			float f18 = (j + 1) * 2.0F;

			entitycomplexpart.h_();
			entitycomplexpart.setPositionRotation(this.locX - ((f11 * f17 + f15 * f18) * f1), 
					this.locY + (adouble2[1] - adouble[1]) * 1.0D - ((f18 + f17) * f9) + 1.5D, 
					this.locZ + ((f12 * f17 + f16 * f18) * f1), 0.0F, 0.0F);
		}

		this.bF = this.checkHitBlocks(this.h.boundingBox)
				| this.checkHitBlocks(this.i.boundingBox);
	}

	public boolean spitFireBallOnTarget(Entity target) {
		if (target == null)
			return false;

		fireballController.fireFireball(target);
		return true;
	}

	private void knockbackNearbyEntities(List<Entity> entities) {
		double pointX = (this.h.boundingBox.a + this.h.boundingBox.d) / 2;
		double pointZ = (this.h.boundingBox.c + this.h.boundingBox.f) / 2;

		for (Entity entity : entities) {
			if (entity instanceof EntityLiving) {
				double motX = entity.locX - pointX;
				double motY = 0.2;
				double motZ = entity.locZ - pointZ;
				
				double normalizer = motX * motX + motZ * motZ;
				motX = motX /normalizer * 4;
				motZ = motZ / normalizer * 4;

				entity.g(motX, motY, motZ);
			}
		}
	}

	private void damageEntities(List<Entity> list) {
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);

			if (entity instanceof EntityLiving) {
				// CraftBukkit start - throw damage events when the dragon
				// attacks
				// The EntityHuman case is handled in EntityHuman, so don't
				// throw it here
				if (!(entity instanceof EntityHuman)) {
					org.bukkit.event.entity.EntityDamageByEntityEvent damageEvent = new org.bukkit.event.entity.EntityDamageByEntityEvent(
							this.getBukkitEntity(),
							entity.getBukkitEntity(),
							org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK,
							plugin.interactConfig().getConfig_dragonDamage());

					Bukkit.getPluginManager().callEvent(damageEvent);

					if (!damageEvent.isCancelled()) {
						entity.damageEntity(DamageSource.mobAttack(this),
								damageEvent.getDamage());
					}
				} else {
					entity.damageEntity(DamageSource.mobAttack(this), plugin
							.interactConfig().getConfig_dragonDamage());
				}
				// CraftBukkit end
			}
		}
	}

	/*/ original: b(double d0) //deleted, because done by MathHelper
	private float normRotation(double rotation) {
		return (float) MathHelper.g(rotation);
	}*/

	@Override
	protected void aI() {
		ticksToDespawn -= 1;
		if ((ticksToDespawn >= 0) && (ticksToDespawn <= 20)) {
			float offsetX = (this.random.nextFloat() - 0.5F) * 8.0F;
			float offsetY = (this.random.nextFloat() - 0.5F) * 4.0F;
			float offsetZ = (this.random.nextFloat() - 0.5F) * 8.0F;

			this.world.a("hugeexplosion", this.locX + offsetX, 
					this.locY + 2 + offsetY,
					this.locZ + offsetZ,
					0, 0, 0);
		}

		if ((ticksToDespawn < 50) && (ticksToDespawn % 5 == 0)) {
			int exp = this.getExpReward() / 20;
			dropEXPOrbs(exp);
		}
		
		move(0, 0.1, 0);
		this.aq = (this.yaw += 20);
		if (ticksToDespawn <= 0) {
			int exp = this.getExpReward() - this.getExpReward() / 2;
			dropEXPOrbs(exp);

			a(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));
			aH();
			die();
		}
	}
	
	private void dropEXPOrbs(int totalAmount){
		while (totalAmount > 0) {
			int toSubtract = EntityExperienceOrb.getOrbValue(totalAmount);
			totalAmount -= toSubtract;
			this.world.addEntity(new EntityExperienceOrb(this.world,
					this.locX, this.locY, this.locZ, toSubtract));
		}
	}

	// Original: a(AxisAlignedBB axisalignedbb)
	private boolean checkHitBlocks(AxisAlignedBB axisalignedbb) {
		int pos1X = MathHelper.floor(axisalignedbb.a);
		int pos1Y = MathHelper.floor(axisalignedbb.b);
		int pos1Z = MathHelper.floor(axisalignedbb.c);

		int pos2X = MathHelper.floor(axisalignedbb.d);
		int pos2Y = MathHelper.floor(axisalignedbb.e);
		int pos2Z = MathHelper.floor(axisalignedbb.f);

		boolean hitSomethingHard = false;
		boolean hitSomething = false;

		// CraftBukkit start - create a list to hold all the destroyed blocks
		List<org.bukkit.block.Block> destroyedBlocks = new ArrayList<org.bukkit.block.Block>();
		org.bukkit.craftbukkit.CraftWorld craftWorld = this.world.getWorld();
		// CraftBukkit end

		for (int blockX = pos1X; blockX <= pos2X; ++blockX) {
			for (int blockY = pos1Y; blockY <= pos2Y; ++blockY) {
				for (int blockZ = pos1Z; blockZ <= pos2Z; ++blockZ) {
					int blockType = this.world
							.getTypeId(blockX, blockY, blockZ);

					if (blockType != 0) {
						if (blockType != Block.OBSIDIAN.id
								&& blockType != Block.WHITESTONE.id
								&& blockType != Block.BEDROCK.id) {
							hitSomething = true;
							// CraftBukkit start - add blocks to list rather
							// than destroying them
							// this.world.setTypeId(k1, l1, i2, 0);
							destroyedBlocks.add(craftWorld.getBlockAt(blockX,
									blockY, blockZ));
							// CraftBukkit end
						} else {
							hitSomethingHard = true;
						}
					}
				}
			}
		}

		if (!hitSomething)
			return hitSomethingHard;

		// CraftBukkit start - set off an EntityExplodeEvent for the dragon
		// exploding all these blocks
		org.bukkit.entity.Entity bukkitEntity = this.getBukkitEntity();
		org.bukkit.event.entity.EntityExplodeEvent event = new org.bukkit.event.entity.EntityExplodeEvent(
				bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0F);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			// this flag literally means 'Dragon hit something hard' (Obsidian,
			// White Stone or Bedrock) and will cause the dragon to slow down.
			// We should consider adding an event extension for it, or perhaps
			// returning true if the event is cancelled.
			return hitSomethingHard;
		} else {
			for (org.bukkit.block.Block block : event.blockList()) {
				craftWorld.explodeBlock(block, event.getYield());
			}
		}
		// CraftBukkit end

		if (!plugin.interactConfig().getConfig_deactivateBlockExplosionEffect()) {
			double posX = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a)
					* this.random.nextFloat();
			double posY = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b)
					* this.random.nextFloat();
			double posZ = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c)
					* this.random.nextFloat();

			this.world.a("largeexplode", posX, posY, posZ, 0, 0, 0);
		}

		return hitSomethingHard;
	}

	public void changeTarget(boolean force) {
		try {
			this.bE = false;

			int homeRange = plugin.interactConfig().getConfig_maxHomeDistance();
			Location homeLocation = getHomeLocation();

			if (getVectorDistance(homeLocation) > homeRange)
				plugin.getContainer().setFlyingHome(getUUID(), true);

			if (isFlyingHome() || forceGoTo != null)
				force = true;
			
			targetController.switchTargetsWithMode(getLocation());
			Location newTarget = targetController.getTargetLocation();
			
			setNewTarget(newTarget, targetController.getLock());
		} catch (Exception e) {
			if (!plugin.interactConfig().getConfig_debugOutput())
				return;
			if (LimitedEnderDragonV131.broadcastedError != 10) {
				LimitedEnderDragonV131.broadcastedError++;
				return;
			}

			LimitedEnderDragonV131.broadcastedError = 0;
			plugin.log("An Error has Accured. Tried to access to an illigel mob (function: changeTarget). Disabling ErrorMessage for massive Spaming!");
			e.printStackTrace();
			return;
		}
	}

	private void setNewTarget(Location location, boolean lockTarget) {
		if (lockTarget)
			forceGoTo = location;

		if (forceGoTo != null)
			location = forceGoTo;

		if (getVectorDistance(location) < 30) {
			plugin.getContainer().setFlyingHome(getUUID(), false);
			if (forceGoTo != null) {
				forceGoTo = null;
				location = plugin.getContainer().getHomeByID(this.getUUID());
				return;
			}
		}

		double vecDistance = 0;
		do {
			this.b = location.getX();
			this.c = (70.0F + this.random.nextFloat() * 50.0F);
			this.d = location.getZ();
			if (forceGoTo == null) {
				this.b += (this.random.nextFloat() * 120.0F - 60.0F);
				this.d += (this.random.nextFloat() * 120.0F - 60.0F);

				double distanceX = this.locX - this.b;
				double distanceY = this.locY - this.c;
				double distanceZ = this.locZ - this.d;

				vecDistance = distanceX * distanceX + distanceY * distanceY
						+ distanceZ * distanceZ;
			} else {
				this.c = location.getY();
				vecDistance = 101;
			}

		} while (vecDistance < 100);
	}

	public boolean isInRange(Location location, int range) {
		if (range == 0)
			return true;
		if (!this.getLocation().getWorld().equals(location.getWorld()))
			return false;

		double posX = location.getX();
		double posY = location.getY();
		double posZ = location.getZ();

		return (getVectorDistance(posX, posY, posZ) <= range);
	}

	private double getVectorDistance(double x, double y, double z) {
		double deltaX = locX - x;
		double deltaY = locY - y;
		double deltaZ = locZ - z;

		deltaX *= deltaX;
		deltaY *= deltaY;
		deltaZ *= deltaZ;

		return Math.sqrt(deltaX + deltaY + deltaZ);
	}

	private double getVectorDistance(Location location) {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		return getVectorDistance(x, y, z);
	}

	public void remove() {
		getBukkitEntity().remove();
	}

	public String getName() {
		return "EnderDragon";
	}

	@Override
	public int getExpReward() {
		return plugin.interactConfig().getConfig_dropEXP();
	}

	public Location getLocation() {
		return getBukkitEntity().getLocation();
	}

	public boolean spawn(boolean firstLoad) {
		return spawnCraftBukkit(firstLoad);
	}

	private boolean spawnCraftBukkit(boolean firstLoad) {
		World world = ((CraftWorld) getLocation().getWorld()).getHandle();
		if (firstLoad)
			this.getLocation().getChunk().isLoaded();
		if (!world.addEntity(this))
			return false;
		setPosition(locX, locY, locZ);
		return true;
	}	

	public Location getHomeLocation() {
		return plugin.getContainer().getHomeByID(getUUID());
	}

	public int getID() {
		return getBukkitEntity().getEntityId();
	}

	public boolean isFlyingHome() {
		return plugin.getContainer().getFlyingHome(getUUID());
	}

	public void setTarget(LivingEntity entity) {
		EntityLiving convertedEntity = (EntityLiving) ((CraftEntity) entity).getHandle();
		targetController.forceTarget(convertedEntity);
	}

	public org.bukkit.entity.Entity getTarget() {
		Entity entity = targetController.getCurrentTarget();
		if(entity == null)
			return null;
		
		return entity.getBukkitEntity();
	}

	public int getLogicCalls() {
		int calls = logicCall;
		logicCall = 0;
		return calls;
	}

	public void goToLocation(Location location) {
		setNewTarget(location, true);
	}

	public void changeUUID(UUID uID) {
		this.uniqueId = UUID.fromString(uID.toString());
	}

	public UUID getUUID() {
		return this.getBukkitEntity().getUniqueId();
	}
	
	public Location getForceLocation(){
		return this.forceGoTo;
	}
	
	public void addEnemy(org.bukkit.entity.Entity entity){
		CraftEntity craftEntity = (CraftEntity) entity;
		targetController.addTarget((EntityLiving)craftEntity.getHandle());
	}
}