package data;

import game.Ressources;

import java.io.Serializable;
import java.util.LinkedList;

public class UnitType implements Serializable {
	public static final long serialVersionUID=1L;
	public static final double BALLISTICFACTOR = 1.1;
	public static final double STRENGTHFACTOR = 1.06;
	public static final double SKILLSFACTOR = 1.1;
	public static final int CHAOTIC=-1,NEUTRAL=0,LAWFUL=1;
	private static int maxSight = 0;
	
	static int getMaxSight() {
		return maxSight;
	}
	
	private final People people;
	private final String name;
	private final int speed, resistance, sightRange, skills, strength, armor,
	alignment, maxKills;
	private final Ressources cost;
	private final SpecialRule[] specialRules;
	
	public UnitType(int speed, String name, int resistance,
			Ressources cost, int sightRange, People people,
			int strength, int skills, SpecialRule[] specialRules,
			int armor, int maxKills, int alignment) {
		/* TODO: Maybe reorder the thousand parameters.
		 * Exceptions, if the values don't fit. 
		 * */
		this.maxKills = maxKills;
		this.armor = armor;
		this.strength = strength;
		this.speed = speed;
		this.name = name;
		this.resistance = resistance;
		this.cost = cost;
		this.sightRange = sightRange;
		this.people = people;
		this.specialRules = specialRules;
		this.skills = skills;
		this.alignment = alignment;
		if (sightRange > maxSight)
			maxSight = sightRange;
	}
	
	protected UnitType(UnitType baseType) {
		maxKills = baseType.getMaxKills();
		armor = baseType.getArmor();
		strength = baseType.getStrength();
		speed = baseType.getSpeed();
		name = baseType.getName();
		resistance = baseType.getResistance();
		cost = baseType.getCost();
		sightRange = baseType.getSightRange();
		people = baseType.getPeople();
		specialRules = baseType.getSpecialRules();
		skills = baseType.getSkills();		
		alignment = baseType.getAlignment();
	}
	
	public int getAlignment() {
		return alignment;
	}
	
	public int getSkills() {
		return skills;
	}
	
	@Override
	public String toString() {
		String string = name + "; skills: " + skills + "; strength: " + strength + "; resistance: " + resistance + "; armor: " + armor;
		if (specialRules.length > 0) {
			string += "; special rules: " + specialRules[0];
			for (int i=1;i<specialRules.length;i++)
				string += ", " + specialRules[i];
		}
		return string;
	}

	protected SpecialRule[] getSpecialRules() {
		return specialRules;
	}

	public LinkedList<String> getSpecialRulesString() {
		LinkedList<String> strings = new LinkedList<String>();
		for (SpecialRule specialRule : specialRules)
			strings.add(specialRule.getName());
		return strings;
	}

	public int getStrength() {
		return strength;
	}

	public int getArmor() {
		return armor;
	}

	public int getSpeed() {
		return speed;
	}
	
	public People getPeople() {
		return people;
	}
	
	public String getName() {
		return name;
	}

	public boolean hasSpecialRule(SpecialRule specialRule) {
		return false;
	}
	
	public boolean hasSpecialRule(int specialRuleId) {
		for (SpecialRule special : specialRules)
			if (special.getId() == specialRuleId)
				return true;
		return false;
	}
	
	public int hit(int boni, int level) {
		return (int)(Math.random() * (skills * Math.pow(level, SKILLSFACTOR) + boni * skills/10));
	}
	
	public boolean isRanged() {
		return false;
	}
		
	public int fight(UnitType otherUnit, int ownLevel, int ownBoni, int otherBoni, int otherLevel, boolean otherCanKill) {
		int ownHit = hit(ownBoni, ownLevel);
		int otherHit = otherUnit.hit(otherBoni, otherLevel);
		if (hasSpecialRule(SpecialRule.REHIT) && !otherUnit.hasSpecialRule(SpecialRule.REHIT) && ownHit < otherHit) {
			ownHit = hit(ownBoni, ownLevel);
		} else if (otherUnit.hasSpecialRule(SpecialRule.REHIT) && !hasSpecialRule(SpecialRule.REHIT) && ownHit >= otherHit) {
			otherHit = otherUnit.hit(otherBoni, otherLevel);
		}
		if (ownHit >= otherHit) {
			if (wounds(ownLevel, otherUnit))
				if (!otherUnit.armorHolds())
					return 1;
		} else if (otherCanKill) {
			if (otherUnit.wounds(otherLevel, this))
				if(!armorHolds())
					return -1;
		}
		return 0;
	}
	
	public boolean wounds(int level, UnitType otherUnit) {
		int wound = wound(level);
		int defense = (int) (Math.random() * otherUnit.getResistance());
		if (hasSpecialRule(SpecialRule.REWOUND) && wound < defense)
			wound = wound(level);
		return wound > defense;
	}
	
	public int wound(int level) {
		return (int)(Math.random() * strength * Math.pow(STRENGTHFACTOR, level));
	}
	
	public boolean armorHolds() {
		return (int)(Math.random()*6) > armor;
	}
	
	public int getResistance() {
		return resistance;
	}
	
	public int getMaxKills() {
		return maxKills;
	}
	
	public int getSightRange() {
		return sightRange;
	}
		
	public Ressources getCost() {
		return cost;
	}
}
