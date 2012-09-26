package de.tobiyas.enderdragonsplus.entity.dragontemples;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import de.tobiyas.enderdragonsplus.util.Consts;

public class DragonTemple {
	
	private File scematicPath;

	public DragonTemple(File completeFile){
		scematicPath = completeFile;
		if(!scematicPath.exists())
			scematicPath = new File(Consts.STDSchematicPath);
	}
	
	public boolean buildAt(World world, int posX, int posY, int posZ){
		return buildAt(new Location(world, posX, posY, posZ));
	}
	
	public boolean buildAt(Location location){
		try {
			DragonTempleStore.pasteScematic(scematicPath, location);
		} catch (WorldEditNowFoundException exception) {
			return false;
		}
		
		return true;
	}
}
