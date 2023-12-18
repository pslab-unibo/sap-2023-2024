package smart_room.distributed;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class PresenceDetectionFrame extends JFrame implements ActionListener {		
	
	private JButton enter;
	private JButton exit;
	private PresDetectSensorSimulator sim;
	
	public PresenceDetectionFrame(PresDetectSensorSimulator sim, String name){
		this.sim = sim;
		setTitle("Pres. Detect. Sensor: " + name);
		setSize(300,70);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		setContentPane(mainPanel);
		
		enter = new JButton("Enter");
		exit = new JButton("Exit");

		enter.addActionListener(this);
		exit.addActionListener(this);

		mainPanel.add(enter);
		mainPanel.add(exit);

		enter.setEnabled(true);
		exit.setEnabled(false);
		sim.updateValue(false);
	}
	
	
	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == enter){
			enter.setEnabled(false);
			exit.setEnabled(true);
			sim.updateValue(true);
		} else if (e.getSource() == exit) {
			enter.setEnabled(true);
			exit.setEnabled(false);
			sim.updateValue(false);
		}
	}

}