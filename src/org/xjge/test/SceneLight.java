package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light2;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.graphics.Shader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneLight extends Scene {

    Light2 light;
    
    public SceneLight() {
        super("test_light");
        
        //XJGE.setClearColor(Color.WHITE);
        
        var cubeA = new Entity().addComponent(new Cube(2, 0, 5, 1));
        var cubeB = new Entity().addComponent(new Cube(-2, 0, 5, 1));
        
        addEntity(cubeA);
        addEntity(cubeB);
        
        light = LightingSystem.request();
        light.range = 20f;
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