package smart_room.centralized;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;


class SingleBoardFrame extends JFrame implements ActionListener {		
	
	
	private SinglelBoardSimulator sim;
	
	private JTextField lumValue;
	private JSlider lumSlider;
	private int currentLumValue;
	private JPanel lightPanel;
	private JButton enter;
	private JButton exit;
	private boolean isOn;
	
	
	public SingleBoardFrame(SinglelBoardSimulator sim){
		this.sim = sim;
		setTitle("Board Simulator");
		setSize(400,600);
		
		JPanel mainPanel = new JPanel();		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JPanel lightControlPanel = new JPanel();
		lightControlPanel.setLayout(new BoxLayout(lightControlPanel, BoxLayout.Y_AXIS));

		JPanel lightTitlePanel = new JPanel();
		lightTitlePanel.setLayout(new FlowLayout());
		lightTitlePanel.add(new JLabel("Light State"));
		lightControlPanel.add(lightTitlePanel);
		
		lightPanel = new LightPanel(200,160);
		lightControlPanel.add(lightPanel);
		mainPanel.add(lightControlPanel);
		
		JSeparator s = new JSeparator();
        s.setOrientation(SwingConstants.HORIZONTAL);
        mainPanel.add(s); 
		
		JPanel lumControlPanel = new JPanel();
		lumControlPanel.setLayout(new BoxLayout(lumControlPanel, BoxLayout.Y_AXIS));

		JPanel lumTitlePanel = new JPanel();
		lumTitlePanel.setLayout(new FlowLayout());
		lumTitlePanel.add(new JLabel("Luminosity Sensor Control Panel"));
		lumControlPanel.add(lumTitlePanel);
				
		lumValue = new JTextField(3);
		lumValue.setText("" + currentLumValue);
		lumValue.setSize(50, 30);
		lumValue.setMinimumSize(lumValue.getSize());
		lumValue.setMaximumSize(lumValue.getSize());
		lumValue.setEditable(false);
			
		lumSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, currentLumValue);
		lumSlider.setSize(300, 60);
		lumSlider.setMinimumSize(lumSlider.getSize());
		lumSlider.setMaximumSize(lumSlider.getSize());
		lumSlider.setMajorTickSpacing(10);
		lumSlider.setMinorTickSpacing(1);
		lumSlider.setPaintTicks(true);
		lumSlider.setPaintLabels(true);
		
		lumSlider.addChangeListener((ChangeEvent ev) -> {
			int newTargetTemp = lumSlider.getValue();
			this.updateLum(newTargetTemp);
			lumValue.setText("" + newTargetTemp);
		});
		
		lumControlPanel.add(lumValue);
		lumControlPanel.add(lumSlider);
		mainPanel.add(lumControlPanel);
		
		JSeparator s1 = new JSeparator();
        s1.setOrientation(SwingConstants.HORIZONTAL);
        mainPanel.add(s1); 
		
		JPanel presDetControlPanel = new JPanel();
		presDetControlPanel.setLayout(new BoxLayout(presDetControlPanel, BoxLayout.Y_AXIS));
		JPanel presTitlePanel = new JPanel();
		presTitlePanel.setLayout(new FlowLayout());
		presTitlePanel.add(new JLabel("Presence Detection Control Panel"));
		presDetControlPanel.add(presTitlePanel);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		enter = new JButton("Enter");
		exit = new JButton("Exit");
		enter.setEnabled(true);
		exit.setEnabled(false);
		sim.updatePresDetValue(false);
		enter.addActionListener(this);
		exit.addActionListener(this);
		buttonsPanel.add(enter);
		buttonsPanel.add(exit);
		presDetControlPanel.add(buttonsPanel);
		mainPanel.add(presDetControlPanel);		
		
	}
	
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		repaint();
	}
	
	
	private void updateLum(int newValue) {
		this.currentLumValue = newValue;
		sim.updateLumValue(((double) newValue) / 100);
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
			sim.updatePresDetValue(true);
		} else if (e.getSource() == exit) {
			enter.setEnabled(true);
			exit.setEnabled(false);
			sim.updatePresDetValue(false);
		}
	}
	
	class LightPanel extends JPanel {
		
	    public LightPanel(int w, int h){
	    	setSize(w,h);
	    }

	    public void paint(Graphics g){    		    		
	    	Graphics2D g2 = (Graphics2D) g;

	    	if (isOn) {
	    		g2.setColor(Color.YELLOW);
	    	} else {
	    		g2.setColor(Color.BLACK);
	    	}
	    	g2.fillRect(20,20,this.getWidth() - 40,this.getHeight() - 40);
        }	    
	}
	

}