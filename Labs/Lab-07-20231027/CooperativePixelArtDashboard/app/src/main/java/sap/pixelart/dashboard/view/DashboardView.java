package sap.pixelart.dashboard.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.*;

import sap.pixelart.dashboard.model.PixelArtLocalModel;
import sap.pixelart.dashboard.model.PixelArtLocalModelListener;


public class DashboardView implements PixelArtLocalModelListener {

	private final PixelArtLocalModel grid;
    private final DashboardFrame frame;
    private final List<PixelGridEventListener> pixelListeners;
	private final List<ColorChangeListener> colorChangeListeners;

    public DashboardView(PixelArtLocalModel grid, int w, int h){
		this.grid = grid;
		this.frame = new DashboardFrame(w, h);   
		pixelListeners = new ArrayList<>();
		colorChangeListeners = new ArrayList<>();
    }

    public void setLocalBrushColor(int color) {
    	frame.setLocalBrushColor(color);
    }
    
    public void addPixelGridEventListener(PixelGridEventListener l) { pixelListeners.add(l); }

	public void addColorChangedListener(ColorChangeListener l) { colorChangeListeners.add(l); }

    public void refresh(){
        frame.refresh();
    }
        
    public void display() {
    	frame.display();
    }
    
	@Override
	public void notifiedPixelChanged(int y, int x, int color) {
		this.refresh();
	}
	
	public class DashboardFrame extends JFrame {
	    private final VisualiserPanel panel;
	    private final int w, h;
		private final List<MouseMovedListener> movedListener;
		private BrushManager brushManager;
		private BrushManager.Brush localBrush;

	    public DashboardFrame(int w, int h){
			this.w = w;
			this.h = h;
			movedListener = new ArrayList<>();
	        setTitle(".:: PixelArt ::.");
			setResizable(false);
	        
			brushManager = new BrushManager();
			localBrush = new BrushManager.Brush(0, 0, 0);
			brushManager.addBrush(localBrush);

			
			panel = new VisualiserPanel(grid, brushManager, w, h);
	        panel.addMouseListener(createMouseListener());
			panel.addMouseMotionListener(createMotionListener());
			var colorChangeButton = new JButton("Change color");
			colorChangeButton.addActionListener(e -> {
				var color = JColorChooser.showDialog(this, "Choose a color", Color.BLACK);
				if (color != null) {
					colorChangeListeners.forEach(l -> l.colorChanged(color.getRGB()));
				}
			});
			// add panel and a button to the button to change color
			add(panel, BorderLayout.CENTER);
			add(colorChangeButton, BorderLayout.SOUTH);
	        getContentPane().add(panel);
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			hideCursor();
			
			this.addMouseMovedListener((int x, int y) -> {
				localBrush.updatePosition(x, y);
				refresh();
			});
		}
			    
	    public void setLocalBrushColor(int color) {
	    	localBrush.setColor(color);
	    }
	    
	    public void refresh(){
	        panel.repaint();
	    }
	        
	    public void display() {
			SwingUtilities.invokeLater(() -> {
				this.pack();
				this.setVisible(true);
			});
	    }
	    
		public void addMouseMovedListener(MouseMovedListener l) { movedListener.add(l); }
		
		private void hideCursor() {
			var cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			var blankCursor = Toolkit.getDefaultToolkit()
					.createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
			// Set the blank cursor to the JFrame.
			this.getContentPane().setCursor(blankCursor);
		}

		private MouseListener createMouseListener () {
			return new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int dx = w / grid.getNumColumns();
					int dy = h / grid.getNumRows();
					int col = e.getX() / dx;
					int row = e.getY() / dy;
					pixelListeners.forEach(l -> l.selectedCell(col, row));
				}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
			};
		}

		private MouseMotionListener createMotionListener() {
			return new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {}

				@Override
				public void mouseMoved(MouseEvent e) {
					movedListener.forEach(l -> l.mouseMoved(e.getX(), e.getY()));
				}
			};
		}
	}	
	
}
