package test;

import java.util.Vector;

import position.InvalidPositionException;
import position.Position;

import army.Army;
import army.Hero;
import army.InvalidLevelsArrayException;
import army.Troop;
import data.RangedUnitType;
import data.SpecialRule;
import data.UnitType;
import game.RessourceAmount;

import game.Game;
import game.Ressources;
import game.Side;
import junit.framework.TestCase;

public class ArmyTest extends TestCase {
	Game game;
	Side side;
	UnitType fastUnit, slowUnit, seeingUnit, blindUnit, farUnit, closeUnit, worker;
	int fastSpeed = 5, slowSpeed = 1, blindSightRange = 1,
			seeingSightRange = 4, closeRange = 2, farRange = 6;

	public ArmyTest() {
		game = new Game(2);
		side = game.getSides().getFirst();
		fastUnit = new UnitType(fastSpeed, "Grauwolf", 14, new Ressources(
				new RessourceAmount[] {}), 2, side.getPeople(), 15, 15,
				new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL);
		slowUnit = new UnitType(slowSpeed, "Grauwolf", 14, new Ressources(
				new RessourceAmount[] {}), 2, side.getPeople(), 15, 15,
				new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL);
		seeingUnit = new UnitType(fastSpeed, "Grauwolf", 14, new Ressources(
				new RessourceAmount[] {}), 4, side.getPeople(), 15, 15,
				new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL);
		blindUnit = new UnitType(slowSpeed, "Grauwolf", 14, new Ressources(
				new RessourceAmount[] {}), 1, side.getPeople(), 15, 15,
				new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL);
		farUnit = new RangedUnitType(new UnitType(fastSpeed, "Grauwolf", 14,
				new Ressources(new RessourceAmount[] {}), 4, side.getPeople(),
				15, 15, new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL), 6, 2, 2);
		closeUnit = new RangedUnitType(new UnitType(slowSpeed, "Grauwolf", 14,
				new Ressources(new RessourceAmount[] {}), 1, side.getPeople(),
				15, 15, new SpecialRule[] {}, 0, 2, UnitType.NEUTRAL), 2, 2, 2);
		worker = new UnitType(fastSpeed, "Grauwolf", 14, new Ressources(
				new RessourceAmount[] {}), 2, side.getPeople(), 15, 15,
				new SpecialRule[] {new SpecialRule("worker", SpecialRule.WORKER)}, 0, 2, UnitType.NEUTRAL);
	}

	public void testSpeed() throws InvalidPositionException,
			InvalidLevelsArrayException {
		Troop fastTroop = new Troop(fastUnit, 10);
		Vector<Troop> troops = new Vector<Troop>();
		troops.add(fastTroop);
		Army army = Army.buildArmy(troops, new Position(10, 10), side);
		assertEquals("The army with only the fast troop isn't fast enough.",
				fastSpeed, army.getSpeed());
		Troop emptySlowTroop = new Troop(slowUnit, 0);
		army.addTroop(emptySlowTroop);
		assertEquals(
				"The army with only the fast and the empty troop isn't fast enough.",
				fastSpeed, army.getSpeed());
		Troop slowTroop = new Troop(slowUnit, 10);
		army.addTroop(slowTroop);
		assertEquals(
				"The army with both troops doesn't have the correct speed.",
				slowSpeed, army.getSpeed());
		army.removeTroop(emptySlowTroop);
		assertEquals(
				"The army doesn't have the correct speed after the removal of the slow unit.",
				fastSpeed, army.getSpeed());
	}

	public void testSightRange() throws InvalidPositionException,
			InvalidLevelsArrayException {
		Troop blindTroop = new Troop(blindUnit, 10);
		Vector<Troop> troops = new Vector<Troop>();
		troops.add(blindTroop);
		Army army = Army.buildArmy(troops, new Position(10, 10), side);
		assertEquals(
				"The army with only the blind unit doesn't have the right sight range.",
				blindSightRange, army.getSightRange());
		Troop emptySeeingTroop = new Troop(seeingUnit, 0);
		army.addTroop(emptySeeingTroop);
		assertEquals(
				"The army with only the blind and the empty troop isn't blind enough.",
				blindSightRange, army.getSightRange());
		Troop seeingTroop = new Troop(seeingUnit, 10);
		army.addTroop(seeingTroop);
		assertEquals(
				"The army with both troops doesn't have the correct sight range.",
				seeingSightRange, army.getSightRange());
		army.removeTroop(emptySeeingTroop);
		assertEquals(
				"The army doesn't have the correct sight range after the removal of the seeing unit.",
				blindSightRange, army.getSightRange());
	}

	public void testRange() throws InvalidPositionException,
			InvalidLevelsArrayException {
		Troop closeTroop = new Troop(closeUnit, 10);
		Vector<Troop> troops = new Vector<Troop>();
		troops.add(closeTroop);
		Army army = Army.buildArmy(troops, new Position(10, 10), side);
		assertEquals(
				"The army with only the close unit doesn't have the right range.",
				closeRange, army.getRange());
		Troop emptyFarTroop = new Troop(farUnit, 0);
		army.addTroop(emptyFarTroop);
		assertEquals(
				"The army with only the close and the empty troop isn't close ranged enough.",
				closeRange, army.getRange());
		Troop farTroop = new Troop(farUnit, 10);
		army.addTroop(farTroop);
		assertEquals(
				"The army with both troops doesn't have the correct range.",
				farRange, army.getRange());
		army.removeTroop(emptyFarTroop);
		assertEquals(
				"The army doesn't have the correct close range after the removal of the far unit.",
				closeRange, army.getRange());
	}

