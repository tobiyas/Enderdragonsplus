package de.tobiyas.enderdragonsplus.meshing;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;

public abstract class MeshFinderTask extends BukkitRunnable {

	
	private final Location start;
	private final Location destination;
	
	private final boolean complexMesh;
	
	
	
	/**
	 * Finds a Mesh.
	 * 
	 * @param start
	 * @param destination
	 */
	public MeshFinderTask(Location start, Location destination, boolean complexMesh) {
		this.start = start;
		this.destination = destination;
		this.complexMesh = complexMesh;
	}
	
	
	@Override
	public void run() {
		MeshPoint start = new MeshPoint(this.start);
		MeshPoint destination = new MeshPoint(this.destination);
		
		List<MeshPoint> allPoints = EnderdragonsPlus.getPlugin().getMeshManager().getMeshCopy(complexMesh);
		List<MeshPoint> way = MeshHelper.getPath(start, destination, allPoints, 50);
		
		new SyncCaller(this, way).runTask(EnderdragonsPlus.getPlugin());
	}
	

	/**
	 * This is called when the Way is found.
	 * <br>This is called on Main thread!
	 */
	public abstract void meshWayFound(List<MeshPoint> points);
	
	
	/**
	 * Starts the Task async!
	 */
	public void start(){
		this.runTaskAsynchronously(EnderdragonsPlus.getPlugin());
	}
	
	
	
	private static class SyncCaller extends  BukkitRunnable {
		
		private final MeshFinderTask task;
		private final List<MeshPoint> points;
		
		public SyncCaller(MeshFinderTask task, List<MeshPoint> points) {
			this.task = task;
			this.points = points;
		}
		
		@Override
		public void run() {
			task.meshWayFound(points);
		}
	}
	
}
