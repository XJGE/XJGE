package org.xjge.main;

//Created: Jan 29, 2023

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
class TestScene extends Scene {

    TestScene() {
        super("test");
        
        entities.put("test", new TestEntity(0, 0, -10, 20));
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.entrySet().forEach(entity -> entity.getValue().update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.entrySet().forEach(entity -> entity.getValue().render(glPrograms.get("palShader"), camera, lights, depthTexHandle));
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}
