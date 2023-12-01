package sap.pixelart.dashboard.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import sap.pixelart.dashboard.model.PixelArtLocalModel;
import sap.pixelart.dashboard.view.*;
import sap.pixelart.library.PixelArtAsyncAPI;

public class Controller {
    static Logger logger = Logger.getLogger("[Controller]");	

	private PixelArtAsyncAPI pixelArtServiceProxy;
	private PixelArtLocalModel pixelLocalViewModel;
	private DashboardView view;
	private String brushId;
	
	public Controller(PixelArtAsyncAPI api) {
		this.pixelArtServiceProxy = api;
	}
	
	public Future<Void> init() {
		Promise<Void> p = Promise.promise();
		
		pixelArtServiceProxy.createBrush()
		.onSuccess(brushId -> {
			this.brushId = brushId;
			logger.log(Level.INFO, "Brush created: " + brushId);
			pixelArtServiceProxy.subscribePixelGridEvents(this::pixelColorChanged)
			.onSuccess(grid -> {
				logger.log(Level.INFO, "PixelGrid subscribed. ");	
				pixelLocalViewModel = new PixelArtLocalModel(grid);
				
				view = new DashboardView(pixelLocalViewModel, 800, 800);
				view.addPixelGridEventListener(this::selectedCell);
				view.addColorChangedListener(this::colorChanged);
				pixelLocalViewModel.addListener(view);
				
				view.display();
				p.complete();
			});
		});
		return p.future();
		
	}
	
	/* events notified by the GUI */

	public void selectedCell(int x, int y) {
		pixelArtServiceProxy.moveBrushTo(brushId, y, x)
		.onSuccess(res -> {
			pixelArtServiceProxy.selectPixel(brushId);
		});					
	}

	public void colorChanged(int color) {
		pixelArtServiceProxy.changeBrushColor(brushId, color)
		.onSuccess(res -> {
			view.setLocalBrushColor(color);
			logger.log(Level.INFO, "Color changed to: " + color);
		});
	}

	/* events notified by the service */
	
	public void pixelColorChanged(int x, int y, int color) {
		logger.log(Level.INFO, "New event from service " + y + " " + x + " color: " + color);
		pixelLocalViewModel.set(x, y, color);
	}
}
