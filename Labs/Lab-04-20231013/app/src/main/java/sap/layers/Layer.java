package sap.layers;

import java.util.Optional;

public interface Layer {

	void init(Optional<Layer> layer);
}
