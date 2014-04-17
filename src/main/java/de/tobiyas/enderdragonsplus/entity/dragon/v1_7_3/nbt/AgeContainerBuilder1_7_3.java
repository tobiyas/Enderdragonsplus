package de.tobiyas.enderdragonsplus.entity.dragon.v1_7_3.nbt;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.AgeContainerBuilder;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainerBuilder1_7_3 extends AgeContainerBuilder {

	
	public static AgeContainer buildFromNBTTag(NBTTagCompound compound) {
		String ageConfigName = compound.getString(NBT_TAG__NAME);
		String agePrettyName = compound.getString(NBT_TAG__PRETTY_AGE_NAME);
		double maxHealth = compound.getDouble(NBT_TAG__MAX_HEALTH);
		double spawnHealth = compound.getDouble(NBT_TAG__SPAWN_HEALTH);
		int exp = compound.getInt(NBT_TAG__EXP);
		double dmg = compound.getDouble(NBT_TAG__DMG);
		int rank = compound.getInt(NBT_TAG__RANK);
		boolean isHostile = compound.getBoolean(NBT_TAG__IS_HOSTILE);

		List<DropContainer> drops = new LinkedList<DropContainer>(); //TODO add implementation

		return new AgeContainer(ageConfigName, agePrettyName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
	}

	
	public static NBTTagCompound saveToNBTTagCompound1_7_3(AgeContainer ageContainer) {
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setString(NBT_TAG__NAME, ageContainer.getAgeName());
		compound.setString(NBT_TAG__PRETTY_AGE_NAME, ageContainer.getAgePrettyName());
		compound.setDouble(NBT_TAG__MAX_HEALTH, ageContainer.getMaxHealth());
		compound.setDouble(NBT_TAG__SPAWN_HEALTH, ageContainer.getSpawnHealth());
		compound.setInt(NBT_TAG__EXP, ageContainer.getExp());
		compound.setDouble(NBT_TAG__DMG, ageContainer.getDmg());
		compound.setInt(NBT_TAG__RANK, ageContainer.getRank());
		compound.setBoolean(NBT_TAG__IS_HOSTILE, ageContainer.isHostile());
		
		//TODO set item//compound("drops", ageContainer.getDrops());
		
		return compound;
	}
}
