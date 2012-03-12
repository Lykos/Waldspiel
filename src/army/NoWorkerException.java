package army;

import game.ForestException;


public class NoWorkerException extends ForestException {

	/**
	 * This exception is thrown, if someone tries to take away a worker
	 * from an army which has no workers.
	 */
	private static final long serialVersionUID = 1L;

	public NoWorkerException(Army army) {
		super("Someone tried to remove a worker from the army " + army + ", which has no workers.");
	}

}
