package humanPlayer;

import game.Building;
import game.Ressources;

import java.awt.event.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

import position.ForestMap;
import army.Army;

public class DefaultView extends JFrame implements View, Serializable {

	/**
	 * This is the default view, most likely, there will never be any other, but
	 * you never know.
	 */
	private static final long serialVersionUID = 1L;
	private JButton endTurn, build, split, shoot, spy, useMagic, levelUp, hunt,
			research, recruit;
	private FPanel controls, armyControls, buildingControls;
	private Grid grid;
	private RessourcesPanel ressourcesPanel;
	private JScrollPane scrollPane;
	private JTextArea armyInfo, buildingInfo;
	private ArrayChooser researchChooser, buildingChooser, recruitmentChooser;

	public DefaultView() {
		setMinimumSize(new Dimension(700, 500));
		Container cont = this.getContentPane();
		cont.setLayout(new BorderLayout());

		researchChooser = new ArrayChooser(this, "Research");
		recruitmentChooser = new ArrayChooser(this, "Recruit");
		buildingChooser = new ArrayChooser(this, "Build");
		
		buildingInfo = new JTextArea();
		research = new JButton("Research");
		recruit = new JButton("Recruit");

		armyInfo = new JTextArea();
		build = new JButton("Build");
		split = new JButton("Split");
		shoot = new JButton("Shoot");
		spy = new JButton("Spy");
		useMagic = new JButton("Use Magic");
		levelUp = new JButton("Extend Building");
		hunt = new JButton("Hunt");

		endTurn = new JButton("End turn");
		endTurn.setEnabled(false);

		buildingControls = new FPanel();
		buildingControls.setLayout(new BoxLayout(buildingControls,
				BoxLayout.Y_AXIS));
		buildingControls.add(research);
		buildingControls.add(recruit);

		armyControls = new FPanel();
		armyControls.setLayout(new BoxLayout(armyControls, BoxLayout.Y_AXIS));
		armyControls.add(armyInfo);
		armyControls.add(build);
		armyControls.add(split);
		armyControls.add(shoot);
		armyControls.add(levelUp);
		armyControls.add(useMagic);
		armyControls.add(spy);
		armyControls.add(hunt);

		controls = new FPanel();
		controls.setLayout(new BorderLayout());
		controls.add(endTurn, BorderLayout.SOUTH);
		cont.add(controls, BorderLayout.EAST);

		grid = new Grid();
		cont.add(grid, BorderLayout.CENTER);

		ressourcesPanel = new RessourcesPanel();
		cont.add(ressourcesPanel, BorderLayout.NORTH);
	}

	private class RessourcesPanel extends JLabel implements Observer {
		private static final long serialVersionUID = 1L;

		private Ressources ressources;

		private void setRessources(Ressources ressources) {
			this.ressources = ressources;
			ressources.addObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			setText(ressources.toString());
		}
	}

	@Override
	public void addEndTurnListener(ActionListener endTurnListener) {
		endTurn.addActionListener(endTurnListener);
	}

