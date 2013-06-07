package de.tobiyas.enderdragonsplus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public class CommandError implements CommandExecutor{

	private EnderdragonsPlus plugin;
	
	public CommandError() {
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edperror").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /edperror");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		
		EnderdragonsPlus.getPlugin().getDebugLogger().logStackTrace(new Exception("TEST"));
		arg0.sendMessage("DONE");
		return true;
	}

}
