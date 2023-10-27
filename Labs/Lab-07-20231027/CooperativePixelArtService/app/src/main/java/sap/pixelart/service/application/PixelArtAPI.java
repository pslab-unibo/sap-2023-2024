package sap.pixelart.service.application;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.pixelart.service.domain.PixelGridEventObserver;

/**
 * This is the API provided by the Application level
 * 
 * - not depending by any infrastructure technology
 *   (but Vert.x library for JSON -- any library could be good)
 *   
 * @author aricci
 *
 */
public interface PixelArtAPI  {
	/**
	 * Create a new brush
	 * 
	 * @return the brushId
	 */
	String createBrush();
	
	/**
	 * Get the list of current brushes
	 * 
	 * @return a JSON array of brush ids 
	 * 		
	 */
	JsonArray getCurrentBrushes();
	
	/**
	 * Get the state of a brush 
	 * 
	 * @param brushId
	 * @return a JSON object storing the current state of a brush
	 * 		   	- fields: "brushId": String, "x": int, "y": int, "color": int
	 * 
	 */
	JsonObject getBrushInfo(String brushId);
	
	/**
	 * Get the current pixel grid state
	 * 
	 * @retur a JSON object containing current pixel grid state:
	 * 			- fields: "numColums": int, "numRows": int, "pixels": JSONArray (row by row)
	 */
	JsonObject getPixelGridState();
	
	/**
	 * Move the specified brush to the specified position
	 * @param brushId
	 * @param y
	 * @param x
	 */
	void moveBrushTo(String brushId, int y, int x);

	/**
	 * Select (color) the pixel where the specified brush is located  
	 * 
	 * @param brushId
	 */
	void selectPixel(String brushId);
	
	/**
	 * 
	 * Change the color of the specified brush
	 * 
	 * @param brushId 
	 * @param color
	 */
	void changeBrushColor(String brushId, int color);
	
	/**
	 * 
	 * Remove the specified brush
	 * 
	 * @param brushId
	 */
	void destroyBrush(String brushId);
	
	
	/**
	 * 
	 * Subscribe the pixel grid events
	 * 
	 * @param l
	 */
	void subscribePixelGridEvents(PixelGridEventObserver l);
}
