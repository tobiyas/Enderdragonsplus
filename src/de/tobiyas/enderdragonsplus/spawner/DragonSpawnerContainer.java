package de.tobiyas.enderdragonsplus.spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet130UpdateSign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DragonSpawnerContainer {

	private HashSet<DragonSpawnPlace> spawnPlaces;
	
	private ArrayList<Location> locations;
	private int respawnTime;
	private int maxDragons;
	
	private HashMap<String, Boolean> hidingBlock;
	private int tickRefresher;
	
	private String spawnerID;
	private EnderdragonsPlus plugin;
	
	
	private DragonSpawnerContainer(int respawnTime, int maxDragons, Location loc, String spawnerID){
		this.plugin = EnderdragonsPlus.getPlugin();
		this.spawnerID = spawnerID;
		this.locations = new ArrayList<Location>();
		
		this.respawnTime = respawnTime;
		this.maxDragons = maxDragons;
		this.tickRefresher = 3;
		
		hidingBlock = new HashMap<String, Boolean>();
		spawnPlaces = new HashSet<DragonSpawnPlace>();
		
		for(int i = 0; i < maxDragons; i++){
			spawnPlaces.add(new DragonSpawnPlace(this, respawnTime));
		}
		linkPosition(loc);
	}
	
	public boolean linkPosition(Location loc){
		if(locations.contains(loc)) return false;
		locations.add(loc);
		createSigns();
		return true;
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
		config.load();
		config.createSection("spawners." + spawnerID);
		config.set("spawners." + spawnerID + ".respawnTime", respawnTime);
		config.set("spawners." + spawnerID + ".maxDragons", maxDragons);
		
		int i = 1;
		for(Location loc : locations)
			config.setLocation("spawners." + spawnerID + ".locs.loc" + i++, loc);

		config.save();
		
		i = 0;
		for(DragonSpawnPlace place : spawnPlaces){
			place.saveSelf(config, spawnerID, i);			
			i++;
		}
		
		config.save();
	}
	
	public static DragonSpawnerContainer createContainer(YAMLConfigExtended config, String id){
		config.load();
		int respawnTime = config.getInt("spawners." + id + ".respawnTime");
		int maxDragons = config.getInt("spawners." + id + ".maxDragons");
		
		ArrayList<Location> list = new ArrayList<Location>();
		for(String posLoc : config.getYAMLChildren("spawners." + id + ".locs")){
			Location tempLoc = config.getLocation("spawners." + id + ".locs." + posLoc);
			if(tempLoc != null)
				list.add(tempLoc);
		}
		
		if(list.size() == 0){
			EnderdragonsPlus.getPlugin().log("Could not load SpawnContainer with ID: " + id);
			return null;
		}
		
		Location initLoc = list.get(0);
		DragonSpawnerContainer dragonSpawnContainer = new DragonSpawnerContainer(respawnTime, maxDragons, initLoc, id);
		
		for(int i = 1; i < list.size(); i++)
			dragonSpawnContainer.linkPosition(list.get(i));
			
		
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
		
		dragonSpawnContainer.createSigns();
		
		return dragonSpawnContainer;
	}
	
	public static DragonSpawnerContainer createContainer(Location loc, int maxDragons, int respawnTime, String id){
		DragonSpawnerContainer container = new DragonSpawnerContainer(respawnTime, maxDragons, loc, id);
		if(container != null)
			container.createSigns();
		
		return container;
	}
	
	public int createSigns(){
		int i = 0;
		for(Location loc : locations){
			if(loc.getBlock().getType() == Material.SIGN_POST)
				continue;
			
			loc.getBlock().setType(Material.SIGN_POST);
			Sign sign = (Sign) loc.getBlock().getState();
			
			sign.setLine(0, ChatColor.RED + "[Respawner]");
			sign.setLine(1, ChatColor.WHITE + "time: " + respawnTime);
			sign.setLine(2, ChatColor.WHITE + "Dragons: " + maxDragons);
			sign.setLine(3, ChatColor.YELLOW + "ID: " + spawnerID);
			
			sign.update();
			i++;
		}
		return i;
	}
	
	public void tick(){
		for(DragonSpawnPlace place : spawnPlaces)
			place.tick();
		checkPlayersInvis();
	}
	
	private void checkPlayersInvis(){
		tickRefresher --;
		if(tickRefresher < 0){
			hidingBlock.clear();
			tickRefresher = 3;
		}
		for(Location loc : locations){
			List<Player> players = loc.getWorld().getPlayers();
			for(Player player : players){
				if(player.getWorld() == loc.getWorld() && player.getLocation().distance(loc) < 100){
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
	}
	
	private void hideSign(Player player){
		for(Location loc : locations)
			player.sendBlockChange(loc, Material.AIR, new Byte("0"));
	}
	
	private void unhideSign(Player player){
		for(Location loc : locations){
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
	}

	public boolean remove(YAMLConfigExtended config, Location loc, boolean removeAll) {
		if(removeAll){
			for(Location tempLoc : locations)
				tempLoc.getBlock().setType(Material.AIR);
			
			locations.clear();
		}
			
		for(Location spawnLoc : locations){
			if(loc.getWorld() == spawnLoc.getWorld() && loc.distance(spawnLoc) < 2){
				locations.remove(spawnLoc);
				spawnLoc.getBlock().setType(Material.AIR);
				break;
			}
				
		}
		
		if(locations.size() > 0) return false;
		
		for(DragonSpawnPlace space : spawnPlaces)
			space.remove();
		
		config.load();
		config.set("spawners." + spawnerID, "1");
		config.set("spawners." + spawnerID, null);
		config.save();
		
		return true;
	}

	public boolean isNear(Location otherLoc) {
		for(Location loc : locations)
			if(loc.getWorld() == otherLoc.getWorld() && loc.distance(otherLoc) <= 1) 
				return true;
		
		return false;
	}

	public void sendInfo(Player player) {
		int i = 1;
		for(Location loc : locations){
			int locX = loc.getBlockX();
			int locY = loc.getBlockY();
			int locZ = loc.getBlockZ();
			String world = loc.getWorld().getName();
			
			player.sendMessage(ChatColor.WHITE + spawnerID + ChatColor.YELLOW + "[" + ChatColor.WHITE + i + ChatColor.YELLOW + "]  X:" + 
								ChatColor.LIGHT_PURPLE + locX + 
								ChatColor.YELLOW + " Y:" + ChatColor.LIGHT_PURPLE + locY + ChatColor.YELLOW + " Z:" + 
								ChatColor.LIGHT_PURPLE + locZ + ChatColor.YELLOW + " world:" + ChatColor.LIGHT_PURPLE + world);
			i++;
		}
	}

	public ArrayList<Location> getLocation() {
		return locations;
	}
	
	public String getSpawnerID(){
		return spawnerID;
	}
}
