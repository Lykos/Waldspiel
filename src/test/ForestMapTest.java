package test;

import position.ForestMap;
import position.InvalidPositionException;
import position.Position;
import army.Army;
import game.Building;
import game.Game;
import game.Side;
import junit.framework.*;

/**
 * A class to test if the Class ForestMap works correctly.
 * @author bernhard
 *
 */
public class ForestMapTest extends TestCase {
	public ForestMapTest() {
		String name = "bla";
		Side side = null;
		while (!name.equals("fox")) {
			Game game = new Game(2);
			side = game.getSides().getFirst();
			name = side.getPeople().getName();
		}
		fooArmy = side.getArmies().get(0);
		while (side.getArmies().size() > 0) {
			Army army = side.getArmies().get(0);
			side.removeArmy(army);
		}
		while (side.getBuildings().size() > 0) {
			Building building = side.getBuildings().get(0);
			side.removeBuilding(building);
		}
		map = new ForestMap(side);
	}
	
	ForestMap map;
	Army fooArmy;
		
	public void testForestMap() {
		for (int x=0;x<=100;x++)
			for (int y=0;y<=100;y++)
				assertFalse("Sees without buildings or units (" + x + "," + y + ")", map.sees(x, y));
	}
	
	public void testaddRemoveArmy() throws InvalidPositionException {
		Position position = new Position(33,32);
		map.addArmy(position, 4);
		for (int x=0;x<=100;x++) {
			for (int y=0;y<=100;y++) {
				if (position.stepsTo(x, y) <= 4) {
					assertTrue("Doesnt't see: (" + x + "," + y + ")", map.sees(x, y));
				} else {
					assertFalse("Sees what should not be seen: (" + x + "," + y + ")", map.sees(x, y));
				}
			}
		}
		map.removeArmy(position, 3);
		for (int x=0;x<=100;x++) {
			for (int y=0;y<=100;y++) {
				if (position.stepsTo(x, y) == 4) {
					assertTrue("Doesnt't see: (" + x + "," + y + ")", map.sees(x, y));
				} else {
					assertFalse("Sees what should not be seen: (" + x + "," + y + ")", map.sees(x, y));
				}
			}
		}
	}

	/**
	 * tests the class ForestMap and its methods.
	 * @param args unused
	 */
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(PositionTest.class);
	}

}
