package humanPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import java.util.Observer;

import position.ForestMap;
import position.Position;

/**
 * @author bernhard
 *
 */
public class Grid extends FPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	private static final int fieldHeight=5, fieldWidth=5;
	
	private ForestMap map;
	
	public void setMap(ForestMap map) {
		this.map = map;
		map.addObserver(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D canvas = (Graphics2D)g;
		setSize(new Dimension(fieldWidth*(Position.XMAX+1),fieldHeight*(Position.YMAX+1)));
		canvas.setColor(Color.lightGray);
		for(int x=0; x <= Position.XMAX+1; x++)
			canvas.drawLine(x*fieldWidth, 0, x*fieldWidth, fieldHeight*(Position.YMAX+1));
		for(int y=0; y <= Position.YMAX+1; y++)
			canvas.drawLine(0, y*fieldHeight, fieldWidth*(Position.XMAX+1), y*fieldHeight);
		Color[] colors = {Color.green, Color.darkGray, Color.black, Color.blue, Color.orange, Color.red}; 
		for(int x = 0; x <= Position.XMAX; x++) {
			for(int y = 0; y <= Position.YMAX; y++) {
				if(map.sees(x, y)) {
					canvas.setColor(colors[map.lookAtInt(x, y)]);
					canvas.fillRect(x*fieldWidth, y*fieldHeight, fieldWidth, fieldHeight);
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}
}
