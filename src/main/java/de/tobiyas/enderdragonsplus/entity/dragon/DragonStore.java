package de.tobiyas.enderdragonsplus.entity.dragon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_4_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class DragonStore {
	
	public static LimitedEnderDragon loadFromFile(String path){
		try{
			LimitedEnderDragon dragon = loadFromFileIntern(path);
			return dragon;
		}catch(Exception e){
			EnderdragonsPlus.getPlugin().log("Could not load dragon in path: " + path);
			EnderdragonsPlus.getPlugin().getDebugLogger().logStackTrace(e);
			e.printStackTrace();
			return null;
		}
	}

	private static LimitedEnderDragon loadFromFileIntern(String path) {
		File file = new File(path);
		if (!file.exists()) {
			EnderdragonsPlus.getPlugin().log(
					"Could not find dragon at: " + path);
			return null;
		}

		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(path);
		} catch (Exception e) {
			EnderdragonsPlus.getPlugin().log("Loading Dragon failed.");
			return null;
		}

		UUID uid = UUID.fromString(config.getString("uuid"));

		String worldName = config.getString("homeLocation.world");
		if(Bukkit.getWorld(worldName) == null){ //Check if dragon is in world, which is not present.
			EnderdragonsPlus.getPlugin().log("Dragon with id: " + uid.toString() + " corrupted and is now deleted.");
			file.delete();
			return null;
		}
		
		String ageType = config.getString("age", "Normal");
		
		double x = config.getDouble("homeLocation.x");
		double y = config.getDouble("homeLocation.y");
		double z = config.getDouble("homeLocation.z");
		
		double actX = config.getDouble("actualPosition.x");
		double actY = config.getDouble("actualPosition.y");
		double actZ = config.getDouble("actualPosition.z");
		World world = ((CraftWorld) Bukkit.getWorld(worldName)).getHandle();

		Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

		int health = config.getInt("health");
		boolean flyingHome = config.getBoolean("flyingHome");

		Location forceLocation = null;

		if (config.isString("forceTarget.world")) {
			double forceLocationX = config.getDouble("forceTarget.x");
			double forceLocationY = config.getDouble("forceTarget.y");
			double forceLocationZ = config.getDouble("forceTarget.z");
			String forceLocationWorld = config.getString("forceTarget.world");

			org.bukkit.World forceWorld = Bukkit.getWorld(forceLocationWorld);
			forceLocation = new Location(forceWorld, forceLocationX,
					forceLocationY, forceLocationZ);
		}

		if (EnderdragonsPlus.getPlugin().interactConfig()
				.getConfig_pluginHandleLoads()) {
			LimitedEnderDragon dragon = new LimitedEnderDragon(location, world,
					uid, ageType);

			dragon.locX = actX;
			dragon.locY = actY;
			dragon.locZ = actZ;

			if (forceLocation != null)
				dragon.goToLocation(forceLocation);

			dragon.setHealth(health);

			EnderdragonsPlus.getPlugin().getContainer()
					.setFlyingHome(dragon.getUUID(), flyingHome);
			file.delete();
			return dragon;

		} else {
			List<org.bukkit.entity.Entity> list = location.getWorld()
					.getEntities();

			org.bukkit.entity.Entity dragon = null;
			for (org.bukkit.entity.Entity entity : list) {
				if (entity.getUniqueId().equals(uid)) {
					dragon = entity;
					break;
				}
			}

			if (dragon == null)
				return null;

			LimitedEnderDragon dragonEntity = (LimitedEnderDragon) ((CraftEntity) dragon)
					.getHandle();
			
			EnderdragonsPlus
					.getPlugin()
					.getContainer()
					.setHomeID(uid, location, location, flyingHome,
							dragonEntity);
			
			EnderdragonsPlus.getPlugin().getContainer()
					.setFlyingHome(uid, flyingHome);
			
			file.delete();
			return null;
		}
	}
	
	public static boolean saveToPath(LimitedEnderDragon dragon) {
		EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
		String path = plugin.getDataFolder() + File.separator + "tempDragons"
				+ File.separator + "dragon." + dragon.getUUID();
		File file = new File(path);
		if (file.exists())
			file.delete();

		Location homeLocation = plugin.getContainer().getHomeByID(dragon.getUUID());
		YamlConfiguration config = new YamlConfiguration();
		config.set("age", dragon.getAgeName());
		
		config.createSection("homeLocation");
		config.set("homeLocation.x", homeLocation.getX());
		config.set("homeLocation.y", homeLocation.getY());
		config.set("homeLocation.z", homeLocation.getZ());
		config.set("homeLocation.world", homeLocation.getWorld().getName());

		config.set("actualPosition.x", dragon.locX);
		config.set("actualPosition.y", dragon.locY);
		config.set("actualPosition.z", dragon.locZ);

		config.set("uuid", dragon.getUUID() + "");

		Location forceGoTo = dragon.getForceLocation();
		if (forceGoTo != null) {
			config.set("forceTarget.x", forceGoTo.getX());
			config.set("forceTarget.y", forceGoTo.getY());
			config.set("forceTarget.z", forceGoTo.getZ());
			config.set("forceTarget.world", forceGoTo.getWorld().getName());
		}

		config.set("flyingHome", dragon.isFlyingHome());

		config.set("health", dragon.getHealth());
		try {
			config.save(path);
		} catch (IOException e) {
			plugin.log("Could not save Dragon.");
			return false;
		}

		return true;
	}
	
	

}
