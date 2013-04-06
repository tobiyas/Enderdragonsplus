package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import net.minecraft.server.v1_5_R2.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class NBTTagDragonStore {
	
	
	public static class DragonNBTReturn {

		private Location homeLocation;
		private Location forceTarget;
		private String ageName;
		private boolean flyingHome;
		private NBTTagCompound properties;
		
		private DragonNBTReturn(){
			homeLocation = new Location(Bukkit.getWorlds().get(0), 0d, 0d, 0d);
			forceTarget = homeLocation.clone();
			ageName = "Normal";
			flyingHome = true;
			properties = new NBTTagCompound();
		}

		public DragonNBTReturn setHomeLocation(Location homeLocation) {
			this.homeLocation = homeLocation;
			return this;
		}

		public DragonNBTReturn setForceTarget(Location forceTarget) {
			this.forceTarget = forceTarget;
			return this;
		}

		public DragonNBTReturn setAgeName(String ageName) {
			this.ageName = ageName;
			return this;
		}

		public DragonNBTReturn setFlyingHome(boolean flyingHome) {
			this.flyingHome = flyingHome;
			return this;
		}

		public String getAgeName() {
			return ageName;
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

		public void setProperties(NBTTagCompound properties) {
			this.properties = properties;
		}
	}
	

	public static void saveToNBT(LimitedEnderDragon dragon, NBTTagCompound compound, NBTTagCompound propertiesCompound) {
		compound.setString("age", dragon.getAgeName());
		
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
		
		compound.setCompound("properties", propertiesCompound);
	}
	

	public static DragonNBTReturn loadFromNBT(LimitedEnderDragon dragon, NBTTagCompound compound){
		String worldName = compound.getString("homeLocation.world");
		String ageType = compound.getString("age");
		
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
		
		DragonNBTReturn returnValue = new DragonNBTReturn();
		returnValue.setHomeLocation(homeLocation)
					.setForceTarget(forceTarget)
					.setAgeName(ageType)
					.setFlyingHome(flyingHome)
					.setProperties(properties);
		return returnValue;
	}
	
}
