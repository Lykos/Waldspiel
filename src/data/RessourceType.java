package data;
import java.io.Serializable;

public class RessourceType implements Serializable {
	public static final long serialVersionUID=1L;
	
	private final String name;
	
	public RessourceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
