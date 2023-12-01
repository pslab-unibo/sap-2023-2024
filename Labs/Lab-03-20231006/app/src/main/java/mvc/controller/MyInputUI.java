package mvc.controller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MyInputUI implements UserInputSource {

	private List<UserInputObserver> observers;

	private MyFrame frame;
	
	public MyInputUI() {		
		observers = new ArrayList<UserInputObserver>();		
	    frame = new MyFrame();
	}

	public void addObserver(UserInputObserver obs){
		observers.add(obs);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			frame.setVisible(true);
		});
	}

	private void log(String msg) {
		System.out.println("[InputUI] " + msg);
	}
	
	class MyFrame extends JFrame implements ActionListener {

		private JTextField state;

		public MyFrame() {
			super("My Input UI");
			
			setSize(300, 70);
			setResizable(false);
			
			JButton button = new JButton("Update");
			button.addActionListener(this);
			
			JPanel panel = new JPanel();
			panel.add(button);		
			
			setLayout(new BorderLayout());
		    add(panel,BorderLayout.NORTH);
		    	    		
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
					System.exit(-1);
				}
			});
		}
	
		public void actionPerformed(ActionEvent ev) {
			try {
				log("New input detected.");
				for (UserInputObserver obs: observers){
					obs.notifyNewUpdateRequested();
				}
			} catch (Exception ex) {
			}
		}	
	}
	
}
