package de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2;

import static de.tobiyas.enderdragonsplus.util.MinecraftChatColorUtils.decodeColors;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_7_R2.DamageSource;
import net.minecraft.server.v1_7_R2.EntityComplexPart;
import net.minecraft.server.v1_7_R2.EntityEnderDragon;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.GenericAttributes;
import net.minecraft.server.v1_7_R2.LocaleI18n;
import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.event.CraftEventFactory;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeNotFoundException;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.collision.CollisionController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.collision.ICollisionController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball.IFireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.DragonHealthController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.health.IDragonHealthContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot.IItemLootController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot.ItemLootController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.DragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.IDragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.NBTTagDragonStore.DragonNBTReturn;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.PropertyController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.ITargetController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.targeting.TargetController;
import de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.nbt.NBTTagDragonStore1_7_2;

public class LimitedEnderDragon extends EntityEnderDragon implements LimitedED {
	
	/**
	 * The Global time the Tick has taken on time.
	 */
	public static Long timeTaken = new Long(0);
	public static long totalLogicCalls = 0;
	
	
	private EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
	public static int broadcastedError = 0;

	private int logicCall = 0;
	
	protected IFireballController fireballController;
	protected ITargetController targetController;
	protected IItemLootController itemController;
	protected IDragonHealthContainer dragonHealthController;
	protected IDragonMoveController dragonMoveController;
	protected AgeContainer ageContainer;
	protected PropertyController propertyController;
	protected ICollisionController collisionController;
	
	protected boolean doNothingLock = false;
	protected Vector oldSpeed;
	protected Vector oldTarget;
	

	public LimitedEnderDragon(Location location, World world) {
		this(location, world, "Normal");
	}

	public LimitedEnderDragon(Location location, World world, UUID uid) {
		this(location, world, uid, "Normal");
	}

	public LimitedEnderDragon(Location location, World world, String ageType){
		super(world);

		setPosition(location.getX(), location.getY(), location.getZ());
		createAllControllers(ageType, location);
	}
	
	public LimitedEnderDragon(Location location, World world, UUID uid, String ageType) {
		super(world);
		changeUUID(uid);
		
		setPosition(location.getX(), location.getY(), location.getZ());
		createAllControllers(ageType, location);
	}

	/**
	 * This is the Craft Bukkit spawn
	 * Controllers are set from the NBTTag
	 * @param world
	 */
	public LimitedEnderDragon(World world) {
		super(world);
	}
	
	
	//This below ar constructors for Bukkit Worlds.
	
	public LimitedEnderDragon(Location location, org.bukkit.World world) {
		this(location, getWorld(world));
	}
	
	public LimitedEnderDragon(Location location, org.bukkit.World world, UUID uid) {
		this(location, getWorld(world), uid);
	}
	
	public LimitedEnderDragon(Location location, org.bukkit.World world, String ageType){
		this(location, getWorld(world), ageType);
	}
	
	public LimitedEnderDragon(Location location, org.bukkit.World world, UUID uid, String ageType) {
		this(location, getWorld(world), uid, ageType);
	}
	
	/**
	 * This is the Craft Bukkit spawn
	 * Controllers are set from the NBTTag
	 * @param world
	 */
	public LimitedEnderDragon(org.bukkit.World world) {
		this(getWorld(world));
	}
	
	
	/**
	 * Convets a world to a world! :D lol
	 * 
	 * @param world to convert.
	 * @return
	 */
	private static World getWorld(org.bukkit.World world){
		CraftWorld craftWorld = (CraftWorld) world;
		return craftWorld.getHandle();
	}
	
	//STOP Constructors
	
