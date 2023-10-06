package task_one.view;

import task_one.model.ModelObserver;
import task_one.model.ModelObserverSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyView implements ModelObserver {
	private static final Logger logger = Logger.getLogger(MyView.class.getName());
	private final ModelObserverSource model;
	private final MyFrame frame;
	
	public MyView(ModelObserverSource model) {		
		this.model = model;		
	    model.addObserver(this);
	    frame = new MyFrame(model.getState());
	}

	@Override
	public void notifyModelUpdated() {
		Logger.getLogger(MyView.class.getName()).log(Level.INFO, "Model updated");
		frame.updateView(model.getState());
	}
		
	public void display() {
		SwingUtilities.invokeLater(() -> frame.setVisible(true));
	}

	
	static class MyFrame extends JFrame  {

		private final JTextField state;

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
				SwingUtilities.invokeLater(() -> state.setText("" + newState));
			} catch (Exception ex){
				logger.log(Level.SEVERE, null, ex);
			}
		}
	}
	
}
