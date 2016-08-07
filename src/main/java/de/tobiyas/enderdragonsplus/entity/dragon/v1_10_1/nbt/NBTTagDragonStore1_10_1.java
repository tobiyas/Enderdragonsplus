package de.tobiyas.enderdragonsplus.entity.dragon.v1_10_1.nbt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.NBTTagDragonStore.DragonNBTReturn;
import de.tobiyas.enderdragonsplus.entity.dragon.v1_10_1.LimitedEnderDragon;

public class NBTTagDragonStore1_10_1 {

	
	public void saveToNBT(LimitedEnderDragon dragon, NBTTagCompound compoundToSaveTo, Map<String,Object> propertiesCompound, 
			Map<String,Float> damageMap, List<String> targetList) {
		
		Location homeLocation = dragon.getHomeLocation();
		compoundToSaveTo.setDouble("homeLocation.x", homeLocation.getX());
		compoundToSaveTo.setDouble("homeLocation.y", homeLocation.getY());
		compoundToSaveTo.setDouble("homeLocation.z", homeLocation.getZ());
		compoundToSaveTo.setString("homeLocation.world", homeLocation.getWorld().getName());

		Location forceGoTo = dragon.getForceLocation();
		if (forceGoTo != null) {
			compoundToSaveTo.setDouble("forceTarget.x", forceGoTo.getX());
			compoundToSaveTo.setDouble("forceTarget.y", forceGoTo.getY());
			compoundToSaveTo.setDouble("forceTarget.z", forceGoTo.getZ());
			compoundToSaveTo.setString("forceTarget.world", forceGoTo.getWorld().getName());
		}

		compoundToSaveTo.setBoolean("flyingHome", dragon.isFlyingHome());
		compoundToSaveTo.setBoolean("isHostile", dragon.isHostile());
		
		compoundToSaveTo.setFloat("currentHealth", dragon.getHealth());
		
		compoundToSaveTo.set("properties", getNBTfromMap(propertiesCompound));
		compoundToSaveTo.set("age", AgeContainerBuilder1_10_1.saveToNBTTagCompound(dragon.getAgeContainer()));
		compoundToSaveTo.setString("uuid", dragon.getUUID().toString());
		
		compoundToSaveTo.set("damagemap", getNBTfromDamageMap(damageMap));
		//compoundToSaveTo.set("targetlist", targetList);
	}
	
	
	
	
	private NBTTagCompound getNBTfromDamageMap(Map<String, Float> damageMap) {
		Map<String,Object> convDamageMap = new HashMap<String, Object>(damageMap);
		return getNBTfromMap(convDamageMap);
	}




	/**
	 * Parses a map to an NBT compound
	 * 
	 * @param toStore
	 * @return
	 */
	private NBTTagCompound getNBTfromMap(Map<String,Object> toStore){
		NBTTagCompound properties = new NBTTagCompound();
		for(Entry<String,Object> entry : toStore.entrySet()){
			String name = entry.getKey();
			Object value = entry.getValue();
			try{
				boolean setSomething = false;
				
				if(value instanceof String){
					properties.setString(name, (String) value);
					setSomething = true;
				}
				
				if(value instanceof Long){
					properties.setLong(name, (Long) value);
					setSomething = true;
				}
				
				if(value instanceof Boolean){
					properties.setBoolean(name, (Boolean) value);
					setSomething = true;
				}
				
				if(value instanceof Byte){
					properties.setByte(name, (Byte) value);
					setSomething = true;
				}
		
				if(value instanceof Double){
					properties.setDouble(name, (Double) value);
					setSomething = true;
				}
				
				if(value instanceof Float){
					properties.setFloat(name, (Float) value);
					setSomething = true;
				}
				
				if(value instanceof Integer){
					properties.setInt(name, (Integer) value);
					setSomething = true;
				}
				
				if(value instanceof Short){
					properties.setShort(name, (Short) value);
					setSomething = true;
				}
				
				if(!setSomething){
					String className = value.getClass().getCanonicalName();
					throw new IllegalArgumentException("The given Class '" + className + "' is not supported by NBTTag.");
				}
				
			}catch(Exception exp){
				String className = value.getClass().getCanonicalName();
				System.out.println("The given Class '" + className + "' is not supported by NBTTag.");
				continue;
			}
		}
		
		return properties;
	}
	

	public DragonNBTReturn loadFromNBT(LimitedED dragon, NBTTagCompound compound){
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
		
		//TODO disabling List + maps for testing.
		//NBTTagCompound properties = compound.getCompound("properties");
		UUID uuid = UUID.fromString(compound.getString("uuid"));
		
		float currentHealth = compound.getFloat("currentHealth");
		//NBTTagCompound damageList = compound.getCompound("damagemap");
		//NBTTagCompound targetList = compound.getCompound("targetlist");
		
		DragonNBTReturn returnValue = new DragonNBTReturn();
		returnValue.setHomeLocation(homeLocation)
					.setForceTarget(forceTarget)
					.setAgeContainer(AgeContainerBuilder1_10_1.buildFromNBTTag(compound.getCompound("age")))
					.setFlyingHome(flyingHome)
					//.setProperties(properties)
					.setUuid(uuid)
					.setCurrentHealth(currentHealth);
					//.setDamageList(damageList)
					//.setTargetList(targetList);
		return returnValue;
	}
	
}
