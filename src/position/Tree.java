package position;

import game.Building;
import game.IsAlreadyBuildException;
import game.Side;

import java.io.Serializable;
import java.util.LinkedList;

import army.Army;

public class Tree implements Serializable {
	public static final long serialVersionUID=1L;

	public static final int NOTHING = 0, OWNBUILDING=1, BUILDING = 2, ARMY = 3, ENEMY = 4, FIGHT = 5;
	
	private LinkedList<Army> armies = new LinkedList<Army>();
	private Building building=null;
	
	public boolean addArmy(Army army) {
		return armies.add(army);
	}
	
	public void build(Building building) throws IsAlreadyBuildException {
		if (this.building != null) {
			throw (new IsAlreadyBuildException(building, this.building));
		}
		this.building = building;
	}
	
	public Building getBuilding() {
		return building;
	}
	
	public void removeBuilding(Building building) {
		building = null;
	}
	
	public boolean removeArmy(Army army) {
		return armies.remove(army);
	}
	
	public boolean hasEnemies(Side friend) {
		for (Army army : armies) {
			if (army.getOwner() != friend)
				return true;
		}
		return false;		
	}
	
	public boolean hasRepairableBuilding(Side friend) {
		if (building == null)
			return false;
		if (building.getOwner() != friend)
			return false;
		return building.getHitpoints() < building.getMaxHitpoints();
	}
	
	public LinkedList<Army> getEnemies(Side friend) {
		LinkedList<Army> enemies = new LinkedList<Army>();
		for (Army army : armies) {
			if (army.getOwner() != friend)
				enemies.add(army);
		}
		return enemies;
	}

	public LinkedList<Army> getFriends(Army army) {
		LinkedList<Army> friends = new LinkedList<Army>();
		for (Army friend : armies) {
			if (army.getOwner() == army.getOwner() && friend != army)
				friends.add(army);
		}
		return friends;
	}

	public boolean buildingAllowed(Side side) {
		return building != null;
	}

	public String lookAt(Side side) {
		String string = new String();
		if (building != null) {
			if (building.getOwner() == side) {
				string += building.toString();
			} else {
				string += building.lookFromFar() + "\n";
			}
		}
		for (Army army : armies) {
			if (army.getOwner() == side) {
				string += army.toString() + "\n";
			} else {
				string += "Army of " + army.getOwner().getName() + "\n";
			}
		}
		return string;
	}
	
	public int lookAtInt(Side side) {
		Side firstSide = null;
		for (Army army : armies) {
			if (firstSide == null) {
				firstSide = army.getOwner();
			} else if (army.getOwner() != firstSide) {
				return FIGHT;
			}
		}
		if (hasEnemies(side))
			return ENEMY;
		if (armies.size() > 0)
			return ARMY;
		if (building != null) {
			if (building.getOwner() == side) {
				return OWNBUILDING;
			} else {
				return BUILDING;
			}
		}
		return NOTHING;
	}

	public String getSpyed() {
		String string = new String();
		if (building != null)
			string += building.toString() + "\n";
		for (Army army : armies) {
			string += army.toString() + "\n";
		}
		return string;
	}
}
