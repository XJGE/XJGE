package dev.theskidster.xjge2.scene;

import dev.theskidster.xjge2.shaderutils.GLProgram;
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
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void exit();
    
}