package dev.theskidster.xjge2.scene;

import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    
    public abstract void render(HashMap<String, GLProgram> glPrograms);
    
    public abstract void exit();
    
}