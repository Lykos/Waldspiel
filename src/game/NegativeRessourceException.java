package game;

public class NegativeRessourceException extends RessourceException {
	public static final long serialVersionUID=1L;
	
	public NegativeRessourceException (RessourceAmount ressource1, RessourceAmount ressource2) {
		super(ressource1 + " is not enough to pay " + ressource2 + ".");
	}
}
