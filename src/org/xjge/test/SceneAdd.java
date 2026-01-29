package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneAdd extends Scene {

    private float angle;
    private Skybox skybox;
    
    public SceneAdd() {
        super("test");
        
        Texture tex1 = Texture.load("test_tex1.png");
        Texture tex2 = Texture.load("test_tex2.png");
        Texture tex3 = Texture.load("test_tex3.png");
        Texture tex4 = Texture.load("test_tex4.png");
        Texture tex5 = Texture.load("test_tex5.png");
        Texture tex6 = Texture.load("test_tex6.png");
        Texture tex7 = Texture.load("test_tex7.png");
        Texture tex8 = Texture.load("test_tex8.png");
        
        var entityA = new Entity();
        var entityB = new Entity().addComponent(new CompTestA()).addComponent(new CompTestB());
        var entityC = new Entity().addComponent(new CompTestA());
        
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
        
        skybox = new Skybox(right, left, top, bottom, front, back, false);
        
        setSkybox(skybox);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        for(var entity : queryEntities(CompTestA.class)) {
            
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}