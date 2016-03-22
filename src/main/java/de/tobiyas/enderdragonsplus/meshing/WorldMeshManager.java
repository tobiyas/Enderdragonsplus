package de.tobiyas.enderdragonsplus.meshing;

import java.util.List;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.meshing.MeshGeneratorTask.MeshGenerationDoneCallback;

public class WorldMeshManager implements MeshGenerationDoneCallback {

	/**
	 * The data for the world-mesh
	 */
	private final WorldMesh worldMesh;
	
	/**
	 * If the Generation is currently Running.
	 */
	private boolean isRunning = false;
	
	
	public WorldMeshManager(WorldMesh worldMesh) {
		this.worldMesh = worldMesh;
	}
	
	
	
	/**
	 * Start a Generation.
	 */
	public void startGeneration(){
		if(isRunning) return;
		new MeshGeneratorTask(worldMesh, this).runTaskTimer(EnderdragonsPlus.getPlugin(), 3, 3);
		isRunning = true;
	}

	
	@Override
	public void meshGenerationDone(List<MeshPoint> points) {
		isRunning = false;
	}
	
}
