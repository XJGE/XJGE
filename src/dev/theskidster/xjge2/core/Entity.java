package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Entity {

    private boolean remove;
    
    public Vector3f position;
    
    protected Entity(Vector3f position) {
        this.position = position;
    }
    
    public abstract void update(double targetDelta);
    
    //Included legacy render method in case we just need one shader- this will likely be the case
    //for most things anyway.
    public abstract void render(GLProgram glProgram, Camera camera);
    
    public abstract void render(Map<String, GLProgram> glPrograms, Camera camera);
    
    //Should ONLY be used to deallocate memory, death animations and other effects should be included in the own 
    //games logic via update method.
    protected abstract void destroy();
    
    protected void remove() {
        remove = true;
    }
    
    public boolean removalRequested() {
        if(remove) destroy();
        return remove;
    }
    
}