	public void testCopy() throws InvalidPositionException {
		Army army = side.getArmies().firstElement();
		Army clone = army.copy(new Position(10, 10), side);
		assertEquals(
				"The clone army doesn't contain the right number of troops.",
				army.getTroops().size(), clone.getTroops().size());
		assertEquals(
				"The clone army doesn't contain the right number of people.",
				army.getTotal(), clone.getTotal());
		Vector<Troop> troops = army.getTroops(), cloneTroops = clone
				.getTroops();
		for (int i = 0; i < troops.size(); i++) {
			assertEquals(
					"A troop doesn't contain the right unit type in the clone.",
					troops.get(i).getUnit(), cloneTroops.get(i).getUnit());
			assertEquals("A troop doesn't contain the right number of units.",
					troops.get(i).getTotal(), cloneTroops.get(i).getTotal());
			Vector<Hero> heroes = troops.get(i).getHeroes(), cloneHeroes = cloneTroops
					.get(i).getHeroes();
			for (int j = 0; j < heroes.size(); j++) {
				Hero hero = heroes.get(j), cloneHero = cloneHeroes.get(j);
				assertEquals("A troops hero doesn't have the right hitpoints.",
						hero.getHitPoints(), cloneHero.getHitPoints());
				assertEquals("A troops hero doesn't have the right name.", hero
						.getName(), cloneHero.getName());
				assertEquals("A troops hero doesn't have the right type.", hero
						.getType(), cloneHero.getType());
				assertEquals("A troops hero doesn't have the right level.",
						hero.getLevel(), cloneHero.getLevel());
			}
		}
	}

	public void testHasUnit() throws InvalidPositionException,
			InvalidLevelsArrayException {
		Troop closeTroop = new Troop(closeUnit, 10);
		Vector<Troop> troops = new Vector<Troop>();
		troops.add(closeTroop);
		Army army = Army.buildArmy(troops, new Position(10, 10), side);
		assertFalse(
				"The army contains the far unit withouth that it has been added.",
				army.hasUnit(farUnit));
		assertTrue(
				"The army doesn't contain the close unit even though it has been added.",
				army.hasUnit(closeUnit));
		Troop emptyFarTroop = new Troop(farUnit, 0);
		army.addTroop(emptyFarTroop);
		assertFalse(
				"The army contains the far unit even though it has only been added empty.",
				army.hasUnit(farUnit));
		assertTrue(
				"The army doesn't contain the close unit any more even though it has been added.",
				army.hasUnit(closeUnit));
		Troop farTroop = new Troop(farUnit, 10);
		army.addTroop(farTroop);
		assertTrue(
				"The army doesn't contain the far unit even though it has been added.",
				army.hasUnit(farUnit));
		assertTrue(
				"The army doesn't contain the close unit any more even though it has been added.",
				army.hasUnit(closeUnit));
		army.removeTroop(emptyFarTroop);
		assertFalse(
				"The army contains the far unit even though it has been removed.",
				army.hasUnit(farUnit));
		assertTrue(
				"The army doesn't contain the close unit any more even though it hasn't been removed.",
				army.hasUnit(closeUnit));
		Hero hero = new Hero(blindUnit, 2);
		Troop heroTroop = new Troop(seeingUnit, 3);
		heroTroop.addHero(hero);
		army.addTroop(heroTroop);
		assertTrue("The army doesn't contain the hero's type.", army
				.hasUnit(blindUnit));
		assertTrue("The army doesn't count the hero's troop.", army
				.hasUnit(seeingUnit));
		while (hero.isAlive())
			hero.wound();
		assertFalse("The army counts dead heroes.", army.hasUnit(blindUnit));
		assertTrue("The army doesn't count the dead hero's troop.", army
				.hasUnit(seeingUnit));
	}
	
	public void testHasWorkers() throws InvalidPositionException, InvalidLevelsArrayException {
		assertTrue("The starting army has no workers.", side.getArmies().firstElement().hasWorkers());
		Troop closeTroop = new Troop(closeUnit, 10);
		Vector<Troop> troops = new Vector<Troop>();
		troops.add(closeTroop);
		Army army = Army.buildArmy(troops, new Position(10, 10), side);
		assertFalse(
				"The army with only the close unit has workers.",
				army.hasWorkers());
		Troop emptyWorkers = new Troop(worker, 0);
		army.addTroop(emptyWorkers);
		assertFalse(
				"The army with only the close and the empty troop has workers.",
				army.hasWorkers());
		Troop workers = new Troop(worker, 10);
		army.addTroop(workers);
		assertTrue(
				"The army with both troops has no workers.",
				army.hasWorkers());
		army.removeTroop(emptyWorkers);
		assertFalse(
				"The army still has workers after their removal.",
				army.hasWorkers());
		Hero hero = new Hero(worker, 2);
		Troop heroTroop = new Troop(seeingUnit, 3);
		heroTroop.addHero(hero);
		army.addTroop(heroTroop);
		assertTrue("The hero worker doesn't count.", army
				.hasWorkers());
		while (hero.isAlive())
			hero.wound();
		assertFalse("The army counts dead heroes as workers.", army.hasWorkers());
	}
}
