package game;

import java.io.Serializable;
import java.util.Vector;

import position.Forest;
import position.Placeable;
import position.Position;

import army.Army;
import army.DifferentPositionException;
import army.InvalidLevelsArrayException;
import army.Troop;

import data.BuildingType;
import data.ResearchType;
import data.UnitType;

public class Building implements Serializable, Placeable {
	/**
	 * This class represents the actual buildings in the game. Not the building types,
	 * which are the same every time you play it, but the actual buildings you put in
	 * the forest, with informations about their level, their hitpoints and so one.
	 */
	public static final long serialVersionUID=1L;
	
	public static final double COSTFACTOR=1.2, LIVEPOINTSFACTOR=1.2;
	
	private static Forest forest;
	
	public static void setForest(Forest newForest) {
		forest = newForest;
	}
	
	public static void build(BuildingType type, Position position, Side owner) throws NegativeRessourceException, IsAlreadyBuildException {
		new Building(type, position, owner);
		owner.pay(type.getCost());
	}
	
	private int level, hitpoints;
	private BuildingType type;
	private Position position;
	private Side owner;
	private Vector<Integer> recruitments;
	private Vector<Integer> researchGoals;
	
	public Building(BuildingType type, Position position, Side owner) throws IsAlreadyBuildException {
		researchGoals = new Vector<Integer>();
		recruitments = new Vector<Integer>();
		this.type = type;
		this.position = position;
		this.owner = owner;
		level = 0;
		hitpoints = type.getHitPoints();
		forest.getPosition(position).build(this);
		owner.addBuilding(this);
	}
	
	public Side getOwner() {
		return owner;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getLevel() {
		return level;
	}
	
	public BuildingType getType() {
		return type;
	}
	
	public String lookFromFar() {
		return type.getFromFar();
	}
	
	public int getMaxHitpoints() {
		return (int) (Math.pow(LIVEPOINTSFACTOR, level) * type.getHitPoints());
	}
	
	public Ressources getLevelUpCost() {
		double factor = Math.pow(COSTFACTOR, level+1);
		return type.getCost().mult(factor);
	}
	
	public void produce() {
		for (int i=0;i<level+1;i++) {
			if (owner.canPay(type.getRessourcesNeeded())) {
				int improvementLevel = owner.getProductionLevel(type.getProductionImprovementId());
				double factor = Math.pow(ResearchType.IMPROVEMENTFACTOR,improvementLevel);
				owner.giveRessources(type.getTurnProduction().mult(factor));
				try {
					owner.pay(type.getRessourcesNeeded());
				} catch (NegativeRessourceException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	public void levelUp() throws NegativeRessourceException {
		owner.pay(getLevelUpCost());
		level++;
		hitpoints = getMaxHitpoints();
		owner.giveRessources(type.getDirectProduction());
	}
	
	public void repair() {
		hitpoints = getMaxHitpoints();
	}
	
	public void newTurn() {
		research();
		produce();
	}
	
	public void endTurn() {
		recruit();
	}
	
	public void addRecruitment(Integer recruitment) {
		if (recruitment < type.getRecruitments(owner.getPeople()).length && recruitment >= 0)
			recruitments.add(recruitment);
	}

	public void addResearchGoal(Integer researchGoal) {
		if (researchGoal < type.getResearchs(owner.getPeople()).length && researchGoal >= 0)
			recruitments.add(researchGoal);
	}
	
	public void recruit() {
		Army newArmy = null;
		for (int i=0;i<level+1;i++) {
			int recruitmentIndex = -1;
			while (recruitments.size() > 0 && recruitmentIndex == -1) {
				recruitmentIndex = recruitments.get(0);
				recruitments.remove(0);
				if (recruitmentIndex >= type.getRecruitments(owner.getPeople()).length || recruitmentIndex < 0)
					recruitmentIndex = -1;
			}
			if (recruitmentIndex == -1)
				return;
			UnitType recruitment = type.getRecruitments(owner.getPeople())[recruitmentIndex];
			if (owner.canPay(recruitment.getCost())) {
				Vector<Troop> troops = new Vector<Troop>();
				int[] numbers = new int[Troop.LEVELS];
				for (int j=0;j<numbers.length;j++) {
					numbers[j] = 0;
				}
				int startlevel = owner.getUnitLevel(recruitment);
				numbers[startlevel] = 1;
				Troop troop = null;
				try {
					troop = new Troop(recruitment, numbers);
				} catch (InvalidLevelsArrayException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				troops.add(troop);
				Army tmpArmy = Army.buildArmy(troops, position, owner);
				if (newArmy != null) {
					try {
						newArmy.merge(tmpArmy);
					} catch (DifferentPositionException ex) {
						ex.printStackTrace();
						System.exit(1);
					}
					tmpArmy.remove();
				} else {
					newArmy = tmpArmy;
				}
				try {
					owner.pay(recruitment.getCost());
				} catch (NegativeRessourceException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}		
		}
	}
	
	public void research() {
		ResearchType research=null;
		for (int i=0;i<level;i++) {
			int researchIndex = -1;
			while (researchGoals.size() > 0 && researchIndex == -1) {
				researchIndex = researchGoals.get(0);
				if (researchIndex >= type.getResearchs(owner.getPeople()).length || researchIndex < 0) {
					researchGoals.remove(0);
					researchIndex = -1;
				} else {
					research = type.getResearchs(owner.getPeople())[researchIndex];
					boolean hasMaxLevel = owner.hasResearch(research,research.getMaxLevel());
					if (hasMaxLevel) {
						researchGoals.remove(0);
						researchIndex = -1;						
					}						
				}
			}
			if (researchGoals.size() == 0)
				return;
			int level = owner.getResearchLevel(research);
			Ressources cost = research.getLevelCost(level);
			if (owner.canPay(cost)) {
				try {
					owner.pay(cost);
					if (level == -1)
						owner.addResearch(new Research(research));
					else
						owner.levelUpResearch(research);
					researchGoals.remove(0);
				} catch (NegativeRessourceException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}		
		}
	}
	
	public void destroy() {
		owner.removeBuilding(this);
		forest.getPosition(position).removeBuilding(this);
	}
	
	public int getHitpoints() {
		return hitpoints;
	}

	public boolean needsWorker() {
		return type.needsWorker();
	}

	public int getSightRange() {
		return type.getSightRange();
	}

	public boolean canRecruit() {
		if(type.getRecruitments(owner.getPeople()).length > 0)
			return true;
		return false;
	}

	public UnitType[] getRecruitments() {
		return type.getRecruitments(owner.getPeople());
	}
	
	public boolean canResearch() {
		if(type.getResearchs(owner.getPeople()).length > 0)
			return true;
		return false;
	}

	public ResearchType[] getResearchs() {
		return type.getResearchs(owner.getPeople());
	}
}
