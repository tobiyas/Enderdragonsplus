package de.tobiyas.enderdragonsplus.bridges;

import net.minecraft.server.EntityEnderDragon;

import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import de.V10lator.RideThaDragon.V10Dragon;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class RideThaDragonBridge implements SpecialDragonBridge{

	private EnderdragonsPlus plugin;
	private boolean RTDEnabled;
	
	
	public RideThaDragonBridge(){
		plugin = EnderdragonsPlus.getPlugin();
		Plugin otherPlugin = plugin.getServer().getPluginManager().getPlugin("RideThaDragon");
		RTDEnabled = (otherPlugin != null  && otherPlugin.isEnabled());
		if(RTDEnabled) 
			plugin.log("RideThaDragon found and hooked.");
	}

	@Override
	public boolean isEnabled() {
		return RTDEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.RTDEnabled = enabled;
	}

	@Override
	public boolean isSpecialDragon(LivingEntity entity) {
		if(!RTDEnabled) return false;
		try{
			EntityEnderDragon dragon = ((CraftEnderDragon) entity).getHandle();
			return dragon instanceof V10Dragon;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public String getPluginName() {
		return "RideThaDragon";
	}
	
}
