package position;

import game.Building;
import game.Game;
import game.Side;

import java.io.Serializable;
import java.util.Observable;

import army.Army;

/**
 * This is the object that stores which squares a side can see. Squares can
 * either be seen by buildings or by units.
 * 
 * @author bernhard
 * 
 */
public class ForestMap extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean[][] buildingSight, armySight;

	private Side owner;

	/**
	 * Constructs a new map, which doesn't see anything for the first moment.
	 * 
	 * @param owner
	 *            the player whose side is controlled by this object
	 */
	public ForestMap(Side owner) {
		this.owner = owner;
		buildingSight = new boolean[Position.XMAX + 1][Position.YMAX + 1];
		armySight = new boolean[Position.XMAX + 1][Position.YMAX + 1];
		for (int x = 0; x <= Position.XMAX; x++) {
			for (int y = 0; y <= Position.YMAX; y++) {
				armySight[x][y] = false;
				buildingSight[x][y] = false;
			}
		}
	}

	/**
	 * If an army does not see anything any more for any reason, this method
	 * should be called. It tests, if the squares, which were seen by the army
	 * before, are still seen.
	 * 
	 * @param position
	 *            the position of the removed army
	 * @param sightRange
	 *            the sightRange of that army
	 */
	public void removeArmy(Position position, int sightRange) {
		int[][] oldArmySight = position.getSurrounding(sightRange);
		int length = oldArmySight[0].length;
		int x, y, i;
		for (i = 0; i < length; i++) {
			x = oldArmySight[0][i];
			y = oldArmySight[1][i];
			armySight[x][y] = false;
		}
		for (Army army1 : owner.getArmies()) {
			int sightColision = sightRange + army1.getSightRange() + 1;
			if (position.stepsTo(army1.getPosition()) <= sightColision) {
				int sight = army1.getSightRange();
				Position position1 = army1.getPosition();
				for (i = 0; i < length; i++) {
					x = oldArmySight[0][i];
					y = oldArmySight[1][i];
					armySight[x][y] = position1.stepsTo(x, y) <= sight;
				}
			}
		}
		notifyObservers(oldArmySight);
	}

	/**
	 * set the sight of a new building to visible
	 * 
	 * @param position
	 *            the position where the new building lies
	 * @param sightRange
	 *            the sightrange of the new building
	 */
	public void addBuilding(Position position, int sightRange) {
		int[][] newBuildingSight = position.getSurrounding(sightRange);
		int length = newBuildingSight[0].length;
		int x, y, i;
		for (i = 0; i < length; i++) {
			x = newBuildingSight[0][i];
			y = newBuildingSight[1][i];
			buildingSight[x][y] = true;
		}
		notifyObservers(newBuildingSight);
	}

	/**
	 * set the sight of a new army to visible
	 * 
	 * @param position
	 *            the position of the army
	 * @param sightRange
	 *            the sightrange of the army
	 */
	public void addArmy(Position position, int sightRange) {
		int[][] newArmySight = position.getSurrounding(sightRange);
		int length = newArmySight[0].length;
		int x, y, i;
		for (i = 0; i < length; i++) {
			x = newArmySight[0][i];
			y = newArmySight[1][i];
			armySight[x][y] = true;
		}
		notifyObservers(newArmySight);
	}

	/**
	 * If a building does not see anything any more for any reason, this method
	 * should be called. It tests, if the squares, which were seen by the
	 * building before, are still seen.
	 * 
	 * @param position
	 *            the position of the removed building
	 * @param sightRange
	 *            the sightRange of that building
	 */
	public void removeBuilding(Position position, int sightRange) {
		int[][] oldBuildingSight = position.getSurrounding(sightRange);
		int length = oldBuildingSight[0].length;
		int x, y, i;
		for (i = 0; i < length; i++) {
			x = oldBuildingSight[0][i];
			y = oldBuildingSight[1][i];
			buildingSight[x][y] = false;
		}
		for (Building building1 : owner.getBuildings()) {
			int sightColision = sightRange + building1.getSightRange() + 1;
			if (position.stepsTo(building1.getPosition()) <= sightColision) {
				int sight = building1.getSightRange();
				Position position1 = building1.getPosition();
				for (i = 0; i < length; i++) {
					x = oldBuildingSight[0][i];
					y = oldBuildingSight[1][i];
					buildingSight[x][y] = position1.stepsTo(x, y) <= sight;
				}
			}
		}
		notifyObservers(oldBuildingSight);
	}

	/**
	 * tests if the player sees a given position
	 * 
	 * @param x
	 *            the x-coordinate to be tested
	 * @param y
	 *            the y-coordinate to be tested
	 * @return if this is in seen by the player
	 */
	public boolean sees(int x, int y) {
		return buildingSight[x][y] || armySight[x][y];
	}

	/**
	 * tests if the player sees a given position
	 * 
	 * @param position
	 *            the position to be tested
	 * @return if this is in seen by the player
	 */
	public boolean sees(Position position) {
		return sees(position.getX(), position.getY());
	}

	/**
	 * Tests, if the player sees a given position and if yes, returns some
	 * information about this position. The information contains only rough
	 * information like who has an army or a building on that square. (Not which
	 * building, what kind of army etc.)
	 * 
	 * @param x
	 *            x-coordinate of the position to look at
	 * @param y
	 *            y-coordinate of the position to look at
	 * @return an int with some information about a given square, whose meaning
	 *         can be looked up in class Tree.
	 */
	public String lookAt(int x, int y) {
		if (sees(x, y)) {
			Game game = (Game) Side.game;
			return game.getForest().getPosition(x, y).lookAt(owner);
		} else {
			return "";
		}
	}

	/**
	 * Same as lookAt, but returns an int, so that computers or graphical views
	 * can use it more efficiently without having to get the information out of
	 * a String.
	 * 
	 * @param x
	 *            x-coordinate of the position to look at
	 * @param y
	 *            y-coordinate of the position to look at
	 * @return an int with some information about a given square, whose meaning
	 *         can be looked up in class Tree.
	 */
	public int lookAtInt(int x, int y) {
		if (sees(x, y)) {
			Game game = (Game) Side.game;
			Forest forest = game.getForest();
			Tree tree = forest.getPosition(x, y);
			return tree.lookAtInt(owner);
		} else {
			return Tree.NOTHING;
		}
	}

	/**
	 * Does the same as lookAt(int x, int y), but takes a position as an
	 * argument.
	 * 
	 * @param position
	 *            the position to show
	 * @return a String with rough information about a given position
	 */
	public String lookAt(Position position) {
		return lookAt(position.getX(), position.getY());
	}

	/**
	 * checks if it still sees a given set of points. This is for example used
	 * if an army moves and doesn't see some points any more.
	 * 
	 * @param criticalPoints
	 *            the points to be tested
	 */
	public void checkArmySight(int[][] criticalPoints) {
		int length = criticalPoints[0].length;
		int x, y, i;
		for (i = 0; i < length; i++) {
			x = criticalPoints[0][i];
			y = criticalPoints[1][i];
			armySight[x][y] = false;
		}
		for (Army army1 : owner.getArmies()) {
			int sight = army1.getSightRange();
			Position position1 = army1.getPosition();
			for (i = 0; i < length; i++) {
				x = criticalPoints[0][i];
				y = criticalPoints[1][i];
				armySight[x][y] = position1.stepsTo(x, y) <= sight;
			}
		}
	}
}
