package de.tobiyas.enderdragonsplus.meshing;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class MeshGeneratorTask extends BukkitRunnable {

	private static final int yOffset = 20;
	
	
	private final long startTime = System.currentTimeMillis();
	
	private final World world;
	
	private final int startX;
	
	
	private int chunkX,chunkZ;
	private final int maxChunkX, maxChunkZ;
	
	private final int totalChunks;

	
	private List<MeshPoint> points = new LinkedList<MeshPoint>();
	
	private final MeshGenerationDoneCallback callback;
	
	
	public MeshGeneratorTask(final World world,
			final int startX, final int startZ,
			final int maxX, final int maxZ,
			MeshGenerationDoneCallback callback){
		
		this.world = world;
		this.callback = callback;
		this.startX = startX;
		
		this.chunkX = startX >> 4;
		this.chunkZ = startZ >> 4;
		
		this.maxChunkX = maxX >> 4;
		this.maxChunkZ = maxZ >> 4;
		
		this.totalChunks = Math.abs(maxChunkX - chunkX) * Math.abs(maxChunkZ - chunkZ);
	}
	
	public MeshGeneratorTask(WorldMesh mesh, MeshGenerationDoneCallback callback){
		this(mesh.getWorld(), 
				mesh.getStartX(), mesh.getStartZ(),
				mesh.getEndX(), mesh.getEndZ(),
				new WorldMeshProxy(mesh, callback));
	}
	
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < 200){
			MeshPoint point = generatePoint(world, chunkX, chunkZ);
			points.add(point);
			
			chunkX ++;
			if(chunkX > maxChunkX){
				chunkZ++;
				chunkX = startX >> 4;
			}
			
			if(chunkZ > maxChunkZ) {
				callback.meshGenerationDone(points);
				this.cancel();
				break;
			}
			
			if(points.size() % 20000 == 0) outPercent();
		}
	}
	
	private void outPercent(){
		double done = points.size();
		double total = totalChunks;
		
		double percent = done / total;
		long taken = System.currentTimeMillis() - startTime;
		double eta = (double) (taken * (1d / percent)) - taken;
		long etaLong = (long)(eta / 1000d);
		
		System.out.println("Mesh Info: " + points.size() + "/" + totalChunks 
				+ " Percent: " + (100d * percent) + " eta: " + etaLong + " seconds"
				+ " for World: " + world.getName());
	}
	

	protected MeshPoint generatePoint(World world, int chunkX, int chunkZ) {
		boolean chunkLoaded = world.isChunkLoaded(chunkX, chunkZ);
		Chunk chunk = world.getChunkAt(chunkX, chunkZ);
		ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, false, false);
		
		int maxY = 0;
		for(int x = 0; x < 16; x++){
			for(int z = 0; z<16; z++){
				maxY = Math.max(snapshot.getHighestBlockYAt(x, z), maxY);
			}
		}
		
		if(!chunkLoaded) chunk.unload(false, false);
		return new MeshPoint(new Location(world, chunkX * 16 + 8, maxY + yOffset, chunkZ*16 + 8));
	}

	
	
	private static class WorldMeshProxy implements MeshGenerationDoneCallback{
		private final WorldMesh mesh;
		private final MeshGenerationDoneCallback proxy;
		
		public WorldMeshProxy(WorldMesh mesh, MeshGenerationDoneCallback proxy) {
			this.mesh = mesh;
			this.proxy = proxy;
		}

		public void meshGenerationDone(List<MeshPoint> points){ 
			if(mesh != null) mesh.setPoints(points); 
			if(proxy != null) proxy.meshGenerationDone(points); 
		}
	}
	
	
	public interface MeshGenerationDoneCallback{
		
		/**
		 * The Mesh generation is done!
		 * @param points to use.
		 */
		public void meshGenerationDone(List<MeshPoint> points);
		
	}
	
}
