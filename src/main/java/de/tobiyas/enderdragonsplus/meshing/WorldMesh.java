package de.tobiyas.enderdragonsplus.meshing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.World;

public class WorldMesh {

	/**
	 * The world this mesh belongs to.
	 */
	private final World world;
	
	/**
	 * The start point for X.
	 */
	private final int startX;
	
	/**
	 * The end Point for X.
	 */
	private final int endX;
	
	/**
	 * The start Point for Z.
	 */
	private final int startZ;
	
	/**
	 * the end point for Z.
	 */
	private final int endZ;
	
	
	/**
	 * the Mesh points to use.
	 */
	private final List<MeshPoint> points = new LinkedList<MeshPoint>();

	
	public WorldMesh(World world, int startX, int endX, int startZ, int endZ) {
		this.world = world;
		this.startX = startX;
		this.endX = endX;
		this.startZ = startZ;
		this.endZ = endZ;
	}
	

	public World getWorld() {
		return world;
	}

	public int getStartX() {
		return startX;
	}

	public int getEndX() {
		return endX;
	}

	public int getStartZ() {
		return startZ;
	}

	public int getEndZ() {
		return endZ;
	}
	
	
	public void setPoints(List<MeshPoint> points) {
		synchronized (points) {
			this.points.clear();
			this.points.addAll(points);
		}
	}
	
	
	public List<MeshPoint> getPoints() {
		synchronized (points) {
			return new ArrayList<MeshPoint>(points);
		}
	}
}
