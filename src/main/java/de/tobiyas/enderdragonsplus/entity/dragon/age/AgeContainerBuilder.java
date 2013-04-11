package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_5_R2.NBTTagCompound;

import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainerBuilder {

	private String ageName;
	private int maxHealth;
	private int spawnHealth;
	private List<DropContainer> drops;
	private int exp;
	private int dmg;
	private boolean isHostile;
	
	private int rank;
	
	public AgeContainerBuilder(){
	}

	public AgeContainerBuilder setAgeName(String ageName) {
		this.ageName = ageName;
		return this;
	}

	public AgeContainerBuilder setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public AgeContainerBuilder setSpawnHealth(int spawnHealth) {
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

	public AgeContainerBuilder setDmg(int dmg) {
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
		return new AgeContainer(ageName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
	}

	public static AgeContainer buildFromNBTTag(NBTTagCompound compound) {
		
		String ageName = compound.getString("name");
		int maxHealth = compound.getInt("dmg");
		int spawnHealth = compound.getInt("spawnHP");
		List<DropContainer> drops = new LinkedList<DropContainer>(); //TODO;
		int exp = compound.getInt("exp");
		int dmg = compound.getInt("dmp");
		int rank = compound.getInt("rank");
		boolean isHostile = compound.getBoolean("isHostile");

		return new AgeContainer(ageName, maxHealth, spawnHealth, exp, dmg, isHostile, rank, drops);
	}

	public static NBTTagCompound saveToNBTTagCompound(AgeContainer ageContainer) {
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setString("name", ageContainer.getAgeName());
		compound.setInt("dmg", ageContainer.getDmg());
		compound.setInt("exp", ageContainer.getExp());
		compound.setInt("maxHP", ageContainer.getMaxHealth());
		compound.setInt("rank", ageContainer.getRank());
		compound.setInt("spawnHP", ageContainer.getSpawnHealth());
		compound.setBoolean("isHostile", ageContainer.isHostile());
		//TODO set item//compound("drops", ageContainer.getDrops());
		
		return compound;
	}
	
	
	
}