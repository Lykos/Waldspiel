package army;

import game.ForestException;


public class DifferentPositionException extends ForestException {

	/**
	 * This exception is thrown, if two armies are merged
	 * without being at the same position.
	 */
	private static final long serialVersionUID = 1L;
	
	public DifferentPositionException(Army army1, Army army2) {
		super(army1 + " " + army1.getPosition() + " cannot be merged with " + army2 + " " + army2.getPosition() + " because they aren't at the same position.");
	}
}
