package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tobiyas.enderdragonsplus.util.Consts;

public class AgeContainerManager {

	private Map<String, AgeContainer> ageMap;
	
	
	/**
	 * Creates a new Association.
	 * Nothing loaded here.
	 */
	public AgeContainerManager(){
		ageMap = new HashMap<String, AgeContainer>();
	}
	
	
	/**
	 * Loads all valid ages to the map.
	 * Clears the map before rebuilding.
	 */
	public void reload(){
		ageMap.clear();
		
		File ageTabelFile = new File(Consts.AgeTablePath);
		
		if(!ageTabelFile.exists()){
			STDAgeContainer.generateDemoAgeContainerFile();
		}
		
		
		List<String> ageNames = AgeContainer.getAllCorrectAgeNames();
		for(String ageName : ageNames){
			if(ageName.equalsIgnoreCase("normal")){
				continue;
			}

			try{	
				AgeContainer ageContainer = new AgeContainer(ageName);
				ageMap.put(ageName.toLowerCase(), ageContainer);
			}catch(Exception exp){}
		}
		
		ageMap.put("normal", STDAgeContainer.generateNormalDragon());
	}
	
	
	/**
	 * Returns an {@link AgeContainer} to the passed ageName.
	 * If the container is not found, an {@link AgeNotFoundException}
	 * is thrown.
	 * 
	 * @param ageName to search
	 * @return the {@link AgeContainer} associated to the name
	 * 
	 * @throws AgeNotFoundException if the name was not found
	 */
	public AgeContainer getAgeContainer(String ageName) throws AgeNotFoundException{
		if(!ageMap.containsKey(ageName.toLowerCase())){
			throw new AgeNotFoundException();
		}
		
		return ageMap.get(ageName.toLowerCase());
	}


	/**
	 * Returns the default Normal {@link AgeContainer}
	 * This container is ALWAYS available
	 * 
	 * @return
	 */
	public AgeContainer getNormalAgeContainer() {
		return ageMap.get("normal");
	}
	
	/**
	 * Returns a Set of all names of ages.
	 * @return
	 */
	public Set<String> getAllAgeNames(){
		return ageMap.keySet();
	}


	public Set<String> getAllIncorrectAgeNames() {
		return AgeContainer.getAllIncorrectAgeNames();
	}
}
