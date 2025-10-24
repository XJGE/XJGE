package org.xjge.test;

import java.util.Map;
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
        
        Entity testEntity = new Entity().addComponent(new TestComponent(1, 0, 0, -4));
        addEntity(testEntity);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.values().forEach(entity -> {
            if(entity.hasComponent(TestComponent.class)) entity.getComponent(TestComponent.class).render(glPrograms.get("test"));
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}