package de.tobiyas.enderdragonsplus.damagewhisperer;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

public class EntityDamageWhisperController {
	
	public EntityDamageWhisperController(){
	}
	
	public void playerGotDamage(Player player){
		new PlayerDamageWhisperer(player);
	}
	
	public void dragonGotDamage(EnderDragon dragon, Player player){
		new DragonDamageWhisperer(dragon, player);
	}
	
}
