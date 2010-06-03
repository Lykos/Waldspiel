package game;

import humanPlayer.HumanPlayer;

import java.io.Serializable;
import java.util.LinkedList;

import data.*;
import dataLoader.DataLoader;
import dataLoader.StupidDataLoader;

public class Game implements Serializable, Runnable, GameDataContainer {
	public static final long serialVersionUID=1L;
	
	private UnitType[] unitTypes;
	private Forest forest;
	private LinkedList<Side> sides, losers;
	private People[] peoples;
	private RessourceType[] ressourceTypes;
	private BuildingType[] buildingTypes;
	private ResearchType[] researchTypes;
	private SpecialRule[] specialRules;
	private int turn;
	
	public static void main(String[] args) {
		Game game = new Game(2);
		new Thread(game).start();
	}
	
	public RessourceType[] getRessourceTypes() {
		return ressourceTypes;
	}
	
	public SpecialRule[] getSpecialRules() {
		return specialRules;
	}
	
	public BuildingType[] getBuildingTypes() {
		return buildingTypes;
	}
	
	public ResearchType[] getResearchTypes() {
		return researchTypes;
	}
	
	public UnitType[] getUnitTypes() {
		return unitTypes;
	}
	
	public Game(int players) {
		LinkedList<Position> starts = new LinkedList<Position>();
		sides = new LinkedList<Side>();
		forest = new Forest();
		Building.setForest(forest);
		Side.setGame(this);
		Army.setForest(forest);
		DataLoader dataLoader = new StupidDataLoader();
		peoples = dataLoader.loadPeoples();
		specialRules = dataLoader.loadSpecialRules();
		ressourceTypes = dataLoader.loadRessourceTypes();
		unitTypes = dataLoader.loadUnitTypes();
		researchTypes = dataLoader.loadResearchTypes();
		buildingTypes = dataLoader.loadBuildingTypes();
		for (int p=0;p<players;p++) {
			People randomPeople = peoples[(int) (Math.random()*peoples.length)];
			boolean needsNew = true;
			Position start = null;
			while (needsNew) {
				start = Position.randomPosition();
				needsNew = false;
				for (Position otherStart : starts)
					if (start.stepsTo(otherStart) < 10)
						needsNew = true;
			}
			Player player = new HumanPlayer();
			Side side = new Side("Player " + p, true, randomPeople, player, start);
			sides.add(side);
			player.setSide(side);
		}
	}	
	
	public void run() {
		for (Side side : sides)
			side.getPlayer().start();
		while (sides.size() > 1) {
			turn++;
			for (Side side : sides) {
				side.newTurn();
			}
			boolean allfinished = false;
			while (!allfinished) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				allfinished = true;
				for (Side side : sides) {
					if (!side.hasFinished())
						allfinished = false;
				}
			}
			for (Side side : sides) {
				side.endTurn();
			}
		}
	}
	
	public int getTurn() {
		return turn;
	}
	
	protected Forest getForest() {
		return forest;
	}
	
	protected void sideLoses(Side side) {
		losers.add(side);
		sides.remove(side);
	}
	
	public LinkedList<Side> getSides() {
		return sides;
	}
	
	public UnitType getWorker(People people) {
		for (UnitType unit : unitTypes) {
			if (unit.hasSpecialRule(SpecialRule.WORKER) && unit.getPeople() == people)
				return unit;
		}
		return null;
	}
}
