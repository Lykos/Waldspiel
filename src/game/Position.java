package game;

import java.io.Serializable;

/**
 * this class represents the position of an army/a building
 * and contains certain vectorgeometric methods and some more
 * specific stuff for the forest map.
 * @author bernhard
 */
public class Position implements Serializable {
	public static final long serialVersionUID=0;
	public static int XMAX=100;
	public static int YMAX=100;
	
	public static final int N=0;
	public static final int NE=1;
	public static final int E=2;
	public static final int SE=3;
	public static final int S=4;
	public static final int SW=5;
	public static final int W=6;
	public static final int NW=7;
	public static final int STAY=100;
	private static final int[] directionsDX = {0,1,1,1,0,-1,-1,-1};
	private static final int[] directionsDY = {-1,-1,0,1,1,1,0,-1};
	
	/**
	 * tests if a given x and y coordinate is still in the map
	 * @param x x-coordinate to test
	 * @param y y-coordinate to test
	 * @return if this is in the map.
	 */
	public static boolean inBounds(Position position) {
		return inBounds(position.getX(), position.getY());
	}

	public static boolean inBounds(int x, int y) {
		return x >= 0 && x <= XMAX && y >= 0 && y <= YMAX; 
	}
	
	private int x;
	private int y;
	
	/**
	 * creates a new point with a given x and y-coordinate.
	 * @param x the x-coordinate of the new point
	 * @param y the y-coordinate of the new point
	 * @throws InvalidPositionException if this is not in the map.
	 */
	public Position(int x, int y) throws InvalidPositionException {
		this.x = x;
		this.y = y;
		if (x<0 || x>XMAX || y<0 || y>YMAX)
			throw (new InvalidPositionException(this));
	}
	
	/**
	 * tests if going into a given direction would still be inside the map.
	 * @param direction the direction to test
	 * @return if the reached position would be in the map
	 * @throws InvalidDirectionException if the direction does not exist
	 */
	public boolean validDirection(int direction) throws InvalidDirectionException {
		if (direction < 0 || direction > 7)
			throw (new InvalidDirectionException(direction));
		return inBounds(x + directionsDX[direction], y + directionsDY[direction]);
	}
	
	/**
	 * go to a specified direction
	 * @param direction where to go.
	 * @throws InvalidDirectionException if this direction does not exist.
	 * @throws InvalidPositionException if this would reach a position outside the map.
	 */
	protected void goDirection(int direction) throws InvalidDirectionException, InvalidPositionException {
		if (direction < 0 || direction > 7)
			throw (new InvalidDirectionException(direction));
		x += directionsDX[direction];
		y += directionsDY[direction];
		if (!inBounds(this))
			throw (new InvalidPositionException(this));
	}
	
		/**
	 * If an army moves, there are some squares which are not seen any more
	 * by this army afterwards. This method calculates those points. This is
	 * a very critical method which has to be improved and debugged.
	 * @param direction the direction the army goes to.
	 * @param sightRange the sightrange of the army
	 * @return the coordinates of that triangle in an array of width two,
	 * first the x, then the y-coordinate.
	 * @throws InvalidDirectionException if the direction is not valid.
	 */
	public int[][] getTriangle(int direction, int sightRange) throws InvalidDirectionException {
		// TODO improve this and test it.
		if (direction < 0 || direction > 7)
			throw (new InvalidDirectionException(direction));
		int[][] yRectangle, xRectangle;
		int dx = directionsDX[direction] * -1;
		int dy = directionsDY[direction] * -1;
		int xRectangleY = y + dy * sightRange;
		int yRectangleX = x + dx * sightRange;
		int xLength = 0;
		int yLength = 0;
		int i;
		if (dy != 0 && xRectangleY >= 0 && xRectangleY <= YMAX) {
			// Does the horizontal rectangle exist?
			int startX = Math.min(100,Math.max(0,x-sightRange));
			int endX = Math.min(100,Math.max(0,x+sightRange));
			// Gets at least 0 and at most 100. The strange dx stuff
			// is used to automatically kill an eventual overlapping
			// at the end.
			xLength = Math.abs(startX-endX)+1;
			xRectangle = new int[2][xLength];
			i=0;
			for (int x=startX;x<=endX;x++) {
				xRectangle[0][i] = x;
				xRectangle[1][i++] = xRectangleY;
			}
		} else {
			xRectangle = new int[2][xLength];
		}
		if (dx != 0 && yRectangleX >= 0 && yRectangleX <= XMAX) {
			int startY = Math.max(0,y-sightRange);
			int endY = Math.min(100,y+sightRange);
			yLength = Math.abs(endY-startY)+1;
			yRectangle = new int[2][yLength];
			i=0;
			for (int y=startY;y<=endY;y++) {
				yRectangle[0][i] = yRectangleX;
				yRectangle[1][i++] = y;
			}
		} else {
			yRectangle = new int[2][yLength];
		}
		boolean overlap = xLength*yLength != 0;
		int[][] triangle = new int[2][xLength+yLength-(overlap?1:0)];
		for (i=0;i<xLength;i++) {
			triangle[0][i] = xRectangle[0][i];
			triangle[1][i] = xRectangle[1][i];
		}
		int startI = xLength;
		int endI = xLength+yLength;
		if (overlap)
			endI--;
		int foo = xLength;
		if (overlap && dy<0)
			foo--;
		for (i=startI;i<endI;i++) {
			triangle[0][i] = yRectangle[0][i-foo];
			triangle[1][i] = yRectangle[1][i-foo];
		}
		return triangle;
	}
	
