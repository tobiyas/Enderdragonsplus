package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.LinkedList;
import java.util.List;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainer {
	
	private EnderdragonsPlus plugin;

	private String ageName;
	private int maxHealth;
	private int spawnHealth;
	private List<DropContainer> drops;
	private int exp;
	private int dmg;
	
	private int rank;
	
	//Constructor
	public AgeContainer(String ageName){
		plugin = EnderdragonsPlus.getPlugin();
		loadAgeContainer(ageName.toLowerCase());
	}
	
	
	
	//Member Methodes
	private void loadAgeContainer(String tempAgeName){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			plugin.getDebugLogger().logError("Could not load Age: " + ageName);
			return;
		}
		
		maxHealth = config.getInt(tempAgeName + STDAgeContainer.maxHealthPath, plugin.interactConfig().getConfig_dragonMaxHealth());
		spawnHealth = config.getInt(tempAgeName + STDAgeContainer.spawnHealthPath, maxHealth);
		rank = config.getInt(tempAgeName + STDAgeContainer.rankPath, 0);
		exp = config.getInt(tempAgeName + STDAgeContainer.expPath, plugin.interactConfig().getConfig_dropEXP());
		dmg = config.getInt(tempAgeName + STDAgeContainer.dmgPath, plugin.interactConfig().getConfig_dragonDamage());
		ageName = config.getString(tempAgeName + STDAgeContainer.ageNamePath, tempAgeName);
		
		drops = new LinkedList<DropContainer>();
		for(String item : config.getYAMLChildren(tempAgeName + STDAgeContainer.lootPrefixPath)){
			DropContainer container = config.getDropContainer(tempAgeName + STDAgeContainer.lootPrefixPath + "." + item);
			if(container != null)
				drops.add(container);
		}
	}
	
	
	
	//Public Methods
	

	public int getDmg() {
		return dmg;
	}



	//Getter
	public String getAgeName() {
		return ageName;
	}


	public int getMaxHealth() {
		return maxHealth;
	}


	public int getSpawnHealth() {
		return spawnHealth;
	}


	public List<DropContainer> getDrops() {
		return drops;
	}


	public int getExp() {
		return exp;
	}


	public int getRank() {
		return rank;
	}
	
	
}
