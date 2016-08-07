package de.tobiyas.enderdragonsplus.meshing;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.move.DragonMoveController;



public class ToTargetController extends DragonMoveController {
	
	
	/**
	 * The Way to use.
	 */
	private List<MeshPoint> way;
	
	/**
	 * The Current Target.
	 */
	private MeshPoint currentTarget = null;
	
	/**
	 * The current index.
	 */
	private int index = 0;
	
	/**
	 * The Player to use.
	 */
	private final Player player;
	
	/**
	 * The will be true when the travel is FINALLY done!
	 */
	private boolean done = false;
	
	/**
	 * adding left - right movement.
	 */
	private boolean left = false;
	
	
	public ToTargetController(Player player, LimitedED dragon, List<MeshPoint> way) {
		super(dragon);
		
		this.player = player;
		this.way = way;
		
		
		//calculate Speed + Time.
		double distance = getDistanceFromWay(way);
		double speed = 19.5;
		
		int time = (int) (distance / speed);
		player.sendMessage(ChatColor.GREEN + "Die benötigte Zeit ist etwa " + time + " Sekunden (" + ((int) distance) + " m Distanz)");
	}
	
	
	/**
	 * Gets the Distance of a Way.
	 * @param way to get the Distance for.
	 * @return the distance for the Way.
	 */
	private int getDistanceFromWay(List<MeshPoint> way){
		int dist = 0;
		MeshPoint last = way.get(0);
		for(int i = 1; i < way.size(); i++){
			dist += last.distance(way.get(i));
			last = way.get(i);
		}
		
		return dist;
	}
	
	
	@Override
	public void moveDragon() {
		//remove dragon when no passenger.
		if(dragon.getPassenger() == null){
			if(done) return;
			
			if(!player.isValid() || !player.isOnline()){
				dragon.getBukkitEntity().remove();
				return;
			}
			
			//YOU SHALL NOT DISMOUNT!
			dragon.setPassenger(player);
		}
		
		if(currentTarget == null){
			currentTarget = way.get(index);
			index++;
		}
		
		if(dragon.getLocation().toVector().distanceSquared(currentTarget) < 4){
			if(index >= way.size()) {
				//first remove the passenger from to the destination.
				Entity passenger = dragon.getPassenger();
				
				//Next remove dragon!
				dragon.remove();
				if(passenger != null){
					//teleport to the destination.
					passenger.teleport(
							new Location(
									passenger.getWorld(), 
									currentTarget.getX(), 
									currentTarget.getY(), 
									currentTarget.getZ()
							)
					);
				}
			}
			
			currentTarget = null;
			return;
		}
		
		Vector vec = currentTarget.clone().subtract(dragon.getLocation().toVector().clone()).normalize();
		dragon.setLastYaw(dragon.getYaw() + (left ? 0.05f : -0.05f));
		left = !left;
		
		dragon.setYaw(calcYawFromVec(vec));
		
		//MOVE BITCH! GET OUT OF THE WAY.
		dragon.setMotion(vec);
		this.moveToDragonMotion();
	}
	
}
