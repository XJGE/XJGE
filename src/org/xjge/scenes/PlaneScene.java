package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.core.Light;
import org.xjge.core.Skybox;
import org.xjge.graphics.Model;

/**
 * Dec 3, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class PlaneScene extends Scene {
    
    Model model;
    
    public PlaneScene() {
        super("plane");
        
        Skybox skybox = new Skybox("sky_noon_top.png", "sky_noon_center.png", "sky_noon_bottom.png", true);
        setSkybox(skybox);
        
        entities.put("plane", new EntityPlane(0, -3f, 0, 50, 50, Color.SILVER));
        entities.put("cube", new EntityCube(0, 3, 0, 1, 1, 1, true));
        
        model = new Model("mod_teapot.fbx");
        
        Game.setClearColor(Color.BLACK);
        
        Light l1 = Light.random();
        Light l2 = Light.random();
        Light l3 = Light.random();
        Light l4 = Light.random();
        Light l5 = Light.random();
        
        l2.position.set(10, 0, 4);
        l3.position.set(20, 0, -17);
        l4.position.set(-16, 0, -3);
        l5.position.set(-2, 0, 10);
        
        lights[0] = Light.midnight();
        
        lights[1] = l1;
        lights[2] = l2;
        lights[3] = l3;
        lights[4] = l4;
        lights[5] = l5;
        
        //TODO: resolution bug found- switching monitors/fullscreen effects performance?
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, shadowMap.depthTexHandle));
        
        //rendering models causes java to return an error: -1073741819
        //model.render(glPrograms.get("default"), lights, 128, shadowMap.depthTexHandle);
    }

    @Override
    public void exit() {
    }

}
