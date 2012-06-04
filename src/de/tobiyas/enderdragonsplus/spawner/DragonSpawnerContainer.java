package de.tobiyas.enderdragonsplus.spawner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet130UpdateSign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnerContainer {

	private HashSet<DragonSpawnPlace> spawnPlaces;
	
	private Location loc;
	private int respawnTime;
	private int maxDragons;
	
	private HashMap<String, Boolean> hidingBlock;
	
	private String spawnerID;
	private EnderdragonsPlus plugin;
	
	
	private DragonSpawnerContainer(int respawnTime, int maxDragons, Location loc, String spawnerID){
		this.plugin = EnderdragonsPlus.getPlugin();
		this.spawnerID = spawnerID;
		this.loc = loc;
		this.respawnTime = respawnTime;
		this.maxDragons = maxDragons;
		
		hidingBlock = new HashMap<String, Boolean>();
		spawnPlaces = new HashSet<DragonSpawnPlace>();
		
		for(int i = 0; i < maxDragons; i++){
			spawnPlaces.add(new DragonSpawnPlace(loc, respawnTime));
		}
	}
	
	public boolean linkDragon(UUID dragonID){
		boolean isDragon = plugin.getContainer().containsID(dragonID);
		if(!isDragon)
			return false;
		for(DragonSpawnPlace place : spawnPlaces){
			if(place.isFree()){
				place.linkDragon(dragonID);
				return true;
			}
		}
		return false;
	}
	
	public boolean setEmptyContainerTo(int timeLeft){
		for(DragonSpawnPlace place : spawnPlaces){
			if(place.isFree()){
				place.setRemainingRespawntime(timeLeft);
				return true;
			}
		}
		
		return false;
	}
	
	public void saveContainer(YAMLConfigExtended config){
		config.createSection("spawners." + spawnerID);
		config.set("spawners." + spawnerID + ".respawnTime", respawnTime);
		config.set("spawners." + spawnerID + ".maxDragons", maxDragons);
		config.setLocation("spawners." + spawnerID + ".loc", loc);
		int i = 0;
		for(DragonSpawnPlace place : spawnPlaces){
			place.saveSelf(config, spawnerID, i);			
			i++;
		}
	}
	
	public static DragonSpawnerContainer createContainer(YAMLConfigExtended config, String id){
		int respawnTime = config.getInt("spawners." + id + ".respawnTime");
		int maxDragons = config.getInt("spawners." + id + ".maxDragons");
		Location loc = config.getLocation("spawners." + id + ".loc");
		if(loc == null){
			EnderdragonsPlus.getPlugin().log("Could not load SpawnContainer with ID: " + id);
			return null;
		}
		
		DragonSpawnerContainer dragonSpawnContainer = new DragonSpawnerContainer(respawnTime, maxDragons, loc, id);
		
		for(int i = 0; i < maxDragons; i++){
			String dragonIDString = config.getString("spawners." + id + ".places." + i + ".id");
			if(dragonIDString != null){
				UUID dragonID = UUID.fromString(dragonIDString);
				dragonSpawnContainer.linkDragon(dragonID);
				continue;
			}
			
			int respawnLeft = config.getInt("spawners." + id + ".places." + i + ".respawnIn");
			dragonSpawnContainer.setEmptyContainerTo(respawnLeft);
		}
		createSign(loc, maxDragons, respawnTime, id);
		return dragonSpawnContainer;
	}
	
	public static DragonSpawnerContainer createContainer(Location loc, int maxDragons, int respawnTime, String id){
		DragonSpawnerContainer container = new DragonSpawnerContainer(respawnTime, maxDragons, loc, id);
		if(container != null)
			createSign(loc, maxDragons, respawnTime, id);
		
		return container;
	}
	
	private static void createSign(Location loc, int maxDragons, int respawnTime, String id){
		loc.getBlock().setType(Material.SIGN_POST);
		Sign sign = (Sign) loc.getBlock().getState();
		
		sign.setLine(0, ChatColor.RED + "[Respawner]");
		sign.setLine(1, ChatColor.WHITE + "time: " + respawnTime);
		sign.setLine(2, ChatColor.WHITE + "Dragons: " + maxDragons);
		sign.setLine(3, ChatColor.YELLOW + "ID: " + id);
		
		sign.update();
	}
	
	public void tick(){
		for(DragonSpawnPlace place : spawnPlaces)
			place.tick();
		checkPlayersInvis();
	}
	
	private void checkPlayersInvis(){
		List<Player> players = loc.getWorld().getPlayers();
		for(Player player : players){
			if(player.getLocation().distance(loc) < 100){
				boolean hasPermission = plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.seeRespawners);
				boolean isInList = hidingBlock.containsKey(player.getName());
				if(!hasPermission && !isInList){
					hidingBlock.put(player.getName(), true);
					hideSign(player);
				}
				
				if(hasPermission && isInList){
					hidingBlock.remove(player.getName());
					unhideSign(player);
				}
			}
		}
	}
	
	private void hideSign(Player player){
		player.sendBlockChange(loc, Material.AIR, new Byte("0"));
	}
	
	private void unhideSign(Player player){
		if(loc.getBlock().getType() != Material.SIGN_POST) return;
		
		Sign sign = (Sign) loc.getBlock().getState();
		for(int i = 0; i < 4; i++){
			String line = sign.getLine(i);
			if(line.length() > 15)
				sign.setLine(i, line.substring(0, 15));
				
		}
		String[] lines = sign.getLines();
		
		player.sendBlockChange(loc, Material.SIGN_POST, new Byte("0"));
		EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
		craftPlayer.netServerHandler.sendPacket(new Packet130UpdateSign(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), lines));
	}

	public void remove(YAMLConfigExtended config) {
		for(DragonSpawnPlace space : spawnPlaces)
			space.remove();
		config.set("spawners." + spawnerID, "1");
		config.set("spawners." + spawnerID, null);
	}

	public boolean isNear(Location otherLoc) {
		return loc.distance(otherLoc) <= 1;
	}

	public boolean resetSign() {
		Block block = loc.getBlock();
		if(block.getType() != Material.SIGN_POST){
			createSign(loc, maxDragons, respawnTime, spawnerID);
			return true;
		}
		return false;
	}

	public void sendInfo(Player player) {
		int locX = loc.getBlockX();
		int locY = loc.getBlockY();
		int locZ = loc.getBlockZ();
		String world = loc.getWorld().getName();
		
		player.sendMessage(ChatColor.WHITE + spawnerID + ChatColor.YELLOW + "  X:" + ChatColor.LIGHT_PURPLE + locX + 
							ChatColor.YELLOW + " Y:" + ChatColor.LIGHT_PURPLE + locY + ChatColor.YELLOW + " Z:" + 
							ChatColor.LIGHT_PURPLE + locZ + ChatColor.YELLOW + " world:" + ChatColor.LIGHT_PURPLE + world);		
	}

	public Location getLocation() {
		return loc;
	}
}
