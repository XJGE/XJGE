package org.xjge.main;

//Created: Jan 25, 2023

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    boolean filterChanged;
    
    public TestScene() {
        super("test");
        
        entities.put("test", new TestEntity(0, 0, -40, 100));
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
        
        /*
        if(Game.tick(60)) {
            filterChanged = !filterChanged;
            XJGE.changeFramebufferFilter(GLFW_JOYSTICK_1, filterChanged);
        }
        */
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, depthTexHandle));
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}
