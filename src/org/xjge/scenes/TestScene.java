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
    
    private Skybox skybox = new Skybox("sky_noon_top.png", "sky_noon_center.png", "sky_noon_bottom.png", true);
    
    private EntityPlane plane1;
    private EntityPlane plane2;
    private EntityPlane plane3;
    private EntityPlane plane4;
    private EntityCube cube1;
    private EntityCube cube2;
    private EntityCube cube3;
    private EntityTeapot teapot;
    private EntityFortress fortress;
    
    public TestScene() {
        super("test");
        
        setSkybox(skybox);
        
        /*
        TODO:
         - shadow mapping should be as simple as setting up a skybox and setting 
           entities model matrix via renderShadow()
         - models should still exhibit lighting even with shadows disabled
         - clean up any loose ends- add documentation.
        */
        
        fortress = new EntityFortress(0, -2.99f, 0);
        plane1 = new EntityPlane(0, -3, 0, 50, 50, Color.BLUE);
        plane2 = new EntityPlane(0, 3, 0, 5, 5, Color.RED);
        plane3 = new EntityPlane(-1, 4, 0, 2, 2, Color.GREEN);
        plane4 = new EntityPlane(30, 3, 30, 2, 2, Color.YELLOW);
        cube1  = new EntityCube(3, 7, 4, 2, 2, 2, true);
        cube2  = new EntityCube(-14, -2, -20, 2, 2, 2, false);
        cube3  = new EntityCube(-10, 5, -15, 2, 2, 2, true);
        teapot = new EntityTeapot(0, -1, 0);
        
        entities.put("fortress", fortress);
        entities.put("plane1", plane1);
        entities.put("plane2", plane2);
        entities.put("plane3", plane3);
        entities.put("plane4", plane4);
        entities.put("cube1", cube1);
        entities.put("cube2", cube2);
        entities.put("cube3", cube3);
        entities.put("teapot", teapot);
        
        addLightAtIndex(0, new Light(1, 0.5f, new Vector3f(18, 6, 20), Color.WHITE, Color.WHITE));
        addLight(new Light(0.1f, 0.5f, new Vector3f(30, 2, 0), Color.YELLOW, Color.WHITE));
        addLight(new Light(0.1f, 0.5f, new Vector3f(-30, 2, 0), Color.YELLOW, Color.WHITE));
        //addLightAtIndex(1, new Light(1, 0.5f, new Vector3f(0, 0, 0), Color.YELLOW, Color.WHITE));
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