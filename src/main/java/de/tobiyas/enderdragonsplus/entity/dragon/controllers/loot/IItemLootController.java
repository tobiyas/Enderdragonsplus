package de.tobiyas.enderdragonsplus.entity.dragon.controllers.loot;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.util.config.returncontainer.DropContainer;

public interface IItemLootController {

	/**
	 * Ticks the Death of the dragon 1 tick further.
	 * This means giving exp and loot to the world.
	 */
	public abstract void deathTick();

	public abstract List<ItemStack> getItemDrops(List<DropContainer> list);

}