	/**
	 * Goes into a direction and returns the squares which are not seen any more
	 * after the movement with a given sightrange.
	 * @param direction where to go
	 * @param sightRange how far this army sees
	 * @return an array with a width of 2 with first the x-coordinates then the
	 * y-coordinates of the points which are not seen any more. 
	 * @throws InvalidDirectionException if the direction is not valid.
	 * @throws InvalidPositionException if this would reach a point outside the map.
	 */
	public int[][] goDirection(int direction, int sightRange) throws InvalidDirectionException, InvalidPositionException {
		int[][] triangle = getTriangle(direction, sightRange);
		goDirection(direction);
		return triangle;
	}
	
	/**
	 * calculates, into which direction one has to go from this point
	 * to reach another point.
	 * @param destination the point you want to reach.
	 * @return the direction.
	 */
	public int toDestination(Position destination) { // Bad code
		int[] candidates = new int[3];
		if (destination.getX() > x) {
			int[] tmp = {SE,E,NE};
			System.arraycopy(tmp,0,candidates,0,3);
		} else if (destination.getX() == x) {
			int[] tmp = {S,STAY,N};
			System.arraycopy(tmp,0,candidates,0,3);
		} else {
			int[] tmp = {SW,W,NW};
			System.arraycopy(tmp,0,candidates,0,3);
		}
		if (destination.getY() > y) {
			return candidates[0];
		} else if (destination.getY() == y) {
			return candidates[1];
		} else {
			return candidates[2];
		}
	}
	
	/**
	 * getter for the x-coordinate
	 * @return the x-coordinate of the point
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * getter for the y-coordinate
	 * @return the y-coordinate of the point
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * calculates the distance between two points.
	 * @param destination the other point
	 * @return integer value of the distance
	 */
	protected int stepsTo(Position destination) {
		return stepsTo(destination.getX(), destination.getY());
	}
	
	/**
	 * calculates the distance between two points.
	 * @param x x-coordinate of the other point
	 * @param y y-coordinate of the other point.
	 * @return integer value of the distance.
	 */
	public int stepsTo(int x, int y) {
		int xDifference = Math.abs(this.x-x);
		int yDifference = Math.abs(this.y-y);
		return Math.max(xDifference, yDifference);
	}
	
	
	/**
	 * calculates the coordinates of a square around the point.
	 * @param distance maximum distance of the returned points
	 * @return an array of width two with all the coordinates of the
	 * square, first the x and then the y coordinate.
	 */
	public int[][] getSurrounding(int distance) {
		int startX = Math.max(0, x-distance), endX = Math.min(x+distance, 100);
		int startY = Math.max(0, y-distance), endY = Math.min(y+distance, 100);
		int area = (endX-startX+1)*(endY-startY+1);
		int[][] result = new int[2][area];
		int x,y,i=0;
		for (x=startX;x<=endX;x++) {
			for (y=startY;y<=endY;y++) {
				result[0][i] = x;
				result[1][i] = y;
				i++;
			}
		}
		return result;
	}
	
	@Override
	/** returns a string representation of the current position.
	 * @return  (x,y)*/
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	/** Returns a random position in the forest, but leaves a border of 10%.*
	 * @return a random Position.*/
	public static Position randomPosition() {
		int x = (int) (Math.random() * (XMAX*0.8) + XMAX*0.1);
		int y = (int) (Math.random() * (YMAX*0.8) + YMAX*0.1);
		try {
			return new Position(x,y);
		} catch (PositionException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
