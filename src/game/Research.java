package game;

import data.ResearchType;

public class Research {
	private int level;
	private ResearchType type;
	protected Research(ResearchType type) {
		this.level = 1;
		this.type = type;
	}
	
	protected Research(ResearchType type, int level) {
		this.level = level;
		this.type = type;
	}
	
	protected Research copy() {
		return new Research(type, level);
	}
	
	public ResearchType getType() {
		return type;
	}
	
	public boolean levelBigger(int minLevel) {
		return level > minLevel;
	}
	
	public int getLevel() {
		return level;
	}
	
	protected void levelUp() {
		if (level < type.getMaxLevel()) {
			levelUp();
		}
	}
}
