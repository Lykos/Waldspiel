package position;

import game.ForestException;



public class PositionException extends ForestException {
	public static final long serialVersionUID=1L;
	
	public PositionException(Position position) {
		super("Something is wrong with the position " + position.toString());
	}
	
	public PositionException(String text) {
		super(text);
	}
}
