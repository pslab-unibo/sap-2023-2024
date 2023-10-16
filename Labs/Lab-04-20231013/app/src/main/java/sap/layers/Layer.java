package sap.layers;

import java.util.Optional;

//Inietto la dipendenza dal livello sotto dinamicamente
public interface Layer {

	void init(Optional<Layer> layer);
}
