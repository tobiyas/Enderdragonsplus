package de.tobiyas.enderdragonsplus.bridges;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R2.EntityEnderDragon;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEnderDragon;
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
		Class<?> entityClass = ((CraftEnderDragon) entity).getHandle().getClass();
		Class<?> enderDragonClass = EntityEnderDragon.class;
		
		return ! entityClass.equals(enderDragonClass);
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
