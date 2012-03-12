package humanPlayer;

import java.awt.Component;

import javax.swing.JPanel;

public class FPanel extends JPanel {

	/**
	 * Same as JPanel, but can find out if it contains a specific component.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Finds out, if the FPanel contains given component.
	 * @param comp the component we are searching for.
	 * @return Tells if comp is among the components of the FPanel.
	 */
	public boolean containsComponent(Component comp) {
		for (Component c : getComponents()) {
			if (c == comp) {
				return true;
			}
		}
		return false;
	}
}
