package de.tobiyas.enderdragonsplus.entity.dragon;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonHealthController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.ItemLootController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.TargetController;

import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.LocaleI18n;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class LimitedEnderDragonV131 extends EntityEnderDragon {
	
	private EnderdragonsPlus plugin;
	public static int broadcastedError = 0;

	private int logicCall = 0;
	private FireballController fireballController;
	private TargetController targetController;
	private ItemLootController itemController;
	private DragonHealthController dragonHealthController;
	private DragonMoveController dragonMoveController;

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
		createAllControllers();
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
		createAllControllers();
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
	
	private void createAllControllers(){
		boolean hostile = plugin.interactConfig().getConfig_dragonsAreHostile();
		targetController = new TargetController(getHomeLocation(), this, hostile);
		fireballController = new FireballController(targetController);
		itemController = new ItemLootController(this);
		dragonHealthController = new DragonHealthController(this);
		dragonMoveController = new DragonMoveController(this);
	}
	
	/** This methode sets the Life of the EnderDragon
	 * @see net.minecraft.server.EntityEnderDragon#a()
	 */
	@Override
	protected void a() {
		this.datawatcher.a(8, Integer.valueOf(0));
		this.datawatcher.a(16, new Integer(this.a));
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

		dragonHealthController.checkRegainHealth();
		
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
			targetController.changeTarget(false);
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
			dragonMoveController.knockbackNearbyEntities(this.world.getEntities(this, this.bA.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			dragonMoveController.knockbackNearbyEntities(this.world.getEntities(this, this.bB.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0)));
			dragonHealthController.damageEntities(this.world.getEntities(this, this.h.boundingBox.grow(1.0D, 1.0D, 1.0D)));
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

		this.bF = dragonMoveController.checkHitBlocks(this.h.boundingBox)
				| dragonMoveController.checkHitBlocks(this.i.boundingBox);
	}

	public boolean spitFireBallOnTarget(Entity target) {
		if (target == null)
			return false;

		fireballController.fireFireball(target);
		return true;
	}

	/** 
	 * Function to drop the EXP if a Dragon is dead
	 * ORIGINAL: aI()
	 * Moved to: ItemLootController
	 * 
	 * @see net.minecraft.server.EntityEnderDragon#aI()
	 */
	@Override
	protected void aI() {
		itemController.deathTick();
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
		targetController.setNewTarget(location, true);
	}

	public void changeUUID(UUID uID) {
		this.uniqueId = UUID.fromString(uID.toString());
	}

	public UUID getUUID() {
		return this.getBukkitEntity().getUniqueId();
	}
	
	public Location getForceLocation(){
		return targetController.getForceGoTo();
	}
	
	public void addEnemy(org.bukkit.entity.Entity entity){
		CraftEntity craftEntity = (CraftEntity) entity;
		targetController.addTarget((EntityLiving)craftEntity.getHandle());
	}
	
	public boolean isInRange(Location loc, double range){
		return targetController.isInRange(loc, range);
	}
}