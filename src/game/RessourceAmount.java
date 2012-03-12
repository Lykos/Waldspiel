package game;

import data.RessourceType;


public class RessourceAmount {
	public static final long serialVersionUID=1L;
	
	private RessourceType type;
	private int amount;
	
	public RessourceAmount(RessourceType type, int amount) {
		this.type = type;
		this.amount = amount;
	}
	
	protected RessourceAmount(RessourceAmount ressource) {
		this.type = ressource.getType();
		this.amount = ressource.getAmount();
	}
	
	private RessourceType getType() {
		return type;
	}

	public RessourceAmount copy() {
		return new RessourceAmount(type, getAmount());
	}
		
	protected RessourceAmount add(RessourceAmount other) throws RessourceIncompatibleException {
		if (other.getType() == type)
			throw (new RessourceIncompatibleException(this, other));
		amount += other.getAmount();
		return this;
	}
	
	protected RessourceAmount sub(RessourceAmount other) throws RessourceIncompatibleException, NegativeRessourceException {
		if (other.getType() == type)
			throw (new RessourceIncompatibleException(this, other));
		if (!enough(other))
			throw (new NegativeRessourceException(this, other));
		amount -= other.getAmount();
		return this;
	}
	
	public boolean enough(RessourceAmount other) throws RessourceIncompatibleException {
		if (other.getType() == type)
			throw (new RessourceIncompatibleException(this, other));
		return amount >= other.getAmount();
	}
	
	public int getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return amount + " " + type;
	}

	public RessourceAmount mult(int factor) {
		return new RessourceAmount(type, getAmount() * factor);
	}
	
	public RessourceAmount mult(double factor) {
		return new RessourceAmount(type, (int) (getAmount() * factor));
	}
}
