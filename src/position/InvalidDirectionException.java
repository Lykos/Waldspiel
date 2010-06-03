package position;



public class InvalidDirectionException extends PositionException {
	public static final long serialVersionUID=1L;
	public InvalidDirectionException(int direction) {
		super ("The direction " + direction + " does not exist.");
	}
}
