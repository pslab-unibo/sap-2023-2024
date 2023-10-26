package sap.pixelart.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.*;

public class PixelArtLocalModel {
	private int nRows;
	private int nColumns;
	private int[][] grid; 
	private List<PixelArtLocalModelListener> listeners;
	
	public PixelArtLocalModel(JsonObject initialState) {
		this.nRows = initialState.getInteger("numRows");
		this.nColumns = initialState.getInteger("numColumns");;
		grid = new int[nRows][nColumns];
		JsonArray pixels = initialState.getJsonArray("pixels");
		int index = 0;
		for (int y = 0; y < nRows; y++) {
			for (int x = 0; x < nColumns; x++) {
				int color = pixels.getInteger(index);
				grid[y][x] = color;
				index++;
			}
		}
		listeners = new ArrayList<>();
	}

	public void set(final int x, final int y, final int color) {
		grid[y][x] = color;
		for (PixelArtLocalModelListener l: listeners) {
			l.notifiedPixelChanged(x, y, color);
		}
	}
	
	public int get(int x, int y) {
		return grid[y][x];
	}
	
	public int getNumRows() {
		return this.nRows;
	}
	

	public int getNumColumns() {
		return this.nColumns;
	}

	public void addListener(PixelArtLocalModelListener l) {
		listeners.add(l);
	}
}
