package dev.theskidster.xjge2.scene;

import java.util.ArrayList;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Scene {

    //TODO: this is real basic, we'll come back and spice it up later.
    
    protected static final ArrayList<Entity> entityList = new ArrayList<>();
    
    public static void addEntity(Entity e) {
        entityList.add(e);
    }
    
    public abstract void update();
    
    public abstract void render();
    
}