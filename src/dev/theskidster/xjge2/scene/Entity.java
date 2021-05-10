package dev.theskidster.xjge2.scene;

import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Entity {

    public Vector3f position;
    
    protected Entity(Vector3f position) {
        this.position = position;
    }
    
    public abstract void update(double targetDelta);
    
    public abstract void render(GLProgram glProgram);
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    //TODO: add removal stuff.
    
}