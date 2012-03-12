package humanPlayer;

import game.Building;
import game.Ressources;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.event.ListSelectionListener;

import position.ForestMap;

import army.Army;

public interface View {

	public abstract void setMap(ForestMap map);
	
	public abstract void setRessources(Ressources ressources);
	
	public abstract void addEndTurnListener(ActionListener endTurnListener);

	public abstract void addGridListener(MouseListener gridListener);
	
	public abstract void enableArmy(Army army);
	
	public abstract void disableArmy();
	
	public abstract void addBuildListener(ActionListener buildListener);
		
	public abstract void addSplitListener(ActionListener splitListener);

	public abstract void addLevelUpListener(ActionListener levelUpListener);

	public abstract void addHuntListener(ActionListener huntListener);

	public abstract void addShootListener(ActionListener shootListener);

	public abstract void addUseMagicListener(ActionListener useMagicListener);

	public abstract void addSpyListener(ActionListener spyListener);

	public abstract void enableBuilding(Building building);
	
	public abstract void disableBuilding();
	
	public abstract void addResearchListener(ActionListener researchListener);

	public abstract void addRecruitListener(ActionListener recruitListener);

	public abstract void newTurn();

	public abstract void endTurn();

	public abstract void setName(String name);

	public abstract int getGridWidth();
	
	public abstract int getGridHeight();
	
	public abstract void findBuildingType();
		
	public abstract Selectable getBuildChooser();

	public abstract void addBuildingTypeListSelectionListener(ListSelectionListener listSelectionListener);
	
	public abstract void addBuildingTypeOkListener(ActionListener okListener);
	
	public abstract void addBuildingTypeCancelListener(ActionListener okListener);
	
	public abstract void noBuildingSelected();

	public abstract void notFindBuildingType();

	public abstract void findResearchType();
	
	public abstract Selectable getResearchChooser();

	public abstract void addResearchTypeListSelectionListener(ListSelectionListener listSelectionListener);
	
	public abstract void addResearchTypeOkListener(ActionListener okListener);

	public abstract void addResearchTypeCancelListener(ActionListener okListener);

	public abstract void noResearchSelected();

	public abstract void notFindResearchType();
	
	public abstract void findRecruit();
	
	public abstract Selectable getRecruitChooser();

	public abstract void addRecruitListSelectionListener(ListSelectionListener listSelectionListener);
	
	public abstract void addRecruitOkListener(ActionListener okListener);

	public abstract void addRecruitCancelListener(ActionListener okListener);
	
	public abstract void noRecruitSelected();

	public abstract void notFindRecruit();

	public abstract void start();

}
