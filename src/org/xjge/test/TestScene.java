package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.UI;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    public TestScene() {
        super("test");
        
        Entity entity = new Entity();
        entity.addComponent(new TestMesh(1, 0f, 0f, -5f));
        
        addEntity(entity);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.values().forEach(entity -> {
            if(entity.hasComponent(TestMesh.class)) entity.getComponent(TestMesh.class).render(glPrograms.get("test"));
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}