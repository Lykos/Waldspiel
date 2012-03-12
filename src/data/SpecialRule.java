package data;
import java.io.Serializable;


public class SpecialRule implements Serializable {
	public static final long serialVersionUID=1L;
	public static final int REHIT=1, WORKER=2, REWOUND=3,
	REWOUNDRANGED=4, SPY = 5, KING=6, LEADER=7, PRIEST=8, PRIESTRESS=9,
	HIDE=10, GEOLOGIST=11, HALFPEACEFUL=12, FLY=13, HUNT=14, HEAL=15,
	PEACEFUL=16, ANTIBUILDING=17, SCOUT=18, GUARDIAN=19, TELEPORT=20,
	GETHELP=21, MAGIC=22, BIOLOGIST=23, INVISIBLE=24, BERSERK=25, REHITRANGED=26;
	
	private final String name;
	private final int id;
	
	public SpecialRule(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() {
		return name;
	}
}
