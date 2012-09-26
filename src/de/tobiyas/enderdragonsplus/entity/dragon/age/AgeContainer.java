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
	
	private int rank;
	
	//Constructor
	public AgeContainer(String ageName){
		plugin = EnderdragonsPlus.getPlugin();
		this.ageName = ageName;
		loadAgeContainer();
	}
	
	
	
	//Member Methodes
	private void loadAgeContainer(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath);
		if(!config.getValidLoad()){
			plugin.getDebugLogger().logError("Could not load Age: " + ageName);
			return;
		}
		
		maxHealth = config.getInt(ageName + "." + "maxHealth", STDAge.STDmaxHealth);
		spawnHealth = config.getInt(ageName + "." + "spawnHealth", maxHealth);
		rank = config.getInt(ageName + "." + "rank", STDAge.STDrank);
		
		drops = new LinkedList<DropContainer>();
		for(String item : config.getYAMLChildren(ageName + "." + "loot")){
			DropContainer container = config.getDropContainer(ageName + ".loot." + item);
			if(container != null)
				drops.add(container);
		}
	}
	
	
	
	//Public Methods
	

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
