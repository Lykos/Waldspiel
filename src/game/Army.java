package game;

import java.io.Serializable;
import java.util.LinkedList;

import data.UnitType;
import data.BuildingType;
import data.SpecialRule;

public class Army implements Serializable, Placeable {
	/**
	 * This is the Army class. Most of the game goes through this class, because
	 * this is what the player controls.
	 * **/
	public static final long serialVersionUID = 1L;
	public static final int FLEEINGBONUS = 4;

	private static Forest forest;

	/**
	 * Set the forest that is used by all armies.
	 * 
	 * @param newForest
	 */
	protected static void setForest(Forest newForest) {
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
	protected static Army buildArmy(LinkedList<Troop> troops,
			Position position, Side owner) {
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

	protected Army copy(Position position, Side owner) {
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

	protected boolean setDestination(int x, int y) {
		try {
			destination = new Position(x, y);
		} catch (InvalidPositionException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Let this army disappear from the forest.
	 */
	protected void remove() {
		forest.getPosition(position).removeArmy(this);
		owner.removeArmy(this);
	}

	/**
	 * Checks if there are no units left in the army.
	 * @return Is the army empty? 
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Merge with all friends at the same position.
	 */
	protected void takeTogether() {
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
	protected void merge(Army other) throws DifferentPositionException {
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
	 * @param troop The new troop.
	 * @throws InvalidLevelsArrayException if the format of a troop is wrong.
	 */
	protected void addTroop(Troop troop) throws InvalidLevelsArrayException {
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
	 * @param troop The troop to be removed.
	 */
	protected void removeTroop(Troop troop) {
		troops.remove(troop);
		if (speed == troop.getMinSpeed())
			calculateSpeed();
		if (sightRange == troop.getMaxSightRange())
			calculateRange();
		if (range == troop.getMaxRange())
			calculateSightRange();
	}

	protected int[][] move(int direction) throws InvalidDirectionException,
			InvalidPositionException {
		if (steps <= 0)
			return new int[2][0];
		steps -= 1;
		if (wasFighting)
			isFleeing = true;
		return forest.move(this, direction);
	}

	protected boolean moveToDestination() {
		int direction = position.toDestination(destination);
		if (direction != Position.STAY) {
			try {
				int[][] criticalPoints = move(direction);
				owner.getMap().addArmy(position, sightRange);
				owner.getMap().checkArmySight(criticalPoints);
				return true;
			} catch (PositionException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
		return false;
	}

	protected void newTurn() {
		steps = speed;
		isFleeing = false;
		canBuild = true;
		canShoot = true;
		canHunt = true;
		canFormate = true;
		canUseMagic = true;
		canDestroy = true;
	}

	protected void endTurn() {
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

	public Side getOwner() {
		return owner;
	}

	public LinkedList<Troop> getTroops() {
		return troops;
	}

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

	public Position getPosition() {
		return position;
	}

	public int getTotal() {
		int sum = 0;
		for (Troop troop : troops)
			sum += troop.getTotal();
		return sum;
	}

	public boolean canBuild(BuildingType building) {
		boolean positionFree = forest.getPosition(position).buildingAllowed(
				owner);
		boolean ownerCanBuild = owner.canBuild(building);
		System.out.println("position: " + positionFree + "; owner: "
				+ ownerCanBuild + "; canBuild: " + canBuild());
		return positionFree && ownerCanBuild && canBuild();
	}

	public boolean noEnemies() {
		return !forest.getPosition(position).hasEnemies(owner);
	}

	public boolean canLevel() {
		Building building = forest.getPosition(position).getBuilding();
		if (building == null || building.getOwner() != owner)
			return false;
		boolean ownerCanLevel = owner.canLevel(building);
		return ownerCanLevel && canBuild();
	}

	protected void removeWorker() throws NoWorkerException {
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

	protected boolean build(BuildingType building) {
		if (!canBuild(building)) {
			return false;
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
		return true;
	}

	protected boolean levelBuilding() {
		if (!canLevel()) {
			return false;
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
		return true;
	}

	public int troops() {
		return troops.size();
	}

	protected void fight(LinkedList<Army> enemies) {
		int enemyTroops = 0;
		for (Army enemy : enemies) {
			enemyTroops += enemy.troops();
		}
		for (Troop troop : troops) {
			int randomIndex = (int) (Math.random() * enemyTroops);
			for (Army enemy : enemies) {
				int troopsNumber = enemy.troops();
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

	public boolean isFleeing() {
		return isFleeing;
	}

	protected void levelUp(int enemyNumber) {
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

	public int getSteps() {
		return steps;
	}

	protected Army split() {
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

	protected boolean repair() {
		if (!canBuild())
			return false;
		canBuild = false;
		canFormate = false;
		steps = 0;
		forest.getPosition(position).getBuilding().repair();
		return true;
	}

	protected boolean useMagic() {
		if (!canUseMagic())
			return false;
		canUseMagic = false;
		// TODO: Implement this.
		return true;
	}

	protected boolean hunt() {
		if (!canHunt())
			return false;
		canHunt = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
		return true;
	}

	public boolean canBuild() {
		System.out.println("canBuild: " + canBuild + "; enemies: "
				+ noEnemies() + "; steps: " + steps + "; workers: "
				+ hasWorkers());
		return canBuild && noEnemies() && steps > 0 && hasWorkers();
	}

	public boolean canRepair() {
		boolean hasRepairableBuilding = forest.getPosition(position)
				.hasRepairableBuilding(owner);
		return canBuild() && hasRepairableBuilding;
	}

	public boolean canFormate() {
		return canFormate && noEnemies() && steps > 0;
	}

	public boolean canHunt() {
		return canHunt && noEnemies() && steps > 0;
	}

	public boolean canUseMagic() {
		return canUseMagic;
	}

	public boolean canShoot() {
		return canShoot && noEnemies() && steps > 0;
	}

	protected boolean shoot() {
		if (!canShoot())
			return false;
		canShoot = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
		return true;
	}

	protected boolean destroy() {
		if (!canDestroy())
			return false;
		canDestroy = false;
		canFormate = false;
		steps = 0;
		// TODO: Implement this.
		return true;
	}

	private boolean canDestroy() {
		return canDestroy && noEnemies() && steps > 0;
	}

	public String spy(int x, int y) {
		try {
			Position spyPosition = new Position(x, y);
			if (getSpyRange() >= position.stepsTo(spyPosition))
				return forest.getPosition(spyPosition).getSpyed();
		} catch (PositionException ex) {
		}
		return "";
	}

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

	@Override
	public String toString() {
		/**
		 * Returns a string representations of the army meant for the players,
		 * which contains the army's non-hero average level, the numbers of each
		 * troop and the heros.
		 */
		String string = new String();
		for (Troop troop : troops)
			string += troop.toString() + "\n";
		int average = averageLevel() + 1;
		return string + "Average level: " + average;
	}

	private int averageLevel() {
		/**
		 * This methods calculates the average level of the army without
		 * counting the heros.
		 */
		int sum = 0, levelSum = 0;
		for (Troop troop : troops) {
			sum += troop.getNHTotal();
			levelSum += troop.getNHLevelSum();
		}
		return levelSum / sum;
	}

	public boolean atDestination() {
		return position.getX() == destination.getX()
				&& position.getY() == destination.getY();
	}
}
