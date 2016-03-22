package de.tobiyas.enderdragonsplus.meshing;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

@SerializableAs("MeshPoint")
public class MeshPoint extends Vector implements ConfigurationSerializable  {
	
	
	public MeshPoint(Map<String,Object> serialized) {
		this.x = Double.parseDouble(serialized.get("x").toString());
		this.y = Double.parseDouble(serialized.get("y").toString());
		this.z = Double.parseDouble(serialized.get("z").toString());
	}
	

	public MeshPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MeshPoint(MeshPoint original) {
		this.x = original.x;
		this.y = original.y;
		this.z = original.z;
	}
	
	
	public MeshPoint(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}
	
	
	/**
	 * The Distance to the Point passed.
	 * 
	 * @param point to check
	 * @return the distance.
	 */
	public double distance(MeshPoint point){
		return Math.sqrt(distanceSquare(point));
	}
	

	/**
	 * The Distance to the Point passed in square.
	 * 
	 * @param point to check
	 * @return the distance.
	 */
	public double distanceSquare(MeshPoint point){
		double dist = 0;
		double tmp = Math.abs(point.x - x);
		dist += tmp*tmp;
		
		tmp = Math.abs(point.y - y);
		dist += tmp*tmp;
		
		tmp = Math.abs(point.z - z);
		dist += tmp*tmp;
		
		return dist;
	}

	@Override
	public String toString() {
		return "{x:"+x+" y:"+y+" z:"+z+"}";
	}


	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> serialized = new HashMap<String, Object>();
		serialized.put("x", x);
		serialized.put("y", y);
		serialized.put("z", z);
		
		return serialized;
	}
}
