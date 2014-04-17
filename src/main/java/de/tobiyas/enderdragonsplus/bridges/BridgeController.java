package de.tobiyas.enderdragonsplus.bridges;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class BridgeController {
	
	@SuppressWarnings("unused")
	private EnderdragonsPlus plugin;
	private List<SpecialDragonBridge> bridge;
	
	public BridgeController(){
		plugin = EnderdragonsPlus.getPlugin();
		bridge = new ArrayList<SpecialDragonBridge>();
		
		bridge.add(new DragonTravelBridge());
		bridge.add(new RideThaDragonBridge());
	}
	
	public boolean isSpecialDragon(LivingEntity entity){
		return false;
//		Class<?> entityClass = ((CraftEnderDragon) entity).getHandle().getClass();
//		String enderDragonClassName = "EntityEnderDragon";
//		
//		return ! entityClass.getName().equalsIgnoreCase(enderDragonClassName);
	}
	
	public boolean isDTActive(String pluginName){
		for(SpecialDragonBridge specialBridge : bridge){
			if(specialBridge.getPluginName().equalsIgnoreCase(pluginName)) 
				return specialBridge.isEnabled();
		}
		
		return false;
	}
	
	public void setDTActive(String pluginName, boolean active){
		for(SpecialDragonBridge specialBridge : bridge){
			if(specialBridge.getPluginName().equalsIgnoreCase(pluginName)) 
				specialBridge.setEnabled(active);
		}
	}

}
