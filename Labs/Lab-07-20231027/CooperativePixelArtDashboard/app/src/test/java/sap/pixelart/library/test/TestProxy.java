package sap.pixelart.library.test;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sap.pixelart.library.PixelArtAsyncAPI;
import sap.pixelart.library.PixelArtServiceLib;
import sap.pixelart.library.test.*;

public class TestProxy {
    static Logger logger = Logger.getLogger("[PixelArtServiceProxy Test]");	

	public static void main(String[] args) {
		
		PixelArtAsyncAPI serviceProxy = PixelArtServiceLib.getInstance().getDefaultInterface();
		
		/*
		serviceProxy.createBrush()
		.onSuccess(brushId -> {
			System.out.println("Brush id: " + brushId);	
			serviceProxy.moveBrushTo(brushId, 10, 5)
			.onSuccess(res -> {
				logger.log(Level.INFO, "ok");
				serviceProxy.getBrushInfo(brushId)
				.onSuccess(brushInfo -> {
					logger.log(Level.INFO, brushInfo.encodePrettily());
					serviceProxy.selectPixel(brushId)
					.onSuccess(res2 -> {
						logger.log(Level.INFO, "select pixel ok");
						serviceProxy.getPixelGridState()
						.onSuccess(pixelGridsState -> {
							logger.log(Level.INFO, pixelGridsState.encodePrettily());
						});
					});
				});
			});
		});
		 */
		
		serviceProxy.subscribePixelGridEvents((int x, int y, int color) -> {
			logger.log(Level.INFO, "NEW EVENT " + y + " " + x + " " + color);
		}).onSuccess(res -> {
			logger.log(Level.INFO, "subscribed: \n" + res.encodePrettily());
		});
		/*
		serviceProxy.getPixelGridState()
		.onSuccess(pixelGridsState -> {
			logger.log(Level.INFO, pixelGridsState.encodePrettily());
		});
		*/
		
		/*
		serviceProxy.getBrushInfo("brush-1")
		.onSuccess(brushes -> {
			logger.log(Level.INFO, brushes.encodePrettily());
		});
		*/
		
		/*
		serviceProxy.getCurrentBrushes()
		.onSuccess(brushes -> {
			logger.log(Level.INFO, brushes.encodePrettily());
		});
		*/
		/*
		serviceProxy.createBrush()
		.onSuccess(brushId -> {
			System.out.println("Brush id: " + brushId);	
		});
		 */
		
		/*
		var brushManager = new BrushManager();
		var localBrush = new BrushManager.Brush(0, 0, randomColor());
		brushManager.addBrush(localBrush);
		// brushManager.addBrush(fooBrush);
		PixelGridDashboardModel grid = new PixelGridDashboardModel(40,40);
		*/
		
		/*
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
		}
		*/

		/*
		PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

		view.addMouseMovedListener((x, y) -> {
			localBrush.updatePosition(x, y);
			view.refresh();
		});

		view.addPixelGridEventListener((x, y) -> {
			// grid.set(x, y, localBrush.getColor());
			// view.refresh();
		});

		view.addColorChangedListener(localBrush::setColor);

		view.display();
		*/
		
	}

}
