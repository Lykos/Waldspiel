package humanPlayer;

public interface Selectable {

	public abstract int getSelectedIndex();
	
	public abstract Object getSelectedValue();

	public void setData(Object[] newData);
}