	private void createAllControllers(DragonNBTReturn returnContainer){
		propertyController = new PropertyController(returnContainer);
		ageContainer = returnContainer.getAgeContainer();
		
		ageContainer.setSpawnHealth(returnContainer.getCurrentHealth());
		
		targetController = new TargetController(returnContainer.getHomeLocation(), this, ageContainer.isHostile(), 
				returnContainer.getTargetList());
		fireballController = new FireballController(this, targetController);
		itemController = new ItemLootController(this);
		dragonHealthController = new DragonHealthController(this, returnContainer.getDamageList());
		dragonMoveController = new DragonMoveController(this);
		collisionController = new CollisionController(this);
		
		this.uniqueID = returnContainer.getUuid();
		
		initStats();
	}
	
	
	private void createAllControllers(String ageType, Location homeLocation){
		boolean hostile = plugin.interactConfig().getConfig_dragonsAreHostile();
		try {
			ageContainer = plugin.getAgeContainerManager().getAgeContainer(ageType);
		} catch (AgeNotFoundException e) {
			ageContainer = plugin.getAgeContainerManager().getNormalAgeContainer();
		}
		
		propertyController = new PropertyController();
		targetController = new TargetController(homeLocation, this, hostile);
		fireballController = new FireballController(this, targetController);
		itemController = new ItemLootController(this);
		dragonHealthController = new DragonHealthController(this);
		dragonMoveController = new DragonMoveController(this);
		collisionController = new CollisionController(this);
		
		
		//Schedules the spawnHealth for next Tick. This should prevent settings from CB side.
		final double spawnhealth = ageContainer.getSpawnHealth();
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnderdragonsPlus.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				try{
					LimitedEnderDragon.this.setHealth((float) spawnhealth);
				}catch(Exception exp){}
			}
		}, 1);
		
		initStats();
	}
	
	
	private void initStats(){
		expToDrop = ageContainer.getExp();
		setHealth((float) ageContainer.getSpawnHealth());
		this.getAttributeInstance(GenericAttributes.a).setValue((float) ageContainer.getMaxHealth()); //sets health to correct value
		plugin.getContainer().registerDragon(this);
		
		
		String dragonName = decodeColors(ageContainer.getAgePrettyName()) + " Dragon";
		if(dragonName.length() > 30){
			dragonName = dragonName.substring(0, 30);
		}
		
		this.setCustomName(dragonName);
	}
	
	
	/** This method sets the Life of the EnderDragon
	 * 
	 * Orig:
	 * - 1.7.2: aD
	 * - 1.7.5: aC
	 * 
	 * @see net.minecraft.server.EntityEnderDragon#aC()
	 */
	@Override
	protected void aC() {
		super.aC();
		//Health is set somewehere else.
		
		/*  Actually doing this: 
		this.datawatcher.a(8, Integer.valueOf(0));
		this.datawatcher.a(9, Byte.valueOf(0));
		this.datawatcher.a(16, new Integer(this.getMaxHealth()));*/
	}
	
	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		// CraftBukkit start - whole method
		List<ItemStack> loot = generateLoot();

        CraftEventFactory.callEntityDeathEvent(this, loot); // raise event even for those times when the entity does not drop loot
        // CraftBukkit end
    }

	

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getName()
	 */
	@Override
	public String getName() {
		return LocaleI18n.get("entity.EnderDragon.name");
	}

	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#dealDamage(net.minecraft.server.DamageSource, float)
	 */
	@Override
	public boolean dealDamage(DamageSource damagesource, float damage) { // CraftBukkit - protected -> public
		if(dragonHealthController.isInvincible()) return false;
		
		//TODO add this again.
		//dragonHealthController.rememberDamage(damagesource.getEntity().getBukkitEntity(), damage);
		dragonMoveController.restoreOldDataIfPossible();
		return super.dealDamage(damagesource, damage);
	}
	

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#e()
	 */
	@Override
	public void e(){
		
		try{
			internalLogicTick();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#internalLogicTick()
	 */
	@Override
	public void internalLogicTick(){
		long before = System.currentTimeMillis();
		logicCall++;
		totalLogicCalls++;
		
		dragonHealthController.recheckHealthNotOvercaped();

		//locks dragons to do absolutely nothing...
		if(doNothingLock){
			return;
		}
		
		if (this.getHealth() <= 0){
			return;
		}

		dragonHealthController.checkRegainHealth();
		dragonMoveController.moveDragon();
		
		
		long after = System.currentTimeMillis();
		long taken = after - before;
		synchronized (timeTaken) {
			timeTaken += taken;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#e(float, float)
	 */
	@Override
	public void e(float motX, float motY){
		if(playerMovedEntity(motX, motY)){
			super.e(motX, motY);
		}
	}
	
	
	/**
	 * We open this up.
	 */
	@Override
	public void callSuperRiding(float sideMot, float forMot, float speed) {
		super.a(sideMot,forMot,speed);
	}

	@Override
	public boolean playerMovedEntity(float motX, float motY){
		return dragonMoveController.playerMovedEntity(motX, motY);
	}
	

	@Override
	public boolean spitFireBallOnTarget(LivingEntity target) {
		if (target == null)
			return false;

		fireballController.fireFireball(target);
		return true;
	}
	

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#spitFireBallOnTarget(org.bukkit.Location)
	 */
	@Override
	public boolean spitFireBallOnTarget(Location location) {
		if(location == null){
			return false;
		}
		fireballController.fireFireballOnLocation(location);
		return true;
	}

	/** 
	 * Function to drop the EXP if a Dragon is dead
	 * ORIGINAL: 
	 * - 1.7.2: aF()
	 * - 1.7.5: aE()
	 * Moved to: ItemLootController
	 * 
	 * @see net.minecraft.server.EntityEnderDragon#aF()
	 */
	@Override
	protected void aE() {
		if(this.dead) return; // CraftBukkit - can't kill what's already dead
		itemController.deathTick();
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#a(net.minecraft.server.NBTTagCompound)
	 */
	@Override
	public void a(NBTTagCompound compound){
		super.a(compound);
		loadAdditionalNBTStuffAndCreateControllers(compound);
	}
	
	@Override
	public void loadAdditionalNBTStuffAndCreateControllers(Object compound){
		NBTTagDragonStore1_7_2 store = new NBTTagDragonStore1_7_2();
		DragonNBTReturn returnContainer = store.loadFromNBT(this, (NBTTagCompound) compound);
		createAllControllers(returnContainer);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#b(net.minecraft.server.NBTTagCompound)
	 */
	@Override
	public void b(NBTTagCompound compound){
		super.b(compound);
		saveAdditionalNBTStuff(compound);
	}
	
	@Override
	public void saveAdditionalNBTStuff(Object compound){
		NBTTagDragonStore1_7_2 store = new NBTTagDragonStore1_7_2();
		
		store.saveToNBT(this, (NBTTagCompound) compound, propertyController.getAllProperties(), 
				dragonHealthController.generatePlayerDamageMap(), targetController.getCurrentTagetsAsStringList());
	}


	@Override
	public void remove() {
		getBukkitEntity().remove();
	}

	@Override
	public void move(double x, double y, double z){
		super.move(x, y, z);

		Location newDragonloc = getLocation();
		//now move the parts to get hits on it.
		for(EntityComplexPart part : children){
			part.h();
			part.setPositionRotation(newDragonloc.getX(), newDragonloc.getY(), newDragonloc.getZ(), yaw, pitch);
		}
	}


	@Override
	public int getExpReward() {
		return ageContainer.getExp();
	}

	@Override
	public Location getLocation() {
		return getBukkitEntity().getLocation();
	}

	@Override
	public boolean spawn() {
		return spawnCraftBukkit();
	}

	private boolean spawnCraftBukkit() {
		World world = ((CraftWorld) getLocation().getWorld()).getHandle();
		Chunk chunk = getLocation().getChunk();
		if(!chunk.isLoaded())
			this.getLocation().getChunk().load();
		
		if (!world.addEntity(this))
			return false;
		setPosition(locX, locY, locZ);
		return true;
	}
	
	/**
	 * Tries to replace a OldDragon with a new One.
	 * 
	 * @param oldDragon
	 */
	public static boolean replaceEntityWithEDPDragon(EnderDragon oldDragon, String ageName){
		EntityEnderDragon castedOldDragon = ((CraftEnderDragon)oldDragon).getHandle();
		castedOldDragon.dead = true;
		
		LimitedEnderDragon newdragon = new LimitedEnderDragon(oldDragon.getLocation(), castedOldDragon.world, oldDragon.getUniqueId(), ageName);		
		newdragon.bukkitEntity = (CraftEnderDragon) oldDragon;
		
		try{
			Field field = CraftEntity.class.getDeclaredField("entity");
			field.setAccessible(true);
			
			field.set(oldDragon, newdragon);

			newdragon.spawn();
			return true;
		}catch (Exception exp) {
			EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
			
			plugin.log("Something gone Wrong with Injecting!");
			plugin.getDebugLogger().logStackTrace(exp);
			//spawning went wrong. Returning...
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getHomeLocation()
	 */
	@Override
	public Location getHomeLocation() {
		return targetController.getHomeLocation();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getID()
	 */
	@Override
	public int getID() {
		return getBukkitEntity().getEntityId();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#isFlyingHome()
	 */
	@Override
	public boolean isFlyingHome() {
		return targetController.isFlyingHome();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setTarget(org.bukkit.entity.LivingEntity)
	 */
	@Override
	public void setTarget(LivingEntity entity) {
		targetController.forceTarget(entity);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getTarget()
	 */
	@Override
	public org.bukkit.entity.LivingEntity getTarget() {
		return targetController.getCurrentTarget();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getLogicCalls()
	 */
	@Override
	public int getLogicCalls() {
		int calls = logicCall;
		logicCall = 0;
		return calls;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#goToLocation(org.bukkit.Location)
	 */
	@Override
	public void goToLocation(Location location) {
		targetController.setNewTarget(location, true);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#changeUUID(java.util.UUID)
	 */
	@Override
	public void changeUUID(UUID uID) {
		this.uniqueID = UUID.fromString(uID.toString());
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getUUID()
	 */
	@Override
	public UUID getUUID() {
		return this.getBukkitEntity().getUniqueId();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getForceLocation()
	 */
	@Override
	public Location getForceLocation(){
		return targetController.getForceGoTo();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#addEnemy(org.bukkit.entity.Entity)
	 */
	@Override
	public void addEnemy(LivingEntity entity){
		targetController.addTarget(entity);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#isInRange(org.bukkit.Location, double)
	 */
	@Override
	public boolean isInRange(Location loc, double range){
		return targetController.isInRange(loc, range);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getPlayerDamageDone()
	 */
	@Override
	public Map<String, Float> getPlayerDamageDone(){
		return dragonHealthController.getPlayerDamage();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getLastPlayerAttacked()
	 */
	@Override
	public String getLastPlayerAttacked(){
		return dragonHealthController.getLastPlayerAttacked();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getDamageByPlayer(java.lang.String)
	 */
	@Override
	public float getDamageByPlayer(String player) {
		return dragonHealthController.getDamageByPlayer(player);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getMeeleDamage()
	 */
	@Override
	public double getMeeleDamage() {
		return ageContainer.getDmg();
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#generateLoot()
	 */
	@Override
	public List<ItemStack> generateLoot(){
		return itemController.getItemDrops(ageContainer.getDrops());
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getAgeName()
	 */
	@Override
	public String getAgeName(){
		return ageContainer.getAgeName();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#isHostile()
	 */
	@Override
	public boolean isHostile() {
		return ageContainer.isHostile();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#forceFlyHome(boolean)
	 */
	@Override
	public void forceFlyHome(boolean flyingHome) {
		targetController.forceFlyingHome(flyingHome);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setNewHome(org.bukkit.Location)
	 */
	@Override
	public void setNewHome(Location newHomeLocation) {
		targetController.setHomeLocation(newHomeLocation);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String property, Object value){
		propertyController.addProperty(property, value);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String property) {
		return propertyController.getProperty(property);
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getAgeContainer()
	 */
	@Override
	public AgeContainer getAgeContainer() {
		return ageContainer;
	}
	
	@Override
	public List<LivingEntity> getAllTargets(){
		return targetController.getAllCurrentTargets();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getTargetLocation()
	 */
	@Override
	public Location getTargetLocation() {
		return targetController.getTargetLocation();
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getFireballController()
	 */
	@Override
	public IFireballController getFireballController() {
		return fireballController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setFireballController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.FireballController)
	 */
	@Override
	public void setFireballController(IFireballController fireballController) {
		this.fireballController = fireballController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getTargetController()
	 */
	@Override
	public ITargetController getTargetController() {
		return targetController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setTargetController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.TargetController)
	 */
	@Override
	public void setTargetController(ITargetController targetController) {
		this.targetController = targetController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getItemController()
	 */
	@Override
	public IItemLootController getItemController() {
		return itemController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setItemController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.ItemLootController)
	 */
	@Override
	public void setItemController(IItemLootController itemController) {
		this.itemController = itemController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getDragonHealthController()
	 */
	@Override
	public IDragonHealthContainer getDragonHealthController() {
		return dragonHealthController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setDragonHealthController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonHealthController)
	 */
	@Override
	public void setDragonHealthController(
			IDragonHealthContainer dragonHealthController) {
		this.dragonHealthController = dragonHealthController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getDragonMoveController()
	 */
	@Override
	public IDragonMoveController getDragonMoveController() {
		return dragonMoveController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setDragonMoveController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonMoveController)
	 */
	@Override
	public void setDragonMoveController(IDragonMoveController dragonMoveController) {
		this.dragonMoveController = dragonMoveController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#getPropertyController()
	 */
	@Override
	public PropertyController getPropertyController() {
		return propertyController;
	}

	/* (non-Javadoc)
	 * @see de.tobiyas.enderdragonsplus.entity.dragon.LimitedED#setPropertyController(de.tobiyas.enderdragonsplus.entity.dragon.controllers.PropertyController)
	 */
	@Override
	public void setPropertyController(PropertyController propertyController) {
		this.propertyController = propertyController;
	}
	
	@Override
	public boolean isSupportedOnCurrentServer(){
		return true;
	}

	@Override
	public void setCollisionController(ICollisionController collisionController) {
		this.collisionController = collisionController;
	}

	@Override
	public org.bukkit.World getBukkitWorld() {
		return getLocation().getWorld();
	}

	@Override
	public ICollisionController getCollisionController() {
		return collisionController;
	}


	@Override
	public Location getMinBBLocation() {
		return new Location(getBukkitWorld(), boundingBox.a, boundingBox.b, boundingBox.c);
	}

	@Override
	public Location getMaxBBLocation() {
		return new Location(getBukkitWorld(), boundingBox.d, boundingBox.e, boundingBox.f);
	}

	@Override
	public float getPitch() {
		return this.pitch;
	}

	@Override
	public void setPitch(float newPitch) {
		this.pitch = newPitch;
	}

	@Override
	public float getYaw() {
		return this.yaw;
	}

	@Override
	public void setYaw(float newYaw) {
		this.yaw = newYaw;
	}

	@Override
	public Entity getPassenger() {
		return getBukkitEntity().getPassenger();
	}

	@Override
	public void setPassenger(Entity newPassenger) {
		this.getBukkitEntity().setPassenger(newPassenger);
	}

	@Override
	public Vector getMotion() {
		return new Vector(motX, motY, motZ);
	}

	@Override
	public void setMotion(Vector vec) {
		this.motX = vec.getX();
		this.motY = vec.getY();
		this.motZ = vec.getZ();
	}

	@Override
	public void setNativeLocation(Location loc) {
		this.locX = loc.getX();
		this.locY = loc.getY();
		this.locZ = loc.getZ();
	}

	@Override
	public float getLastYaw() {
		return this.lastYaw;
	}

	@Override
	public void setLastYaw(float newLastYaw) {
		this.lastYaw = newLastYaw;
	}
	
	@Override
	public void sHealth(double newHealth) {
		this.setHealth((float) Math.min(this.getMaxHealth(), newHealth));
	}
	
	@Override
	public void sMaxHealth(double maxHealth) {
		((EnderDragon)this.getBukkitEntity()).setMaxHealth(maxHealth);
	}
	
	@Override
	public double gHealth() {
		return this.getHealth();
	}
	
	@Override
	public double gMaxHealth() {
		return super.getMaxHealth();
	}

	
	@Override
	public float getPassengerSideMot() {
		if(passenger == null) return 0;
		return ((EntityLiving)passenger).be;
	}

	@Override
	public float getPassengerForMot() {
		if(passenger == null) return 0;
		return ((EntityLiving)passenger).bd;
	}

	@Override
	public void setPassengerSideMot(float newValue) {
		if(passenger == null) return;
		((EntityLiving)passenger).be = newValue;
	}

	@Override
	public void setPassengerForMot(float newValue) {
		if(passenger == null) return;
		((EntityLiving)passenger).bd = newValue;
	}

	@Override
	public void move(Vector directionToMove) {
		this.move(directionToMove.getX(), directionToMove.getY(), directionToMove.getZ());
	}

	@Override
	public boolean damage(DamageCause cause, double value) {
		if(dragonHealthController.isInvincible()) return false;
		
		return this.damageEntity(DamageSource.MAGIC, (float) value);
	}

	@Override
	public boolean dealDamage(DamageCause damagesource, float amount) {
		if(dragonHealthController.isInvincible()) return false;
		
		return this.damage(damagesource, amount);
	}


	@Override
	public String getPlayerIsJumpingFieldName() {
		return "bc";
	}
	
	@Override
	public boolean hasCollision() {
		return this.dragonMoveController.hasCollision();
	}

	@Override
	public void setCollision(boolean collision) {
		this.dragonMoveController.setCollision(collision);
		this.collisionController.setCollision(collision);
	}
	
	
}