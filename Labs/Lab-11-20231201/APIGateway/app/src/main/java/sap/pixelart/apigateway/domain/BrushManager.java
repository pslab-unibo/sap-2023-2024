package sap.pixelart.apigateway.domain;

import java.util.Collection;
import java.util.HashMap;

/**
 * Brush manager entity, managing a collection of brushes
 * 
 * @author aricci
 *
 */
public class BrushManager {
    private HashMap<String,Brush> brushes = new java.util.HashMap<>();

    public BrushManager() {
    	brushes = new java.util.HashMap<>();
    }
    
    public void addBrush(String id, Brush brush) {
        brushes.put(id, brush);
    }

    public Brush getBrush(String id) {
        return brushes.get(id);
    }
    
    public void removeBrush(String id) {
    	brushes.remove(id);	
    }
    
    public Collection<String> getBrushesId() {
    	return brushes.keySet();
    }

}
