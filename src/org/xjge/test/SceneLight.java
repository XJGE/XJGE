package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.core.UIManager;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneLight extends Scene {

    Entity cubeA;
    Entity cubeB;
    Entity cubeC;
    Entity plane;
    
    Light lightA;
    Light lightB;
    Light lightC;
    
    CameraOverhead camA;
    CameraOverhead camB;
    CameraTransition camT;
    
    public SceneLight() {
        super("test_light");
        
        XJGE.setClearColor(Color.BLACK);
        //XJGE.enableBloom = true;
        
        cubeA = new Entity().addComponent(new Prism(2, 0, -5, 1f, 1f, 1f));
        cubeB = new Entity().addComponent(new Prism(-2, 0, -8f, 1.5f, 1.5f, 1.5f));
        cubeC = new Entity().addComponent(new Prism(-4, 0, 7, 2f, 6f, 2f));
        plane = new Entity().addComponent(new Prism(0, -1f, 0, 20f, 0.25f, 20f));
        
        cubeA.getComponent(Prism.class).roughness = 0.7f;
        cubeA.getComponent(Prism.class).ambientFactor = 0.3f;
        cubeA.getComponent(Prism.class).specularColor.set(0.04f);
        
        cubeC.getComponent(Prism.class).roughness = 0.1f;
        cubeC.getComponent(Prism.class).ambientFactor = 0.05f;
        cubeC.getComponent(Prism.class).metallic = 0.5f;
        
        plane.getComponent(Prism.class).roughness = 0.7f;
        plane.getComponent(Prism.class).specularColor.set(0.5f);
        
        addEntity(cubeA);
        addEntity(cubeB);
        addEntity(cubeC);
        addEntity(plane);
        
        var right  = Texture.load("sky_right.png");
        var left   = Texture.load("sky_left.png");
        var top    = Texture.load("sky_top.png");
        var bottom = Texture.load("sky_bottom.png");
        var front  = Texture.load("sky_front.png");
        var back   = Texture.load("sky_back.png");
        setSkybox(new Skybox(right, left, top, bottom, front, back, false));
        
        lightA = LightingSystem.request();
        lightA.position.set(2, 1.5f, 7);
        lightA.brightness = 0.5f;
        lightA.color.copy(Color.PURPLE);
        
        lightB = LightingSystem.request();
        lightB.position.set(-2, 1.5f, -7);
        lightB.type = LightType.SPOT;
        lightB.color.copy(Color.RED);
        
        //LightB = LightingSystem.request();
        lightC = LightingSystem.request(); //TODO: you can assign this to A or B and it will still work
        lightC.position.set(4, 5, -5);
        lightC.type = LightType.WORLD;
        
        camA = new CameraOverhead( 10, 10, 10, -1,-1,-1);
        camB = new CameraOverhead(-10, 10,-10,  1,-1, 1);
        
        Window.setViewportCamera(GLFW_JOYSTICK_1, camA);
        UIManager.addWidget(GLFW_JOYSTICK_1, "camera_control", new UIChangeCamera(camA, camB));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(int viewportID, Camera camera, int depthTexHandle) {
        for(var cube : queryEntities(Prism.class)) {
            cube.getComponent(Prism.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
        removeEntity(cubeA.uuid);
        removeEntity(cubeB.uuid);
        removeEntity(cubeC.uuid);
        removeEntity(plane.uuid);
        
        LightingSystem.release(lightA);
        LightingSystem.release(lightB);
        LightingSystem.release(lightC);
    }

}