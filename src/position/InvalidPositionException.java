package position;



public class InvalidPositionException extends PositionException {
	public static final long serialVersionUID=1L;
	
	public InvalidPositionException(Position position) {
		super("The position " + position + " is not in the field.\nx has to be between 0 and " + Position.XMAX + "y has to bebetween 0 and " + Position.YMAX + ".");
	}
}
