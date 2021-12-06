package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.core.Light;

/**
 * Dec 3, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class PlaneScene extends Scene {
    
    public PlaneScene() {
        super("plane");
        
        entities.put("plane", new EntityPlane(0, -3f, 0, 50, 50, Color.SILVER));
        entities.put("cube", new EntityCube(0, 3, 0, 1, 1, 1, true));
        
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
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, shadowMap.depthTexHandle));
    }

    @Override
    public void exit() {
    }

}
