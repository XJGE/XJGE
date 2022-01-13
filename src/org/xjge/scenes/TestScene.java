package org.xjge.scenes;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 * Jan 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    TestScene() {
        super("test");
        
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "testWidget", new TestWidget());
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
