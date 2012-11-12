package de.tobiyas.enderdragonsplus.damagewhisperer;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class DragonDamageWhisperer implements Runnable{
	
	private static LinkedList<EnderDragon> damageList = new LinkedList<EnderDragon>();
	
	private EnderDragon dragon;
	private int dragonHealth;
	private String damager;
	
	private int tickerTime = 5;

	public DragonDamageWhisperer(EnderDragon dragon, Player player) {
		if(damageList.contains(dragon))
			return;
		
		damager = player.getName();
		dragonHealth = dragon.getHealth();
		this.dragon = dragon;
		
		damageList.add(dragon);
		EnderdragonsPlus plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, tickerTime);
	}

	@Override
	public void run() {
		damageList.remove(dragon);
		Player player = Bukkit.getPlayer(damager);
		if(player == null)
			return;
		int newHealth = dragon.getHealth();
		if(newHealth < dragonHealth)
			broadcastDamage(player, dragonHealth - newHealth);
	}
	
	private void broadcastDamage(Player player, int damage){
		int actualLife = dragon.getHealth();
		int maxLife = dragon.getMaxHealth();
		
		String midLifeString = parsePersentageLife(actualLife, maxLife);
		player.sendMessage(ChatColor.YELLOW + "The Dragon has " + midLifeString + ChatColor.YELLOW + " health left. You did " + 
				ChatColor.LIGHT_PURPLE + damage + ChatColor.YELLOW + " damage.");
	}
	
	private String parsePersentageLife(int actual, int max){
		float currentPercentage = actual / max;
		
		if(currentPercentage < 0.2)
			return ChatColor.RED + "" + actual + "/" + max;
		
		if(currentPercentage < 0.5)
			return ChatColor.YELLOW + "" + actual + "/" + max;
		
		return ChatColor.GREEN + "" + actual + "/" + max;
	}

}
