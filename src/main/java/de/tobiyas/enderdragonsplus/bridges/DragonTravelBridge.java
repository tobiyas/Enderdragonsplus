package de.tobiyas.enderdragonsplus.bridges;

/*
import net.minecraft.server.v1_6_R2.EntityEnderDragon;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEnderDragon;
import com.xemsdoom.dt.XemDragon;
*/

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class DragonTravelBridge implements SpecialDragonBridge{
	
	private EnderdragonsPlus plugin;
	private boolean dtEnabled;
	
	
	public DragonTravelBridge(){
		plugin = EnderdragonsPlus.getPlugin();
		Plugin otherPlugin = plugin.getServer().getPluginManager().getPlugin("DragonTravel");
		dtEnabled = (otherPlugin != null  && otherPlugin.isEnabled());
		if(dtEnabled)
			plugin.log("DragonTravel found and hooked.");
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
			//Include when DragonTravel is working again!
			/*EntityEnderDragon dragon = ((CraftEnderDragon) entity).getHandle();
			return false; //dragon instanceof XemDragon;
			*/
			return false;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public String getPluginName() {
		return "DragonTravel";
	}

}
