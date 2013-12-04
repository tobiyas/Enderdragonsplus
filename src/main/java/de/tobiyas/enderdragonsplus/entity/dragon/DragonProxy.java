package de.tobiyas.enderdragonsplus.entity.dragon;

import java.util.UUID;

import org.bukkit.Location;

import net.minecraft.server.World;

public class DragonProxy extends LimitedEnderDragon {

	public DragonProxy(World world) {
		super(world);
	}



	public DragonProxy(Location location, World world, String ageType) {
		super(location, world, ageType);
	}




	public DragonProxy(Location location, World world, UUID uid, String ageType) {
		super(location, world, uid, ageType);
	}




	public DragonProxy(Location location, World world, UUID uid) {
		super(location, world, uid);
	}




	public DragonProxy(Location location, World world) {
		super(location, world);
	}



	/**
	 * The MC_Version of the Dragon.
	 */
	protected final String MC_VERSION = "1.7.R1";
	
	

	
	/**
	 * The internal Logic Tick.
	 */
	@Override
	public void e(){
		super.internalLogicTick();
	}
	
	
	
	
}
