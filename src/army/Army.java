package army;

import game.Building;
import game.ForestException;
import game.Ressources;
import game.Side;

import java.io.Serializable;
import java.util.LinkedList;

import position.Direction;
import position.Forest;
import position.InvalidDirectionException;
import position.InvalidPositionException;
import position.Placeable;
import position.Position;
import position.PositionException;

import data.UnitType;
import data.BuildingType;
import data.SpecialRule;

/**
 * This is the Army class. Most of the game goes through this class, because
 * this is what the player controls.
 * 
 * @author bernhard
 */
public class Army implements Serializable, Placeable {

	public static final long serialVersionUID = 1L;
	public static final int FLEEINGBONUS = 4;

	private static Forest forest;

	/**
	 * Set the forest that is used by all armies.
	 * 
	 * @param newForest
	 */
	public static void setForest(Forest newForest) {
		forest = newForest;
	}

	/**
	 * Make a new army at the given position and put it into the forest.
	 * 
	 * @param troops
	 *            The troops to be contained by this army.
	 * @param position
	 *            The position of this army.
	 * @param owner
	 *            The owner of this army.
	 * @return The new army.
	 */
	public static Army buildArmy(LinkedList<Troop> troops, Position position,
			Side owner) {
		Army army = new Army(troops, position, owner);
		forest.getPosition(position).addArmy(army);
		army.getOwner().addArmy(army);
		return army;
	}

	private Side owner;
	private LinkedList<Troop> troops;
	private Position position, destination;
	private int steps = 0, speed, sightRange, range;
	private boolean wasFighting = false, isFleeing = false, canHunt = false,
			canFormate = false;
	private boolean canBuild = false, canUseMagic = false, canShoot = false,
			canDestroy = false;

	public Army(LinkedList<Troop> troops, Position position, Side owner) {
		this.position = position;
		this.owner = owner;
		this.troops = troops;
		this.destination = position;
		calculateRange();
		calculateSightRange();
		calculateSpeed();
	}

	public int getSightRange() {
		return sightRange;
	}

	public int getRange() {
		return range;
	}

	public int getSpeed() {
		return speed;
	}

	public Army copy(Position position, Side owner) {
		LinkedList<Troop> new_troops = new LinkedList<Troop>();
		for (int i = 0; i < troops.size(); i++)
			new_troops.add(troops.get(i));
		return new Army(new_troops, position, owner);
	}

	public boolean hasUnit(UnitType unit) {
		for (Troop troop : troops)
			if (troop.getUnit() == unit)
				return true;
		return false;
	}

	public boolean hasWorkers() {
		for (Troop troop : troops)
			if (troop.getUnit().hasSpecialRule(SpecialRule.WORKER))
				return true;
		return false;
	}

	public Troop findUnit(UnitType unit) {
		for (Troop troop : troops)
			if (troop.getUnit() == unit)
				return troop;
		return null;
	}

	/**
	 * Set the destination of this army.
	 * 
	 * @param destination
	 *            The new destination.
	 */
	public void setDestination(Position destination) {
		this.destination = destination;
	}

	/**
	 * Let this army disappear from the forest.
	 */
	public void remove() {
		forest.getPosition(position).removeArmy(this);
		owner.removeArmy(this);
	}

	/**
	 * Checks if there are no units left in the army.
	 * 
	 * @return Is the army empty?
	 */
	public boolean isEmpty() {
		return getTotal() == 0;
	}

