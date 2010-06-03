package data;

import game.Ressources;

import java.io.Serializable;

public class BuildingType implements Serializable {
	/** 
	 * This class stands for the building types, but not for the actual buildings on the map.
	 */
	public static final long serialVersionUID=1L;
	
	public static final int MAXLEVEL=9;
	private static int maxSight=0;
	
	public static int getMaxSight() {
		return maxSight;
	}
	
	private final String name;
	private final int hitPoints, sightRange, productionImprovementId;
	private final People[] peoples;
	private final BuildingType[] conditions;
	private final Ressources cost, ressourcesNeeded, turnProduction, directProduction;
	private final UnitType[] recruitments;
	private final ResearchType[] researchs;
	private final String fromFar;
	
	public BuildingType(String name, int hitPoints, int sightRange,
			People[] peoples, BuildingType[] conditions, Ressources cost,
			Ressources directProduction, Ressources ressourcesNeeded,
			Ressources turnProduction, UnitType[] recruitments,
			ResearchType[] researchs, int productionImprovementId,
			String fromFar) {
		this.cost = cost;
		this.hitPoints = hitPoints;
		this.name = name;
		this.directProduction = directProduction;
		this.turnProduction = turnProduction;
		this.ressourcesNeeded = ressourcesNeeded;
		this.peoples = peoples;
		this.conditions = conditions;
		this.recruitments = recruitments;
		this.researchs = researchs;
		this.productionImprovementId = productionImprovementId;
		this.fromFar = fromFar;
		this.sightRange = sightRange;
		if (sightRange > maxSight)
			maxSight = sightRange;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFromFar() {
		return fromFar;
	}
	
	public int getProductionImprovementId() {
		return productionImprovementId;
	}
	
	public UnitType[] getRecruitments(People people) {
		// TODO: not elegant
		int length = 0, i=0;
		for (UnitType unitType : recruitments)
			if (unitType.getPeople() == people)
				length++;
		UnitType[] peopleRecruitments = new UnitType[length];
		for (UnitType researchType : recruitments)
			if (researchType.getPeople() == people) 
				peopleRecruitments[i++] = researchType;
		return peopleRecruitments;
	}
	
	public UnitType[] getRecruitments() {
		return recruitments;
	}
	
	public ResearchType[] getResearchs() {
		return researchs;
	}
	
	public ResearchType[] getResearchs(People people) {
		// TODO: not elegant
		int length = 0, i=0;
		for (ResearchType researchType : researchs)
			if (researchType.canResearch(people))
				length++;
		ResearchType[] peopleResearchs = new ResearchType[length];
		for (ResearchType researchType : researchs)
			if (researchType.canResearch(people)) 
				peopleResearchs[i++] = researchType;
		return peopleResearchs;
	}
	
	public int getSightRange() {
		return sightRange;
	}
	
	public int getHitPoints() {
		return hitPoints;
	}
	
	public Ressources getTurnProduction() {
		return turnProduction;
	}
	
	public Ressources getDirectProduction() {
		return directProduction;
	}
	
	public Ressources getRessourcesNeeded() {
		return ressourcesNeeded;
	}
	
	public BuildingType[] getConditions() {
		return conditions;
	}
	
	public People[] getPeoples() {
		return peoples;
	}
	
	public boolean needsWorker() {
		return !turnProduction.empty();
	}

	public Ressources getCost() {
		return cost;
	}
}
