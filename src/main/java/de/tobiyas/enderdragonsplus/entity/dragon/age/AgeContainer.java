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
	private boolean isHostile;
	
	private int rank;
	
	//Constructor
	public AgeContainer(String ageName) throws AgeNotFoundException{
		plugin = EnderdragonsPlus.getPlugin();
		this.ageName = ageName;

		loadAgeContainer(ageName.toLowerCase());
		
		if(!areAllNeededFieldsSet()){
			throw new AgeNotFoundException();
		}
		
	}
	
	
	//secure constructor with STD stuff
	public AgeContainer() {
		plugin = EnderdragonsPlus.getPlugin();
		this.ageName = "Normal";
		loadAgeContainer(ageName.toLowerCase());
	}
	
	
	//constructor withh ALL fields
	public AgeContainer(String ageName, int maxHealth, int spawnHealth, int exp, int dmg, boolean isHostile, int rank, List<DropContainer> drops){
		this.ageName = ageName;
		this.maxHealth = maxHealth;
		this.spawnHealth = spawnHealth;
		this.exp = exp;
		this.dmg = dmg;
		this.isHostile = isHostile;
		this.rank = rank;
		this.drops = drops;
	}


	private boolean areAllNeededFieldsSet(){
		String tempAgeName = ageName.toLowerCase();
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			plugin.getDebugLogger().logWarning("Age config could not be load correct.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.maxHealthPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
						STDAgeContainer.maxHealthPath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.spawnHealthPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.spawnHealthPath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.rankPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.rankPath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.expPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.expPath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.dmgPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.dmgPath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.ageNamePath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.ageNamePath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.ageIsHostilePath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.ageIsHostilePath + " not set/incorrect set.");
			return false;
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.lootPrefixPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.lootPrefixPath + " not set / set incorrect.");
			return false;
		}
		
		return true;
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
		isHostile = config.getBoolean(tempAgeName + STDAgeContainer.ageIsHostilePath, plugin.interactConfig().getConfig_dragonsAreHostile());
		
		drops = new LinkedList<DropContainer>();
		for(String item : config.getChildren(tempAgeName + STDAgeContainer.lootPrefixPath)){
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



	public boolean isHostile() {
		return isHostile;
	}
	
	/**
	 * Checks if the age with that name is given in a config
	 * 
	 * @param ageName
	 * @return
	 */
	public static boolean ageExists(String ageName){
		try{
			return new AgeContainer(ageName).areAllNeededFieldsSet();
		}catch(AgeNotFoundException e){
			return false;
		}
	}
	
	/**
	 * Gives all available ages as List
	 * 
	 * @return
	 */
	public static List<String> getAllAgeNames(){
		List<String> ageNames = new LinkedList<String>();
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			return ageNames;
		}
		
		ageNames.addAll(config.getRootChildren());
		return ageNames;
	}
	
	/**
	 * Gives all available ages as List
	 * The Agenames are checked if they are available and correct to load before
	 * 
	 * @return
	 */
	public static List<String> getAllCorrectAgeNames(){
		List<String> ageNames = new LinkedList<String>();
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			return ageNames;
		}
		
		for(String ageName : config.getRootChildren()){
			if(ageExists(ageName)){
				ageNames.add(ageName);
			}
		}
		return ageNames;
	}

 }
