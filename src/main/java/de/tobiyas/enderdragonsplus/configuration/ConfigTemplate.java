package de.tobiyas.enderdragonsplus.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.util.Consts;

public class ConfigTemplate {
	
	private EnderdragonsPlus plugin;
	private File configFile;
	
	public ConfigTemplate(){
		plugin = EnderdragonsPlus.getPlugin();
		
		File pluginFolder = new File(plugin.getDataFolder().toString());
		if(!pluginFolder.exists())
			pluginFolder.mkdirs();
		
		plugin = EnderdragonsPlus.getPlugin();
		configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
	}
	
	public boolean isOldConfigVersion(){
		if(!configFile.exists())
			return true;
		
		boolean isOldVersion = true;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			
			while(true){
				String currentLine = reader.readLine();
				if(currentLine == null)
					break;

				if(currentLine.contains("#TemplateVersion ")){
					currentLine = currentLine.replace("#TemplateVersion ", "");
					
					if(currentLine.equalsIgnoreCase(Consts.ConfigVersion))
						isOldVersion = false;
					break;
				}
			}
			
			reader.close();
		}catch(Exception e){
			plugin.log("Could not get Version");
		}
		
		return isOldVersion;
	}
	
	private String modifyLine(String line){
		if(line.length() == 0) 
			return line;
		
		if(line.contains("#")) 
			return line;
		
		if(!line.contains(":"))
			return line;
		
		String[] nodes = line.split(":");		
		String node = nodes[0];
		Object obj = plugin.getConfig().get(node);
		if(obj == null)
			return "#" + line;
		
		return node + ": " + obj.toString();
	}
	
	public void writeTemplate(){
		
		if(configFile.exists())
			configFile.delete();
		
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			plugin.log("Could not create new Config File.");
			e.printStackTrace();
		}
		
		String content = ConfigText.getConfig();
		String[] lines = content.split("\n");
		
		content = "";
		for(String line : lines){
			content += modifyLine(line) + System.getProperty("line.separator");
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
			out.write(content);
			out.close();
		} catch (IOException e) {
			plugin.log("Error on replacing the Config File");
			e.printStackTrace();
		}
	}
}
