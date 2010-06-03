package data;

import game.RessourceAmount;
import game.Ressources;

import java.io.Serializable;

public class ResearchType implements Serializable {
	public static final int UNITTYPE=1,PRODUCTION=2,BUILDINGSLEVEL=3;
	public static final double COSTFACTOR=1.2,IMPROVEMENTFACTOR=1.2;
	
	private static final long serialVersionUID = 1L;
	
	private final int id, maxLevel, productionImprovementId, highLevel;
	private final Ressources cost;
	private final People[] peoples;
	private final RessourceType highLevelRessource;
	private final String name, description;
	private final UnitType unit;
	
	public ResearchType(String name, People[] peoples, int id, String description, Ressources cost, int maxLevel, RessourceType highLevelRessource, int highLevel) {
		this.id = id;
		this.peoples = peoples;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.maxLevel = maxLevel;
		this.unit = null;
		this.productionImprovementId = 0;
		this.highLevel = highLevel;
		this.highLevelRessource = highLevelRessource;
	}
	
	public ResearchType(String name, People[] peoples, int id, String description, Ressources cost, int maxLevel, RessourceType highLevelRessource, int highLevel, UnitType unit) {
		this.id = id;
		this.peoples = peoples;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.maxLevel = maxLevel;
		this.unit = unit;
		this.productionImprovementId = 0;
		this.highLevel = highLevel;
		this.highLevelRessource = highLevelRessource;
	}

	public ResearchType(String name, People[] peoples, int id, String description, Ressources cost, int maxLevel, RessourceType highLevelRessource, int highLevel, int productionImprovementId) {
		this.id = id;
		this.peoples = peoples;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.maxLevel = maxLevel;
		this.unit = null;
		this.productionImprovementId = productionImprovementId;
		this.highLevel = highLevel;
		this.highLevelRessource = highLevelRessource;
	}
	
	public boolean canResearch(People people) {
		for (People people1 : peoples)
			if (people == people1)
				return true;
		return false;
	}

	public UnitType getUnitType() {
		// if (id != IMPROVEMENT) Exception
		return unit;
	}
		
	public int getId() {
		return id;
	}
	
	public int getProductionImprovementId() {
		return productionImprovementId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Ressources getCost() {
		return cost;
	}
	
	public Ressources getLevelCost(int level) {
		double costFactor = Math.pow(ResearchType.COSTFACTOR,level);
		cost.mult(costFactor);
		if (level >= highLevel) {
			int highLevelAmount = (int) Math.pow(ResearchType.COSTFACTOR,level-highLevel);
			return cost.plus(new RessourceAmount(highLevelRessource, highLevelAmount));
		} else {
			return cost;
		}
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
}
