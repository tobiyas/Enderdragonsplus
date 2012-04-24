package de.tobiyas.enderdragonsplus.datacontainer;

import org.bukkit.Location;

import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;

public class DragonInfoContainer {

	public int ID;
	public Location location;
	public Location homeLocation;
	public boolean flyingHome;
	public boolean isLoaded;
	public LimitedEnderDragon dragon;
	public boolean firstLoad = true;
	
	
	public DragonInfoContainer(int ID, Location location, Location homeLocation, boolean flyingHome, boolean isLoaded, LimitedEnderDragon dragon){
		this.ID = ID;
		this.location = location;
		this.homeLocation = homeLocation;
		this.flyingHome = flyingHome;
		this.isLoaded = isLoaded;
		this.dragon = dragon;
	}
}
