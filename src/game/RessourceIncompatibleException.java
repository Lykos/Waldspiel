package game;

public class RessourceIncompatibleException extends RessourceException {
	public static final long serialVersionUID=1L;
	
	public RessourceIncompatibleException(RessourceAmount ressource1, RessourceAmount ressource2) {
		super ("The Ressource " + ressource1 + " can't be subtracted/added to " + ressource2);
	}
}
