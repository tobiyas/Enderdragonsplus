package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import java.util.UUID;

import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainerBuilder;

public class NBTTagDragonStore {
	
	
	public static class DragonNBTReturn {

		private Location homeLocation;
		private Location forceTarget;
		private boolean flyingHome;
		private NBTTagCompound properties;
		private NBTTagList damageList;
		private AgeContainer ageContainer;
		private NBTTagList targetList;
		
		private float currentHealth;
		
		
		private UUID uuid;


		private DragonNBTReturn(){
			homeLocation = new Location(Bukkit.getWorlds().get(0), 0d, 0d, 0d);
			forceTarget = homeLocation.clone();
			flyingHome = true;
			properties = new NBTTagCompound();
			currentHealth = 42;
		}

		
		public DragonNBTReturn setHomeLocation(Location homeLocation) {
			this.homeLocation = homeLocation;
			return this;
		}

		public DragonNBTReturn setForceTarget(Location forceTarget) {
			this.forceTarget = forceTarget;
			return this;
		}

		public DragonNBTReturn setFlyingHome(boolean flyingHome) {
			this.flyingHome = flyingHome;
			return this;
		}

		public DragonNBTReturn setProperties(NBTTagCompound properties) {
			this.properties = properties;
			return this;
		}

		public DragonNBTReturn setAgeContainer(NBTTagCompound nbtTagCompound) {
			ageContainer = AgeContainerBuilder.buildFromNBTTag(nbtTagCompound);
			return this;
		}

		public DragonNBTReturn setCurrentHealth(float currentHealth) {
			this.currentHealth = currentHealth;
			return this;
		}

		public DragonNBTReturn setUuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}
		
		public DragonNBTReturn setDamageList(NBTTagList damageList) {
			this.damageList = damageList;
			return this;
		}		


		public DragonNBTReturn setTargetList(NBTTagList targetList) {
			this.targetList = targetList;
			return this;
		}


		public Location getHomeLocation() {
			return homeLocation;
		}

		public Location getForceTarget() {
			return forceTarget;
		}

		public boolean isFlyingHome() {
			return flyingHome;
		}

		public NBTTagCompound getProperties() {
			return properties;
		}

		public AgeContainer getAgeContainer() {
			return ageContainer;
		}

		public UUID getUuid() {
			return uuid;
		}

		public float getCurrentHealth(){
			return currentHealth;
		}

		public NBTTagList getDamageList() {
			return damageList;
		}


		public NBTTagList getTargetList() {
			return targetList;
		}
		
	}
	

	public static void saveToNBT(LimitedEnderDragon dragon, NBTTagCompound compound, NBTTagCompound propertiesCompound, 
			NBTTagList damageMap, NBTTagList targetList) {
		Location homeLocation = dragon.getHomeLocation();
		compound.setDouble("homeLocation.x", homeLocation.getX());
		compound.setDouble("homeLocation.y", homeLocation.getY());
		compound.setDouble("homeLocation.z", homeLocation.getZ());
		compound.setString("homeLocation.world", homeLocation.getWorld().getName());

		Location forceGoTo = dragon.getForceLocation();
		if (forceGoTo != null) {
			compound.setDouble("forceTarget.x", forceGoTo.getX());
			compound.setDouble("forceTarget.y", forceGoTo.getY());
			compound.setDouble("forceTarget.z", forceGoTo.getZ());
			compound.setString("forceTarget.world", forceGoTo.getWorld().getName());
		}

		compound.setBoolean("flyingHome", dragon.isFlyingHome());
		compound.setBoolean("isHostile", dragon.isHostile());
		
		compound.setFloat("currentHealth", dragon.getHealth());
		
		compound.setCompound("properties", propertiesCompound);
		compound.setCompound("age", AgeContainerBuilder.saveToNBTTagCompound(dragon.getAgeContainer()));
		compound.setString("uuid", dragon.getUUID().toString());
		
		compound.set("damagemap", damageMap);
		compound.set("targetlist", targetList);
	}
	

	public static DragonNBTReturn loadFromNBT(LimitedEnderDragon dragon, NBTTagCompound compound){
		String worldName = compound.getString("homeLocation.world");
		
		double x = compound.getDouble("homeLocation.x");
		double y = compound.getDouble("homeLocation.y");
		double z = compound.getDouble("homeLocation.z");
		
		Location homeLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
		boolean flyingHome = compound.getBoolean("flyingHome");

		Location forceTarget = null;
		if (compound.hasKey("foceTarget.world")) {
			double forceLocationX = compound.getDouble("forceTarget.x");
			double forceLocationY = compound.getDouble("forceTarget.y");
			double forceLocationZ = compound.getDouble("forceTarget.z");
			String forceLocationWorld = compound.getString("forceTarget.world");

			org.bukkit.World forceWorld = Bukkit.getWorld(forceLocationWorld);
			forceTarget = new Location(forceWorld, forceLocationX,
					forceLocationY, forceLocationZ);
		}
		
		NBTTagCompound properties = compound.getCompound("properties");
		UUID uuid = UUID.fromString(compound.getString("uuid"));
		
		float currentHealth = compound.getFloat("currentHealth");
		NBTTagList damageList = compound.getList("damagemap");
		NBTTagList targetList = compound.getList("targetlist");
		
		DragonNBTReturn returnValue = new DragonNBTReturn();
		returnValue.setHomeLocation(homeLocation)
					.setForceTarget(forceTarget)
					.setAgeContainer(compound.getCompound("age"))
					.setFlyingHome(flyingHome)
					.setProperties(properties)
					.setUuid(uuid)
					.setCurrentHealth(currentHealth)
					.setDamageList(damageList)
					.setTargetList(targetList);
		return returnValue;
	}
	
}
