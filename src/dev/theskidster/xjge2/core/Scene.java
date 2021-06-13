package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Scene {

    public final String name;
    
    protected final LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
    
    public Scene(String name) {
        this.name = name;
    }
    
    public abstract void update(double targetDelta);
    
    public abstract void render(Map<String, GLProgram> glPrograms, Camera camera);
    
    public abstract void exit();
    
    void processRemoveRequests() {
        entities.entrySet().removeIf(entry -> entry.getValue().removalRequested());
    }
    
}