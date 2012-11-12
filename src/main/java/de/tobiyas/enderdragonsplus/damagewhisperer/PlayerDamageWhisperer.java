package de.tobiyas.enderdragonsplus.damagewhisperer;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class PlayerDamageWhisperer implements Runnable{
	
	private static LinkedList<String> damageList = new LinkedList<String>();
	
	private EnderdragonsPlus plugin;
	private int tickerTime = 5;
	
	private String playerName;
	private int health;

	public PlayerDamageWhisperer(Player player) {
		if(damageList.contains(player.getName()))
			return;
		
		plugin = EnderdragonsPlus.getPlugin();
		
		playerName = player.getName();
		health = player.getHealth();
		
		damageList.add(player.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, tickerTime);
	}

	@Override
	public void run() {
		damageList.remove(playerName);
		Player player = Bukkit.getPlayer(playerName);
		if(player == null)
			return;
		
		int newHealth = player.getHealth();
		if(newHealth < health)
			broadcastDamage(player, health - newHealth);
	}
	
	private void broadcastDamage(Player player, int damage){
		player.sendMessage(ChatColor.YELLOW + "The Dragon has done " + ChatColor.LIGHT_PURPLE + damage + ChatColor.YELLOW + " damage to you.");
	}

}
