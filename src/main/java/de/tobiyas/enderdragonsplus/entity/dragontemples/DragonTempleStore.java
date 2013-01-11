package de.tobiyas.enderdragonsplus.entity.dragontemples;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class DragonTempleStore {
	
	public static boolean pasteScematic(File file, Location location) throws WorldEditNotFoundException{
		if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit"))
			throw new WorldEditNotFoundException();
		
		SchematicFormat format = MCEditSchematicFormat.getFormat(file);
		Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		try {
			BukkitWorld world = new BukkitWorld(location.getWorld());
			EditSession session = new EditSession(world, Integer.MAX_VALUE);
			
			CuboidClipboard clipBoard = format.load(file);
			
			Vector newOffset = new Vector();
			newOffset = newOffset.setX(Math.floor(clipBoard.getWidth() / 2));
			newOffset = newOffset.setY(0);
			newOffset = newOffset.setZ(Math.floor(clipBoard.getLength() / 2));
			
			clipBoard.setOffset(newOffset);
			
			clipBoard.paste(session, vector, true);
		} catch (Exception exception) {
			return false;
		}

		return true;
	}
}
