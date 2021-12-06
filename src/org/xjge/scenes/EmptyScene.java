package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * Dec 3, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EmptyScene extends Scene {

    //Used to test performance
    public EmptyScene() {
        super("empty");
        
        /*
        entities.put("cube", new EntityCube(0, 0, 0, 1, 2, 1, true));
        
        Light light = Light.beacon();
        light.position.x = 4;
        
        lights[0] = Light.daylight();
        lights[1] = light;
        */
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        //entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        //entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, 0));
    }

    @Override
    public void exit() {
    }

}
