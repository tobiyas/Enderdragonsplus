package de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties;

import java.util.List;

import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.util.config.returncontainer.DropContainer;

public class AgeContainerBuilder {

	protected static final String NBT_TAG__NAME = "name";
	protected static final String NBT_TAG__PRETTY_AGE_NAME = "agePrettyName";
	protected static final String NBT_TAG__MAX_HEALTH = "maxHP";
	protected static final String NBT_TAG__SPAWN_HEALTH = "spawnHP";
	
	//protected static final String NBT_TAG__DROPS= "drops"; //TODO add drops
	protected static final String NBT_TAG__EXP = "exp";

	protected static final String NBT_TAG__DMG = "dmg";
	protected static final String NBT_TAG__IS_HOSTILE = "isHostile";
	protected static final String NBT_TAG__RANK = "rank";
	
	
	protected String ageConfigName;
	protected String prettyAgeName;
	
	protected double maxHealth;
	protected double spawnHealth;
	protected List<DropContainer> drops;
	protected int exp;
	protected double dmg;
	protected boolean isHostile;
	
	protected int rank;
	
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
}
