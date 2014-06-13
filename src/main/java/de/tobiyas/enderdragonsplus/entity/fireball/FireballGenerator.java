package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class FireballGenerator {
	
	
	public static LimitedFireball generate(World world, LivingEntity shooter, 
			double x, double y, double z){
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R3")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R3.LimitedFireball_1_7R3(
					((org.bukkit.craftbukkit.v1_7_R3.CraftWorld)world).getHandle(), 
					((org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity) shooter).getHandle(),
					x,y,z
			);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R2")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R2.LimitedFireball_1_7R2(
					((org.bukkit.craftbukkit.v1_7_R2.CraftWorld)world).getHandle(), 
					((org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity) shooter).getHandle(),
					x,y,z
			);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R1")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R1.LimitedFireball_1_7R1(
					((org.bukkit.craftbukkit.v1_7_R1.CraftWorld)world).getHandle(), 
					((org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity) shooter).getHandle(),
					x,y,z
			);
		}
		
		return null;
	}
}
