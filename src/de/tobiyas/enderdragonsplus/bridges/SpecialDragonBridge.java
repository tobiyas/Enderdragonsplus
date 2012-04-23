package de.tobiyas.enderdragonsplus.bridges;

import org.bukkit.entity.LivingEntity;

public interface SpecialDragonBridge {

	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean isSpecialDragon(LivingEntity entity);
	
	public String getPluginName();
}
