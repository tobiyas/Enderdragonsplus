package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.configuration.Config;
import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class STDAgeContainer {

	public static final String STDName = "Demo";
	
	public static final String STDAgeName = "Demo";
	public static final int STDmaxHealth = 200;
	public static final int STDspawnHealth = 200;
	public static final List<ItemStack> STDdrops = new LinkedList<ItemStack>();
	public static final int STDexp = 1000;
	public static final int STDdmg = 10;
	public static final boolean STDageIsHostile = false;
	
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
	
	public static void generateDemoAgeContainerFile(){
		File ageFile = new File(Consts.AgeTablePath);
		if(ageFile.exists()){
			return;
		}else{
			try{
				ageFile.createNewFile();
			}catch(Exception exp){}
		}
		
		YAMLConfigExtended config = new YAMLConfigExtended(ageFile);
		
		String lowerSTDName = STDName.toLowerCase();
		config.set(lowerSTDName + maxHealthPath, STDmaxHealth);
		config.set(lowerSTDName + spawnHealthPath, STDspawnHealth);
		config.set(lowerSTDName + rankPath, STDrank);
		config.set(lowerSTDName + dmgPath, STDdmg);
		config.set(lowerSTDName + expPath, STDexp);
		config.set(lowerSTDName + ageNamePath, "&a" + STDName + "&c");
		config.set(lowerSTDName + ageIsHostilePath, STDageIsHostile);
		
		config.setDropContainer(lowerSTDName + lootPrefixPath + ".coolitem", 2, 0, 1, 64, 0.5);
		
		config.save();
	}
	
	/**
	 * Generates a normal dragon with the data given in the Configuration
	 * @return
	 */
	public static AgeContainer generateNormalDragon(){
		
		Config config = EnderdragonsPlus.getPlugin().interactConfig();
		
		String ageName = "'&aNormal&c'";
		int maxHealth = config.getConfig_dragonMaxHealth();
		int spawnHealth = config.getConfig_dragonHealth();
		int exp = config.getConfig_dropEXP();
		int dmg = config.getConfig_dragonDamage();
		boolean isHostile = config.getConfig_dragonsAreHostile();
		int rank = 0;
		List<DropContainer> drops = new LinkedList<DropContainer>();
		
		AgeContainer normalContainer = new AgeContainer("Normal", ageName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
		return normalContainer;
	}
}
