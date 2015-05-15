package de.tobiyas.enderdragonsplus.entity.dragon;

import static de.tobiyas.enderdragonsplus.util.MinecraftChatColorUtils.decodeColors;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityEnderDragon;
import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.GenericAttributes;
import net.minecraft.server.v1_8_R2.LocaleI18n;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.event.CraftEventFactory;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeNotFoundException;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.CollisionController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonHealthController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.ItemLootController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.NBTTagDragonStore;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.NBTTagDragonStore.DragonNBTReturn;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.PropertyController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.TargetController;

public class LimitedEnderDragon extends EntityEnderDragon {
	
	private EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
	public static int broadcastedError = 0;

	private int logicCall = 0;
	
	protected FireballController fireballController;
	protected TargetController targetController;
	protected ItemLootController itemController;
	protected DragonHealthController dragonHealthController;
	protected DragonMoveController dragonMoveController;
	protected AgeContainer ageContainer;
	protected PropertyController propertyController;
	protected CollisionController collisionController;
	
	/**
	 * The Connected Crystal.
	 */
	protected Entity connectedCrystal;
	
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
	
	private void createAllControllers(DragonNBTReturn returnContainer){
		propertyController = new PropertyController(returnContainer);
		ageContainer = returnContainer.getAgeContainer();
		
		ageContainer.setSpawnHealth(returnContainer.getCurrentHealth());
		
		targetController = new TargetController(returnContainer.getHomeLocation(), this, ageContainer.isHostile(), 
				returnContainer.getTargetList());
		fireballController = new FireballController(targetController);
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
		fireballController = new FireballController(targetController);
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
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue((float) ageContainer.getMaxHealth()); //sets health to correct value
		plugin.getContainer().registerDragon(this);
		
		
		String dragonName = decodeColors(ageContainer.getAgePrettyName()) + " Dragon";
		if(dragonName.length() > 30){
			dragonName = dragonName.substring(0, 30);
		}
		
		this.setCustomName(dragonName);
	}
	
	
//	/** This method sets the Life of the EnderDragon
//	 * @see net.minecraft.server.EntityEnderDragon#aD()
//	 */
//	@Override
//	protected void aD() {
//		super.aD();
//		//Health is set somewehere else.
//		
//		/*  Actually doing this: 
//		this.datawatcher.a(8, Integer.valueOf(0));
//		this.datawatcher.a(9, Byte.valueOf(0));
//		this.datawatcher.a(16, new Integer(this.getMaxHealth()));*/
//	}
	
	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		// CraftBukkit start - whole method
		List<ItemStack> loot = generateLoot();

