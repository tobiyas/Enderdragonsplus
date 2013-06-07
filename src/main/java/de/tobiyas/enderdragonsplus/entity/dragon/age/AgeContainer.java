package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.LinkedList;
import java.util.List;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainer {
	
	private EnderdragonsPlus plugin;

	private String ageConfigName;
	private String agePrettyName;
	private int maxHealth;
	private int spawnHealth;
	private List<DropContainer> drops;
	private int exp;
	private int dmg;
	private boolean isHostile;
	
	private int rank;
	
	AgeContainer(String ageName) throws AgeNotFoundException{
		plugin = EnderdragonsPlus.getPlugin();
		this.ageConfigName = ageName;

		loadAgeContainer(ageName.toLowerCase());
		checkAnyFieldBroken();
	}
	
	
	//constructor with ALL fields
	public AgeContainer(String ageConfigName, String agePrettyName, int maxHealth, int spawnHealth, int exp, int dmg, boolean isHostile, int rank, List<DropContainer> drops){
		this.ageConfigName = ageConfigName;
		this.agePrettyName = agePrettyName;
		this.maxHealth = maxHealth;
		this.spawnHealth = spawnHealth;
		this.exp = exp;
		this.dmg = dmg;
		this.isHostile = isHostile;
		this.rank = rank;
		this.drops = drops;
	}


	private void checkAnyFieldBroken() throws AgeFieldIncorrectException{
		String tempAgeName = ageConfigName.toLowerCase();
		
		List<String> brokenFields = new LinkedList<String>();
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			plugin.getDebugLogger().logWarning("Age config could not be load correct.");
			brokenFields.add("ROOT");
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.maxHealthPath) ||
				!config.isInt(tempAgeName + STDAgeContainer.maxHealthPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
						STDAgeContainer.maxHealthPath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.maxHealthPath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.spawnHealthPath)||
				!config.isInt(tempAgeName + STDAgeContainer.spawnHealthPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.spawnHealthPath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.spawnHealthPath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.rankPath)||
				!config.isInt(tempAgeName + STDAgeContainer.rankPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.rankPath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.rankPath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.expPath)||
				!config.isInt(tempAgeName + STDAgeContainer.expPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.expPath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.expPath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.dmgPath)||
				!config.isInt(tempAgeName + STDAgeContainer.dmgPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.dmgPath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.dmgPath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.ageNamePath)||
				!config.isString(tempAgeName + STDAgeContainer.ageNamePath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.ageNamePath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.ageNamePath);
		}
		
		if(!config.contains(tempAgeName + STDAgeContainer.ageIsHostilePath)||
				!config.isBoolean(tempAgeName + STDAgeContainer.ageIsHostilePath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.ageIsHostilePath + " not set/incorrect set.");
			brokenFields.add(STDAgeContainer.ageIsHostilePath);
		}
		
		/*
		if(!config.contains(tempAgeName + STDAgeContainer.lootPrefixPath)||
				!config.isList(tempAgeName + STDAgeContainer.rankPath)){
			plugin.getDebugLogger().logWarning("Age: " + tempAgeName + ", path: " + 
					STDAgeContainer.lootPrefixPath + " not set / set incorrect.");
			brokenFields.add(STDAgeContainer.lootPrefixPath);
		}*/ //TODO fix the saving to the NBT Tag
		
		if(brokenFields.size() > 0){
			throw new AgeFieldIncorrectException(brokenFields);
		}
	}
	
	
	//Member Methodes
	private void loadAgeContainer(String tempAgeName){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.AgeTablePath).load();
		if(!config.getValidLoad()){
			plugin.getDebugLogger().logError("Could not load Age: " + agePrettyName);
			return;
		}
		
		maxHealth = config.getInt(tempAgeName + STDAgeContainer.maxHealthPath, plugin.interactConfig().getConfig_dragonMaxHealth());
		spawnHealth = config.getInt(tempAgeName + STDAgeContainer.spawnHealthPath, maxHealth);
		rank = config.getInt(tempAgeName + STDAgeContainer.rankPath, 0);
		exp = config.getInt(tempAgeName + STDAgeContainer.expPath, plugin.interactConfig().getConfig_dropEXP());
		dmg = config.getInt(tempAgeName + STDAgeContainer.dmgPath, plugin.interactConfig().getConfig_dragonDamage());
		agePrettyName = config.getString(tempAgeName + STDAgeContainer.ageNamePath, tempAgeName);
		isHostile = config.getBoolean(tempAgeName + STDAgeContainer.ageIsHostilePath, plugin.interactConfig().getConfig_dragonsAreHostile());
		
		drops = new LinkedList<DropContainer>();
		/*for(String item : config.getChildren(tempAgeName + STDAgeContainer.lootPrefixPath)){
			DropContainer container = config.getDropContainer(tempAgeName + STDAgeContainer.lootPrefixPath + "." + item);
			if(container != null)
				drops.add(container);
		}*/ //TODO fix the saving to the NBT Tag
	}
	
	
	
	//Public Methods
	

	public int getDmg() {
		return dmg;
	}

	//Getter
	public String getAgeName() {
		return agePrettyName;
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


	public String getAgePrettyName() {
		return agePrettyName;
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
			new AgeContainer(ageName);
			return true;
		}catch(AgeNotFoundException e){
			return false;
		}
	}
	
	
	public static List<String> getIncorrectFieldsOfAge(String ageName){
		try{
			new AgeContainer(ageName).checkAnyFieldBroken();
			return new LinkedList<String>();
		}catch(AgeNotFoundException exp){
			AgeFieldIncorrectException correctExp = (AgeFieldIncorrectException) exp;
			return correctExp.getFieldNames();
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
	
	/**
	 * Checks if all Ages are syntactic correct
	 * 
	 * returns a list of all NON correct syntactic ages as name
	 * @return
	 */
	public static List<String> getAllIncorrectAgeNames(){
		List<String> notCorrect = new LinkedList<String>();
		
		List<String> allNames = getAllAgeNames();
		List<String> correctNames = getAllCorrectAgeNames();
		
		for(String name : allNames){
			if(!correctNames.contains(name)){
				notCorrect.add(name);
			}
		}
		
		return notCorrect;
	}



 }
