package data;

import game.Ressources;

import java.io.Serializable;

public class People implements Serializable {
	public static final long serialVersionUID=1L;
	
	private final String name;
	private final UnitType[] startingUnits;
	private final ResearchType[] startingResearchs;
	private final int[] numbers, levels;
	private final Ressources startingRessources;
	private UnitType king; // TODO: Implement the whole king stuff!!
	private BuildingType headQuader;
	
	public People(String name, Ressources startingRessources,
			int researchs, int units) {
		// TODO: Exceptions for non-compatible lengths.
		this.startingResearchs = new ResearchType[researchs];
		this.levels = new int[researchs];
		this.numbers = new int[units];
		this.startingUnits = new UnitType[units];
		this.name = name;
		this.startingRessources = startingRessources;
	}
	
	public void setStartingResearchs(ResearchType[] startingResearchs, int[] levels) {
		int i;
		for (i=0;i<this.levels.length;i++) {
			this.startingResearchs[i] = startingResearchs[i];
			this.levels[i] = levels[i];
		}
	}
	
	public void setKing(UnitType king) {
		this.king = king;
	}
	
	public void addstartingTroops(UnitType[] types, int[] numbers) {
		// TODO: Exceptions for non-compatible lengths
		int i;
		for (i=0;i<this.numbers.length;i++) {
			startingUnits[i] = types[i];
			this.numbers[i] = numbers[i];
		}
	}
	
	public void setHeadQuader(BuildingType building) {
		headQuader = building;
	}

	public ResearchType[] getStartingResearchs() {
		ResearchType[] tmp = new ResearchType[levels.length];
		System.arraycopy(startingResearchs, 0, tmp, 0, startingResearchs.length);
		return tmp;
	}
	
	public int[] getLevels() {
		int[] tmpLevels = new int[levels.length];
		System.arraycopy(levels, 0, tmpLevels, 0, levels.length);
		return tmpLevels;
	}
	
	public UnitType[] getStartingUnits() {
		UnitType[] tmp = new UnitType[numbers.length];
		System.arraycopy(startingUnits, 0, tmp, 0, numbers.length);
		return tmp;
	}
	
	public int[] getNumbers() {
		int[] tmpNumbers = new int[numbers.length];
		System.arraycopy(numbers, 0, tmpNumbers, 0, numbers.length);
		return tmpNumbers;
	}
	
	public UnitType getKing() {
		return king;
	}
	
	public String getName() {
		return name;
	}

	public Ressources getStartingRessources() {
		return startingRessources.copy();
	}

	public BuildingType getHeadQuader() {
		return headQuader;
	}
}
