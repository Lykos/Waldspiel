package humanPlayer;

import javax.swing.JFrame;

import game.Building;
import game.Ressources;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.event.ListSelectionListener;

import position.ForestMap;
import army.Army;

public class SecondView extends JFrame implements View, Serializable {

	@Override
	public void addBuildListener(ActionListener buildListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBuildingTypeCancelListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBuildingTypeListSelectionListener(
			ListSelectionListener listSelectionListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBuildingTypeOkListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEndTurnListener(ActionListener endTurnListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGridListener(MouseListener gridListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHuntListener(ActionListener huntListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addLevelUpListener(ActionListener levelUpListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecruitCancelListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecruitListSelectionListener(
			ListSelectionListener listSelectionListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecruitListener(ActionListener recruitListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecruitOkListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResearchListener(ActionListener researchListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResearchTypeCancelListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResearchTypeListSelectionListener(
			ListSelectionListener listSelectionListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResearchTypeOkListener(ActionListener okListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addShootListener(ActionListener shootListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSplitListener(ActionListener splitListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSpyListener(ActionListener spyListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUseMagicListener(ActionListener useMagicListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableArmy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableBuilding() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableArmy(Army army) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableBuilding(Building building) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTurn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void findBuildingType() {
		// TODO Auto-generated method stub

	}

	@Override
	public void findRecruit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void findResearchType() {
		// TODO Auto-generated method stub

	}

	@Override
	public Selectable getBuildChooser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGridHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGridWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Selectable getRecruitChooser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Selectable getResearchChooser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void newTurn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noBuildingSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noRecruitSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noResearchSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notFindBuildingType() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notFindRecruit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notFindResearchType() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMap(ForestMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRessources(Ressources ressources) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

}
