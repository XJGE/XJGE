package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.core.Window;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneAdd extends Scene implements PropertyChangeListener {

    private boolean subscribed;
    private float angle;
    private Skybox skybox;
    
    public SceneAdd() {
        super("test");
        
        var entityA = new Entity();
        var entityB = new Entity().addComponent(new CompTestA()).addComponent(new CompTestB());
        var entityC = new Entity().addComponent(new CompTestA()).addComponent(new Cube(0, 0, 10, 1));
        
        System.out.println("EntityA: " + entityA.uuid);
        System.out.println("EntityB: " + entityB.uuid);
        System.out.println("EntityC: " + entityC.uuid);
        
        addEntity(entityA);
        addEntity(entityB);
        addEntity(entityC);
        
        var right  = Texture.load("sky_right.png");
        var left   = Texture.load("sky_left.png");
        var top    = Texture.load("sky_top.png");
        var bottom = Texture.load("sky_bottom.png");
        var front  = Texture.load("sky_front.png");
        var back   = Texture.load("sky_back.png");
        
        Window.setViewportCamera(GLFW_JOYSTICK_2, new OrthoCam());
        
        //skybox = new Skybox(right, left, top, bottom, front, back, false);
        //setSkybox(skybox);
        //XJGE.enableBloom = true;
        //XJGE.setClearColor(Color.BLACK);
        //skybox.setBloomThreshold(0.9f);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        if(!subscribed) {
            Window.addObserver(this);
            subscribed = true;
        }
        
        for(var entity : queryEntities(CompTestA.class)) {
            
        }
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        for(var entity : queryEntities(Cube.class)) {
            entity.getComponent(Cube.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "WINDOW_WIDTH_CHANGED", "WINDOW_HEIGHT_CHANGED" -> {
                Window.setResolution(Window.getWidth(), Window.getHeight());
            }
        }
    }

}