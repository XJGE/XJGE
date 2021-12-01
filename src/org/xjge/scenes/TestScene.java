package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Light;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    private float angle = 0;
    
    private Skybox skybox;
    
    private EntityFortress fortress;
    
    public TestScene() {
        super("test");
        
        skybox = new Skybox("sky_noon_top.png", "sky_noon_center.png", "sky_noon_bottom.png", true);
        
        /*
        skybox = new Skybox("sky_sunset_center.png", "sky_sunset_center.png", "sky_sunset_top.png", 
                "sky_sunset_bottom.png", "sky_sunset_center.png", "sky_sunset_back.png", true);
        */
        
        /*
        skybox = new Skybox("sky_midnight_center.png", "sky_midnight_center.png", "sky_midnight_top.png", 
                "sky_midnight_bottom.png", "sky_midnight_front.png", "sky_midnight_center.png", true);
        */
        
        setSkybox(skybox);
        
        entities.put("plane", new EntityPlane(0, -3, 0, 100, 100, Color.GRAY));
        entities.put("teapot", new EntityTeapot(0, 0, 0));
        entities.put("sprite", new EntitySprite(0, 6, 3, 1.5f, 2));
        entities.put("fortress", new EntityFortress(0, -2.99f, 0));
        
        addLightAtIndex(0, Light.daylight());
        
        addLight(new Light(0.1f, 0, 0.5f, new Vector3f(-27, -2, 0), Color.LIME));
        
        /*
        TODO
        - centralize world light shadow map options.
        - entities will determine individually if they wish to render shadows 
          with the new renderShadow() method.
        - add enable / disable shadows during runtime, might be another way
          we can squeeze out more performance say if a level doesnt require
          shadows to be drawn.
        - refactor / clean up this mess!
        - add doc
        - remove tests
        - recompile jars
        */
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        skybox.getModelMatrix().rotationY((float) Math.toRadians(angle -= 0.01f));
        //skybox.getModelMatrix().rotationY((float) Math.toRadians(8f));
        
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}