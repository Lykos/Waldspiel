package army;

import game.ForestException;


public class InvalidLevelsArrayException extends ForestException {
	public static final long serialVersionUID=1L;
	public InvalidLevelsArrayException(int length) {
		super("There are 10 stages, but this stage Array has " + length + " entries.");
	}
}
