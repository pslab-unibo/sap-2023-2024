package sap.pixelart.apigateway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pixel grid entity
 * - observable
 * 
 * @author aricci
 *
 */
public class PixelGrid {
	private final int nRows;
	private final int nColumns;
	private final int[][] grid;
    private final List<PixelGridEventObserver> pixelListeners;
	
	public PixelGrid(final int nRows, final int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		grid = new int[nRows][nColumns];
		pixelListeners = new ArrayList<>();
	}

	public void clear() {
		for (int i = 0; i < nRows; i++) {
			Arrays.fill(grid[i], 0);
		}
	}
	
	public void set(final int x, final int y, final int color) {
		grid[y][x] = color;
		pixelListeners.forEach(l -> l.pixelColorChanged(x, y, color));

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
	
    public void addPixelGridEventListener(PixelGridEventObserver l) { 
    	pixelListeners.add(l); 
    }
	
}
