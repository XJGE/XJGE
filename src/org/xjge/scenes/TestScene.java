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
        
        setSkybox(skybox);
        
        /*
        TODO:
         - shadow mapping should be as simple as setting up a skybox and setting 
           entities model matrix via renderShadow()
         - models should still exhibit lighting even with shadows disabled
         - clean up any loose ends- add documentation.
        */
        
        
        entities.put("plane", new EntityPlane(0, -3, 0, 100, 100, Color.GRAY));
        entities.put("teapot", new EntityTeapot(6, 0, -10));
        entities.put("teapot2", new EntityTeapot(6, 4, -5));
        entities.put("sprite", new EntitySprite(0, 1, -6, 1.5f, 2));
        entities.put("sprite2", new EntitySprite(0, 2, -4, 1.5f, 2));
        
        addLightAtIndex(0, Light.daylight());
        
        
        /*
        TODO:
        - add new uType for sprites that want to exhibit lighting effects.
        - refactor / clean up this mess!
        - add doc
        - remove tests
        - recompile jars
        */
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        skybox.getModelMatrix().rotationY((float) Math.toRadians(angle -= 0.01f));
        
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