package army;


import java.io.Serializable;
import java.util.Vector;

import position.Position;

import data.RangedUnitType;
import data.SpecialRule;
import data.UnitType;

public class CopyOfTroop implements Serializable {
	public static final long serialVersionUID=1L;
	public static final int LEVELS=10;
	
	private UnitType unit;
	private int[] numbers = new int[LEVELS];
	private Vector<Hero> heroes = new Vector<Hero>();
	
	public CopyOfTroop(UnitType unit, int[] numbers) throws InvalidLevelsArrayException {
		this.unit = unit;
		if (numbers.length != LEVELS) {
			throw (new InvalidLevelsArrayException(numbers.length));
		}
		for (int i=0;i<LEVELS;i++)
			this.numbers[i] = numbers[i];
	}
	
	public CopyOfTroop(UnitType unit, int number, Hero hero) {
		this(unit, number);
		addHero(hero);
	}
	
	public CopyOfTroop(UnitType unit, int number) {
		this.unit = unit;
		numbers[0] = number;
		for (int i=1;i<LEVELS;i++)
			numbers[i] = 0;
	}
	
	public CopyOfTroop(UnitType unit, int[] numbers, Hero[] heros) throws InvalidLevelsArrayException {
		this(unit, numbers);
		for (Hero hero : heros) {
			addHero(hero);
		}
	}
	
	public void addHero(Hero hero) {
		heroes.add(hero);
	}
	
	public void removeHero(Hero hero) {
		heroes.remove(hero);
	}

