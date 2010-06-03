package position;



public class InvalidDirectionException extends PositionException {
	public static final long serialVersionUID=1L;
	public InvalidDirectionException(Direction direction) {
		super ("The direction " + direction + " is not valid here.");
	}
}
