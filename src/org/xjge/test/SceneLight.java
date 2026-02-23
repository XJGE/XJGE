package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
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
        
        XJGE.setClearColor(Color.BLACK);
        
        var cubeA = new Entity().addComponent(new Prism(2, 0, 7, 1f, 1f, 1f));
        var cubeB = new Entity().addComponent(new Prism(-2, 0, 7, 1f, 1f, 1f));
        var plane = new Entity().addComponent(new Prism(0, -1f, 0, 20f, 0.25f, 20f));
        
        addEntity(cubeA);
        addEntity(cubeB);
        addEntity(plane);
        
        lightA = LightingSystem.request();
        lightA.position.set(2, 1.5f, -7);
        lightA.brightness = 0.5f;
        lightA.color.copy(Color.PURPLE);
        
        lightB = LightingSystem.request();
        lightB.position.set(-2, 1.5f, 7);
        lightB.type = LightType.SPOT;
        lightB.color.copy(Color.RED);
        
        //LightB = LightingSystem.request();
        lightC = LightingSystem.request(); //TODO: you can assign this to A or B and it will still work
        lightC.position.set(4, 5, 5);
        lightC.type = LightType.WORLD;
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        for(var cube : queryEntities(Prism.class)) {
            cube.getComponent(Prism.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

}