package de.tobiyas.enderdragonsplus.entity.dragon.controllers.fireball;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface IFireballController {

	public abstract void forceSpitFireball();

	public abstract void checkSpitFireBall();

	public abstract void fireFireball(LivingEntity target);

	public abstract void fireFireballToDirection(Location direction);

	public abstract void fireFireballOnLocation(Location location);

}