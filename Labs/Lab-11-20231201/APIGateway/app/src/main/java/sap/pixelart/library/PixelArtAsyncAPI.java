package sap.pixelart.library;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * Async Interface of a library for interacting with the PixelArt Service
 * 
 * @author aricci
 *
 */
public interface PixelArtAsyncAPI  {
	
	Future<String> createBrush();
	
	Future<JsonArray> getCurrentBrushes();
	Future<JsonObject> getBrushInfo(String brushId);
	Future<Void> moveBrushTo(String brushId, int y, int x);
	Future<Void> selectPixel(String brushId);
	Future<Void> changeBrushColor(String id, int color);
	Future<Void> destroyBrush(String brushId);

	Future<JsonObject> getPixelGridState();
	
	Future<JsonObject> subscribePixelGridEvents(PixelGridEventObserver l);
}