	public CopyOfTroop copy() {
		int[] newNumbers = new int[LEVELS];
		int i;
		for (i=0;i<LEVELS;i++)
			newNumbers[i] = numbers[i];
		CopyOfTroop newTroop = null;
		Hero[] newHeros = new Hero[heroes.size()];
		i=0;
		for (Hero hero : heroes) {
			newHeros[i] = hero.copy();
			i++;
		}		
		try {
			newTroop = new CopyOfTroop(unit, newNumbers, newHeros);
		} catch (InvalidLevelsArrayException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return newTroop;
	}
	
	public UnitType getUnit() {
		return unit;
	}
	
	public void merge(CopyOfTroop troop) throws InvalidLevelsArrayException {
		int[] newNumbers = troop.getNumbers();
		if (newNumbers.length != LEVELS)
			throw (new InvalidLevelsArrayException(newNumbers.length));
		for(int i=0;i<newNumbers.length;i++)
			numbers[i] = newNumbers[i];
	}
	
	public int[] getNumbers() {
		return numbers;
	}
	
	public int getMinSpeed() {
		int min = unit.getSpeed();
		for (Hero hero : heroes) {
			if (hero.getType().getSpeed() < min)
				min = hero.getType().getSpeed();
		}
		return min;
	}
	
	public int getMaxKills() {
		int sum = 0;
		for (int a : numbers)
			sum += a * unit.getMaxKills();
		for (Hero hero : heroes)
			sum += hero.getType().getMaxKills();
		return sum;
	}
	
	public boolean isRanged() {
		if (unit.isRanged())
			return true;
		for (Hero hero : heroes)
			if (hero.getType().isRanged())
				return true;
		return false;
	}
	
	public int getMaxRange() {
		/**
		 * Finds out the max range of ranged attacks of
		 * this troop or its heros.
		 */
		int max = 0;
		if (unit.getClass() == RangedUnitType.class && getNHTotal() > 0)
			max = ((RangedUnitType)unit).getRange();
		for (Hero hero : heroes) {
			UnitType heroType = hero.getType();
			if (heroType.getClass() == RangedUnitType.class) {
				RangedUnitType rangedHeroType = (RangedUnitType) heroType;
				if (rangedHeroType.getRange() > max) {
					max = rangedHeroType.getRange();
				}
			}
		}
		return max;		
	}
	
	public int getMaxSightRange() {
		int max = unit.getSightRange();
		if (getNHTotal() == 0)
			max = 0;
		for (Hero hero : heroes) {
			if (hero.getType().getSightRange() > max)
				max = hero.getType().getSightRange();
		}
		return max;		
	}
	
	public void fight(CopyOfTroop otherTroop, int ownBoni, int otherBoni) {
		int i, tmp, maxDead = otherTroop.getMaxKills();
		for (int level=0;level<LEVELS + heroes.size();level++) {
			tmp = Math.min(maxDead, numbers[level]);
			for (i=0;i<tmp;i++) {
				searchAndKill(level, otherTroop, ownBoni, otherBoni, true);
			}
			for (i=tmp;i<numbers[level];i++) {
				searchAndKill(level, otherTroop, ownBoni, otherBoni, false);
			}
			if (level < LEVELS) {
				maxDead -= numbers[level];
			} else {
				maxDead--;
			}
		}
	}
	
	public int randomLevel() {
		int tmp = (int)Math.random()*getTotal();
		for (int a : numbers) {
			if (tmp < a) {
				return a;
			} else {
				tmp -= a;
			}
		}
		return tmp + LEVELS;
	}
	
	public int getTotal() {
		int sum=heroes.size();
		for (int a : numbers)
			sum += a;
		return sum;
	}
	
	public void searchAndKill(int tmpOwnLevel, CopyOfTroop otherTroop, int ownBoni, int otherBoni, boolean otherCanKill) {
		int tmpOtherLevel = otherTroop.randomLevel(), otherLevel, ownLevel;
		UnitType otherUnit, ownUnit;
		if (tmpOtherLevel >= LEVELS) {
			Hero hero = otherTroop.getHeroes().get(tmpOtherLevel-LEVELS);
			otherUnit = hero.getType();
			otherLevel = hero.getLevel();
		} else {
			otherUnit = otherTroop.unit;
			otherLevel = tmpOtherLevel;
		}
		if (tmpOwnLevel > LEVELS) {
			Hero hero = heroes.get(tmpOtherLevel-LEVELS);
			ownUnit = hero.getType();
			ownLevel = hero.getLevel();			
		} else {
			ownUnit = otherTroop.unit;
			ownLevel = tmpOwnLevel;
		}
		switch (ownUnit.fight(otherUnit, ownLevel, ownBoni, otherBoni, otherLevel, otherCanKill)) {
		case 0:
			break;
		case -1:
			killOne(ownLevel);
			break;
		case 1:
			otherTroop.killOne(tmpOtherLevel);
			break;
		default:
			// TODO: Exception
		}
	}
	
	public Vector<Hero> getHeroes() {
		return heroes;
	}
	
	public void killOne(int level) {
		if (level < LEVELS) {
			if (numbers[level] > 0)
				numbers[level] -= 1;
		} else {
			Hero hero = heroes.get(level-LEVELS);
			if (hero.wound())
				heroes.remove(hero);
		}
	}
	
	public void levelUp(int bonus) {
		int level;
		for (level=0;level<LEVELS-1;level++) {
			int chance = (level+1)*5 + bonus;
			int number = numbers[level];
			for (int i=0;i<number;i++) {
				if (chance < 3 || (int) (Math.random() * chance) == 0) {
					numbers[i]--;
					numbers[i+1]++;
				}
			}
		}
		for (Hero hero : heroes)
			hero.levelUp(bonus);
	}
	
	public void attackRanged(Position position) {
	}

	public boolean removeOne() {
		int i;
		for (i=0;i<LEVELS;i++) {
			if (numbers[i] > 1) {
				numbers[i]--;
				return true;
			}
		}
		return false;
	}

	public int getNHLevelSum() {
		/**
		 * returns the sum of the levels of non-hero units.
		 * (Used for calculating the average)
		 */
		int levelSum = 0, level;
		for (level=0;level<LEVELS;level++)
			levelSum += numbers[level] * level;
		return levelSum;
	}
	
	public int getNHAverageLevel() {
		int average = 0;
		try {
			average = getNHLevelSum()/getNHTotal();
		} catch (ArithmeticException ex) {}
		return average;
	}

	public int getNHTotal() {
		int sum = 0, level;
		for (level=0;level<LEVELS;level++)
			sum += numbers[level];
		return sum;
	}

	public int getMaxSpyRange() {
		int max = unit.getSightRange();
		if (getNHTotal() == 0 && !unit.hasSpecialRule(SpecialRule.SPY))
			max = 0;
		for (Hero hero : heroes) {
			UnitType heroType = hero.getType();
			if (heroType.getSightRange() > max && heroType.hasSpecialRule(SpecialRule.SPY))
				max = heroType.getSightRange();
		}
		return max;
	}
	
	public String toString() {
		String string = unit.getName() + ": " + getTotal();
		for (Hero hero : heroes)
			string += "\n\t" + hero.toString();
		return string;
	}

	public boolean isEmpty() {
		return getTotal() == 0;
	}

	public boolean hasUnit(UnitType unitType) {
		if (unit == unitType && getNHTotal() > 0)
			return true;
		for (Hero hero : heroes)
			if (hero.getType() == unitType && hero.isAlive())
				return true;
		return false;
	}

	public boolean hasSpecialRule(int specialRule) {
		if (unit.hasSpecialRule(specialRule))
			return true;
		for (Hero hero : heroes)
			if (hero.isAlive() && hero.getType().hasSpecialRule(specialRule))
				return true;
		return false;
	}
}
