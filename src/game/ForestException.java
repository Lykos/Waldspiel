package game;

public class ForestException extends Exception {

	/**
	 * Superclass for all my own exceptions.
	 */
	private static final long serialVersionUID = 1L;
	
	public ForestException(String text) {
		super(text);
	}
	
	public ForestException() {
		super();
	}
}
