package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class FireballGenerator {
	
	
	public static LimitedFireball generate(World world, LivingEntity shooter, 
			double x, double y, double z){
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_9_R1")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_9_R1.LimitedFireball_1_9R1(
					world, 
					shooter,
					x,y,z
					);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_8_R3")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_8_R3.LimitedFireball_1_8R3(
					world, 
					shooter,
					x,y,z
					);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_8_R2")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_8_R2.LimitedFireball_1_8R2(
					world, 
					shooter,
					x,y,z
					);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_8_R1")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_8_R1.LimitedFireball_1_8R1(
					world, 
					shooter,
					x,y,z
					);
		}

		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R4")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R4.LimitedFireball_1_7R4(
					world, 
					shooter,
					x,y,z
					);
		}

		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R3")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R3.LimitedFireball_1_7R3(
					world, 
					shooter,
					x,y,z
			);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R2")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R2.LimitedFireball_1_7R2(
					world, 
					shooter,
					x,y,z
			);
		}
		
		if(EnderdragonsPlus.getRelocationAddition().equalsIgnoreCase("1_7_R1")){
			return new de.tobiyas.enderdragonsplus.entity.fireball.v1_7_R1.LimitedFireball_1_7R1(
					world, 
					shooter,
					x,y,z
			);
		}
		
		return null;
	}
}
