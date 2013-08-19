package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_6_R2.NBTTagCompound;

import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainerBuilder {

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
		
		String ageConfigName = compound.getString("name");
		String agePrettyName = compound.getString("agePrettyName");
		double maxHealth = compound.getDouble("maxHP");
		double spawnHealth = compound.getDouble("spawnHP");
		List<DropContainer> drops = new LinkedList<DropContainer>(); //TODO add implementation
		int exp = compound.getInt("exp");
		double dmg = compound.getDouble("dmp");
		int rank = compound.getInt("rank");
		boolean isHostile = compound.getBoolean("isHostile");

		return new AgeContainer(ageConfigName, agePrettyName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
	}

	
	public static NBTTagCompound saveToNBTTagCompound(AgeContainer ageContainer) {
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setString("name", ageContainer.getAgeName());
		compound.setString("agePrettyName", ageContainer.getAgePrettyName());
		compound.setDouble("dmg", ageContainer.getDmg());
		compound.setInt("exp", ageContainer.getExp());
		compound.setDouble("maxHP", ageContainer.getMaxHealth());
		compound.setInt("rank", ageContainer.getRank());
		compound.setDouble("spawnHP", ageContainer.getSpawnHealth());
		compound.setBoolean("isHostile", ageContainer.isHostile());
		
		//TODO set item//compound("drops", ageContainer.getDrops());
		
		return compound;
	}
	
	
	
}
