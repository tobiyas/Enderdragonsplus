package de.tobiyas.enderdragonsplus.entity.firebreath;

import org.bukkit.Location;
import org.bukkit.Material;

public class BurningBlock {

	private int ticksToBurn;
	private Location location;
	private boolean done = false;
	private Material oldMaterial;
	
	
	public BurningBlock(Location location, int ticksToBurn){
		this.ticksToBurn = ticksToBurn;
		this.location = location;
		this.oldMaterial = location.getBlock().getType();
	}
	
	public void tick(){
		if(ticksToBurn != 0){
			location.getBlock().setType(Material.FIRE);
			ticksToBurn--;
		}
		
		if(ticksToBurn == 0){
			location.getBlock().setType(oldMaterial);
			done = true;
		}
	}

	public int getTicksToBurn() {
		return ticksToBurn;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isDone() {
		return done;
	}
}
