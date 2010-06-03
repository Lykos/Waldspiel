package game;
import java.io.Serializable;


public interface Player extends Serializable {
	public void setSide(Side side);
	public void start();
	public void newTurn();
}
