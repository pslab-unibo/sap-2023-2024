package sap.pixelart.apigateway.application;

import io.vertx.core.json.*;
import sap.pixelart.apigateway.domain.*;

/**
 * Application layer implemetation
 * 
 * - using the domain layer
 * 
 * @author aricci
 *
 */
public class PixelArtServiceImpl implements PixelArtAPI {

	private BrushManager brushManager;
	private PixelGrid grid;
	private int brushCounter;
	
	public void init() {
		brushCounter = 0;
		brushManager = new BrushManager();
		grid = new PixelGrid(40,40);
	}

	@Override
	public String createBrush() {
		brushCounter++;
		String brushId ="brush-" + brushCounter; 
		brushManager.addBrush(brushId, new Brush(0,0,0));
		return brushId;
	}

	@Override
	public JsonArray getCurrentBrushes() {
		var c = brushManager.getBrushesId();
		JsonArray list = new JsonArray();
		for (String s: c) {
			list.add(s);
		}
		return list;
	}
	
	@Override
	public void moveBrushTo(String brushId, int y, int x) {
		Brush b = brushManager.getBrush(brushId);
		b.updatePosition(x, y);		
	}

	@Override
	public void selectPixel(String brushId) {
		Brush b = brushManager.getBrush(brushId);
		grid.set(b.getX(), b.getY(), b.getColor());
	}

	@Override
	public void changeBrushColor(String brushId, int color) {
		Brush b = brushManager.getBrush(brushId);
		b.setColor(color);
	}

	@Override
	public void destroyBrush(String brushId) {
		brushManager.removeBrush(brushId);
	}

	@Override
	public JsonObject getBrushInfo(String brushId) {
		Brush b = brushManager.getBrush(brushId);
		JsonObject info = new JsonObject();
		info.put("brushId", brushId);
		info.put("color", b.getColor());
		info.put("x", b.getX());
		info.put("y", b.getY());
		return info;
	}

	@Override
	public JsonObject getPixelGridState() {
		JsonObject info = new JsonObject();
		info.put("numColumns", grid.getNumColumns());
		info.put("numRows", grid.getNumRows());
		JsonArray pixels = new JsonArray();
		for (int y = 0; y < grid.getNumRows(); y++) {
			for (int x = 0; x < grid.getNumColumns(); x++)
				pixels.add(grid.get(x, y));
		}
		info.put("pixels", pixels);
		return info;
	}

	@Override
	public void subscribePixelGridEvents(PixelGridEventObserver l) {
		grid.addPixelGridEventListener(l);
	}
}
