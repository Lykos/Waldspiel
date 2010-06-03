package game;

import java.io.Serializable;
import data.UnitType;;

public class Hero implements Serializable {
	public static final long serialVersionUID=1L;
	
	private UnitType type;
	private String name;
	private int hitPoints, level;
	
	public Hero(UnitType type, int hitPoints) {
		this(type, hitPoints, type.getName());
	}
	
	public Hero(UnitType type, int hitPoints, String name) {
		this.hitPoints = hitPoints;
		this.type = type;
		this.name = name;
		this.level = 0;
	}
	
	public UnitType getType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean wound() {
		// true if he really dies
		return --hitPoints <= 0;
	}
	
	public String getName() {
		return name;
	}
	
	public void levelUp(int bonus) {
		if (level == Troop.LEVELS-1)
			return;
		int chance = (level+1)*5 + bonus;
		if (chance < 3 || (int) (Math.random() * chance) == 0)
			level++;
	}
	
	public Hero copy() {
		return new Hero(type, hitPoints, name);
	}

	public int getHitPoints() {
		return hitPoints;
	}
	
	@Override
	public String toString() {
		return name + ": " + type.getName() + " level " + level + " hitpoints: " + hitPoints;
	}
}
