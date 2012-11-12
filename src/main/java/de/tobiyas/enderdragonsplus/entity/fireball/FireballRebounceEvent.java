package de.tobiyas.enderdragonsplus.entity.fireball;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class FireballRebounceEvent extends EntityEvent implements Cancellable{

	private boolean isCancelled;
	private RebounceReason reason;
	private Entity rebouncer;
	
	private static final HandlerList handlers = new HandlerList();
	
	public FireballRebounceEvent(Fireball fireball, RebounceReason reason, Entity rebouncer) {
		super(fireball);
		this.reason = reason;
		this.rebouncer = rebouncer;
		isCancelled = false;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}
	
	public RebounceReason getReason(){
		return reason;
	}
	
	public Entity getRebouncer(){
		return rebouncer;
	}
	
	
	//ENUM
	public enum RebounceReason {
		MELEE_ATTACK,
		ARROW;
	}

}
