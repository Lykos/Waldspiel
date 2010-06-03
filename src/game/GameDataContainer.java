package game;

import data.*;

public interface GameDataContainer {
	public RessourceType[] getRessourceTypes();
	
	public SpecialRule[] getSpecialRules();
	
	public BuildingType[] getBuildingTypes();
	
	public ResearchType[] getResearchTypes();
	
	public UnitType[] getUnitTypes();
	
	public UnitType getWorker(People people);
}
