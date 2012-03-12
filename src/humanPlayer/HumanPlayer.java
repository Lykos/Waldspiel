package humanPlayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import position.InvalidPositionException;
import position.Position;

import army.AlreadyAtDestinationException;
import army.Army;
import army.InvalidBuildException;

import data.BuildingType;

import game.Building;
import game.Player;
import game.Side;

public class HumanPlayer implements Serializable, Player {

	/**
	 * This is the Interface between the player and the game. If you like MVC,
	 * this is the controller, the game is the model and the View is the view.
	 */
	private static final long serialVersionUID = 1L;

	private class EndTurnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// finished = true;
			side.finish();
			view.endTurn();
		}
	}

	private Side side;
	private View view;
	// private volatile boolean finished = false;
	private EndTurnListener endTurnListener;
	private GridListener gridListener;
	private Building activatedBuilding = null;
	private Army activatedArmy = null;
	private BuildListener buildListener;
	private Builder builder;
	private BuildCancelListener buildCancelListener;
	private RecruitListener recruitListener;
	private Recruiter recruiter;
	private RecruitCancelListener recruitCancelListener;
	private ResearchListener researchListener;
	private Researcher researcher;
	private ResearchCancelListener researchCancelListener;
	private Selectable buildChooser, recruitChooser, researchChooser;

	public HumanPlayer() {
		view = new DefaultView();

		// create normal listeners
		endTurnListener = new EndTurnListener();
		gridListener = new GridListener();
		buildListener = new BuildListener();

		// subscribe them to the view.
		view.addEndTurnListener(endTurnListener);
		view.addGridListener(gridListener);
		view.addBuildListener(buildListener);

		// get the objects of the choosers
		buildChooser = view.getBuildChooser();
		recruitChooser = view.getRecruitChooser();
		researchChooser = view.getResearchChooser();

		// create choose command listeners
		buildListener = new BuildListener();
		builder = new Builder();
		buildCancelListener = new BuildCancelListener();
		recruitListener = new RecruitListener();
		recruiter = new Recruiter();
		recruitCancelListener = new RecruitCancelListener();
		researchListener = new ResearchListener();
		researcher = new Researcher();
		researchCancelListener = new ResearchCancelListener();

		// add them to the view
		view.addBuildListener(buildListener);
		view.addBuildingTypeListSelectionListener(builder);
		view.addBuildingTypeOkListener(builder);
		view.addBuildingTypeCancelListener(buildCancelListener);
		view.addRecruitListener(recruitListener);
		view.addRecruitListSelectionListener(recruiter);
		view.addRecruitOkListener(recruiter);
		view.addRecruitCancelListener(recruitCancelListener);
		view.addResearchListener(researchListener);
		view.addResearchTypeListSelectionListener(researcher);
		view.addResearchTypeOkListener(researcher);
		view.addResearchTypeCancelListener(researchCancelListener);
	}

	@Override
	public void setSide(Side side) {
		this.side = side;
		view.setMap(side.getMap());
		view.setRessources(side.getRessources());
		view.setName(side.getName());
	}

	@Override
	public void newTurn() {
		// finished = false;
		view.newTurn();
	}

	private class GridListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			boolean armyFound = false;
			int h = e.getX() / (view.getGridWidth() / (Position.XMAX + 1));
			int v = e.getY() / (view.getGridHeight() / (Position.YMAX + 1));
			if (h >= 0 && h < Position.XMAX && v >= 0 && v <= Position.YMAX) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					for (Army army : side.getArmies()) {
						Position position = army.getPosition();
						int x = position.getX(), y = position.getY();
						if (x == h && y == v) {
							activatedArmy = army;
							view.enableArmy(army);
							armyFound = true;
							break;
						}
					}
					if (!armyFound && activatedArmy != null) {
						try {
							activatedArmy.setDestination(new Position(h, v));
						} catch (InvalidPositionException e1) {
							// TODO Auto-generated catch block. Try to find a
							// way to handle this.
							e1.printStackTrace();
							System.exit(1);
						}
						while (!activatedArmy.atDestination()
								&& activatedArmy.getSteps() > 0) {
							try {
								activatedArmy.moveToDestination();
							} catch (AlreadyAtDestinationException e1) {
								// TODO Auto-generated catch block. Try to find
								// a way to handle this.
								e1.printStackTrace();
								System.exit(1);
							}
						}
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					System.out.println("blahochzwei");
					for (Building building : side.getBuildings()) {
						Position position = building.getPosition();
						int x = position.getX(), y = position.getY();
						if (x == h && y == v) {
							activatedBuilding = building;
							view.enableBuilding(building);
						}
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	} // end class GridListener

	private class BuildListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("blabla");
			if (activatedArmy.canBuild()) {
				System.out.println("blablabla");
				builder.restart();
				
				// debug
				String examples[] = {"Ha", "li"};
				
				buildChooser.setData(examples/*side.getBuildableTypes()*/);
				view.findBuildingType();
			}
		}
	} // end class BuildListener

	private class Builder implements ActionListener, ListSelectionListener {
		private BuildingType building = null;

		private void restart() {
			building = null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (building == null) {
				view.noBuildingSelected();
				return;
			}
			if (activatedArmy.canBuild(building)) {
				try {
					activatedArmy.build(building);
				} catch (InvalidBuildException e1) {
					// TODO Auto-generated catch block. Try to find a way to
					// handle this.
					e1.printStackTrace();
				}
				view.notFindBuildingType();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			building = (BuildingType) buildChooser.getSelectedValue();
		}
	} // end class Builder

	private class BuildCancelListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.notFindBuildingType();
		}
	} // end class BuildCancelListener

	private class RecruitListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (activatedBuilding.canRecruit()) {
				recruiter.restart();
				recruitChooser.setData(activatedBuilding.getRecruitments());
				view.findRecruit();
			}
		}
	} // end class RecruitListener

	private class Recruiter implements ActionListener, ListSelectionListener {
		private Integer recruitmentIndex = null;

		private void restart() {
			recruitmentIndex = null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (recruitmentIndex == null) {
				view.noResearchSelected();
				return;
			}
			if (activatedBuilding.canRecruit()) {
				activatedBuilding.addRecruitment(recruitmentIndex);
				view.notFindRecruit();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			recruitmentIndex = recruitChooser.getSelectedIndex();
		}
	} // end class Recruiter

	private class RecruitCancelListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.notFindRecruit();
		}
	} // end class RecruitCancelListener

	private class ResearchListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (activatedBuilding.canResearch()) {
				researchChooser.setData(activatedBuilding.getResearchs());
				view.findResearchType();
				researcher.restart();
			}
		}
	} // end class ResearchListener

	private class Researcher implements ActionListener, ListSelectionListener {
		private Integer researchIndex = null;

		private void restart() {
			researchIndex = null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (researchIndex == null) {
				view.noResearchSelected();
				return;
			}
			if (activatedBuilding.canResearch()) {
				activatedBuilding.addResearchGoal(researchIndex);
				view.notFindResearchType();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			researchIndex = researchChooser.getSelectedIndex();
		}
	} // end class Researcher

	private class ResearchCancelListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.notFindResearchType();
		}
	} // end class ResearchCancelListener

	@Override
	public void start() {
		view.start();
	}
}
