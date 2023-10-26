package sap.pixelart.service.domain;

public interface PixelGridEventObserver {
	void pixelColorChanged(int x, int y, int color);
}
