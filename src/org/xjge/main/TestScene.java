package org.xjge.main;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 *
 * @author thesk
 */
public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "test", new OpacityWidget());
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
