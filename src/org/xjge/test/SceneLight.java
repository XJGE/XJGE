package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneLight extends Scene {

    public SceneLight() {
        super("test_light");
        
        XJGE.setClearColor(Color.BLACK);
        
        var cubeA = new Entity().addComponent(new Cube(2, 0, 10, 1));
        var cubeB = new Entity().addComponent(new Cube(-2, 0, 10, 1));
        
        addEntity(cubeA);
        addEntity(cubeB);
        
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        for(var cube : queryEntities(Cube.class)) {
            cube.getComponent(Cube.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

}