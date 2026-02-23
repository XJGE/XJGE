package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneLight extends Scene {

    Light lightA;
    Light lightB;
    Light lightC;
    
    public SceneLight() {
        super("test_light");
        
        //XJGE.setClearColor(Color.WHITE);
        
        var cubeA = new Entity().addComponent(new Cube(2, 0, 7, 0.5f));
        var cubeB = new Entity().addComponent(new Cube(-2, 0, 7, 0.5f));
        
        addEntity(cubeA);
        addEntity(cubeB);
        
        //lightA = LightingSystem.request();
        //lightA.range = 20f;
        //lightA.position.z = 2f;
        
        lightB = LightingSystem.request();
        lightB.position.set(-4, 2, 8);
        lightB.type = LightType.WORLD;
        
        //LightB = LightingSystem.request();
        //lightC = LightingSystem.request(); //TODO: you can assign this to A or B and it will still work
        //lightC.position.set(6, 3, 9);
        //lightC.color.copy(Color.BLUE);
        //lightC.type = LightType.SPOT;
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