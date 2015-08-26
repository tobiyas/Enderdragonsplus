package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainerBuilder {

	private static final String NBT_TAG__NAME = "name";
	private static final String NBT_TAG__PRETTY_AGE_NAME = "agePrettyName";
	private static final String NBT_TAG__MAX_HEALTH = "maxHP";
	private static final String NBT_TAG__SPAWN_HEALTH = "spawnHP";
	
	//private static final String NBT_TAG__DROPS= "drops"; //TODO add drops
	private static final String NBT_TAG__EXP = "exp";

	private static final String NBT_TAG__DMG = "dmg";
	private static final String NBT_TAG__IS_HOSTILE = "isHostile";
	private static final String NBT_TAG__RANK = "rank";
	
	
	private String ageConfigName;
	private String prettyAgeName;
	
	private double maxHealth;
	private double spawnHealth;
	private List<DropContainer> drops;
	private int exp;
	private double dmg;
	private boolean isHostile;
	
	private int rank;
	
	public AgeContainerBuilder(){
	}


	public AgeContainerBuilder setprettyAgeName(String agePrettyName) {
		this.prettyAgeName = agePrettyName;
		return this;
	}

	public AgeContainerBuilder setAgeName(String ageName) {
		this.ageConfigName = ageName;
		return this;
	}

	public AgeContainerBuilder setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public AgeContainerBuilder setSpawnHealth(double spawnHealth) {
		this.spawnHealth = spawnHealth;
		return this;
	}

	public AgeContainerBuilder setDrops(List<DropContainer> drops) {
		this.drops = drops;
		return this;
	}

	public AgeContainerBuilder setExp(int exp) {
		this.exp = exp;
		return this;
	}

	public AgeContainerBuilder setDmg(double dmg) {
		this.dmg = dmg;
		return this;
	}

	public AgeContainerBuilder setHostile(boolean isHostile) {
		this.isHostile = isHostile;
		return this;
	}

	public AgeContainerBuilder setRank(int rank) {
		this.rank = rank;
		return this;
	}
	
	public AgeContainer build(){
		return new AgeContainer(ageConfigName, prettyAgeName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
	}

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

	
	public static NBTTagCompound saveToNBTTagCompound(AgeContainer ageContainer) {
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
