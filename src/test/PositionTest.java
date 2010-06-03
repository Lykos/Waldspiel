package test;

import position.InvalidDirectionException;
import position.InvalidPositionException;
import position.Position;
import position.PositionException;
import junit.framework.*;
/**
 * A class to test if the Position class works correctly.
 * @author bernhard
 *
 */
public class PositionTest extends TestCase {
	
	public PositionTest() {
		super("PositionTest");
		try {
			createStandardPositions();
		} catch (PositionException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * tests the constructor of the position class.
	 * @throws InvalidPositionException
	 */
	public void testPosition() throws InvalidPositionException {
		Position position = new Position(32, 34);
		assertEquals("x value is not set correctly.", position.getX(), 32);
		assertEquals("y value is not set correctly.", position.getY(), 34);
	}
	
	private Position positionCenter;
	private Position positionTop;
	private Position positionBottom;
	private Position positionLeft;
	private Position positionRight;
	private Position positionTopRight;
	private Position positionTopLeft;
	private Position positionBottomLeft;
	private Position positionBottomRight;
	
	/**
	 * creates the standard testing positions in all edges and corners.
	 * @throws PositionException
	 */
	private void createStandardPositions() throws PositionException {
		positionCenter = new Position(44,43);
		positionTop = new Position(44,0);
		positionBottom = new Position(44,100);
		positionLeft = new Position(0,43);
		positionRight = new Position(100,43);
		positionTopRight = new Position(100,0);
		positionTopLeft = new Position(0,0);
		positionBottomLeft = new Position(0,100);
		positionBottomRight = new Position(100,100);
	}
	
	public static void assertCoordinatesSame(int[][] expectedArray, int[][] actualArray) {
		assertEquals("length not the same", expectedArray[0].length, actualArray[0].length);
		for (int i=0;i<expectedArray[0].length;i++) {
			assertEquals(i + "th x-coordinate not equal", expectedArray[0][i], actualArray[0][i]);
			assertEquals(i + "th y-coordinate not equal", expectedArray[1][i], actualArray[1][i]);
		}
	}

	/**
	 * tests, if the validPosition methods works correctly and notices,
	 * when the direction would lead outside the map.
	 * @throws PositionException
	 */
	public void testValidDirection() throws PositionException {
		Position testedPosition;
		int direction;
		boolean toldValid;
		for (direction=0;direction<7;direction++) {
			testedPosition = positionCenter;
			assertTrue(direction + " is not considered a valid direction.", positionCenter.validDirection(direction));
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionTop;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.N || direction == Position.NE || direction == Position.NW)
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
			else
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionBottom;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.S || direction == Position.SE || direction == Position.SW)
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
			else
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionLeft;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.W || direction == Position.SW || direction == Position.NW)
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
			else
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionRight;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.E || direction == Position.NE || direction == Position.SE)
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
			else
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionTopRight;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.SW || direction == Position.S || direction == Position.W)
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
			else
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionTopLeft;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.S || direction == Position.SE || direction == Position.E)
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
			else
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionBottomLeft;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.E || direction == Position.N || direction == Position.NE)
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
			else
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
		}
		for (direction=0;direction<7;direction++) {
			testedPosition = positionBottomRight;
			toldValid = testedPosition.validDirection(direction);
			if (direction == Position.W || direction == Position.NW || direction == Position.N)
				assertTrue(direction + " is not considered a valid direction for " + testedPosition + ".", toldValid);
			else
				assertFalse(direction + " is not considered a false direction for " + testedPosition + ".", toldValid);
		}
	}
	
	/**
	 * tests the method inBounds(int x, int y) for some extrema.
	 */
	public void testIntegersInBounds() {
		assertTrue(Position.inBounds(0, 0));
		assertFalse(Position.inBounds(0, -1));
		assertFalse(Position.inBounds(-1, 0));
		assertFalse(Position.inBounds(-1, -1));
		assertTrue(Position.inBounds(0, 100));
		assertFalse(Position.inBounds(0, 101));
		assertFalse(Position.inBounds(-1, 100));
		assertFalse(Position.inBounds(-1, 101));
		assertTrue(Position.inBounds(100, 0));
		assertFalse(Position.inBounds(100, -1));
		assertFalse(Position.inBounds(101, 0));
		assertFalse(Position.inBounds(101, -1));
		assertTrue(Position.inBounds(100, 100));
		assertFalse(Position.inBounds(100, 101));
		assertFalse(Position.inBounds(101, 100));
		assertFalse(Position.inBounds(101, 101));
	}

	/**
	 * tests the method inBounds(Position position) for some extrema.
	 */
	public void testPositionInBounds() {
		assertTrue(Position.inBounds(positionCenter));
		assertTrue(Position.inBounds(positionTop));
		assertTrue(Position.inBounds(positionBottom));
		assertTrue(Position.inBounds(positionLeft));
		assertTrue(Position.inBounds(positionRight));
		assertTrue(Position.inBounds(positionTopLeft));
		assertTrue(Position.inBounds(positionTopRight));
		assertTrue(Position.inBounds(positionBottomLeft));
		assertTrue(Position.inBounds(positionBottomRight));
	}
	
	/**
	 * tests the critical method getTriangle(int direction, int sightRange)
	 * for a central position in two directions.
	 * @throws InvalidDirectionException
	 */
	public void testCenterGetTriangle() throws InvalidDirectionException {
		int[][] expectedTriangle1 = {{42,43,44,45,46},{45,45,45,45,45}}; 
		int[][] actualTriangle = positionCenter.getTriangle(Position.N, 2);
		assertCoordinatesSame(expectedTriangle1, actualTriangle);
		int[][] expectedTriangle2 = {{42,43,44,45,46,42,42,42,42},{45,45,45,45,45,41,42,43,44}}; 
		actualTriangle = positionCenter.getTriangle(Position.NE, 2);
		assertCoordinatesSame(expectedTriangle2, actualTriangle);
	}
	
	/**
	 * tests if the getTriangle(int direction, int sightRange) works
	 * correctly for the corner at the bottom rightmost position.
	 * nw,w,sw,s,se are tested.
	 * @throws PositionException
	 */
	public void testBottomRightGetTriangle() throws PositionException {
		int[][] expectedTriangle1 = {{},{}}; 
		int[][] actualTriangle = positionBottomRight.getTriangle(Position.NW, 2);
		assertCoordinatesSame(expectedTriangle1, actualTriangle);
		int[][] expectedTriangle2 = {{},{}}; 
		actualTriangle = positionBottomRight.getTriangle(Position.W, 2);
		assertCoordinatesSame(expectedTriangle2, actualTriangle);		
		int[][] expectedTriangle3 = {{98,99,100},{98,98,98}}; 
		actualTriangle = positionBottomRight.getTriangle(Position.SW, 2);
		assertCoordinatesSame(expectedTriangle3, actualTriangle);
		int[][] expectedTriangle4 = {{98,99,100},{98,98,98}}; 
		actualTriangle = positionBottomRight.getTriangle(Position.S, 2);
		assertCoordinatesSame(expectedTriangle4, actualTriangle);
		int[][] expectedTriangle5 = {{98,99,100,98,98},{98,98,98,99,100}}; 
		actualTriangle = positionBottomRight.getTriangle(Position.SE, 2);
		assertCoordinatesSame(expectedTriangle5, actualTriangle);
	}

	/**
	 * tests if the method getSurrounding(int distance)
	 * works correctly.
	 */
	public void testGetSurrounding() {
		int[][] expectedSquare1 = {
				{42,42,42,42,42,43,43,43,43,43,44,44,44,44,44,45,45,45,45,45,46,46,46,46,46},
				{41,42,43,44,45,41,42,43,44,45,41,42,43,44,45,41,42,43,44,45,41,42,43,44,45}
		};
		int[][] actualSquare = positionCenter.getSurrounding(2);
		assertCoordinatesSame(expectedSquare1, actualSquare);
		int[][] expectedSquare2 = {
				{42,42,42,43,43,43,44,44,44,45,45,45,46,46,46},
				{0,1,2,0,1,2,0,1,2,0,1,2,0,1,2}
		};
		actualSquare = positionTop.getSurrounding(2);
		assertCoordinatesSame(expectedSquare2, actualSquare);
		int[][] expectedSquare3 = {
				{0,0,0,1,1,1,2,2,2},
				{0,1,2,0,1,2,0,1,2}
		};
		actualSquare = positionTopLeft.getSurrounding(2);
		assertCoordinatesSame(expectedSquare3, actualSquare);
		actualSquare = positionCenter.getSurrounding(4);
		int x,y,i = 0;
		for (x=40;x<=48;x++) {
			for (y=39;y<=47;y++) {
				assertEquals(i + "th x-coordinate not equal", x, actualSquare[0][i]);
				assertEquals(i + "th y-coordinate not equal", y, actualSquare[1][i]);
				i++;
			}
		}
	}

	/**
	 * tests the class Position
	 * @param args unused
	 */
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(PositionTest.class);
	}

}
