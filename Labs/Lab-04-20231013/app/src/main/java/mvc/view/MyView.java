package mvc.view;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import mvc.model.*;

import mvc.model.ModelObserver;

public class MyView implements ModelObserver {

	private ModelObserverSource model;
	private MyFrame frame;
	
	public MyView(ModelObserverSource model) {		
		this.model = model;		
	    model.addObserver(this);	    
	    frame = new MyFrame(model.getState());
	}

	public void notifyModelUpdated() {
		log("model updated => updating the view");
		frame.updateView(model.getState());
	}
		
	public void display() {
		SwingUtilities.invokeLater(() -> {
			frame.setVisible(true);
		});
	}

	private void log(String msg) {
		System.out.println("[View] " + msg);
	}
	
	class MyFrame extends JFrame  {

		private JTextField state;

		public MyFrame(int initState) {
			super("My View");
			
			setSize(300, 70);
			setResizable(false);
			
			state = new JTextField(10);
			state.setText("" + initState);

			JPanel panel = new JPanel();
			panel.add(state);
			
			setLayout(new BorderLayout());
		    add(panel,BorderLayout.NORTH);
		    	    		
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
					System.exit(-1);
				}
			});
		}
	
		public void updateView(int newState) {
			try {
				SwingUtilities.invokeLater(() -> {
					state.setText("" + newState);
				});
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
}
