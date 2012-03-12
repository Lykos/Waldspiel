package game;


public class IsAlreadyBuildException extends ForestException {
	/**
	 * This Exception is thrown, if someone tries to build
	 * something on a tree, where already stands a building.
	 */
	private static final long serialVersionUID = 1L;
	
	public IsAlreadyBuildException(Building building, Building buildingHere) {
		super("Someone has tried to build a " + building.getType().getName() + " on position " + building.getPosition() + " but here is already a " + buildingHere.getType().getName() + ".");
	}
}