	@Override
	public void setMap(ForestMap map) {
		grid.setMap(map);
		scrollPane = new JScrollPane(grid,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void setName(String name) {
		setTitle(name);
	}

	@Override
	public void endTurn() {
		endTurn.setEnabled(false);
	}

	@Override
	public void newTurn() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					endTurn.setEnabled(true);
					repaint();
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void addGridListener(MouseListener gridListener) {
		grid.addMouseListener(gridListener);
	}

	@Override
	public void addBuildListener(ActionListener buildListener) {
		build.addActionListener(buildListener);
	}

	@Override
	public void addHuntListener(ActionListener huntListener) {
		hunt.addActionListener(huntListener);
	}

	@Override
	public void addLevelUpListener(ActionListener levelUpListener) {
		levelUp.addActionListener(levelUpListener);
	}

	@Override
	public void addRecruitListener(ActionListener recruitListener) {
		recruit.addActionListener(recruitListener);
	}

	@Override
	public void addResearchListener(ActionListener researchListener) {
		research.addActionListener(researchListener);
	}

	@Override
	public void addShootListener(ActionListener shootListener) {
		shoot.addActionListener(shootListener);
	}

	@Override
	public void addSplitListener(ActionListener splitListener) {
		split.addActionListener(splitListener);
	}

	@Override
	public void addUseMagicListener(ActionListener useMagicListener) {
		useMagic.addActionListener(useMagicListener);
	}

	@Override
	public void enableArmy(Army army) {
		armyInfo.setText(army.toString());
		if (!controls.containsComponent(armyControls))
			controls.add(armyControls, BorderLayout.CENTER);
	}

	@Override
	public void enableBuilding(Building building) {
		buildingInfo.setText(building.toString());
		if (!controls.containsComponent(buildingControls))
			controls.add(buildingControls, BorderLayout.NORTH);
	}

	@Override
	public void disableArmy() {
		controls.remove(armyControls);
	}

	@Override
	public void disableBuilding() {
		controls.remove(buildingControls);
	}

	@Override
	public void addSpyListener(ActionListener spyListener) {
		spy.addActionListener(spyListener);
	}

	@Override
	public void setRessources(Ressources ressources) {
		ressourcesPanel.setRessources(ressources);
		getContentPane().add(ressourcesPanel, BorderLayout.NORTH);
	}

	@Override
	public int getGridHeight() {
		return grid.getHeight();
	}

	@Override
	public int getGridWidth() {
		return grid.getWidth();
	}

	@Override
	public void addBuildingTypeCancelListener(ActionListener cancelListener) {
		buildingChooser.addCancelListener(cancelListener);
	}

	@Override
	public void addBuildingTypeListSelectionListener(
			ListSelectionListener listSelectionListener) {
		buildingChooser.addSelectionListener(listSelectionListener);
	}

	@Override
	public void addBuildingTypeOkListener(ActionListener okListener) {
		buildingChooser.addOkListener(okListener);
	}

	@Override
	public void addRecruitCancelListener(ActionListener cancelListener) {
		recruitmentChooser.addCancelListener(cancelListener);
	}

	@Override
	public void addRecruitListSelectionListener(
			ListSelectionListener listSelectionListener) {
		recruitmentChooser.addSelectionListener(listSelectionListener);
	}

	@Override
	public void addRecruitOkListener(ActionListener okListener) {
		recruitmentChooser.addOkListener(okListener);
	}

	@Override
	public void addResearchTypeCancelListener(ActionListener cancelListener) {
		researchChooser.addCancelListener(cancelListener);
	}

	@Override
	public void addResearchTypeListSelectionListener(
			ListSelectionListener listSelectionListener) {
		researchChooser.addSelectionListener(listSelectionListener);
	}

	@Override
	public void addResearchTypeOkListener(ActionListener okListener) {
		researchChooser.addOkListener(okListener);
	}

	@Override
	public void findBuildingType() {
		buildingChooser.setVisible(true);
	}

	@Override
	public void findRecruit() {
		recruitmentChooser.setVisible(true);
	}

	@Override
	public void findResearchType() {
		researchChooser.setVisible(true);
	}

	@Override
	public Selectable getBuildChooser() {
		return buildingChooser;
	}

	@Override
	public Selectable getRecruitChooser() {
		return recruitmentChooser;
	}

	@Override
	public Selectable getResearchChooser() {
		return researchChooser;
	}

	@Override
	public void noBuildingSelected() {
		buildingChooser.nothingSelected("No building selected.");
	}

	@Override
	public void noRecruitSelected() {
		recruitmentChooser.nothingSelected("No unit selected.");
	}

	@Override
	public void noResearchSelected() {
		researchChooser.nothingSelected("No research goal selected.");
	}

	@Override
	public void notFindBuildingType() {
		buildingChooser.setVisible(false);
	}

	@Override
	public void notFindRecruit() {
		recruitmentChooser.setVisible(false);
	}

	@Override
	public void notFindResearchType() {
		researchChooser.setVisible(false);
	}

	@Override
	public void start() {
		setVisible(true);
	}
}
