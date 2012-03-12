package data;

public class RangedUnitType extends UnitType {
	
	/**
	 * This is the class of the ranged Units. If a unit
	 * has to shoot, you can cast it to a RangedUniType
	 * and it can do all the ranged stuff.
	 */
	private static final long serialVersionUID = 1L;
	
	private final int range, rangedStrength, ballisticalSkills;
	
	public RangedUnitType(UnitType unitWithoutRange, int range, int rangedStrength, int ballisticalSkills) {
		super(unitWithoutRange);
		this.range = range;
		this.rangedStrength = rangedStrength;
		this.ballisticalSkills = ballisticalSkills;
	}

	@Override
	public boolean isRanged() {
		return range > 0 && rangedStrength > 0;
	}
	
	@Override
	public String toString() {
		String string = getName() + "; skills: " + getSkills() + "; strength: " + getStrength() + "; resistance: " + getResistance ()+ "; armor: " + getArmor();
		if (getSpecialRules().length > 0) {
			string += "; special rules: " + getSpecialRules()[0];
			for (int i=1;i<getSpecialRules().length;i++)
				string += ", " + getSpecialRules()[i];
		}
		return string;
	}

	public int getRange() {
		return range;
	}
	
	private int woundRanged() {
		return (int)(Math.random() * rangedStrength);
	}
	
	public boolean woundsRanged(UnitType otherUnit) {
		int wound = woundRanged();
		int defense = (int) (Math.random() * otherUnit.getResistance());
		if (hasSpecialRule(SpecialRule.REWOUNDRANGED) && wound < defense)
			wound = woundRanged();
		return wound > defense;
	}
	
	public boolean rangedAttack(int level, int distance, int number, UnitType otherUnit) {
		int hit = rangedHit(level);
		int target = (int)(Math.random() * distance * Math.max((50-number)/5,1));
		if (hasSpecialRule(SpecialRule.REHITRANGED) && hit <= target);
			hit = rangedHit(level);
		if (hit > target) {
			if (woundsRanged(otherUnit))
				if (!otherUnit.armorHolds())
					return true;
		}
		return false;
	}
	
	public int rangedHit(int level) {
		return (int)(Math.random() * ballisticalSkills * Math.pow(BALLISTICFACTOR,level));
	}
}
