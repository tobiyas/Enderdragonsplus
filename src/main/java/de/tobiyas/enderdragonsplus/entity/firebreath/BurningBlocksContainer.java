package de.tobiyas.enderdragonsplus.entity.firebreath;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;

public class BurningBlocksContainer{

	private List<BurningBlock> burningBlocks;
	
	private boolean allTicksDone = false;
	
	private int burningLength = 20; //TODO
	private int spreadRange = 5;
	
	
	public BurningBlocksContainer() {
		burningBlocks = new LinkedList<BurningBlock>();
	}
	
	//spreads the fire one block in all directions
	public void tick(){
		boolean somethingTicked = false;
		
		if(spreadRange >= 0){
			somethingTicked = true;
			spreadRange--;
						
		}
		
		for(BurningBlock block : burningBlocks){
			if(!block.isDone()){
				block.tick();
			}
		}
		
		if(!somethingTicked){
			allTicksDone = true;
		}
	}
	
	public boolean containsLocation(Location location){
		for(BurningBlock block : burningBlocks){
			if(block.getLocation().equals(location)){
				return true;				
			}
		}
		
		return false;
	}

	
	public boolean areAllTicksDone() {
		return allTicksDone;
	}
	
	public void addBlock(Location location){
		burningBlocks.add(new BurningBlock(location, burningLength));
	}
}
