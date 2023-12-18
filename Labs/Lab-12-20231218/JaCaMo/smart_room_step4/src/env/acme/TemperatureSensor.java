package acme;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.*;
import cartago.*;
import cartago.tools.*;

public class TemperatureSensor extends Artifact {

	private TemperatureSensorGUIFrame frame;
	private static int START_TEMPERATURE = 18;
	
	public void init() {
		try {
			defineObsProperty("temperature",START_TEMPERATURE);		
			frame = new TemperatureSensorGUIFrame(this);
			SwingUtilities.invokeAndWait(() -> {
				frame.setVisible(true);	
			});
			log("ready.");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	void notifyNewTemperature(double newValue){
		ObsProperty prop = getObsProperty("temperature");
		prop.updateValue(newValue);
		frame.setTempValue(newValue);
	}

	@OPERATION void updateTemperature(double delta){
		ObsProperty prop = getObsProperty("temperature");
		double newValue = prop.doubleValue()+delta; 
		prop.updateValue(newValue);
		frame.setTempValue(newValue);
	}

	@INTERNAL_OPERATION void closed(WindowEvent ev){
		System.exit(0);
	}
		
	class TemperatureSensorGUIFrame extends JFrame {		
		
		private JButton tempButton;
		private JTextField target;
		private JTextField tempValue;
		private TemperatureSensor artifact;
		
		public TemperatureSensorGUIFrame(TemperatureSensor artifact){
			this.artifact = artifact;
			setTitle("..:: Room Temperature Sensor ::..");
			setSize(400,100);
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			setContentPane(mainPanel);

			JPanel currentTempPanel = new JPanel();
			currentTempPanel.setLayout(new BoxLayout(currentTempPanel, BoxLayout.X_AXIS));
			
			tempValue = new JTextField(5);
			tempValue.setText("0");
			tempValue.setSize(100, 30);
			tempValue.setMinimumSize(tempValue.getSize());
			tempValue.setMaximumSize(tempValue.getSize());
			tempValue.setEditable(false);

			currentTempPanel.add(new JLabel("Current temperature: "));
			currentTempPanel.add(tempValue);

			//
			
			JPanel setTempPanel = new JPanel();
			setTempPanel.setLayout(new BoxLayout(setTempPanel, BoxLayout.X_AXIS));

			tempButton = new JButton("set");
			tempButton.setSize(80,50);
			
			tempButton.addActionListener(ev -> {
				artifact.beginExtSession();
				try {
					double newValue = Double.parseDouble(target.getText());
					artifact.notifyNewTemperature(newValue);
					artifact.endExtSession();
				} catch (Exception ex){
					ex.printStackTrace();
					artifact.endExtSessionWithFailure();
				}				
			});
			target = new JTextField(5);
			target.setText("0");
			target.setSize(100, 30);
			target.setMinimumSize(target.getSize());
			target.setMaximumSize(target.getSize());
			target.setEditable(true);
			
			setTempPanel.add(new JLabel("Change simulated temperature to: "));
			setTempPanel.add(target);
			setTempPanel.add(Box.createRigidArea(new Dimension(0,5)));
			setTempPanel.add(tempButton);
			
			mainPanel.add(currentTempPanel);
			setTempPanel.add(Box.createRigidArea(new Dimension(10,0)));
			mainPanel.add(setTempPanel);
		}
		
		public void setTempValue(double v){
			SwingUtilities.invokeLater(() -> {
				tempValue.setText(""+v);
			});
		}
	}
}
