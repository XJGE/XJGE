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
        XJGE.enableBloom = true;
        
        var cubeA = new Entity().addComponent(new Prism(2, 0, 5, 1f, 1f, 1f));
        var cubeB = new Entity().addComponent(new Prism(-2, 0, 6.25f, 1.5f, 1.5f, 1.5f));
        var cubeC = new Entity().addComponent(new Prism(-4, 0, -7, 2f, 6f, 2f));
        var plane = new Entity().addComponent(new Prism(0, -1f, 0, 20f, 0.25f, 20f));
        
        cubeA.getComponent(Prism.class).material.roughness = 0.7f;
        cubeA.getComponent(Prism.class).material.ambientFactor = 0.3f;
        cubeA.getComponent(Prism.class).material.specular.set(0.04f);
        
        cubeC.getComponent(Prism.class).material.roughness = 0.1f;
        cubeC.getComponent(Prism.class).material.ambientFactor = 0.05f;
        cubeC.getComponent(Prism.class).material.metallic = 1f;
        
        plane.getComponent(Prism.class).material.roughness = 0.7f;
        plane.getComponent(Prism.class).material.specular.set(0.5f);
        
        addEntity(cubeA);
        addEntity(cubeB);
        addEntity(cubeC);
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