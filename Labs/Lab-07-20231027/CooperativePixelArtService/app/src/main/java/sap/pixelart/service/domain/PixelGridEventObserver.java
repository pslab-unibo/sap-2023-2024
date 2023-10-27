package sap.pixelart.service.domain;

/**
 * Interface for the pixel grid observers
 * 
 * @author aricci
 *
 */
public interface PixelGridEventObserver {
	void pixelColorChanged(int x, int y, int color);
}
