package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import net.minecraft.server.v1_4_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class NBTTagDragonStore {
	
	
	public static class DragonNBTReturn {

		private Location homeLocation;
		private Location forceTarget;
		private String ageName;
		private boolean flyingHome;
		
		private DragonNBTReturn(Location homeLocation, Location forceTarget, String ageName, boolean flyingHome){
			this.homeLocation = homeLocation;
			this.forceTarget = forceTarget;
			this.ageName = ageName;
			this.flyingHome = flyingHome;
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
	}
	

	public static void saveToNBT(LimitedEnderDragon dragon, NBTTagCompound compound) {
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
		
		DragonNBTReturn returnValue = new DragonNBTReturn(homeLocation, forceTarget, ageType, flyingHome);
		return returnValue;
	}
	
	
}
