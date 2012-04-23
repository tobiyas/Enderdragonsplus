package de.tobiyas.enderdragonsplus.bridges;

import net.minecraft.server.EntityEnderDragon;

import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.LivingEntity;

import TheEnd.DragonTravel.DragonTravelMain;
import TheEnd.DragonTravel.XemDragon;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class DragonTravelBridge implements SpecialDragonBridge{
	
	private EnderdragonsPlus plugin;
	private DragonTravelMain dtPlugin;
	private boolean dtEnabled;
	
	
	public DragonTravelBridge(){
		plugin = EnderdragonsPlus.getPlugin();
		dtPlugin = (DragonTravelMain) plugin.getServer().getPluginManager().getPlugin("DragonTravel");
		dtEnabled = (dtPlugin != null  && dtPlugin.isEnabled());
		if(dtEnabled) plugin.log("DragonTravel found and hooked.");
	}
	
	public boolean isXemDragon(LivingEntity entity){
		if(!dtEnabled) return false;
		try{
			EntityEnderDragon dragon = ((CraftEnderDragon) entity).getHandle();
			return dragon instanceof XemDragon;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean isEnabled() {
		return dtEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.dtEnabled = enabled;
	}

	@Override
	public boolean isSpecialDragon(LivingEntity entity) {
		if(!dtEnabled) return false;
		try{
			EntityEnderDragon dragon = ((CraftEnderDragon) entity).getHandle();
			return dragon instanceof XemDragon;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public String getPluginName() {
		return "DragonTravel";
	}

}
