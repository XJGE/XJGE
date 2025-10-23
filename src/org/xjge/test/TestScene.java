package org.xjge.test;

import java.util.Map;
import static javax.management.Query.value;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TestScene extends Scene {

    public TestScene() {
        super("test scene");
        
        Entity testEntity = new Entity().addComponent(new TestComponent());
        addEntity(testEntity);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
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