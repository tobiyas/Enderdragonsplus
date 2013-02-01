package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonInfoContainer {

	public UUID ID;
	public Location location;
	public Location homeLocation;
	public boolean flyingHome;
	public boolean isLoaded;
	public LimitedEnderDragon dragon;
	public boolean firstLoad = true;
	public HashMap<String, Object> properties;
	
	
	public DragonInfoContainer(UUID ID, Location location, Location homeLocation, boolean flyingHome, boolean isLoaded, LimitedEnderDragon dragon){
		this.ID = ID;
		this.location = location;
		this.homeLocation = homeLocation;
		this.flyingHome = flyingHome;
		this.isLoaded = isLoaded;
		this.dragon = dragon;
		this.properties = new HashMap<String, Object>();
	}
}
