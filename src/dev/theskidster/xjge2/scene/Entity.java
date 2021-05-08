package dev.theskidster.xjge2.scene;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Entity {

    public Vector3f position;
    
    public abstract void update();
    
    public abstract void render();
    
}