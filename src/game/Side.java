package game;

import java.io.Serializable;
import java.util.LinkedList;

import data.People;
import data.ResearchType;
import data.BuildingType;
import data.UnitType;

public class Side implements Serializable {
	public static final long serialVersionUID=1L;
	
	protected static Game gameGame;
	protected static GameDataContainer game;
	protected static void setGame(Game newGame) {
		game = newGame;
		gameGame = newGame;
	}
	
	private String name;
	private boolean human, finished = false, kingDead = false;
	private ForestMap map;
	private People people;
	private LinkedList<String> messages;
	private LinkedList<Building> buildings;
	private LinkedList<Army> armies;
	private LinkedList<Research> researchs;
	private Player player;
	private Hero king;
	private final UnitType kingType;
	private Ressources ressources;
	private int lives;
	private Position start;
	
	protected Side(String name, boolean human, People people,
			Player player, Position start) {
		this.name = name;
		this.human = human;
		this.people = people;
		map = new ForestMap(this);
		messages = new LinkedList<String>();
		this.player = player;
		// Get the default start stuff for the given people.
		ressources = new Ressources(this.people.getStartingRessources());
		System.out.println("Ressourcenlaenge: " + ressources.getRessources().size() + " Ressourcen: " + ressources);
		armies = new LinkedList<Army>();
		UnitType[] startingUnits = people.getStartingUnits();
		int[] numbers = people.getNumbers();
		LinkedList<Troop> startingTroops = new LinkedList<Troop>();
		int i;
		kingType = people.getKing();
		king = new Hero(kingType, 2, name);
		startingTroops.add(new Troop(startingUnits[0],numbers[0], king));
		for (i=1;i<numbers.length;i++) {
			startingTroops.add(new Troop(startingUnits[i],numbers[i]));
		}
		Army.buildArmy(startingTroops,start,this);
		ResearchType[] startingResearchs = people.getStartingResearchs();
		int[] levels = people.getLevels();
		researchs = new LinkedList<Research>();
		for (i=0;i<levels.length;i++) {
			researchs.add(new Research(startingResearchs[i],levels[i]));
		}
		buildings = new LinkedList<Building>();
		try {
			addBuilding(new Building(people.getHeadQuader(), start, this));
		} catch (IsAlreadyBuildException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public ForestMap getMap() {
		return map;
	}
	
	public boolean canBuild(BuildingType building) {
		if (!canPay(building.getCost()))
			return false;
		for (BuildingType condition : building.getConditions())
			if (!hasBuilding(condition))
				return false;
		return true;
	}
	
	public boolean canLevel(Building building) {
		Ressources cost = building.getLevelUpCost(); 
		if (!canPay(cost))
			return false;
		for (Research research : researchs)
			if (research.getType().getId() == ResearchType.BUILDINGSLEVEL) {
				return research.getLevel() > building.getLevel() + 1;
			}
		return false;
	}
	
	public boolean canPay(Ressources cost) {
		return ressources.enough(cost);
	}
	
	protected void removeArmy(Army army) {
		armies.remove(army);
		Position position = army.getPosition();
		map.removeArmy(position, army.getSightRange());
	}
	
	protected void giveRessources(Ressources ressources) {
		this.ressources.add(ressources);
	}
	
	public boolean hasBuilding(BuildingType building) {
		return buildings.contains(building);
	}
	
	protected void addBuilding(Building building) {
		buildings.add(building);
		map.addBuilding(building.getPosition(), building.getSightRange());
	}
	
	protected void removeBuilding(Building building) {
		buildings.remove(building);
		map.removeBuilding(building.getPosition(), building.getSightRange());
	}
	
	protected void pay(Ressources cost) throws NegativeRessourceException {
		ressources.sub(cost);
	}
	
	protected void newTurn() {
		if (kingDead)
			newKing();
		for (Building building : buildings)
			building.newTurn();
		for (Army army : armies)
			army.newTurn();
		finished = false;
		try {
			player.newTurn();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(name + " loses because he threw an exception.");
			lose();
		}
	}
	
	public void finish() {
		finished = true;
	}
	
	public void endTurn() {
		for (Building building : buildings)
			building.endTurn();
		for (Army army : armies)
			army.endTurn();
		if (king.getHitPoints() <= 0) {
			kingDead = true;
			if (lives-- <= 0)
				lose();
		}
	}
	
	
	
	protected void newKing() {
		king = new Hero(people.getKing(), 2, name);
		LinkedList<Troop> heroTroops = new LinkedList<Troop>();
		UnitType worker = game.getWorker(people);
		heroTroops.add(new Troop(worker, 0, king));
		Army.buildArmy(heroTroops, start, this);
	}
	
	protected void addArmy(Army army) {
		armies.add(army);
		map.addArmy(army.getPosition(), army.getSightRange());
	}
	
	public boolean hasResearch(ResearchType researchType) { 
		for (Research research : researchs) {
			if (research.getType() == researchType) {
				return true;
			}
		}
		return false;
	}

	public boolean hasResearch(ResearchType researchType, int minLevel) { 
		for (Research research : researchs) {
			if (research.getType() == researchType && research.levelBigger(minLevel))
				return true;
		}
		return false;
	}

	public int getUnitLevel(UnitType unit) {
		for (Research research : researchs) {
			ResearchType researchType = research.getType();
			if (researchType.getId() == ResearchType.UNITTYPE && researchType.getUnitType() == unit)
				return research.getLevel();
		}
		return 0;
	}
	
	public int getProductionLevel(int productionImprovementId) {
		for (Research research : researchs) {
			ResearchType researchType = research.getType();
			if (researchType.getId() == ResearchType.PRODUCTION && researchType.getProductionImprovementId() == productionImprovementId)
				return research.getLevel();
		}
		return 0;
	}
	
	public int getResearchLevel(ResearchType researchType) {
		for (Research research : researchs) {
			if (researchType == research.getType())
				return research.getLevel();
		}
		return 0;
	}

	protected void addResearch(Research research) {
		researchs.add(research);
	}

	protected void levelUpResearch(ResearchType researchType) {
		for (Research research : researchs) {
			if (researchType == research.getType())
				research.levelUp();
		}		
	}
	
	public LinkedList<Army> getArmies() {
		return armies;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasFinished() {
		return finished;
	}
	
	public People getPeople() {
		return people;
	}
	
	public boolean isHuman() {
		return human;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<String> getMessages() {
		LinkedList<String> tmpMessages = (LinkedList<String>) messages.clone();
		messages.clear();
		return tmpMessages;
	}
	
	public LinkedList<Building> getBuildings() {
		return buildings;
	}
	
	public LinkedList<Research> getResearchs() {
		return researchs;
	}
	
	public Ressources getRessources() {
		return ressources;
	}
	
	public void lose() {
		gameGame.sideLoses(this);
	}
	
	public boolean setArmyDestination(Army army, int x, int y) {
		return army.setDestination(x, y);
	}
	
	public boolean moveArmytoDestination(Army army) {
		return army.moveToDestination();	
	}
	
	public boolean armyBuilds(Army army, BuildingType building) {
		return army.build(building);			
	}
	
	public boolean armyLevels(Army army) {
		return army.levelBuilding();
	}
	
	public boolean armyRepairs(Army army) {
		return army.repair();
	}
	
	public boolean armyHunts(Army army) {
		return army.hunt();
	}
	
	public boolean armyUsesMagic(Army army) {
		return army.useMagic();
	}
	
	public boolean armyShoots(Army army) {
		return army.shoot();
	}

	public boolean armyDestroys(Army army) {
		return army.destroy();
	}

	public BuildingType[] getBuildableTypes() {
		// not elegant!
		int length=0;
		System.out.println("lengthwhole:" + game.getBuildingTypes().length);
		for (BuildingType type : game.getBuildingTypes())
			if (canBuild(type))
				length++;
		BuildingType[] buildingTypes = new BuildingType[length];
		int i=0;
		System.out.println("lengthbuildable:" + length);
		for (BuildingType type : game.getBuildingTypes())
			if (canBuild(type))
				buildingTypes[i++] = type;		
		return buildingTypes;
	}

	public Player getPlayer() {
		return player;
	}

}