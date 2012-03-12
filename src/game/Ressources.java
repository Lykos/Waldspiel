package game;

import java.util.Observable;
import java.util.Vector;

public class Ressources extends Observable {
	public static final long serialVersionUID=1L;
	
	private Vector<RessourceAmount> ressources;
	
	public Ressources(RessourceAmount[] ressources) {
		this.ressources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources)
			this.ressources.add(ressource);
	}
	
	public Ressources(Vector<RessourceAmount> ressources) {
		this.ressources = ressources;
	}
	
	public Ressources(Ressources ressources) {
		this.ressources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources.getRessources()) {
			this.ressources.add(new RessourceAmount(ressource));
		}
	}
	
	public Ressources copy() {
		Vector<RessourceAmount> newRessources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources)
			newRessources.add(ressource.copy());
		return new Ressources(newRessources);
	}
	
	public Vector<RessourceAmount> getRessources() {
		return ressources;
	}
	
	public void add(Ressources other) {
		for (RessourceAmount ressource : other.getRessources())
			add(ressource);
	}
	
	public void add(RessourceAmount ressource) {
		if (ressources.contains(ressource)) {
			try {
				ressources.get(ressources.indexOf(ressource)).add(ressource);
			} catch (RessourceException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			ressources.add(new RessourceAmount(ressource));
		}
		notifyObservers();
	}
			
	public boolean enough(Ressources other) {
		for (RessourceAmount ressource : other.getRessources())
			if (!enough(ressource))
				return false;
		return true;
	}
	
	public boolean enough(RessourceAmount ressource) {
		boolean enough = false;
		if (ressources.contains(ressource)) {
			try {
				enough = ressources.get(ressources.indexOf(ressource)).enough(ressource);
			} catch (RessourceIncompatibleException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
		return enough;
	}
	
	public void sub(Ressources other) throws NegativeRessourceException {
		for (RessourceAmount ressource : other.getRessources())
			sub(ressource);
	}
	
	public void sub(RessourceAmount ressource) throws NegativeRessourceException {
		if (ressources.contains(ressource)) {
			try {
				ressources.get(ressources.indexOf(ressource)).sub(ressource);
			} catch (RessourceIncompatibleException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			throw (new NegativeRessourceException(null, ressource));
		}
		notifyObservers();
	}
	
	public Ressources plus(RessourceAmount additional) {
		Vector<RessourceAmount> newRessources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources)
			newRessources.add(ressource.copy());
		newRessources.add(additional.copy());
		return new Ressources(newRessources);
	}

	public Ressources mult(int factor) {
		Vector<RessourceAmount> newRessources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources)
			newRessources.add(ressource.mult(factor));
		return new Ressources(newRessources);
	}
	
	public Ressources mult(double factor) {
		Vector<RessourceAmount> newRessources = new Vector<RessourceAmount>();
		for (RessourceAmount ressource : ressources)
			newRessources.add(ressource.mult(factor));
		return new Ressources(newRessources);
	}
		
	public boolean empty() {
		for (RessourceAmount ressource : ressources)
			if (ressource.getAmount() > 0)
				return false;
		return true;
	}
	
	public String toString() {
		String string = new String();
		for (RessourceAmount ressource : ressources) {
			if (string.length() > 0)
				string += ",\t";
			string += ressource.toString();
		}
		return string;
	}
}
