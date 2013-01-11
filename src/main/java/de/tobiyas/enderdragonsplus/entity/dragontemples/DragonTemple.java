package de.tobiyas.enderdragonsplus.entity.dragontemples;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.util.Consts;

public class DragonTemple {
	
	private File scematicPath;

	public DragonTemple(File completeFile){
		scematicPath = completeFile;
		if(!scematicPath.exists()){
			if(scematicPath.equals(new File(Consts.STDSchematicPath)))
				return;
				
			EnderdragonsPlus.getPlugin().log("Tried to load invalid Dragon Temple File! File does not exist!" + 
			scematicPath.getPath());
		}
	}
	
	public boolean buildAt(World world, int posX, int posY, int posZ){
		return buildAt(new Location(world, posX, posY, posZ));
	}
	
	public boolean buildAt(Location location){
		try {
			return DragonTempleStore.pasteScematic(scematicPath, location);
		} catch (WorldEditNotFoundException exception) {
			return false;
		} catch (NoClassDefFoundError exp){
			return false;
		}
	}
}
