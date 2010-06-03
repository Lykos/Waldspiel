package game;

import data.*;

public interface DataLoader {
	public UnitType[] loadUnitTypes();
	public ResearchType[] loadResearchTypes();
	public SpecialRule[] loadSpecialRules();
	public RessourceType[] loadRessourceTypes();
	public People[] loadPeoples();
	public BuildingType[] loadBuildingTypes();
}
