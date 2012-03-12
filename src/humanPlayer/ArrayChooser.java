package humanPlayer;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

public class ArrayChooser extends JDialog implements Selectable {

	/**
	 * A dialog window to choose recruitments, researchs, buildings to be build etc.
	 */
	private static final long serialVersionUID = 1L;
	
	private JList list;
	private JButton ok, cancel;
	public ArrayChooser(Frame owner, String title) {
		super(owner, title, true);
		initialize(title);
	}

	private void initialize(String title) {
		setLayout(new BorderLayout());
		list = new JList();
		ok = new JButton("Ok");
		cancel = new JButton("Cancel");
		JScrollPane scrollPane = new JScrollPane(list);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(ok, BorderLayout.EAST);
		controls.add(cancel, BorderLayout.WEST);
		getContentPane().add(controls, BorderLayout.SOUTH);		
	}

	public ArrayChooser(Dialog owner, String title) {
		super(owner, title, true);
		initialize(title);
	}
	
	public void setData(Object[] newData) {
		System.out.println("Data set in " + getTitle());
		list.setListData(newData);
		System.out.println("Datalength: " + newData.length);
	}
	
	public void addSelectionListener(ListSelectionListener listener) {
		list.addListSelectionListener(listener);
	}
	
	public void addOkListener(ActionListener listener) {
		ok.addActionListener(listener);
	}
	
	public void addCancelListener(ActionListener listener) {
		cancel.addActionListener(listener);
	}

	@Override
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}

	@Override
	public Object getSelectedValue() {
		return list.getSelectedValue();
	}

	public void nothingSelected(String message) {
		JOptionPane.showMessageDialog(this, message, "Nothing selected", JOptionPane.WARNING_MESSAGE);
	}
}
