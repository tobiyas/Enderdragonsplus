package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class STDAgeContainer {

	public static final String STDName = "Normal";
	
	public static final String STDAgeName = "Normal";
	public static final int STDmaxHealth = 200;
	public static final int STDspawnHealth = 200;
	public static final List<ItemStack> STDdrops = new LinkedList<ItemStack>();
	public static final int STDexp = 1000;
	public static final int STDdmg = 10;
	
	public static final int STDrank = 0;
	
	
	//paths
	public static String ageNamePath = ".ageName";
	public static String maxHealthPath = ".maxHealth";
	public static String spawnHealthPath = ".spawnHealth";
	public static String rankPath = ".rank";
	public static String expPath = ".exp";
	public static String dmgPath = ".dmg";
	public static String lootPrefixPath = ".loot";
	public static String ageIsHostilePath = ".isHostile";
	
	public static void generateSTDAgeContainer(){
		File ageFile = new File(Consts.AgeTablePath);
		if(ageFile.exists())
			return;
		
		YAMLConfigExtended config = new YAMLConfigExtended(ageFile);
		config.save();
		
		String lowerSTDName = STDName.toLowerCase();
		config.set(lowerSTDName + maxHealthPath, STDmaxHealth);
		config.set(lowerSTDName + spawnHealthPath, STDspawnHealth);
		config.set(lowerSTDName + rankPath, STDrank);
		config.set(lowerSTDName + dmgPath, STDdmg);
		config.set(lowerSTDName + expPath, STDexp);
		config.set(lowerSTDName + ageNamePath, "&a" + STDName + "&c");
		
		config.setDropContainer(lowerSTDName + lootPrefixPath + ".coolitem", 2, 0, 1, 64, 0.5);
		
		config.save();
	}
}
