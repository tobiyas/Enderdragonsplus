package de.tobiyas.enderdragonsplus.entity.dragon;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class LimitedEnderDragonVersionManager {

	
	/**
	 * Generates a ED where wanted.
	 * 
	 * @param loc the location.
	 * @return the Generated ED
	 */
	public static LimitedED generate(Location loc){
		return generate(loc, "Normal");
	}

	
	/**
	 * Generates a ED where wanted.
	 * 
	 * @param loc the location.
	 * @return the Generated ED
	 */
	public static LimitedED generate(Location loc, String ageName){
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_8_R1")){
			return new de.tobiyas.enderdragonsplus.entity.dragon.v1_8_1.LimitedEnderDragon(loc, loc.getWorld(), ageName);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R4")){
			return new de.tobiyas.enderdragonsplus.entity.dragon.v1_7_4.LimitedEnderDragon(loc, loc.getWorld(), ageName);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R3")){
			return new de.tobiyas.enderdragonsplus.entity.dragon.v1_7_3.LimitedEnderDragon(loc, loc.getWorld(), ageName);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R2")){
			return new de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon(loc, loc.getWorld(), ageName);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R1")){
			return new de.tobiyas.enderdragonsplus.entity.dragon.v1_7_1.LimitedEnderDragon(loc, loc.getWorld(), ageName);
		}
		
		return null;
	}
	
	
	/**
	 * returns if the Current Version is supported.
	 * 
	 * @return true if supported
	 */
	public static boolean isCurrentVersionSupported(){
		String support = EnderdragonsPlus.getRelocationAddition();
		if(support.equalsIgnoreCase("1_8_R1")) return true;
		if(support.equalsIgnoreCase("1_7_R4")) return true;
		if(support.equalsIgnoreCase("1_7_R3")) return true;
		if(support.equalsIgnoreCase("1_7_R2")) return true;
		if(support.equalsIgnoreCase("1_7_R1")) return true;
		
		return false;
	}
	
	
	/**
	 * Returns the class 
	 * 
	 * @return
	 */
	public static Class<?> getCurrentClass(){
		String support = EnderdragonsPlus.getRelocationAddition();
		if(support.equalsIgnoreCase("1_8_R1")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_8_1.LimitedEnderDragon.class;
		if(support.equalsIgnoreCase("1_7_R4")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_4.LimitedEnderDragon.class;
		if(support.equalsIgnoreCase("1_7_R3")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_3.LimitedEnderDragon.class;
		if(support.equalsIgnoreCase("1_7_R2")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon.class;
		if(support.equalsIgnoreCase("1_7_R1")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_1.LimitedEnderDragon.class;
		
		return null;
	}
	
	/**
	 * Replaces the Dragon passed with a new one.
	 * 
	 * @param oldDragon to replace
	 * @param ageName to set
	 * 
	 * @return true if worked.
	 */
	public static boolean replaceEntityWithEDPDragon(EnderDragon oldDragon, String ageName){
		String support = EnderdragonsPlus.getRelocationAddition();
		if(support.equalsIgnoreCase("1_8_R1")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_8_1.LimitedEnderDragon.replaceEntityWithEDPDragon(oldDragon, ageName);
		if(support.equalsIgnoreCase("1_7_R4")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_4.LimitedEnderDragon.replaceEntityWithEDPDragon(oldDragon, ageName);
		if(support.equalsIgnoreCase("1_7_R3")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_3.LimitedEnderDragon.replaceEntityWithEDPDragon(oldDragon, ageName);
		if(support.equalsIgnoreCase("1_7_R2")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon.replaceEntityWithEDPDragon(oldDragon, ageName);
		if(support.equalsIgnoreCase("1_7_R1")) return de.tobiyas.enderdragonsplus.entity.dragon.v1_7_1.LimitedEnderDragon.replaceEntityWithEDPDragon(oldDragon, ageName);
		
		return false;
	}
}
