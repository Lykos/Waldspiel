package position;

import java.io.Serializable;


import army.Army;

public class Forest implements Serializable {
	public static final long serialVersionUID=1L;
	private Tree[][] field;
	
	public Forest() {
		field = new Tree[Position.XMAX+1][Position.YMAX+1];
		for (int x=0;x<=Position.XMAX;x++)
			for (int y=0;y<=Position.YMAX;y++)
				field[x][y] = new Tree();
	}
	
	public int[][] move(Army army, int direction) throws InvalidDirectionException, InvalidPositionException {
		Position position = army.getPosition();
		getPosition(position).removeArmy(army);
		int[][] result = position.goDirection(direction, army.getSightRange());
		getPosition(position).addArmy(army);
		return result;
	}
	
	public Tree getPosition(Position position) {
		return field[position.getX()][position.getY()];
	}
	
	public Tree getPosition(int x, int y) {
		return field[x][y];
	}
}
