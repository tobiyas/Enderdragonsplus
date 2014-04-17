package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon;

public class CommandLag implements CommandExecutor{

	private final long startDate = System.currentTimeMillis();
	
	private EnderdragonsPlus plugin;
	
	public CommandLag() {
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edplag").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edplag");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		long pluginRunning = System.currentTimeMillis() - startDate;
		
		long totalTime = LimitedEnderDragon.timeTaken;
		long totalCalls = LimitedEnderDragon.totalLogicCalls;
		
		double timePerCall = (double)totalTime / (double)totalCalls;
		
		double percentTimeTotal = (double)totalTime / (double)pluginRunning;
		percentTimeTotal *= 100;
		
		sender.sendMessage(ChatColor.GREEN + "Total calls: " + totalCalls + " Total Tick time: " + totalTime 
				+ " TimePerTick: " + timePerCall + " TotalPercentage of CPU: " + percentTimeTotal);
		return true;
	}

}