        CraftEventFactory.callEntityDeathEvent(this, loot); // raise event even for those times when the entity does not drop loot
        // CraftBukkit end
    }

	

	@Override
	public String getName() {
		return LocaleI18n.get("entity.EnderDragon.name");
	}

	
	@Override
	public boolean dealDamage(DamageSource damagesource, float i) { // CraftBukkit - protected -> public
		dragonHealthController.rememberDamage(damagesource, i);
		dragonMoveController.restoreOldDataIfPossible();
		return super.dealDamage(damagesource, i);
	}
	

	/**
	 *  Logic call. All Dragon logic on tick
	 * @see net.minecraft.server.EntityEnderDragon#e()
	 */
	@Override
	public void m(){
		try{
			internalLogicTick();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void internalLogicTick(){
		logicCall++;
		
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
	}
	
	/**
	 * The should be the Riding thingy.
	 */
	@Override
	public void g(float motX, float motY){
		if(dragonMoveController.playerMovedEntity(motX, motY)){
			super.g(motX, motY);
		}
	}


	public boolean spitFireBallOnTarget(Entity target) {
		if (target == null)
			return false;

		fireballController.fireFireball(target);
		return true;
	}
	

	/**
	 * Fires a Fireball to a location.
	 * 
	 * @param location to fire to.
	 * @return true if worked.
	 */
	public boolean spitFireBallOnTarget(Location location) {
		if(location == null){
			return false;
		}
		fireballController.fireFireballOnLocation(location);
		return true;
	}

	/** 
	 * Function to drop the EXP if a Dragon is dead
	 * ORIGINAL: aA()
	 * 1.7.9: aE
	 * 1.7.10: aF
	 * 1.8: aY
	 * 1.8.3: aZ
	 * 
	 * Moved to: ItemLootController
	 * 
	 * @see net.minecraft.server.EntityEnderDragon#aY()
	 */
	@Override
	protected void aZ() {
		if(this.dead) return; // CraftBukkit - can't kill what's already dead
		itemController.deathTick();
	}
	
	
	@Override
	public void a(NBTTagCompound compound){
		super.a(compound);
		DragonNBTReturn returnContainer = NBTTagDragonStore.loadFromNBT(this, compound);
		createAllControllers(returnContainer);
	}
	
	/**
	 * Saving dragon data to NBT Compound
	 * @see net.minecraft.server.v1_5_R2.EntityLiving#b(net.minecraft.server.v1_5_R2.NBTTagCompound)
	 */
	@Override
	public void b(NBTTagCompound compound){
		super.b(compound);
		NBTTagDragonStore.saveToNBT(this, compound, propertyController.getAllProperties(), 
				dragonHealthController.generatePlayerDamageMapAsNBT(), targetController.getCurrentTagetsAsNBTList());
	}

	public void remove() {
		getBukkitEntity().remove();
	}


	@Override
	public int getExpReward() {
		return ageContainer.getExp();
		//return plugin.interactConfig().getConfig_dropEXP();
	}

	public Location getLocation() {
		return getBukkitEntity().getLocation();
	}
	
	/**
	 * Returns if the Dragon is Jumping.
	 * 
	 * @return true if jumping.
	 */
	public boolean isJumping(){
		return aY;
	}

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

	public Location getHomeLocation() {
		return targetController.getHomeLocation();
	}

	public int getID() {
		return getBukkitEntity().getEntityId();
	}

	public boolean isFlyingHome() {
		return targetController.isFlyingHome();
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
		this.uniqueID = UUID.fromString(uID.toString());
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
	
	public Map<String, Float> getPlayerDamageDone(){
		return dragonHealthController.getPlayerDamage();
	}
	
	public String getLastPlayerAttacked(){
		return dragonHealthController.getLastPlayerAttacked();
	}

	public float getDamageByPlayer(String player) {
		return dragonHealthController.getDamageByPlayer(player);
	}

	public double getMeeleDamage() {
		return ageContainer.getDmg();
	}
	
	public List<ItemStack> generateLoot(){
		return itemController.getItemDrops(ageContainer.getDrops());
	}
	
	public String getAgeName(){
		return ageContainer.getAgeName();
	}

	public boolean isHostile() {
		return ageContainer.isHostile();
	}

	public void forceFlyHome(boolean flyingHome) {
		targetController.forceFlyingHome(flyingHome);
	}

	public void setNewHome(Location newHomeLocation) {
		targetController.setHomeLocation(newHomeLocation);
	}

	public void setProperty(String property, Object value) throws IllegalArgumentException{
		propertyController.addProperty(property, value);
	}

	public Object getProperty(String property) {
		return propertyController.getProperty(property);
	}

	public AgeContainer getAgeContainer() {
		return ageContainer;
	}
	
	public List<Entity> getAllTargets(){
		return targetController.getAllCurrentTargets();
	}

	public Location getTargetLocation() {
		return targetController.getTargetLocation();
	}

	public FireballController getFireballController() {
		return fireballController;
	}

	public void setFireballController(FireballController fireballController) {
		this.fireballController = fireballController;
	}

	public TargetController getTargetController() {
		return targetController;
	}

	public void setTargetController(TargetController targetController) {
		this.targetController = targetController;
	}

	public ItemLootController getItemController() {
		return itemController;
	}

	public void setItemController(ItemLootController itemController) {
		this.itemController = itemController;
	}

	public DragonHealthController getDragonHealthController() {
		return dragonHealthController;
	}

	public void setDragonHealthController(
			DragonHealthController dragonHealthController) {
		this.dragonHealthController = dragonHealthController;
	}

	public DragonMoveController getDragonMoveController() {
		return dragonMoveController;
	}

	public void setDragonMoveController(DragonMoveController dragonMoveController) {
		this.dragonMoveController = dragonMoveController;
	}

	public PropertyController getPropertyController() {
		return propertyController;
	}

	public void setPropertyController(PropertyController propertyController) {
		this.propertyController = propertyController;
	}

	public CollisionController getCollisionController() {
		return collisionController;
	}

	public void setCollisionController(CollisionController collisionController) {
		this.collisionController = collisionController;
	}

	public Entity getConnectedCrystal() {
		return connectedCrystal;
	}

	public void setConnectedCrystal(Entity connectedCrystal) {
		this.connectedCrystal = connectedCrystal;
	}
	
}