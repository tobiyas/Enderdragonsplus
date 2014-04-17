package de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;

public class NBTTagDragonStore {
	
	
	public static class DragonNBTReturn {

		private Location homeLocation;
		private Location forceTarget;
		private boolean flyingHome = false;
		private Map<String,Object> properties = new HashMap<String, Object>();
		private Map<String,Float> damageList = new HashMap<String, Float>();
		private AgeContainer ageContainer;
		private List<String> targetList = new LinkedList<String>();
		
		private float currentHealth;
		
		private UUID uuid;


		public DragonNBTReturn(){
			homeLocation = new Location(Bukkit.getWorlds().get(0), 0d, 0d, 0d);
			forceTarget = homeLocation.clone();
			flyingHome = true;
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

		public DragonNBTReturn setProperties(Map<String,Object> properties) {
			this.properties = properties;
			return this;
		}

		public DragonNBTReturn setAgeContainer(AgeContainer ageContainer) {
			this.ageContainer = ageContainer;
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
		
		public DragonNBTReturn setDamageList(Map<String,Float> damageList) {
			this.damageList = damageList;
			return this;
		}		


		public DragonNBTReturn setTargetList(List<String> targetList) {
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

		public Map<String,Object> getProperties() {
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

		public Map<String,Float> getDamageList() {
			return damageList;
		}


		public List<String> getTargetList() {
			return targetList;
		}
		
	}
	
}