	/**
	 * Merge with all friends at the same position.
	 */
	public void takeTogether() {
		LinkedList<Army> friends = forest.getPosition(position)
				.getFriends(this);
		for (Army friend : friends) {
			try {
				merge(friend);
			} catch (DifferentPositionException ex) {
				// This should never happen because it's by definition armies in
				// the same position.
				// TODO No! Concurrency!
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * Merge this army with a given other army.
	 * 
	 * @param other
	 *            The other army.
	 * @throws DifferentPositionException
	 *             if the other army is at a different position.
	 */
	public void merge(Army other) throws DifferentPositionException {
		if (other.getPosition() != position)
			throw new DifferentPositionException(this, other);
		try {
			for (Troop troop : other.getTroops())
				addTroop(troop);
			other.remove();
		} catch (InvalidLevelsArrayException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Add a new troop to this army.
	 * 
	 * @param troop
	 *            The new troop.
	 * @throws InvalidLevelsArrayException
	 *             if the format of a troop is wrong.
	 */
	public void addTroop(Troop troop) throws InvalidLevelsArrayException {
		Troop existingTroop = findUnit(troop.getUnit());
		if (existingTroop == null) {
			troops.add(troop);
			if (troop.getMinSpeed() < speed)
				speed = troop.getMinSpeed();
			if (troop.getMaxSightRange() < sightRange)
				sightRange = troop.getMaxSightRange();
			if (troop.getMaxRange() < range)
				range = troop.getMaxRange();
		} else {
			existingTroop.merge(troop);
		}
	}

	/**
	 * Remove a given troop from the army.
	 * 
	 * @param troop
	 *            The troop to be removed.
	 */
	public void removeTroop(Troop troop) {
		troops.remove(troop);
		if (speed == troop.getMinSpeed())
			calculateSpeed();
		if (sightRange == troop.getMaxSightRange())
			calculateRange();
		if (range == troop.getMaxRange())
			calculateSightRange();
	}

	/**
	 * Move into a given direction.
	 * 
	 * @param direction
	 *            The direction where the army should move.
	 * @return No idea what this is.
	 * @throws InvalidDirectionException
	 *             If the direction is not valid.
	 * @throws InvalidPositionException
	 *             If this results in an invalid position.
	 */
	public int[][] move(Direction direction) throws InvalidDirectionException,
			InvalidPositionException {
		// TODO: Improve this!
		if (steps <= 0)
			return new int[2][0];
		steps -= 1;
		if (wasFighting)
			isFleeing = true;
		return forest.move(this, direction);
	}

	/**
	 * Move towards the destination.
	 * 
	 * @throws AlreadyAtDestinationException
	 *             if the army is already there.
	 */
	public void moveToDestination() throws AlreadyAtDestinationException {
		Direction direction = position.toDestination(destination);
		if (direction != Direction.STAY) {
			try {
				int[][] criticalPoints = move(direction);
				owner.getMap().addArmy(position, sightRange);
				owner.getMap().checkArmySight(criticalPoints);
			} catch (PositionException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			throw new AlreadyAtDestinationException();
		}
	}

	/**
	 * Start a new turn ant update the corresponding values.
	 */
	public void newTurn() {
		steps = speed;
		isFleeing = false;
		canBuild = true;
		canShoot = true;
		canHunt = true;
		canFormate = true;
		canUseMagic = true;
		canDestroy = true;
	}

	/**
	 * End a new turn and fight if necessary.
	 */
	public void endTurn() {
		takeTogether();
		LinkedList<Army> enemies = forest.getPosition(position).getEnemies(
				owner);
		if (enemies.size() > 0) {
			fight(enemies);
			wasFighting = true;
		} else {
			wasFighting = false;
		}
	}

	/**
	 * Get the owner of the army.
	 * 
	 * @return The owner of the army.
	 */
	public Side getOwner() {
		return owner;
	}

	/**
	 * Get the troops of this army.
	 * 
	 * @return The troops of this army.
	 */
	public LinkedList<Troop> getTroops() {
		return troops;
	}

	/**
	 * Calculate the speed of this army.
	 */
	private void calculateSpeed() {
		if (troops.size() == 0) {
			speed = 0;
			return;
		}
		speed = troops.getFirst().getMinSpeed();
		for (Troop troop : troops)
			if (troop.getMinSpeed() < speed)
				speed = troop.getMinSpeed();
	}

	/**
	 * Calculate the sight range of this army.
	 */
	private void calculateSightRange() {
		if (troops.size() == 0) {
			sightRange = 0;
			return;
		}
		sightRange = troops.getFirst().getMaxSightRange();
		for (Troop troop : troops)
			if (troop.getMaxSightRange() < sightRange)
				sightRange = troop.getMaxSightRange();
	}

	/**
	 * Calculate the range of ranged attacks of this army.
	 */
	private void calculateRange() {
		if (troops.size() == 0) {
			range = 0;
			return;
		}
		range = troops.getFirst().getMaxRange();
		for (Troop troop : troops)
			if (troop.getMaxRange() < range)
				range = troop.getMaxRange();
	}

	/**
	 * Get the position of this army.
	 * 
	 * @return The position of this army.
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Get the total number of units of this army.
	 * 
	 * @return The total number of units of this army.
	 */
	public int getTotal() {
		int sum = 0;
		for (Troop troop : troops)
			sum += troop.getTotal();
		return sum;
	}

	/**
	 * Can the current army build the given building?
	 * 
	 * @param building
	 *            The building to be build.
	 * @return Is it possible?
	 */
	public boolean canBuild(BuildingType building) {
		boolean positionFree = forest.getPosition(position).buildingAllowed(
				owner);
		boolean ownerCanBuild = owner.canBuild(building);
		System.out.println("position: " + positionFree + "; owner: "
				+ ownerCanBuild + "; canBuild: " + canBuild());
		return positionFree && ownerCanBuild && canBuild();
	}

	/**
	 * Check if the army is currently surrounded by any enemies.
	 * 
	 * @return Is the army left in peace?
	 */
	public boolean noEnemies() {
		return !forest.getPosition(position).hasEnemies(owner);
	}

	/**
	 * Checks if the army able to level up the building here.
	 * 
	 * @return Is it possible?
	 */
	public boolean canLevel() {
		Building building = forest.getPosition(position).getBuilding();
		if (building == null || building.getOwner() != owner)
			return false;
		boolean ownerCanLevel = owner.canLevel(building);
		return ownerCanLevel && canBuild();
	}

	/**
	 * Remove a worker of this army.
	 * 
	 * @throws NoWorkerException
	 *             if there are no workers.
	 */
	public void removeWorker() throws NoWorkerException {
		for (Troop troop : troops) {
			if (troop.getUnit().hasSpecialRule(SpecialRule.WORKER)) {
				if (!troop.removeOne())
					throw (new NoWorkerException(this));
				if (troop.getTotal() == 0)
					troops.remove(troop);
				return;
			}
		}
		throw (new NoWorkerException(this));
	}

	/**
	 * Build the given building.
	 * 
	 * @param building
	 *            The building to be build.
	 * @throws InvalidBuildException
	 *             If the army can't build this.
	 */
	public void build(BuildingType building) throws InvalidBuildException {
		if (!canBuild(building)) {
			throw new InvalidBuildException();
		}
		canBuild = false;
		canFormate = false;
		steps = 0;
		try {
			if (building.needsWorker())
				removeWorker();
			Building.build(building, position, owner);
		} catch (ForestException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Level up the building here.
	 * 
	 * @throws InvalidLevelException
	 *             If the army is not able to level this building.
	 * 
	 */
	public void levelBuilding() throws InvalidLevelException {
		if (!canLevel()) {
			throw new InvalidLevelException();
		}
		canBuild = false;
		canFormate = false;
		steps = 0;
		Building building = forest.getPosition(position).getBuilding();
		Ressources cost = building.getLevelUpCost();
		try {
			owner.pay(cost);
			if (building.needsWorker())
				removeWorker();
			building.levelUp();
		} catch (ForestException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * The number of troops of this army.
	 * 
	 * @return The number of troops.
	 */
	public int numTroops() {
		return troops.size();
	}

	/**
	 * Fight against a given other army.
	 * 
	 * @param enemies
	 *            The other army to fight against.
	 */
	public void fight(LinkedList<Army> enemies) {
		int enemyTroops = 0;
		for (Army enemy : enemies) {
			enemyTroops += enemy.numTroops();
		}
		for (Troop troop : troops) {
			int randomIndex = (int) (Math.random() * enemyTroops);
			for (Army enemy : enemies) {
				int troopsNumber = enemy.numTroops();
				if (randomIndex < troopsNumber) {
					Troop enemyTroop = enemy.getTroops().get(randomIndex);
					int ownBoni = 0, otherBoni = 0;
					if (isFleeing)
						otherBoni += FLEEINGBONUS;
					if (enemy.isFleeing())
						ownBoni += FLEEINGBONUS;
					troop.fight(enemyTroop, ownBoni, otherBoni);
					if (troop.getTotal() <= 0)
						troops.remove(troop);
					if (enemyTroop.getTotal() <= 0)
						enemy.troops.remove(enemyTroop);
					if (getTotal() <= 0)
						remove();
					if (enemy.getTotal() <= 0)
						enemy.remove();
					break;
				}
				randomIndex -= troopsNumber;
			}
		}
	}

	/**
	 * Checks if this army is currently fleeing.
	 * 
	 * @return Is the army fleeing?
	 */
	public boolean isFleeing() {
		return isFleeing;
	}

	/**
	 * Level up the units according to the enemy number.
	 * 
	 * @param enemyNumber
	 *            The number of enemies that were fought.
	 */
	public void levelUp(int enemyNumber) {
		int ownNumber = getTotal();
		if (ownNumber <= 0)
			return;
		if (enemyNumber <= 0)
			enemyNumber = 1;
		int bonus = 0;
		if (ownNumber < enemyNumber)
			bonus -= Math.min(enemyNumber / ownNumber, 6) * 2;
		if (ownNumber > enemyNumber)
			bonus += Math.min(ownNumber / enemyNumber, 6) * 2;
		for (Troop troop : troops)
			troop.levelUp(bonus);
	}

	/**
	 * The steps left in this turn for this army.
	 * 
	 * @return The steps left.
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Split this army.
	 * 
	 * @return
	 */
	public Army split() {
		if (!canFormate())
			return null;
		canFormate = false;
		canHunt = false;
		canBuild = false;
		canShoot = false;
		canDestroy = false;
		steps = 0;
		// TODO: Implement this.
		return null;
	}

	/**
	 * Repair the building here.
	 * 
	 * @throws InvalidRepairException
	 *             if the army actually isn't allow to repair right now.
	 */
	public void repair() throws InvalidRepairException {
		if (!canBuild())
			throw new InvalidRepairException();
		canBuild = false;
		canFormate = false;
		steps = 0;
		forest.getPosition(position).getBuilding().repair();
	}

	/**
	 * Use magic.
	 * 
	 * @return Did it succeed?
	 * @throws InvalidMagicException
	 *             if the army isn't allowed to use magic right now.
	 */
	public void useMagic() throws InvalidMagicException {
		if (!canUseMagic())
			throw new InvalidMagicException();
		canUseMagic = false;
		// TODO: Implement this.
	}

	/**
	 * Hunt food.
	 * 
	 * @return Did it succeed?
	 * @throws InvalidHuntException
	 *             if the army isn't allowed to hunt right now.
	 */
	public void hunt() throws InvalidHuntException {
		if (!canHunt())
			throw new InvalidHuntException();
		canHunt = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
	}

	/**
	 * Checks if this army is able to build.
	 * 
	 * @return Is it possible?
	 */
	public boolean canBuild() {
		return canBuild && noEnemies() && steps > 0 && hasWorkers();
	}

	/**
	 * Checks if this army is able to repair the building here.
	 * 
	 * @return Is it possible?
	 */
	public boolean canRepair() {
		boolean hasRepairableBuilding = forest.getPosition(position)
				.hasRepairableBuilding(owner);
		return canBuild() && hasRepairableBuilding;
	}

	/**
	 * Checks if this army is able to format.
	 * 
	 * @return Is it possible?
	 */
	public boolean canFormate() {
		return canFormate && noEnemies() && steps > 0;
	}

	/**
	 * Checks if this army is able to hunt.
	 * 
	 * @return Is it possible?
	 */
	public boolean canHunt() {
		return canHunt && noEnemies() && steps > 0;
	}

	/**
	 * Checks if this army is able to use magic now.
	 * 
	 * @return Is it possible?
	 */
	public boolean canUseMagic() {
		return canUseMagic;
	}

	/**
	 * Checks if the army is able to shoot.
	 * 
	 * @return Is it possible?
	 */
	public boolean canShoot() {
		return canShoot && noEnemies() && steps > 0;
	}

	/**
	 * Let all ranged units shoot.
	 * 
	 * @return Did it succeed?
	 * @throws InvalidShootException if the army actually isn't allowed to shoot right now.
	 */
	public void shoot() throws InvalidShootException {
		if (!canShoot())
			throw new InvalidShootException();
		canShoot = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
	}

	/**
	 * Destroy the enemy building here.
	 * 
	 * @return Did it succeed?
	 * @throws InvalidDestroyException
	 *             if the army isn't actually allowed to destroy.
	 */
	public boolean destroy() throws InvalidDestroyException {
		if (!canDestroy())
			throw new InvalidDestroyException();
		canDestroy = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
		return true;
	}

	/**
	 * Is this army able to destroy the a building here?
	 * 
	 * @return Is it possible?
	 */
	private boolean canDestroy() {
		return canDestroy && noEnemies() && steps > 0;
	}

	/**
	 * Spy out the surroundings.
	 * 
	 * @param x
	 *            The x coordinate to spy on.
	 * @param y
	 *            The y coordinate to spy on.
	 * @return The informations collected.
	 */
	public String spy(int x, int y) {
		// TODO: Don't return a string.
		try {
			Position spyPosition = new Position(x, y);
			if (getSpyRange() >= position.stepsTo(spyPosition))
				return forest.getPosition(spyPosition).getSpyed();
		} catch (PositionException ex) {
		}
		return "";
	}

	/**
	 * The range of the spying abilities of this army.
	 * 
	 * @return The spy range.
	 */
	public int getSpyRange() {
		if (troops.size() == 0) {
			return -1;
		}
		int spyRange = troops.getFirst().getMaxSpyRange();
		for (Troop troop : troops)
			if (troop.getMaxSightRange() < sightRange)
				spyRange = troop.getMaxSpyRange();
		return spyRange;
	}

	/**
	 * Returns a string representations of the army meant for the players, which
	 * contains the army's non-hero average level, the numbers of each troop and
	 * the heros.
	 */
	@Override
	public String toString() {

		String string = new String();
		for (Troop troop : troops)
			string += troop.toString() + "\n";
		int average = averageLevel() + 1;
		return string + "Average level: " + average;
	}

	/**
	 * This methods calculates the average level of the army without counting
	 * the heros.
	 */
	private int averageLevel() {
		int sum = 0, levelSum = 0;
		for (Troop troop : troops) {
			sum += troop.getNHTotal();
			levelSum += troop.getNHLevelSum();
		}
		return levelSum / sum;
	}

	/**
	 * Checks if the army has reached her destination.
	 * 
	 * @return Is the destination reached?
	 */
	public boolean atDestination() {
		return position.getX() == destination.getX()
				&& position.getY() == destination.getY();
	}
}
