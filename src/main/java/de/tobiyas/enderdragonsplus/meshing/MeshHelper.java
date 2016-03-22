package de.tobiyas.enderdragonsplus.meshing;

import java.util.LinkedList;
import java.util.List;

public class MeshHelper {

	
	/**
	 * Gets the nearest Points to the point passed.
	 * 
	 * @param root to check from
	 * @param maxDist the max-dist to check
	 * @param allPoints to check.
	 * 
	 * @return a list of all points near.
	 */
	public static List<MeshPoint> getPointsNear(MeshPoint root, double maxDist, List<MeshPoint> allPoints){
		maxDist *= maxDist;
		
		List<MeshPoint> result = new LinkedList<MeshPoint>();
		for(MeshPoint point : allPoints){
			//Skip own node, if there.
			if(point == root) continue;
			
			double distSquare = point.distanceSquare(root);
			if(distSquare > 0 && distSquare < maxDist){
				result.add(point);
			}
		}
		
		return result;
	}
	
	
	/**
	 * Gets the Next point to the destination.
	 * 
	 * @param start to use.
	 * @param destination to use.
	 * @param points to use.
	 * @return the nearest point.
	 */
	public static MeshPoint getNextPointTo(MeshPoint start, MeshPoint destination, List<MeshPoint> points){
		if(points.isEmpty()) return null;
		
		MeshPoint nearest = points.get(0);
		double minDist = start.distanceSquare(nearest) + nearest.distanceSquare(destination);
		
		for(int i = 1; i < points.size(); i++){
			MeshPoint other = points.get(i);
			
			double newMin = start.distanceSquare(other) + other.distanceSquare(destination);
			if(newMin < minDist) {
				minDist = newMin;
				nearest = other;
			}
		}
		
		return nearest;
	}
	
	
	/**
	 * Returns a Path from start -> Destination.
	 * 
	 * @param start to use
	 * @param destination to use.
	 * @param allPoints to use.
	 * @return a path from source to dest.
	 */
	public static List<MeshPoint> getPath(MeshPoint start, MeshPoint destination, List<MeshPoint> allPoints, double maxDistBetween){
		double currentSearchDist = maxDistBetween;
		
		List<MeshPoint> path = new LinkedList<MeshPoint>();
		path.add(start);
		
		//Calculate the current dist to target.
		double maxDistSquare = start.distanceSquare(destination);
		
		List<MeshPoint> nearestPoints = getPointsNear(start, maxDistBetween, allPoints);
		while(maxDistSquare > currentSearchDist*currentSearchDist){
			MeshPoint nearestPoint = getNextPointTo(path.get(path.size()-1), destination, nearestPoints);
			if(nearestPoint != null && !path.contains(nearestPoint)) {
				maxDistSquare = nearestPoint.distanceSquare(destination);
				currentSearchDist = maxDistBetween;
				nearestPoints = getPointsNear(nearestPoint, currentSearchDist, allPoints);
				path.add(nearestPoint);
			}else{
				currentSearchDist *= 2;
				nearestPoints = getPointsNear(path.get(path.size()-1), currentSearchDist, allPoints);
			}
			
		}
		
		
		path.add(destination);
		return path;
	}
	
}
